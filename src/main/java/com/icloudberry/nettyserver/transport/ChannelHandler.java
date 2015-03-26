package com.icloudberry.nettyserver.transport;

import com.icloudberry.nettyserver.helper.PropertyHelper;
import com.icloudberry.nettyserver.service.SumService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.netty.channel.ChannelHandler.Sharable;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Handle requests from the clients.
 * Accept only http-requests.
 * <p/>
 * Author: icloudberry
 */
@Component
@Sharable
public class ChannelHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private static final Logger logger = Logger.getLogger("ChannelHandler");

    @Autowired
    private SumService sumService;

    @Autowired
    private PropertyHelper helper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.log(Level.INFO, "Response is sent");
        ctx.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        logger.log(Level.SEVERE, "Exception caught", cause);
        ctx.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        ChannelFuture future = ctx.write(createResponse(req));
        if (!HttpHeaders.isKeepAlive(req)) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Creates response based on the received request.
     *
     * @param request received http-request
     * @return FullHttpRequest entity
     */
    protected FullHttpResponse createResponse(HttpRequest request) {
        logger.log(Level.INFO, "Response is being created...");
        // if the method is not HTTP GET
        if (!request.getMethod().equals(HttpMethod.GET)) {
            logger.log(Level.INFO, "Received method is not supported: " + request.getMethod());
            return constructResponse(HTTP_1_1, NOT_IMPLEMENTED, helper.getMessageNotImplemented());
        }

        // else if the method is HTTP GET
        // if the method is HTTP GET and contains header 100-Continue
        if (HttpHeaders.is100ContinueExpected(request)) {
            // allows server to accept 100-Continue request
            logger.log(Level.INFO, "100-Continue Header received");
            return constructResponse(HTTP_1_1, CONTINUE, null);
        }

        // else if the method is HTTP GET and does not contain 100-Continue header

        //decode the incoming request
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());

        // if the path does not equal to the required path
        if (!helper.getPath().equals(queryStringDecoder.path())) {
            logger.log(Level.INFO, "Received URI is not supported: {0}", queryStringDecoder.path());
            return constructResponse(HTTP_1_1, NOT_FOUND, helper.getMessageNotFound());
        }

        Integer first;
        Integer second;
        // get parameters from the request
        try {
            first = getIntParameter(helper.getFirstParam(), queryStringDecoder);
            second = getIntParameter(helper.getSecondParam(), queryStringDecoder);
        } catch (NumberFormatException ex) {
            logger.log(Level.INFO, "Received parameters are not integers.");
            return constructResponse(HTTP_1_1, BAD_REQUEST, helper.getMessageBadRequest());
        }
        // validate parameters
        if (!isParamsValid(first, helper.getMinValue(), helper.getMaxValue(), true)
                || !isParamsValid(second, helper.getMinValue(), helper.getMaxValue(), false)) {
            logger.log(Level.INFO, "Received parameters are not valid. First: {0}, second: {1}", new Object[]{first, second});
            return constructResponse(HTTP_1_1, BAD_REQUEST, helper.getMessageBadRequest());
        }

        int result;
        // if parameters are valid
        if (second != null) {
            result = sumService.getSum(first, second);
        } else {
            result = sumService.getSum(first, 0);
        }

        logger.log(Level.INFO, "Result is calculated: {0}", result);
        return constructResponse(HTTP_1_1, OK, "Result: " + result);
    }

    /**
     * Helper method to validate parameters. Param should be >= 0 and <= maxValue.
     *
     * @param param     parameter to validate
     * @param minValue  min possible value of the param
     * @param maxValue  max possible value of the param
     * @param mandatory is this param mandatory (should not be null) or not
     * @return return true if the param is valid, false otherwise
     */
    private boolean isParamsValid(Integer param,Integer minValue, Integer maxValue, boolean mandatory) {
        if (!mandatory && param == null) return true;
        else if (mandatory && param == null) return false;
        else return (param >= minValue && param <= maxValue);
    }

    /**
     * Helper method to extract param value from the request.
     *
     * @param paramName          name of the parameter to extract
     * @param queryStringDecoder QueryStringDecoder object
     * @return Integer value of the parameter
     * @throws NumberFormatException if the parameter is not a number
     */
    private Integer getIntParameter(String paramName, QueryStringDecoder queryStringDecoder) throws NumberFormatException {
        // extracting request parameter(s)
        Integer result = null;
        List<String> param = queryStringDecoder.parameters().get(paramName);
        if (param != null && !param.isEmpty()) {
            // level number is mandatory parameter
            result = Integer.valueOf(param.get(0));
        }
        return result;
    }

    /**
     * Helper method to construct response.
     *
     * @param version HTTP version
     * @param status  HTTP status to respond
     * @param message message to send
     * @return FullHttpResponse instance ready to be sent to the client
     */
    private FullHttpResponse constructResponse(HttpVersion version, HttpResponseStatus status, String message) {
        ByteBuf msg = message != null ? Unpooled.wrappedBuffer(message.getBytes()) : Unpooled.buffer(0);
        FullHttpResponse response = new DefaultFullHttpResponse(version, status, msg);
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
}
