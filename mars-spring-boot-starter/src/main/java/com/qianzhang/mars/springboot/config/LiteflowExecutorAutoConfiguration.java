package com.qianzhang.mars.springboot.config;

import com.qianzhang.mars.property.LiteflowConfig;
import com.qianzhang.mars.util.ExecutorHelper;
import com.qianzhang.mars.util.LiteFlowExecutorPoolShutdown;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

/**
 * 线程池装配类
 * 这个装配前置条件是需要LiteflowConfig，LiteflowPropertyAutoConfiguration以及SpringAware
 * @author justin.xu
 */
@Configuration
@AutoConfigureAfter({LiteflowPropertyAutoConfiguration.class})
@ConditionalOnProperty(prefix = "liteflow", name = "enable", havingValue = "true")
@ConditionalOnBean(LiteflowConfig.class)
public class LiteflowExecutorAutoConfiguration {

    @Bean("whenExecutors")
    public ExecutorService executorService(LiteflowConfig liteflowConfig) {
        return ExecutorHelper.loadInstance().buildExecutor();
    }

    @Bean
    public LiteFlowExecutorPoolShutdown liteFlowExecutorPoolShutdown() {
        return new LiteFlowExecutorPoolShutdown();
    }
}
