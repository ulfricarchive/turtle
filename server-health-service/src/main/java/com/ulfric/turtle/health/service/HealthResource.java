package com.ulfric.turtle.health.service;

import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.turtle.health.model.HealthCPURequest;
import com.ulfric.turtle.health.model.HealthCPUResponse;
import com.ulfric.turtle.manage.HealthStatus;
import com.ulfric.turtle.message.Response;
import com.ulfric.turtle.method.GET;

public class HealthResource {

	@Inject private HealthStatus status;

	@GET
	public Response cpuPercent(HealthCPURequest request)
	{
		HealthCPUResponse response = new HealthCPUResponse();

		response.setCpuPercent(this.status.cpuPercent());

		return response;
	}

}
