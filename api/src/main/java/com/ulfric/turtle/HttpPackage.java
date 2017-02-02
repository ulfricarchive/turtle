package com.ulfric.turtle;

import java.lang.reflect.Method;

public class HttpPackage {

	private final Method method;
	private final Class<? extends Request> request;
	private final Class<? extends Response> response;

	public HttpPackage(Method method, Class<? extends Request> request, Class<? extends Response> response)
	{
		this.method = method;
		this.request = request;
		this.response = response;
	}

	public Method getMethod()
	{
		return this.method;
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
