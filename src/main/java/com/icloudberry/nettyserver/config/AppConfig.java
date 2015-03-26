package com.icloudberry.nettyserver.config;

import com.icloudberry.nettyserver.transport.HttpServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Spring configuration class.
 * <p/>
 * Author: icloudberry
 */
@Configuration
@ComponentScan(basePackages = "com.icloudberry.nettyserver")
@PropertySource("classpath:application.properties")
public class AppConfig {

    /**
     * Server initializer bean.
     */
    @Autowired
    @Qualifier("serverInitializer")
    private HttpServerInitializer serverInitializer;

    /**
     * Server bootstrap bean initialization.
     *
     * @return ServerBootstrap bean.
     */
    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(serverInitializer)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        return b;
    }

    /**
     * BossGroup bean initialization.
     *
     * @return NioEventLoopGroup bossGroup
     */
    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup();
    }

    /**
     * WorkerGroup bean initialization.
     *
     * @return NioEventLoopGroup workerGroup
     */
    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup();
    }

    /**
     * Necessary to make the Value annotations work.
     *
     * @return PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
