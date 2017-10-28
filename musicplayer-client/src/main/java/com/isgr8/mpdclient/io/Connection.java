package com.isgr8.mpdclient.io;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.io.Closeable;
import java.io.IOException;

public class Connection implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);
    private final String host;
    private final int port;
    private final SslContext sslContext;
    private EventLoopGroup nioEventLoopGroup;
    private Channel channel;
    private Flux<String> outbound;
    private FluxSink<String> outboundSink;
    private Mono<Void> lastWrite = Mono.empty();
    private ClientInitializer clientInitializer;
    private Flux<String> inboundMessages;
    private boolean connected;
    private boolean shutdown;

    public Connection(SslContext sslContext, String host, int port) {
        this.sslContext = sslContext;
        this.host = host;
        this.port = port;
        this.clientInitializer = new ClientInitializer(host, port, sslContext);
        this.outbound = Flux.create(stringFluxSink -> this.outboundSink = stringFluxSink);
        this.nioEventLoopGroup = new NioEventLoopGroup();

    }

    public Connection(String host, int port) {
        this(null, host, port);
    }


    public Mono<String> connect() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup).channel(NioSocketChannel.class).handler(clientInitializer);
        channel = bootstrap.connect(host, port).sync().channel();
        outbound.takeWhile(s -> this.connected).subscribe(this::writeAndFlush);
        ClientInitializer.getClientHandler().onChannelUnregistered(this::reconnect);
        inboundMessages = clientInitializer.getInboundMessages();
        return inboundMessages
                .next()
                .map(version -> version.replace("OK MPD ", ""))
                .doOnSuccess(s -> this.connected = true);
    }

    public boolean isConnected() {
        return connected;
    }

    public void send(String msg) {
        outboundSink.next(msg);
    }

    public Flux<String> getInboundMessages() {
        return inboundMessages;
    }

    public Mono<Void> shutdown() {
        shutdown = true;
        Mono<Void> awaitChannelClose = toMono(channel.closeFuture());
        Mono<Void> awaitEventLoopShutdown = toMono(nioEventLoopGroup.shutdownGracefully());
        return lastWrite.and(awaitChannelClose).and(awaitEventLoopShutdown).then();
    }

    @Override
    public void close() throws IOException {
        this.shutdown().block();
    }

    private void reconnect() {
        this.connected = false;
        if (!shutdown) {
            try {
                this.connect();
            } catch (InterruptedException e) {
                LOGGER.info("Reconnection attempt was interrupted.");
            }
        }
    }

    private void writeAndFlush(String message) {
        LOGGER.debug("tx: {}", message);
        lastWrite = toMono(channel.writeAndFlush(message));
    }

    private static Mono<Void> toMono(Future channelFuture) {
        return Mono.create(voidMonoSink -> channelFuture.addListener(future -> voidMonoSink.success()));
    }
}
