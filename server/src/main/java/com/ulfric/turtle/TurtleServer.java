package com.ulfric.turtle;

import javax.net.ssl.SSLContext;

import com.ulfric.commons.exception.Try;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.util.Headers;

public final class TurtleServer {

	public static void main(String[] args)
	{
		TurtleServer server = new TurtleServer();
		server.start();
	}

	private final Undertow undertow;

	private TurtleServer()
	{
		this.undertow = this.build();
	}

	private Undertow build()
	{
		return Undertow.builder()
				.setServerOption(UndertowOptions.ENABLE_HTTP2, true)
				.addHttpsListener(8080, "localhost", Try.to(SSLContext::getDefault))
				.setHandler(exchange ->
				{
					exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
				})
				.build();
	}

	private void start()
	{
		this.undertow.start();
	}

}