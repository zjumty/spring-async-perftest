package org.devzen.config;

import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO: 这里需要写注释
 */
@Configuration
public class JettyConfiguration {

    @Value("${jetty.threadPool.minSize}")
    private int minSize;

    @Value("${jetty.threadPool.maxSize}")
    private int maxSize;

    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory() {
        JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();
        factory.addServerCustomizers(server -> {
            QueuedThreadPool threadPool = (QueuedThreadPool) server.getThreadPool();
            threadPool.setMaxThreads(maxSize);
            threadPool.setMinThreads(minSize);
            threadPool.setName("jetty-");
        });

        return factory;
    }
}
