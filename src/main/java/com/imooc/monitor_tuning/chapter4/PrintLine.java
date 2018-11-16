package com.imooc.monitor_tuning.chapter4;
import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.ProbeClassName;
import com.sun.btrace.annotations.ProbeMethodName;

@BTrace
public class PrintLine {
	
	@OnMethod(
	        clazz="com.imooc.monitor_tuning.chapter4.Ch4Controller",
	        method="exception",
	        location=@Location(value=Kind.LINE, line=41)
	)
	public static void anyRead(@ProbeClassName String pcn, @ProbeMethodName String pmn, int line) {
		BTraceUtils.println(pcn+","+pmn + "," +line);
		BTraceUtils.println();
    }
}
