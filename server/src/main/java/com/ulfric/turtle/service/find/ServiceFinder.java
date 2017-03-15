package com.ulfric.turtle.service.find;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import com.ulfric.commons.artifact.Artifact;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.scope.Shared;
import com.ulfric.commons.exception.Try;
import com.ulfric.turtle.logging.Log;
import com.ulfric.turtle.service.DirectoryProvider;
import com.ulfric.turtle.service.TokenProvider;
import com.ulfric.turtle.service.load.ServiceLoader;

@Shared
public class ServiceFinder {

	private static final String REPO_URL = "http://repo.dev.ulfric.com/artifactory/all" +
			"/{group}/{artifact}/{version}/{artifact}-{version}.jar";

	private final Path servicesDirectory;

	@Inject private ObjectFactory factory;
	@Inject private DirectoryProvider directory;
	@Inject private Log logger;
	@Inject private TokenProvider token;

	public ServiceFinder()
	{
		this.servicesDirectory = this.loadServicesDirectory();
	}

	private Path loadServicesDirectory()
	{
		Path folder = this.directory.getPathInDirectory("services");

		if (!Files.exists(folder))
		{
			Try.to(() -> Files.createDirectory(folder));
		}

		return folder;
	}

	public ServiceLoader find(Artifact artifact)
	{
		Path path = this.getPathToSaveTo(artifact);
		this.downloadJar(artifact, path);

		ServiceLoader loader = this.factory.requestExact(ServiceLoader.class);
		loader.initializePath(path);

		return loader;
	}

	private void downloadJar(Artifact artifact, Path path)
	{
		URL url = this.getUrlFor(artifact);

		this.downloadFile(path, url);
	}

	private void downloadFile(Path to, URL url)
	{
		try
		{
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			this.setPost(connection);
			this.authenticate(connection);

			ReadableByteChannel channel = Channels.newChannel(connection.getInputStream());
			FileOutputStream output = new FileOutputStream(to.toFile());

			output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
		}
		catch (IOException exception)
		{
			this.logger.write(exception);
		}
	}

	private Path getPathToSaveTo(Artifact artifact)
	{
		return this.servicesDirectory.resolve(
				artifact.getArtifact() + "-" + artifact.getVersion().getFull() + ".jar"
		);
	}

	private URL getUrlFor(Artifact artifact)
	{
		return Try.to(() ->
				new URL(
						ServiceFinder.REPO_URL
						.replace("{group}", this.formatMavenId(artifact.getGroup()))
						.replace("{artifact}", this.formatMavenId(artifact.getArtifact()))
						.replace("{version}", artifact.getVersion().getFull())
				)
		);
	}

	private String formatMavenId(String id)
	{
		return id.replace(".", "/");
	}

	private void setPost(HttpURLConnection connection)
	{
		Try.to(() -> connection.setRequestMethod("POST"));
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Language", "en-US");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
	}

	private void authenticate(HttpURLConnection connection)
	{
		connection.setRequestProperty("X-JFrog-Art-Api", this.token.getJFrogAuthenticationToken());
	}

}
