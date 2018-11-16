package com.imooc.monitor_tuning.chapter8;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleDateFormatUtil {
	private static ThreadLocal<SimpleDateFormat> dateFormatHolder = new ThreadLocal<SimpleDateFormat>() {  
        protected SimpleDateFormat initialValue() {  
        	return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }  
	};
	public static void main(String[] args) {
		dateFormatHolder.get().format(new Date());
	}
}
