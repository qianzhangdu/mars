package com.qianzhang.mars.springboot;

import com.qianzhang.mars.core.FlowExecutor;
import org.springframework.beans.factory.InitializingBean;

/**
 * 执行器初始化类
 * 主要用于在启动时执行执行器的初始化方法，避免在运行执行器时第一次初始化而耗费时间
 * @author qianzhang
 */
public class LiteflowExecutorInit implements InitializingBean {

    private FlowExecutor flowExecutor;

    public LiteflowExecutorInit(FlowExecutor flowExecutor) {
        this.flowExecutor = flowExecutor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        flowExecutor.init();
    }
}
