package com.ulfric.turtle;

import java.util.Set;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.container.SkeletalComponent;

class HttpExchangeComponent extends SkeletalComponent {

	private final ObjectFactory factory;
	private final Set<ExchangeController> exchangeControllers;

	HttpExchangeComponent(ObjectFactory factory, Set<ExchangeController> exchangeControllers)
	{
		this.factory = factory;
		this.exchangeControllers = exchangeControllers;
	}

	@Override
	public void onEnable()
	{
		TurtleServer server = this.factory.requestExact(TurtleServer.class);

		this.exchangeControllers.forEach(server::registerExchange);
	}

}
