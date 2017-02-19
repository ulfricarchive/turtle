package com.ulfric.turtle.service;

import java.io.File;
import java.nio.file.Files;

import com.ulfric.commons.cdi.initialize.Initialize;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;

@Shared
public class TokenProvider {

	@Inject private DirectoryProvider directory;

	private String token;

	@Initialize
	private void init()
	{
		File authenticationFile = this.getAuthenticationFile();

		this.token = this.getTokenFromFile(authenticationFile);
	}

	private File getAuthenticationFile()
	{
		return this.directory.getFileInDirectory("authtoken");
	}

	private String getTokenFromFile(File file)
	{
		return Try.to(() -> Files.readAllLines(file.toPath()).get(0));
	}

	public String getJFrogAuthenticationToken()
	{
		return this.token;
	}

}
