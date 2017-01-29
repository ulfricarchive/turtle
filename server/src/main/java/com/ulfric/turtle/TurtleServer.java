package com.ulfric.turtle;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.container.Container;
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
		server.load();
		server.start();
	}

	private final Map<HttpMethod, Map<String, ExchangeController>> allControllers = new HashMap<>();
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

					Map<String, ExchangeController> pathControllers = this.allControllers.get(HttpMethod.valueOf(exchange.getRequestMethod().toString()));

					ExchangeController controller = pathControllers.get(exchange.getRequestPath());
					if (controller != null)
					{
						Response response = (Response) controller.getFunction().apply(new Request());

						// do something with response
					}

				})
				.build();
	}

	private void load()
	{
		for (HttpMethod httpMethod : HttpMethod.values())
		{
			this.allControllers.put(httpMethod, new CaseInsensitiveMap<>());
		}

		Container.registerComponentWrapper(Object.class, this.factory.requestExact(ExchangeComponentWrapper.class));
	}

	private void start()
	{
		this.undertow.start();
	}

	public void stop()
	{
		this.undertow.stop();
	}

	public void registerExchange(ExchangeController exchangeController)
	{
		this.allControllers
				.get(exchangeController.getTarget().getMethod())
				.put(exchangeController.getTarget().getPath(), exchangeController);
	}

}