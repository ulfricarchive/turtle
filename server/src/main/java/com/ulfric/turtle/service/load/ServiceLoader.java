package com.ulfric.turtle.service.load;

import java.nio.file.Path;
import java.util.jar.JarFile;

import com.ulfric.turtle.TurtleService;

public final class ServiceLoader {

	private final Path path;
	private final JarFile jar;

	public ServiceLoader(JarFile jar, Path path)
	{
		this.jar = jar;
		this.path = path;
	}

	public TurtleService load()
	{

	}

	public JarFile getJar()
	{
		return this.jar;
	}

	public Path getPath()
	{
		return this.path;
	}

}
