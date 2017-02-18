package com.ulfric.turtle.http;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import com.ulfric.commons.reflect.HandleUtils;
import com.ulfric.turtle.message.Request;
import com.ulfric.turtle.message.Response;

public class HttpPackage {

	private final Method method;
	private final MethodHandle handle;
	private final Class<? extends Request> request;
	private final Class<? extends Response> response;

	public HttpPackage(Method method, Class<? extends Request> request, Class<? extends Response> response)
	{
		this.method = method;
		this.handle = HandleUtils.getMethod(method);
		this.request = request;
		this.response = response;
	}

	public Method getMethod()
	{
		return this.method;
	}

	public MethodHandle getHandle()
	{
		return this.handle;
	}

	public Class<? extends Request> getRequest()
	{
		return this.request;
	}

	public Class<? extends Response> getResponse()
	{
		return this.response;
	}

}
