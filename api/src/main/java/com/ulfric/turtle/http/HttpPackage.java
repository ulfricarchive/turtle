package com.ulfric.turtle.http;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import com.ulfric.turtle.model.Request;
import com.ulfric.turtle.model.Response;

public interface HttpPackage {

	Method getMethod();

	MethodHandle getHandle();

	Class<? extends Request> getRequest();

	Class<? extends Response> getResponse();

}
