package com.ulfric.turtle.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.jar.JarFile;

import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;
import com.ulfric.turtle.log.LoggingProvider;

@Shared
public class ServiceFinder {

	private static final String REPO_URL = "http://repo.dev.ulfric.com/artifactory/all" +
			"/{group}/{artifact}/{version}/{artifact}-{version}.jar";

	private final File servicesDirectory;

	@Inject private DirectoryProvider directory;
	@Inject private LoggingProvider logging;
	@Inject private TokenProvider token;

	public ServiceFinder()
	{
		this.servicesDirectory = this.loadServicesDirectory();
	}

	private File loadServicesDirectory()
	{
		File folder = this.directory.getFileInDirectory("services");

		if (!folder.exists())
		{
			if (!folder.mkdirs())
			{
				throw new RuntimeException("No permission to create services directory");
			}
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
		File file = this.getFileToSaveTo(artifact);
		URL url = this.getUrlFor(artifact);

		this.downloadFile(file, url);

		return Try.to(() -> new JarFile(file, false));
	}

	private void downloadFile(File to, URL url)
	{
		try
		{
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			this.setPost(connection);
			this.authenticate(connection);

			ReadableByteChannel channel = Channels.newChannel(connection.getInputStream());
			FileOutputStream output = new FileOutputStream(to);

			output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
		}
		catch (IOException exception)
		{
			this.logging.log(exception);
		}
	}

	private File getFileToSaveTo(ServiceArtifact artifact)
	{
		return new File(
				this.servicesDirectory,
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
