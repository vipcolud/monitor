package com.imooc.monitor_tuning.chapter4;

import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;

@BTrace
public class PrintJinfo {
    static {
    	BTraceUtils.println("System Properties:");
    	BTraceUtils.printProperties();
    	BTraceUtils.println("VM Flags:");
    	BTraceUtils.printVmArguments();
    	BTraceUtils.println("OS Enviroment:");
    	BTraceUtils.printEnv();
    	BTraceUtils.exit(0);
    }
}