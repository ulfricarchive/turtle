package com.ulfric.turtle.service.load;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.container.Container;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.turtle.TurtleServer;
import com.ulfric.turtle.TurtleService;
import com.ulfric.turtle.logging.Log;

public final class ServiceLoader {

	private final Set<TurtleService> services = new HashSet<>();

	@Inject private ObjectFactory factory;
	@Inject private TurtleServer server;
	@Inject private Log logger;

	private Path path;

	public void initializePath(Path path)
	{
		if (this.path != null)
		{
			throw new IllegalStateException("Path already initialized!");
		}
		this.path = path;
	}

	public void loadService()
	{
		ServiceClassLoader classLoader = new ServiceClassLoader(this);

		try (FileSystem system = FileSystems.newFileSystem(this.path, classLoader))
		{}
		catch (IOException exception)
		{
			this.logger.write(exception);
		}

		this.readClasses(classLoader);
	}

	public void enableService()
	{
		this.services.forEach(Container::enable);
	}

	public void disable()
	{
		this.services.forEach(Container::disable);
	}

	private void readClasses(ServiceClassLoader classLoader)
	{
		classLoader.getClasses().values().forEach(this::readClass);
	}

	private void readClass(Class<?> clazz)
	{
		if (clazz.isAssignableFrom(TurtleService.class))
		{
			@SuppressWarnings("unchecked")
			Class<? extends TurtleService> serviceClass = (Class<? extends TurtleService>) clazz;

			this.loadService(serviceClass);
		}
	}

	private void loadService(Class<? extends TurtleService> serviceClass)
	{
		TurtleService service = this.factory.requestExact(serviceClass);

		this.services.add(service);
	}

	public Path getPath()
	{
		return this.path;
	}

}
