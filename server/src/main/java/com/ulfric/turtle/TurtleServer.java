package com.ulfric.turtle;

import javax.net.ssl.SSLContext;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.util.Headers;

@Shared
public class TurtleServer {

	private final Undertow undertow;
	private final TurtleConfiguration configuration;

	@Inject
	private ObjectFactory factory;

	private volatile boolean running = false;

	private TurtleServer()
	{
		this.configuration = TurtleConfiguration.loadConfiguration();
		this.undertow = this.buildUndertowInstance();
	}

	private Undertow buildUndertowInstance()
	{
		TurtleConfiguration configuration = this.configuration;
		return Undertow.builder()
				.setServerOption(UndertowOptions.ENABLE_HTTP2, configuration.getHttp2())
				.addHttpsListener(configuration.getPort(), configuration.getHost(), this.getSslContext())
				.setHandler(this.createHttpHandler())
				.build();
	}

	private SSLContext getSslContext()
	{
		return Try.to(SSLContext::getDefault);
	}

	private HttpHandler createHttpHandler()
	{
		return exchange -> exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
	}

	public void start()
	{
		this.undertow.start();
		this.running = true;
	}

	public boolean isRunning()
	{
		return this.running;
	}

	public void stop()
	{
		this.undertow.stop();
		this.running = false;
	}

}