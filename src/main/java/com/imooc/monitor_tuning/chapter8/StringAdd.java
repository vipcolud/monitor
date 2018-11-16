package com.imooc.monitor_tuning.chapter8;

public class StringAdd {
	public static void main(String[] args) {
	}
	
	/**
	 public static void f1();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=3, locals=2, args_size=0
         0: ldc           #19                 // String
         2: astore_0
         3: iconst_0
         4: istore_1
         5: goto          31
         8: new           #21                 // class java/lang/StringBuilder  
        11: dup
        12: aload_0
        13: invokestatic  #23                 // Method java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;  
        16: invokespecial #29                 // Method java/lang/StringBuilder."<init>":(Ljava/lang/String;)V   new StringBuilder（src）
        19: ldc           #32                 // String A
        21: invokevirtual #34                 // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        24: invokevirtual #38                 // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
        27: astore_0
        28: iinc          1, 1
        31: iload_1
        32: bipush        10
        34: if_icmplt     8
        37: getstatic     #42                 // Field java/lang/System.out:Ljava/io/PrintStream;
        40: aload_0
        41: invokevirtual #48                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        44: return
	 * */
	public static void f1() {
		String src = "";
		for(int i=0;i<10;i++) {
			//每一次循环都会new一个StringBuilder
			src = src + "A"; 
		}
		System.out.println(src);
	}
	public static void f2() {
		//只要一个StringBuilder
		StringBuilder src = new StringBuilder();
		for(int i=0;i<10;i++) {
			src.append("A");
		}
		System.out.println(src);
	}
	
	
}
