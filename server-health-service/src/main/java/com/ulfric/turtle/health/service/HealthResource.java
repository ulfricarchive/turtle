package com.ulfric.turtle.health.service;

import com.ulfric.dragoon.bean.Beans;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.turtle.health.model.HealthCPURequest;
import com.ulfric.turtle.health.model.HealthCPUResponse;
import com.ulfric.turtle.manage.HealthStatus;
import com.ulfric.turtle.model.Response;
import com.ulfric.turtle.method.GET;

public class HealthResource {

	@Inject private HealthStatus status;

	@GET
	public Response cpuPercent(HealthCPURequest request)
	{
		HealthCPUResponse response = Beans.create(HealthCPUResponse.class);

		response.setCpuPercent(this.status.cpuPercent());

		return response;
	}

}
