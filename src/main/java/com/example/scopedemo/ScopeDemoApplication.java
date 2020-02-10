package com.example.scopedemo;

import com.example.scopedemo.concurrent.ContextInheritingThreadPoolExecutor;
import com.example.scopedemo.controller.MetaInfoContextInitializingFilter;
import com.example.scopedemo.service.ExecutionMetaInfoContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.RequestScope;

import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
public class ScopeDemoApplication {
	@Bean
	public ThreadPoolExecutor executorService() {
		return ContextInheritingThreadPoolExecutor.cachedThreadPool();
	}

	public static void main(String[] args) {
		SpringApplication.run(ScopeDemoApplication.class, args);
	}

}
