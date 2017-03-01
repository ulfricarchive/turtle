package com.ulfric.turtle.manage;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class HealthStatusImpl implements HealthStatus {

	@Override
	public double cpuPercent()
	{
		// To be changed to actual useful information - this is mainly a functional placeholder
		return ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getSystemCpuLoad();
	}

}
