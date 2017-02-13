package com.ulfric.turtle.service;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;
import com.ulfric.turtle.TurtleServer;

@Shared
public class ServiceFinder {

	private final File directory;
	private final File servicesDirectory;
	private final Deployment deployment;
	private final List<ServiceLoader> services = new ArrayList<>();

	@Inject private ObjectFactory factory;
	@Inject private TurtleServer server;

	public ServiceFinder()
	{
		this.directory = this.loadDirectory();
		this.servicesDirectory = this.loadServicesDirectory();
		this.deployment = this.loadDeployment();

		this.findServices();
	}

	private File loadDirectory()
	{
		return Try.to(() -> new File(this.jarUrl().toURI()));
	}

	private File loadServicesDirectory()
	{
		File folder = new File(this.directory, "services");

		if (!folder.exists())
		{
			if (!folder.mkdirs())
			{
				throw new RuntimeException("No permission to create services directory");
			}
		}

		return folder;
	}

	private Deployment loadDeployment()
	{
		return new Deployment(this.directory);
	}

	private void findServices()
	{
		File[] files = this.servicesDirectory.listFiles(file -> file.getName().endsWith(".jar"));

		if (files == null)
		{
			// Maybe log fact that Turtle has no services???
			return;
		}

		Stream.of(files).forEach(this::packageService);
	}

	private void packageService(File file)
	{
		JarFile jar = Try.to(() -> new JarFile(file, false)); // Can use true as second argument
															  // to checked signed jar files
		this.services.add(new ServiceLoader(jar));
	}

	private URL jarUrl()
	{
		return this.getClass().getProtectionDomain().getCodeSource().getLocation();
	}

	public List<ServiceLoader> getServices()
	{
		return this.services;
	}

}
