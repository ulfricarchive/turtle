package com.ulfric.turtle.exchange;

import java.util.HashSet;
import java.util.Set;

import com.ulfric.commons.cdi.container.SkeletalComponent;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.turtle.TurtleServer;

public class HttpExchangeComponent extends SkeletalComponent {

	private final Set<ExchangeController> exchangeControllers = new HashSet<>();

	@Inject private TurtleServer server;

	void addExchangeController(ExchangeController exchangeController)
	{
		this.exchangeControllers.add(exchangeController);
	}

	@Override
	public void onEnable()
	{
		this.exchangeControllers.forEach(this.server::registerExchange);
	}

	@Override
	public void onDisable()
	{
		this.exchangeControllers.forEach(this.server::unregisterExchange);
	}

}
