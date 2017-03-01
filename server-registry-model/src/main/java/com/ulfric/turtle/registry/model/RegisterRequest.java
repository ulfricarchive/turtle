package com.ulfric.turtle.registry.model;

import com.ulfric.commons.artifact.Artifact;
import com.ulfric.turtle.message.Request;

public class RegisterRequest extends Request {

	private Artifact artifact;
	private RegistryAction action;

	public Artifact getArtifact()
	{
		return this.artifact;
	}

	public void setArtifact(Artifact artifact)
	{
		this.artifact = artifact;
	}

	public RegistryAction getAction()
	{
		return this.action;
	}

	public void setAction(RegistryAction action)
	{
		this.action = action;
	}

}
