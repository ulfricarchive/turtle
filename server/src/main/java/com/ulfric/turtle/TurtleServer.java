package com.ulfric.turtle;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import io.undertow.Undertow;
import io.undertow.util.Headers;

public class TurtleServer {

	public static void main(String[] args) throws NoSuchAlgorithmException
	{
		Undertow server = Undertow.builder()
				.addHttpsListener(8080, "localhost", SSLContext.getDefault())
				.setHandler(exchange -> {
					exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
				})
				.build();

		server.start();
	}

}