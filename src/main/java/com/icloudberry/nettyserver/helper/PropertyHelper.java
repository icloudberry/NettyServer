package com.icloudberry.nettyserver.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Helper class that provides access to the values from properties file.
 * <p/>
 * Author: icloudberry
 */
@Service
public class PropertyHelper {

    @Value("${http.server.port}")
    private int httpPort;

    @Value("${http.request.path}")
    private String path;

    /* Params */

    @Value("${param.name.first}")
    private String firstParam;

    @Value("${param.name.second}")
    private String secondParam;

    @Value("${algo.default.max}")
    private Integer maxValue;

    @Value("${algo.default.min}")
    private Integer minValue;

    /* Messages */

    @Value("${message.not.implemented}")
    private String messageNotImplemented;

    @Value("${message.not.found}")
    private String messageNotFound;

    @Value("${message.bad.request}")
    private String messageBadRequest;

    public String getFirstParam() {
        return firstParam;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public String getMessageBadRequest() {
        return messageBadRequest;
    }

    public String getMessageNotImplemented() {
        return messageNotImplemented;
    }

    public String getPath() {
        return path;
    }

    public String getSecondParam() {
        return secondParam;
    }

    public String getMessageNotFound() {
        return messageNotFound;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public int getHttpPort() {
        return httpPort;
    }
}
