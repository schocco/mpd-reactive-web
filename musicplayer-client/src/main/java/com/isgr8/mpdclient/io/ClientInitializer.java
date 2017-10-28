package com.isgr8.mpdclient.io;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import reactor.core.publisher.Flux;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();
    private static final MpdClientHandler CLIENT_HANDLER = new MpdClientHandler();
    private static final int MAX_FRAME_LENGTH = 8192;
    private String host;
    private int port;
    private SslContext sslContext;

    public ClientInitializer(String host, int port, SslContext sslContext) {
        this.host = host;
        this.port = port;
        this.sslContext = sslContext;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(ch.alloc(), host, port));
        }
        pipeline.addLast(new DelimiterBasedFrameDecoder(MAX_FRAME_LENGTH, Delimiters.lineDelimiter()));
        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);
        pipeline.addLast(CLIENT_HANDLER);
    }

    Flux<String> getInboundMessages() {
        return CLIENT_HANDLER.getInboundMessages();
    }

    static MpdClientHandler getClientHandler() {
        return CLIENT_HANDLER;
    }
}
