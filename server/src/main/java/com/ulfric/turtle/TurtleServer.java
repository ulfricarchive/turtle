package com.ulfric.turtle;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.ulfric.commons.artifact.Artifact;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.container.Container;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.dragoon.scope.Shared;
import com.ulfric.commons.exception.Try;
import com.ulfric.turtle.exchange.ExchangeFeatureWrapper;
import com.ulfric.turtle.exchange.ExchangeController;
import com.ulfric.turtle.exchange.ExchangeHandler;
import com.ulfric.turtle.health.service.Health;
import com.ulfric.turtle.manage.HealthStatus;
import com.ulfric.turtle.manage.HealthStatusImpl;
import com.ulfric.turtle.manage.ServiceDeployment;
import com.ulfric.turtle.manage.ServiceDeploymentImpl;
import com.ulfric.turtle.method.HttpMethod;
import com.ulfric.turtle.registry.service.Registry;
import com.ulfric.turtle.service.find.ServiceFinder;
import com.ulfric.turtle.service.load.ServiceLoader;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpServerExchange;

@Shared
public class TurtleServer extends Container {

	public static void main(String[] args)
	{
		ObjectFactory factory = ObjectFactory.newInstance();
		TurtleServer server = factory.requestExact(TurtleServer.class);
		server.enable();
	}

	private final Map<Artifact, ServiceLoader> services = new HashMap<>();
	private final Map<HttpMethod, Map<String, ExchangeController>> allControllers = new HashMap<>();
	private final Undertow undertow;

	@Inject private ObjectFactory factory;
	@Inject private ServiceFinder finder;

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

	@Override
	public void onLoad()
	{
		for (HttpMethod httpMethod : HttpMethod.values())
		{
			this.allControllers.put(httpMethod, new CaseInsensitiveMap<>());
		}

		Container.registerFeatureWrapper(Object.class, this.factory.requestExact(ExchangeFeatureWrapper.class));

		this.factory.bind(ServiceDeployment.class).to(ServiceDeploymentImpl.class);
		this.factory.bind(HealthStatus.class).to(HealthStatusImpl.class);

		this.install(Registry.class);
		this.install(Health.class);
	}

	@Override
	public void onEnable()
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
		try
		{
			this.undertow.stop();
		}
		finally
		{
			this.running = false;
		}
	}

	public void loadService(Artifact artifact)
	{
		ServiceLoader loader = this.finder.find(artifact);
		this.services.put(artifact, loader);

		loader.loadService();
		loader.enableService();
	}

	public void unloadService(Artifact artifact)
	{
		ServiceLoader loader = this.services.get(artifact);

		loader.disable();
		this.services.remove(artifact);
	}

	public void registerExchange(ExchangeController exchangeController)
	{
		this.allControllers
				.get(exchangeController.getTarget().getMethod())
				.put(exchangeController.getTarget().getPath(), exchangeController);
	}

	public void unregisterExchange(ExchangeController exchangeController)
	{
		if (this.allControllers
				.get(exchangeController.getTarget().getMethod())
				.get(exchangeController.getTarget().getPath())
				.equals(exchangeController))
		{
			this.allControllers
					.get(exchangeController.getTarget().getMethod())
					.remove(exchangeController.getTarget().getPath());
		}
	}

	public ExchangeController getController(HttpServerExchange exchange)
	{
		Map<String, ExchangeController> pathControllers = this.allControllers.get(HttpMethod.valueOf(exchange.getRequestMethod().toString()));

		return pathControllers.get(exchange.getRequestPath());
	}

}