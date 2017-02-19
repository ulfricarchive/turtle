package com.ulfric.turtle.service;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;

@Shared
public class DirectoryProvider {

	private final Path directory;

	public DirectoryProvider()
	{
		this.directory = this.loadDirectory();
	}

	public Path getDirectory()
	{
		return this.directory;
	}

	public Path getPathInDirectory(String name)
	{
		return this.directory.resolve(name);
	}

	private Path loadDirectory()
	{
		return Try.to(() -> Paths.get(this.jarUrl().toURI()));
	}

	private URL jarUrl()
	{
		return this.getClass().getProtectionDomain().getCodeSource().getLocation();
	}

}
