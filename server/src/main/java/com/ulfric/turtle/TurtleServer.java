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
import com.ulfric.turtle.exchange.ExchangeComponentWrapper;
import com.ulfric.turtle.exchange.ExchangeController;
import com.ulfric.turtle.exchange.ExchangeHandler;
import com.ulfric.turtle.json.GsonProvider;
import com.ulfric.turtle.method.HttpMethod;
import com.ulfric.turtle.service.find.ServiceFinder;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpServerExchange;

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

	private ServiceFinder finder;

	@Inject private ObjectFactory factory;
	@Inject private GsonProvider gsonProvider;

	private volatile boolean running = false;

	private TurtleServer()
	{
		this.undertow = this.buildUndertowInstance();
	}

	private Undertow buildUndertowInstance()
	{
		return Undertow.builder()
				.setServerOption(UndertowOptions.ENABLE_HTTP2, true)
				.addHttpsListener(8080, "localhost", Try.to(SSLContext::getDefault))
				.setHandler(this.factory.requestExact(ExchangeHandler.class))
				.build();
	}

	private void load()
	{
		for (HttpMethod httpMethod : HttpMethod.values())
		{
			this.allControllers.put(httpMethod, new CaseInsensitiveMap<>());
		}

		Container.registerComponentWrapper(Object.class, this.factory.requestExact(ExchangeComponentWrapper.class));

		this.findServices();
		this.loadServices();
	}

	private void findServices()
	{
		this.finder = this.factory.requestExact(ServiceFinder.class);
	}

	private void loadServices()
	{

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

	public void registerExchange(ExchangeController exchangeController)
	{
		this.allControllers
				.get(exchangeController.getTarget().getMethod())
				.put(exchangeController.getTarget().getPath(), exchangeController);
	}

	public ExchangeController getController(HttpServerExchange exchange)
	{
		Map<String, ExchangeController> pathControllers = this.allControllers.get(HttpMethod.valueOf(exchange.getRequestMethod().toString()));

		return pathControllers.get(exchange.getRequestPath());
	}

}