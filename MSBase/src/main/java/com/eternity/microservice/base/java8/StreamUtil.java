package com.eternity.microservice.base.java8;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamUtil {
	public static <T,S> List<S> map(List<T> tList, Function<T,S> tsFunction){
		return tList.stream().map(tsFunction).collect(Collectors.toList());
	}
}
