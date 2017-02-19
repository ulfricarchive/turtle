package com.ulfric.turtle.service.load;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.container.Container;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.turtle.TurtleService;
import com.ulfric.turtle.logging.Log;
import com.ulfric.turtle.method.HttpMethod;

public final class ServiceLoader {

	private final Set<TurtleService> services = new HashSet<>();
	private final Set<Object> httpHandlers = new HashSet<>();

	@Inject private ObjectFactory factory;
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

	public void load()
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

	public void enable()
	{
		this.services.forEach(Container::enable);
		this.httpHandlers.forEach(this::enableHttpHandler);
	}

	private void enableHttpHandler(Object instance)
	{

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

		if (Stream.of(clazz.getDeclaredMethods()).anyMatch(this::isHttpMethod))
		{
			this.loadHttpClass(clazz);
		}
	}

	private boolean isHttpMethod(Method method)
	{
		return Stream.of(method.getAnnotations())
				.map(Annotation::getClass)
				.anyMatch(HttpMethod::isHttpAnnotation);
	}

	private void loadService(Class<? extends TurtleService> serviceClass)
	{
		TurtleService service = this.factory.requestExact(serviceClass);

		this.services.add(service);
	}

	private void loadHttpClass(Class<?> httpClass)
	{
		Object instance = this.factory.request(httpClass);

		this.httpHandlers.add(instance);
	}

	public Path getPath()
	{
		return this.path;
	}

}
