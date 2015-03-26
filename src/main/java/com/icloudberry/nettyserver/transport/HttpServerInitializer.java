package com.icloudberry.nettyserver.transport;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Initialize channel pipeline with all handlers.
 * <p/>
 * Author: icloudberry
 */
@Component
@Qualifier("serverInitializer")
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * ChannelHandler bean instance.
     */
    @Autowired
    private ChannelHandler channelHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());  // init Encoder and Decoder
        p.addLast(channelHandler);  // add handler to handle request and send response
    }
}
