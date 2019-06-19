package com.eternity.microservice.base.java8.today20190612;

@FunctionalInterface
public interface IPredicate<T> {
	boolean apply(T t);
	
}
