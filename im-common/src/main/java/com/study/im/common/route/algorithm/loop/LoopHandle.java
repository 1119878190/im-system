package com.study.im.common.route.algorithm.loop;

import com.study.im.common.enums.UserErrorCode;
import com.study.im.common.exception.ApplicationException;
import com.study.im.common.route.RouteHandle;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 负载均衡--轮询
 *
 * @author lx
 * @date 2023/05/08
 */
public class LoopHandle implements RouteHandle {

    private AtomicLong index = new AtomicLong();

    @Override
    public String routeServer(List<String> values, String key) {

        int size = values.size();
        if (size == 0){
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        Long num = index.incrementAndGet() % size;
        if (num < 0){
            num = 0L;
        }
        return values.get(num.intValue());
    }
}
