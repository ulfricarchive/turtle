package com.ulfric.turtle.service;

public final class ServiceArtifact {

	private final String group;
	private final String artifact;
	private final ServiceVersion version;

	public ServiceArtifact(String group, String artifact, ServiceVersion version)
	{
		this.group = group;
		this.artifact = artifact;
		this.version = version;
	}

	public String getGroup()
	{
		return this.group;
	}

	public String getArtifact()
	{
		return this.artifact;
	}

	public ServiceVersion getVersion()
	{
		return this.version;
	}

}
