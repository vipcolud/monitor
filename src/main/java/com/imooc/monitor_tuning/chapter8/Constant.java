package com.imooc.monitor_tuning.chapter8;

public class Constant {
	
	public static void main(String[] args) {
	}
	/**
	     0: ldc           #8                  // String hello
         2: astore_0
         3: ldc           #46                 // String helloworld
         5: astore_1
         6: ldc           #48                 // String hellohelloworld
         8: astore_2
         9: getstatic     #31                 // Field java/lang/System.out:Ljava/io/PrintStream;
        12: aload_2
        13: invokevirtual #37                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        16: return
	 * */
	public static void f1() {
		final String x="hello";
	    final String y=x+"world";
	    String z=x+y;
	    System.out.println(z);
	}
	/**
	    0: ldc           #19                 // String hello
         2: astore_1
         3: ldc           #21                 // String helloworld
         5: astore_2
         6: new           #42                 // class java/lang/StringBuilder
         9: dup
        10: ldc           #19                 // String hello
        12: invokespecial #44                 // Method java/lang/StringBuilder."<init>":(Ljava/lang/String;)V
        15: aload_2
        16: invokevirtual #46                 // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        19: invokevirtual #50                 // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
        22: astore_3
        23: getstatic     #25                 // Field java/lang/System.out:Ljava/io/PrintStream;
        26: aload_3
        27: invokevirtual #31                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        30: return
	 * */
	public void f2(){
	      final String x="hello";
	      String y=x+"world";
	      String z=x+y;
	      System.out.println(z);
	}
	
}
