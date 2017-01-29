package com.ulfric.turtle;

class ExchangeController {

	private final HttpTarget target;
	private final HttpFunction function;

	ExchangeController(HttpTarget target, HttpFunction function)
	{
		this.target = target;
		this.function = function;
	}

	public HttpTarget getTarget()
	{
		return this.target;
	}

	public HttpFunction getFunction()
	{
		return this.function;
	}

}
