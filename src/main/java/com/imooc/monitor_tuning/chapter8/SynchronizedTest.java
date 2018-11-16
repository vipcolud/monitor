package com.imooc.monitor_tuning.chapter8;

public class SynchronizedTest {
	public static void main(String[] args) {
	}
	public synchronized void f1() {//在this對象上加鎖
		System.out.println("f1");
	}
	public  void f2() {
		synchronized(this) {
			System.out.println("f2");
		}
	}
	public static synchronized void f3() {
		System.out.println("f3");
	}
	public static void f4() {
		synchronized(SynchronizedTest.class) {
			System.out.println("f4");
		}
	}
}
