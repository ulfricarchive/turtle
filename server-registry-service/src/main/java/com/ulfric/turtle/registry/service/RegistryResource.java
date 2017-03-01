package com.ulfric.turtle.registry.service;

import java.util.function.Consumer;

import com.ulfric.commons.artifact.Artifact;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.turtle.manage.ServiceDeployment;
import com.ulfric.turtle.message.Response;
import com.ulfric.turtle.method.POST;
import com.ulfric.turtle.registry.model.RegisterRequest;
import com.ulfric.turtle.registry.model.RegisterResponse;

public class RegistryResource {

	@Inject private ServiceDeployment deployment;

	@POST
	public Response serviceStart(RegisterRequest request)
	{
		return this.act(request, this.deployment::load);
	}

	@POST
	public Response serviceStop(RegisterRequest request)
	{
		return this.act(request, this.deployment::unload);
	}

	private Response act(RegisterRequest request, Consumer<Artifact> consumer)
	{
		RegisterResponse response = new RegisterResponse();
		try
		{
			consumer.accept(request.getArtifact());

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
