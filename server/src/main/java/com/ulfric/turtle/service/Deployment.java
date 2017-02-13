package com.ulfric.turtle.service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ulfric.commons.exception.Try;

public final class Deployment {

	private final File directory;
	private final JsonObject object;

	Deployment(File directory)
	{
		this.directory = directory;
		this.object = this.loadDeployment();
	}

	private JsonObject loadDeployment()
	{
		JsonParser parser = new JsonParser();

		return parser.parse(this.getJson()).getAsJsonObject();
	}

	private String getJson()
	{
		File deployment = new File(this.directory, "deployment.json");

		List<String> lines = Try.to(() -> Files.readAllLines(deployment.toPath()));

		return lines.stream().collect(Collectors.joining());
	}

}
