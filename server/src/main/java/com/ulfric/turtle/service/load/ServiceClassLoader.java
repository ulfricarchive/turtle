package com.ulfric.turtle.service.load;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import com.ulfric.commons.exception.Try;

public final class ServiceClassLoader extends URLClassLoader {

	private final Map<String, Class<?>> classes = new HashMap<>();

	public ServiceClassLoader(ServiceLoader loader)
	{
		super(new URL[] { ServiceClassLoader.getJarURL(loader) }, ServiceClassLoader.parentClassLoader());
	}

	private static URL getJarURL(ServiceLoader loader)
	{
		return Try.to(() -> loader.getPath().toUri().toURL());
	}

	private static ClassLoader parentClassLoader()
	{
		return ServiceClassLoader.class.getClassLoader();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return this.classes.computeIfAbsent(name, ignored -> Try.to(() -> super.findClass(name)));
	}

	Map<String, Class<?>> getClasses()
	{
		return classes;
	}

}
