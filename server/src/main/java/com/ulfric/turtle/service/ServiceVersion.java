package com.ulfric.turtle.service;

public final class ServiceVersion {

	private final int major;
	private final int minor;
	private final int security;

	public ServiceVersion(int major, int minor, int security)
	{
		this.major = major;
		this.minor = minor;
		this.security = security;
	}

	public int getMajor()
	{
		return this.major;
	}

	public int getMinor()
	{
		return this.minor;
	}

	public int getSecurity()
	{
		return this.security;
	}

	public String getFullVersion()
	{
		return this.major + "." + this.minor + "." + this.security;
	}

}
