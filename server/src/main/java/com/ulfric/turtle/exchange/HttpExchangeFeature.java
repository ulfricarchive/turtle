package com.ulfric.turtle.exchange;

import java.util.HashSet;
import java.util.Set;

import com.ulfric.dragoon.container.SkeletalFeature;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.turtle.TurtleServer;

public class HttpExchangeFeature extends SkeletalFeature {

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
