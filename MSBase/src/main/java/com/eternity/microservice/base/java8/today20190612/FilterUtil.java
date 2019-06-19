package com.eternity.microservice.base.java8.today20190612;

import java.util.ArrayList;
import java.util.List;

public class FilterUtil {
	
	/**
	 * 该方法接受一个需要过滤的集合和一个过滤条件(通过重写接口的apply方法来定义条件) by LDF
	 * @param t 需要过滤的集合
	 * @param iPredicate 过滤的条件
	 * @param <T> 集合的泛型类
	 * @return 过滤后的集合
	 */
	public static <T> List<T> filter(List<T> t, IPredicate<? super T> iPredicate){
		
		List<T> returnList = new ArrayList<>();
		for (T t1 : t) {
			if (iPredicate.apply(t1)) {
				returnList.add(t1);
			}
		}
		return returnList;
	}
}
