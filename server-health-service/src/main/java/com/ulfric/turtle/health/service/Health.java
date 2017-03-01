package com.ulfric.turtle.health.service;

import com.ulfric.turtle.TurtleService;

public class Health extends TurtleService {

	@Override
	public void onLoad()
	{
		this.install(HealthResource.class);
	}

}
