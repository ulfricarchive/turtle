package com.ulfric.turtle.service;

import java.io.File;
import java.net.URL;

import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;

@Shared
public class DirectoryProvider {

	private final File directory;

	public DirectoryProvider()
	{
		this.directory = this.loadDirectory();
		this.directory.mkdirs();
	}

	public File getDirectory()
	{
		return this.directory;
	}

	public File getFileInDirectory(String name)
	{
		return new File(this.directory, name);
	}

	private File loadDirectory()
	{
		return Try.to(() -> new File(this.jarUrl().toURI()));
	}

	private URL jarUrl()
	{
		return this.getClass().getProtectionDomain().getCodeSource().getLocation();
	}

}
