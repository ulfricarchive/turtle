package com.ulfric.turtle;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;

final class TurtleConfiguration {

	static TurtleConfiguration loadConfiguration()
	{
		Path deployment = TurtleConfiguration.getDeploymentPath();

		if (Files.notExists(deployment))
		{
			return new TurtleConfiguration();
		}

		try (Reader reader = Files.newBufferedReader(deployment))
		{
			return new Gson().fromJson(reader, TurtleConfiguration.class);
		}
		catch (IOException propagate)
		{
			throw new RuntimeException(propagate);
		}
	}

	private static Path getDeploymentPath()
	{
		return Paths.get(TurtleConfiguration.getDeploymentPropertyOrDefault());
	}

	private static String getDeploymentPropertyOrDefault()
	{
		return System.getProperty("turtle.deployment", "deployment.json");
	}

	private String host = "localhost";
	private int port = 8080;
	private boolean http2 = true;

	public String getHost()
	{
		return this.host;
	}

	public int getPort()
	{
		return this.port;
	}

	public boolean getHttp2()
	{
		return this.http2;
	}

}