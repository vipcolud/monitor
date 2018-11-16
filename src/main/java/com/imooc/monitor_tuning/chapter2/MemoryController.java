package com.imooc.monitor_tuning.chapter2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemoryController {
	
	private List<User>  userList = new ArrayList<User>();
	private List<Class<?>>  classList = new ArrayList<Class<?>>();
	
	/**
	 * -Xmx32M -Xms32M
	 * */
	@GetMapping("/heap")
	public String heap() {
		int i=0;
		while(true) {
			userList.add(new User(i++, UUID.randomUUID().toString()));
		}
	}
	
	
	/**
	 * -XX:MetaspaceSize=32M -XX:MaxMetaspaceSize=32M
	 * */
	@GetMapping("/nonheap")
	public String nonheap() {
		while(true) {
			classList.addAll(Metaspace.createClasses());
		}
	}
	
}
