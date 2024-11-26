package co.sumit.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ApplicationConfig {

	@Value("${async_executor_core_pool_size}")
	private int asyncExecutorCorePoolSize;
	
	@Value("${async_executor_max_pool_size}")
	private int asyncExecutorMaxPoolSize;

    @Bean
    Executor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(asyncExecutorCorePoolSize);
		executor.setMaxPoolSize(asyncExecutorMaxPoolSize);
		executor.initialize();
		return executor;
	}
}
