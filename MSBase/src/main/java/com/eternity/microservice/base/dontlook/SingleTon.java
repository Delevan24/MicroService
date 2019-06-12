package com.eternity.microservice.base.dontlook;

import sun.security.jca.GetInstance;

/*
//可用 饿汉
public class SingleTon {
	private SingleTon(){
	}
	private static SingleTon instance = new SingleTon();
	
	public static SingleTon  getInstance(){
		return instance;
	}
	
}*/
/*
// 可用 静态代码块 饿汉
public class SingleTon {
	private SingleTon(){
	}
	private static SingleTon instance;
	static {
		instance = new SingleTon();
	}
	
	public static SingleTon  getInstance(){
		return instance;
	}
	
}*/

/*
//不推荐 线程不安全
public class SingleTon {
	private SingleTon(){
	}
	private static SingleTon instance;
	
	public static SingleTon  getInstance(){
		if (instance == null){
			instance = new SingleTon();
		}
		return instance;
	}
	
}*/

/*
//不推荐
public class SingleTon {
	private SingleTon(){
	}
	private static SingleTon instance;
	
	public synchronized static SingleTon  getInstance(){
		if (instance == null){
			instance = new SingleTon();
		}
		return instance;
	}
	
}*/

/*

//推荐用  双重检查
public class SingleTon {
	private SingleTon(){
	}
	private static volatile SingleTon instance;
	
	public synchronized static SingleTon  getInstance(){
		if (instance == null){
			synchronized (SingleTon.class){
				if (instance==null){
					instance = new SingleTon();
				}
			}
		}
		return instance;
	}
}
*/

/*
//匿名内部类法 推荐
public class SingleTon {
	private SingleTon(){
	}
	private static class SingleInstance{
		private static SingleTon INSTANCE = new SingleTon();
	}
	
	public static SingleTon getInstance(){
		return SingleInstance.INSTANCE;
	}
}*/

// 枚举法 推荐
public enum SingleTon{
	INSTANCE;
	public void whatevermethod(){
	
	}
}

class Single{
	public static void main(String[] args){
		SingleTon singleTon = SingleTon.valueOf(null);
	}
	
}




