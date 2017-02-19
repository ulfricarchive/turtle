package com.ulfric.turtle.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashSet;
import java.util.Set;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;
import com.ulfric.turtle.TurtleServer;

@Shared
public class ServiceFinder {

	private static final String REPO_URL = "http://repo.dev.ulfric.com/artifactory/all" +
			"/{group}/{artifact}/{version}/{artifact}-{version}.jar";

	private final File directory;
	private final File servicesDirectory;
	private final Set<ServiceLoader> loaders = new HashSet<>();

	@Inject private ObjectFactory factory;
	@Inject private TurtleServer server;

	public ServiceFinder()
	{
		this.directory = this.loadDirectory();
		this.servicesDirectory = this.loadServicesDirectory();
	}

	private File loadDirectory()
	{
		return Try.to(() -> new File(this.jarUrl().toURI()));
	}

	private File loadServicesDirectory()
	{
		File folder = new File(this.directory, "services");

		if (!folder.exists())
		{
			if (!folder.mkdirs())
			{
				throw new RuntimeException("No permission to create services directory");
			}
		}

		return folder;
	}

	public void downloadJar(ServiceArtifact artifact)
	{
		File file = this.getFileToSaveTo(artifact);
		URL url = this.getUrlFor(artifact);

		try
		{
			ReadableByteChannel channel = Channels.newChannel(url.openStream());
			FileOutputStream output = new FileOutputStream(file);

			output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
		}
		catch (IOException e)
		{

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

	private URL jarUrl()
	{
		return this.getClass().getProtectionDomain().getCodeSource().getLocation();
	}

}
