package com.ulfric.turtle;

import javax.net.ssl.SSLContext;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.util.Headers;

@Shared
public class TurtleServer {

	public static void main(String[] args)
	{
		ObjectFactory factory = ObjectFactory.newInstance();
		TurtleServer server = factory.requestExact(TurtleServer.class);
		server.start();
	}

	private final Undertow undertow;

	@Inject
	private ObjectFactory factory;

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

	public void start()
	{
		this.undertow.start();
	}

	public void stop()
	{
		this.undertow.stop();
	}

}