package com.imooc.monitor_tuning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MonitorTuningApplication extends SpringBootServletInitializer{ 

	public static void main(String[] args) {
		SpringApplication.run(MonitorTuningApplication.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MonitorTuningApplication.class);
    }
}









