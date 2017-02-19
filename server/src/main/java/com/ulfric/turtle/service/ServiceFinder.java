package com.ulfric.turtle.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;

import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;
import com.ulfric.turtle.logging.Log;

@Shared
public class ServiceFinder {

	private static final String REPO_URL = "http://repo.dev.ulfric.com/artifactory/all" +
			"/{group}/{artifact}/{version}/{artifact}-{version}.jar";

	private final Path servicesDirectory;

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

	public ServiceLoader find(ServiceArtifact artifact)
	{
		JarFile jar = this.downloadJar(artifact);

		return new ServiceLoader(jar);
	}

	private JarFile downloadJar(ServiceArtifact artifact)
	{
		Path path = this.getPathToSaveTo(artifact);
		URL url = this.getUrlFor(artifact);

		this.downloadFile(path, url);

		return Try.to(() -> new JarFile(path.toFile(), false));
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

	private Path getPathToSaveTo(ServiceArtifact artifact)
	{
		return this.servicesDirectory.resolve(
				artifact.getArtifact() + "-" + artifact.getVersion().getFullVersion() + ".jar"
		);
	}

	private URL getUrlFor(ServiceArtifact artifact)
	{
		return Try.to(() ->
				new URL(
						ServiceFinder.REPO_URL
						.replace("{group}", this.formatMavenId(artifact.getGroup()))
						.replace("{artifact}", this.formatMavenId(artifact.getArtifact()))
						.replace("{version}", artifact.getVersion().getFullVersion())
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
