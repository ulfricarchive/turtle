package com.ulfric.turtle.exchange;

import com.ulfric.turtle.http.HttpPackage;
import com.ulfric.turtle.http.HttpTarget;

public class ExchangeController {

	private final HttpTarget target;
	private final HttpPackage httpPackage;

	ExchangeController(HttpTarget target, HttpPackage httpPackage)
	{
		this.target = target;
		this.httpPackage = httpPackage;
	}

	public HttpTarget getTarget()
	{
		return this.target;
	}

	public HttpPackage getHttpPackage()
	{
		return this.httpPackage;
	}

}
