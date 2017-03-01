package com.ulfric.turtle.registry.model;

import com.ulfric.commons.artifact.Artifact;
import com.ulfric.turtle.message.Request;

public class RegisterRequest extends Request {

	private Artifact artifact;

	public Artifact getArtifact()
	{
		return this.artifact;
	}

	public void setArtifact(Artifact artifact)
	{
		this.artifact = artifact;
	}

}
