package com.icloudberry.nettyserver.server;

import com.icloudberry.nettyserver.helper.PropertyHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * HttpServer bean class.
 * Start http-server after initialization (in @PostConstruct method).
 * <p/>
 * Author: icloudberry
 */
@Component
public class HttpServer {

    /**
     * ServerBootstrap bean
     */
    @Autowired
    private ServerBootstrap bootstrap;

    @Autowired
    private PropertyHelper helper;

    /**
     * Channel future instance. To close channel before current bean destruction.
     */
    private ChannelFuture f;

    /**
     * Start http-server.
     *
     * @throws Exception
     */
    @PostConstruct
    public void run() throws Exception {
        f = bootstrap.bind(helper.getHttpPort()).sync().channel().closeFuture().sync();
    }

    /**
     * Stop http-server.
     *
     * @throws Exception
     */
    @PreDestroy
    public void stop() throws Exception {
        f.channel().close();
    }
}
