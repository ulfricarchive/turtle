package com.ulfric.turtle.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.ulfric.commons.exception.Try;
import com.ulfric.turtle.method.HttpMethod;

public interface HttpTarget {

	HttpMethod getMethod();

	String getPath();

	static String pathOfMethod(Annotation annotation)
	{
		Method method = MethodUtils.getMatchingMethod(annotation.getClass(), "path");

		if (method == null)
		{
			throw new RuntimeException("Annotation provided (" + annotation + ") is not a valid HTTP method!");
		}

		method.setAccessible(true);

		return Try.to(() -> (String) method.invoke(annotation));
	}

}
