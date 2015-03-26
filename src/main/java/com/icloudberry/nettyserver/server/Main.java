package com.icloudberry.nettyserver.server;

import com.icloudberry.nettyserver.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.logging.Logger;

/**
 * Main application class.
 * Initialize Spring application context, that allow to start http-server.
 * <p/>
 * Author: icloudberry
 */
public class Main {
    private static final Logger logger = Logger.getLogger("Main");

    public static void main(String[] args) {
        logger.info("Application is starting...");
        AbstractApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        applicationContext.registerShutdownHook();
    }
}
