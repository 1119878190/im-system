package com.study.im.common.route.algorithm.random;

import com.study.im.common.enums.UserErrorCode;
import com.study.im.common.exception.ApplicationException;
import com.study.im.common.route.RouteHandle;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机处理---随机策略
 *
 * @author lx
 * @date 2023/05/07
 */
public class RandomHandle implements RouteHandle {

    @Override
    public String routeServer(List<String> values, String key) {

        int size = values.size();
        if (size == 0){
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        int random = ThreadLocalRandom.current().nextInt(size);

        return values.get(random);
    }
}
