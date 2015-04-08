package org.devzen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * TODO: 这里需要写注释
 */
@Configuration
public class ThreadPoolConfiguration {

    @Value("${threadPool.coreSize}")
    private int coreSize;

    @Value("${threadPool.queueCapacity}")
    private int queueCapacity;


    @Bean(name="workerPool")
    public ThreadPoolExecutorFactoryBean executorService() {
        ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(coreSize);
        bean.setMaxPoolSize(coreSize);
        bean.setQueueCapacity(queueCapacity);
        bean.setThreadNamePrefix("worker-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return bean;
    }
}
