package com.ulfric.turtle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

import com.google.gson.Gson;
import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.container.Container;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;
import com.ulfric.turtle.exchange.ExchangeComponentWrapper;
import com.ulfric.turtle.exchange.ExchangeController;
import com.ulfric.turtle.message.Request;
import com.ulfric.turtle.message.Response;
import com.ulfric.turtle.method.HttpMethod;
import com.ulfric.turtle.method.PARAM;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpServerExchange;
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
				.setHandler(this::handle)
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

	private void handle(HttpServerExchange exchange) throws Exception
	{
		this.setResponseHeaders(exchange);

		ExchangeController controller = this.getController(exchange);

		if (controller != null)
		{
			Request request = this.factory.requestExact(controller.getHttpPackage().getRequest());

			if (request == null)
			{
				exchange.setStatusCode(500);

				return;
			}

			this.injectInto(exchange, request);

			Method method = controller.getHttpPackage().getMethod();

			Object instance = this.factory.request(method.getDeclaringClass());

			Object responseObject =
					method.getParameterCount() == 1 ?
							method.invoke(instance, request) :
							method.invoke(instance);

			Response response =
					responseObject instanceof Response ?
							(Response) responseObject :
							new Response();

			exchange.getResponseSender().send(response.respond());
		}

	}

	private void setResponseHeaders(HttpServerExchange exchange)
	{
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
	}

	private ExchangeController getController(HttpServerExchange exchange)
	{
		Map<String, ExchangeController> pathControllers = this.allControllers.get(HttpMethod.valueOf(exchange.getRequestMethod().toString()));

		return pathControllers.get(exchange.getRequestPath());
	}

	private void injectInto(HttpServerExchange exchange, Request request)
	{
		for (Field field : request.getClass().getDeclaredFields())
		{
			if (field.isAnnotationPresent(PARAM.class))
			{
				String path = field.getName();

				Deque<String> deque = exchange.getQueryParameters().get(path);

				if (deque == null)
				{
					continue;
				}

				String param = deque.element();

				if (param == null)
				{
					continue;
				}

				Object value = new Gson().fromJson(param, field.getType());

				field.setAccessible(true);

				Try.to(() -> field.set(request, value));
			}
		}
	}

}