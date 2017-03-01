package com.ulfric.turtle.health.model;

import com.ulfric.turtle.message.Response;

public class HealthCPUResponse extends Response {

	private double cpuPercent;

	public double getCpuPercent()
	{
		return this.cpuPercent;
	}

	public void setCpuPercent(double cpuPercent)
	{
		this.cpuPercent = cpuPercent;
	}

}
