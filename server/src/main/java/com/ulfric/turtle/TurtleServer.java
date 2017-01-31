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

	@Inject
	private ObjectFactory factory;

	private TurtleServer()
	{
		this.undertow = this.buildUndertowInstance();
	}

	private Undertow buildUndertowInstance()
	{
		return Undertow.builder()
				.setServerOption(UndertowOptions.ENABLE_HTTP2, true)
				.addHttpsListener(this.getPort(), this.getHost(), this.getSslContext())
				.setHandler(this.createHttpHandler())
				.build();
	}

	private int getPort()
	{
		return 8080;
	}

	private String getHost()
	{
		return "localhost";
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
	}

	public void stop()
	{
		this.undertow.stop();
	}

}