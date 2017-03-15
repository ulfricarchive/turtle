package com.ulfric.turtle.service;

import java.nio.file.Files;
import java.nio.file.Path;

import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.scope.Shared;
import com.ulfric.commons.exception.Try;

@Shared
public class TokenProvider {

	@Inject private DirectoryProvider directory;

	private String token;

	@Initialize
	private void init()
	{
		Path authenticationPath = this.getAuthenticationPath();

		this.token = this.getTokenFromPath(authenticationPath);
	}

	private Path getAuthenticationPath()
	{
		return this.directory.getPathInDirectory("authtoken");
	}

	private String getTokenFromPath(Path path)
	{
		return Try.to(() -> Files.readAllLines(path).get(0));
	}

	public String getJFrogAuthenticationToken()
	{
		return this.token;
	}

}
