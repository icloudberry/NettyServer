package com.icloudberry.nettyserver.service;

import org.springframework.stereotype.Service;

/**
 * Sum service implementation.
 * <p/>
 * Author: icloudberry
 */
@Service
public class SumServiceImpl implements SumService {

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSum(int a, int b) {
        return a + b;
    }
}
