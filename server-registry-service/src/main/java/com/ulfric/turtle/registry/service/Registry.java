package com.ulfric.turtle.registry.service;

import com.ulfric.turtle.TurtleService;

public class Registry extends TurtleService {

	@Override
	public void onEnable()
	{
		this.install(RegistryResource.class);
	}

}
