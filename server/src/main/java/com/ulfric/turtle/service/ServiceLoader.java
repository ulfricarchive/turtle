package com.ulfric.turtle.service;

import java.util.jar.JarFile;

import com.ulfric.turtle.TurtleService;

public final class ServiceLoader {

	private final JarFile jar;

	ServiceLoader(JarFile jar)
	{
		this.jar = jar;
	}

	public TurtleService load()
	{

	}

}
