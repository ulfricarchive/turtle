package com.ulfric.turtle.registry.service;

import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.turtle.TurtleServer;
import com.ulfric.turtle.message.Response;
import com.ulfric.turtle.method.POST;
import com.ulfric.turtle.registry.model.RegisterRequest;
import com.ulfric.turtle.registry.model.RegisterResponse;

public class RegistryResource {

	@Inject private TurtleServer server;

	@POST
	public Response register(RegisterRequest request)
	{
		RegisterResponse response = new RegisterResponse();
		try
		{
			if (request.getAction().toStart())
			{
				this.server.loadService(request.getArtifact());
			}
			else
			{
				this.server.unloadService(request.getArtifact());
			}
			response.setSuccessful(true);
			return response;
		}
		catch (Exception exception)
		{
			exception.printStackTrace();

			response.setSuccessful(false);
			return response;
		}
	}

}
