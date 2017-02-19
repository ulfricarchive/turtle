package com.ulfric.turtle.service.load;

import java.net.URL;
import java.net.URLClassLoader;

import com.ulfric.commons.exception.Try;

public final class ServiceClassLoader extends URLClassLoader {

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

}
