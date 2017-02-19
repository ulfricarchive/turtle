package com.ulfric.turtle.http;

import com.ulfric.turtle.method.HttpMethod;

public class TurtleHttpTarget implements HttpTarget {

	private final HttpMethod method;
	private final String path;

	public TurtleHttpTarget(HttpMethod method, String path)
	{
		this.method = method;
		this.path = path;
	}

	public HttpMethod getMethod()
	{
		return this.method;
	}

	public String getPath()
	{
		return this.path;
	}

}
