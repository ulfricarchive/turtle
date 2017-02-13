package com.ulfric.turtle.exchange;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.stream.Stream;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;
import com.ulfric.turtle.TurtleServer;
import com.ulfric.turtle.json.GsonProvider;
import com.ulfric.turtle.message.Request;
import com.ulfric.turtle.message.Response;
import com.ulfric.turtle.method.PARAM;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

@Shared
public class ExchangeHandler implements HttpHandler {

	@Inject private ObjectFactory factory;
	@Inject private TurtleServer server;
	@Inject private GsonProvider gsonProvider;

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception
	{
		this.setResponseHeaders(exchange);

		ExchangeController controller = this.server.getController(exchange);

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

	private void injectInto(HttpServerExchange exchange, Request request)
	{
		Stream.of(request.getClass().getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(PARAM.class))
				.forEach(field ->
				{
					String param = this.getParameter(exchange, field);

					if (param == null)
					{
						return;
					}

					Object value = this.gsonProvider.getGson().fromJson(param, field.getType());

					field.setAccessible(true);

					Try.to(() -> field.set(request, value));
				});
	}

	private String getParameter(HttpServerExchange exchange, Field field)
	{
		String path = field.getName();

		Deque<String> deque = exchange.getQueryParameters().get(path);

		if (deque == null)
		{
			return null;
		}

		return deque.element();
	}

}
