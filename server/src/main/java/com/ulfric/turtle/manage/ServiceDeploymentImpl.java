package com.ulfric.turtle.manage;

import com.ulfric.commons.artifact.Artifact;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.turtle.TurtleServer;

public class ServiceDeploymentImpl implements ServiceDeployment {

	@Inject private TurtleServer server;

	@Override
	public void load(Artifact artifact)
	{
		this.server.loadService(artifact);
	}

	@Override
	public void unload(Artifact artifact)
	{
		this.server.unloadService(artifact);
	}
}
