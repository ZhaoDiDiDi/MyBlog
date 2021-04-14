package com.it.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync    // 异步启动
public class AsyncConfiguration {

    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);    // 设置核心线程数
        executor.setMaxPoolSize(10);    // 设置最大线程数
        executor.setThreadNamePrefix("sob_blog_task_worker-");  // 设置默认线程名称
        executor.setQueueCapacity(30);
        executor.initialize();
        return executor;
    }
}
