package com.ulfric.turtle.health.model;

import com.ulfric.turtle.model.Response;

public interface HealthCPUResponse extends Response {

	double getCpuPercent();

	void setCpuPercent(double cpuPercent);

}
