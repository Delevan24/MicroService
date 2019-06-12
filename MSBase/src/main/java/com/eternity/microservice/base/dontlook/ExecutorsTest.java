package com.eternity.microservice.base.dontlook;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorsTest {
	
	public static void main(String[] args) {
		// cachedThreadPool();
		// fixedThreadPool();
		// scheduledThreadPool();
		singleThreadPool();
		// 获取当前设备的CPU个数
		// 前者称为计算密集型（CPU密集型）computer-bound 设置线程数为CPU数，后者称为I/O密集型，I/O-bound 大部分都在等待可以设置为2倍CPU数。
		System.out.println(Runtime.getRuntime().availableProcessors());
	}
	
	
	public static void cachedThreadPool() {
		ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
		for (int i = 0; i < 10; i++) {
			final int temp = i;
			newCachedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						System.out.println(Thread.currentThread().getName() + ",i:" + temp);
					}
				}
			});
		}
	}
	
	public static void fixedThreadPool() {
		ExecutorService newCachedThreadPool = Executors.newFixedThreadPool(5);
		for (int i = 0; i < 10; i++) {
			final int temp = i;
			newCachedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						System.out.println(Thread.currentThread().getName() + ",i:" + temp);
					}
				}
			});
		}
	}
	
	public static void scheduledThreadPool() {
		ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(5);
		for (int i = 0; i < 10; i++) {
			final int temp = i;
			newScheduledThreadPool.schedule(new Runnable() {
				@Override
				public void run() {
					System.out.println(Thread.currentThread().getName() + ",i:" + temp);
				}
			}, 3, TimeUnit.SECONDS);
		}
	}
	
	public static void singleThreadPool() {
		ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
		for (int i = 0; i < 10; i++) {
			final int temp = i;
			newSingleThreadExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						System.out.println(Thread.currentThread().getName() + ",i:" + temp);
					}
				}
			});
		}
	}
	
	
}
