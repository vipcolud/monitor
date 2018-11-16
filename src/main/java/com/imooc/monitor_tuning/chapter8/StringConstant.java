package com.imooc.monitor_tuning.chapter8;

public class StringConstant {
		public static void main(String[] args) {
	        String hello = "Hello", lo = "lo";
	        System.out.print((hello == "Hello") ); 
	        System.out.print((Other.hello == hello) ); 
	        System.out.print((hello == ("Hel"+"lo")) ); 
	        System.out.print((hello == ("Hel"+lo))); 
	        System.out.println(hello == ("Hel"+lo).intern());
	    }
		public static class Other{
			public static String hello = "Hello";
		}
}


