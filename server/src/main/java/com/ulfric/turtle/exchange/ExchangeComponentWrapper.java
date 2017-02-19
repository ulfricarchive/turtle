package com.ulfric.turtle.exchange;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.container.Component;
import com.ulfric.commons.cdi.container.ComponentWrapper;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.turtle.http.HttpPackage;
import com.ulfric.turtle.http.HttpTarget;
import com.ulfric.turtle.http.TurtleHttpPackage;
import com.ulfric.turtle.http.TurtleHttpTarget;
import com.ulfric.turtle.logging.Log;
import com.ulfric.turtle.message.Request;
import com.ulfric.turtle.message.Response;
import com.ulfric.turtle.method.HttpMethod;

public class ExchangeComponentWrapper implements ComponentWrapper<Object> {

	@Inject private ObjectFactory factory;
	@Inject private Log logger;
	@Inject private HttpExchangeComponent component;

	@Override
	public Component apply(Component component, Object object)
	{
		Stream.of(object.getClass().getDeclaredMethods())
				.filter(this::hasHttpAnnotation)
				.forEach(method ->
				{
					Annotation annotation = Stream.of(method.getDeclaredAnnotations())
							.filter(HttpMethod::isHttpAnnotation)
							.findFirst()
							.orElse(null);

					HttpMethod httpMethod = HttpMethod.getMethod(annotation.getClass());

					Class<?> returnType = method.getReturnType();
					Parameter[] parameters = method.getParameters();

					if (parameters.length == 0 || parameters[0].getType().isAssignableFrom(Request.class) ||
							!(returnType.equals(Void.TYPE) || returnType.isAssignableFrom(Response.class)))
					{
						this.logger.write(new IllegalStateException(
								"Method " + method.getName() + " in class " +
										object.getClass().getName() + " doesn't have " +
										"correct parameter / return types"
						));
						return;
					}

					@SuppressWarnings("unchecked")
					Class<? extends Request> requestClass = (Class<? extends Request>) parameters[0].getType();
					@SuppressWarnings("unchecked")
					Class<? extends Response> responseClass =
							returnType.equals(Void.TYPE) ?
									Response.class :
									(Class<? extends Response>) returnType;

					HttpTarget target = new TurtleHttpTarget(httpMethod, HttpTarget.pathOfMethod(annotation));
					HttpPackage httpPackage = new TurtleHttpPackage(method, requestClass, responseClass);

					this.component.addExchangeController(new ExchangeController(target, httpPackage));
				});

		return this.component;
	}

	private boolean hasHttpAnnotation(Method method)
	{
		return Stream.of(method.getDeclaredAnnotations())
				.anyMatch(HttpMethod::isHttpAnnotation);
	}

}
