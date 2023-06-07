package com.study.im.service.config;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;

import java.util.List;

/**
 * 向mybatis-plus添加自定义通用方法
 *
 * @author lx
 * @date 2023/06/07
 */
public class EasySqlInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        // 给每一个 mapper 添加一个 InsertBatchSomeColumn 方法
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new InsertBatchSomeColumn()); // 添加InsertBatchSomeColumn方法
        return methodList;
    }

}
