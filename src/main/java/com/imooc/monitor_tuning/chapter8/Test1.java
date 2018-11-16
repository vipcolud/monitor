package com.imooc.monitor_tuning.chapter8;

public class Test1 {
	public static void main(String args) {
		int a=2;
		int b=3;
		int c = a + b;
		System.out.println(c);
	}
	/***
	 public static void main(java.lang.String);
    descriptor: (Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      # 操作数栈的深度2
      # 本地变量表最大长度（slot为单位），64位的是2，其他是1，索引从0开始，如果是非static方法索引0代表this，后面是入参，后面是本地变量
      # 1个参数，实例方法多一个this参数
      stack=2, locals=4, args_size=1
         0: iconst_2  #常量2压栈
         1: istore_1  #出栈保存到本地变量1里面
         2: iconst_3  #常量3压栈
         3: istore_2  #出栈保存到本地变量2里面
         4: iload_1    #局部变量1压栈
         5: iload_2    #局部变量2压栈
         6: iadd        # 栈顶两个元素相加，计算结果压栈
         7: istore_3  # 出栈保存到局部变量3里面
         8: getstatic     #16                 // Field java/lang/System.out:Ljava/io/PrintStream;
        11: iload_3
        12: invokevirtual #22                 // Method java/io/PrintStream.println:(I)V
        15: return
      LineNumberTable:
        line 5: 0
        line 6: 2
        line 7: 4
        line 8: 8
        line 9: 15
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      16     0  args   Ljava/lang/String;
            2      14     1     a   I
            4      12     2     b   I
            8       8     3     c   I
	 **/
}
