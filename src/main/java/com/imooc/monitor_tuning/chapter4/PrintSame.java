package com.imooc.monitor_tuning.chapter4;
import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.ProbeClassName;
import com.sun.btrace.annotations.ProbeMethodName;

@BTrace
public class PrintSame {
	
	@OnMethod(
	        clazz="com.imooc.monitor_tuning.chapter4.Ch4Controller",
	        method="same"
	)
	public static void anyRead(@ProbeClassName String pcn, @ProbeMethodName String pmn, String  name,int id) {
		BTraceUtils.println(pcn+","+pmn + "," + name+ "," + id);
		BTraceUtils.println();
    }
}
