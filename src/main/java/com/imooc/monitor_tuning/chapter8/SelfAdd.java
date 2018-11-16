package com.imooc.monitor_tuning.chapter8;

public class SelfAdd {
	
	public static void main(String[] args) {
		f3();
		f4();
	}
	/**
	 public static void f1();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=1, args_size=0
         0: iconst_0
         1: istore_0
         2: goto          15
         5: getstatic     #19                 // Field java/lang/System.out:Ljava/io/PrintStream;
         8: iload_0
         9: invokevirtual #25                 // Method java/io/PrintStream.println:(I)V
        12: iinc          0, 1
        15: iload_0
        16: bipush        10
        18: if_icmplt     5
        21: return
	 * */
	public static void f1() {
		for(int i=0;i<10;i++) {
			System.out.println(i);
		}
	}
	/**
	 public static void f2();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=1, args_size=0
         0: iconst_0
         1: istore_0
         2: goto          15
         5: getstatic     #19                 // Field java/lang/System.out:Ljava/io/PrintStream;
         8: iload_0
         9: invokevirtual #25                 // Method java/io/PrintStream.println:(I)V
        12: iinc          0, 1
        15: iload_0
        16: bipush        10
        18: if_icmplt     5
        21: return
	 * */
	public static void f2() {
		for(int i=0;i<10;++i) {
			System.out.println(i);
		}
	}
	/**
	 public static void f3();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=0
         0: iconst_0
         1: istore_0
         2: iload_0
         3: iinc          0, 1
         6: istore_1
         7: getstatic     #19                 // Field java/lang/System.out:Ljava/io/PrintStream;
        10: iload_1
        11: invokevirtual #25                 // Method java/io/PrintStream.println:(I)V
        14: return
	 * */
	public static void f3() {
		int i=0;
		int j = i++;
		System.out.println(j);
	}
	/**
	public static void f4();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=0
         0: iconst_0
         1: istore_0
         2: iinc          0, 1
         5: iload_0
         6: istore_1
         7: getstatic     #19                 // Field java/lang/System.out:Ljava/io/PrintStream;
        10: iload_1
        11: invokevirtual #25                 // Method java/io/PrintStream.println:(I)V
        14: return 
	 * */
	public static void f4() {
		int i=0;
		int j = ++i;
		System.out.println(j);
	}
}
