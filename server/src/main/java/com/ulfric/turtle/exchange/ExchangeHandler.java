package com.ulfric.turtle.exchange;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Deque;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;
import com.ulfric.commons.exception.Try;
import com.ulfric.turtle.TurtleServer;
import com.ulfric.turtle.json.GsonProvider;
import com.ulfric.turtle.message.Request;
import com.ulfric.turtle.message.Response;

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
			Request request = this.getRequest(exchange, controller);

			if (request == null)
			{
				exchange.setStatusCode(500);

				return;
			}

			Method method = controller.getHttpPackage().getMethod();
			MethodHandle handle = controller.getHttpPackage().getHandle();

			Object instance = this.factory.request(method.getDeclaringClass());

			Object responseObject = Try.to(() ->
					method.getParameterCount() == 1 ?
							handle.invokeExact(instance, request) :
							handle.invokeExact(instance)
			);

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

	private Request getRequest(HttpServerExchange exchange, ExchangeController controller)
	{
		return this.gsonProvider.getGson().fromJson(
				this.getPayload(exchange),
				controller.getHttpPackage().getRequest()
		);
	}

	private String getPayload(HttpServerExchange exchange)
	{
		Deque<String> deque = exchange.getQueryParameters().get("payload");

		if (deque == null)
		{
			return null;
		}

		return deque.element();
	}

}
