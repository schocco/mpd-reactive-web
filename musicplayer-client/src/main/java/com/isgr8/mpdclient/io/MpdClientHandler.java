package com.isgr8.mpdclient.io;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.WorkQueueProcessor;

@ChannelHandler.Sharable
public class MpdClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MpdClientHandler.class);
    private Flux<String> inboundMessages;
    private final WorkQueueProcessor<String> workQueueProcessor;
    private ChannelClosedHandler onChannelClosedHandler;

    MpdClientHandler() {
        workQueueProcessor = WorkQueueProcessor.create();
        inboundMessages = Flux.from(workQueueProcessor);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        LOGGER.info("rx: {}", msg);
        workQueueProcessor.onNext(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        workQueueProcessor.onError(cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        if(onChannelClosedHandler != null) {
            onChannelClosedHandler.apply();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    Flux<String> getInboundMessages() {
        return inboundMessages;
    }

    void onChannelUnregistered(ChannelClosedHandler onChannelCLosedHandler) {
        this.onChannelClosedHandler = onChannelCLosedHandler;
    }

    public interface ChannelClosedHandler {
        void apply();
    }
}
