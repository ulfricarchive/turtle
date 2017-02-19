package com.ulfric.turtle.service.load;

import com.google.gson.JsonObject;

public final class ServiceConfiguration {

	private final ServiceLoader loader;
	private final JsonObject config;

	public ServiceConfiguration(ServiceLoader loader)
	{
		this.loader = loader;

	}

}
