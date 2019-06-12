package com.eternity.microservice.base.common;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

/**
 java8引入了一套全新的时间日期API，本篇随笔将说明学习java8的这套API。
 
 java。time包中的是类是不可变且线程安全的。新的时间及日期API位于java.time中，下面是一些关键类
 
 ●Instant——它代表的是时间戳
 
 ●LocalDate——不包含具体时间的日期，比如2014-01-14。它可以用来存储生日，周年纪念日，入职日期等。
 
 ●LocalTime——它代表的是不含日期的时间
 
 ●LocalDateTime——它包含了日期及时间，不过还是没有偏移信息或者说时区。
 
 ●ZonedDateTime——这是一个包含时区的完整的日期时间，偏移量是以UTC/格林威治时间为基准的。
 
 */
public class BaseDateMethod {
	
	/**
	 * 如何在java8中获取当天的日期
	 * 这个类与java.util.Date略有不同，因为它只包含日期，没有时间。
	 * @return
	 */
	public static LocalDate getCurrentLocalDate(){
		LocalDate now = LocalDate.now();
		return now;
	}
	
	
	public static LocalDate getLocalDateForYear(LocalDate localDate,long year,boolean isNext){
		LocalDate time;
		if (isNext){
			time = localDate.minus(year, ChronoUnit.YEARS);
		}else {
			time = localDate.plus(year, ChronoUnit.YEARS);
		}
		return time;
	}
	
	public static void main(String[] args){
	testA(10);
	}
	private static void testA(int sz){
		long startTime=System.currentTimeMillis(); //开始测试时间
		Random random = new Random();
		int a[] = new int[sz];
		for (int i = 0; i < a.length; i++) {
			a[i] = random.nextInt(sz);
			for (int j = 1; j < i; j++) {
				while (a[i] == a[j]) {//如果重复，退回去重新生成随机数
					i--;
				}
			}
		}
		long endTime=System.currentTimeMillis(); //获取结束时间
		System.out.println("网上思路代码运行时间： "+(endTime-startTime)+"ms");
	}
	
}
