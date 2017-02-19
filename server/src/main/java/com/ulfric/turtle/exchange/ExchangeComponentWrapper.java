package com.ulfric.turtle.exchange;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.container.Component;
import com.ulfric.commons.cdi.container.ComponentWrapper;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.turtle.http.TurtleHttpPackage;
import com.ulfric.turtle.http.HttpTarget;
import com.ulfric.turtle.http.TurtleHttpTarget;
import com.ulfric.turtle.message.Request;
import com.ulfric.turtle.message.Response;
import com.ulfric.turtle.method.DELETE;
import com.ulfric.turtle.method.GET;
import com.ulfric.turtle.method.HttpMethod;
import com.ulfric.turtle.method.POST;

public class ExchangeComponentWrapper implements ComponentWrapper<Object> {

	private static final Class<?>[] httpMethods = {
			GET.class, POST.class, DELETE.class
	};

	@Inject
	private ObjectFactory factory;

	@Override
	public Component apply(Component component, Object object)
	{
		Set<ExchangeController> exchangeControllers = new HashSet<>();

		for (Method method : object.getClass().getDeclaredMethods())
		{
			Optional<Annotation> optional = this.getHttpAnnotation(method);

			if (optional.isPresent())
			{
				Class<?> returnType = method.getReturnType();
				Parameter[] parameters = method.getParameters();

				if (parameters.length == 0 || parameters[0].getType().isAssignableFrom(Request.class) ||
						!(returnType.equals(Void.TYPE) || returnType.isAssignableFrom(Response.class)))
				{
					// log error
					continue;
				}

				@SuppressWarnings("unchecked")
				Class<? extends Request> requestClass = (Class<? extends Request>) parameters[0].getType();
				@SuppressWarnings("unchecked")
				Class<? extends Response> responseClass =
						returnType.equals(Void.TYPE) ?
								Response.class :
								(Class<? extends Response>) returnType;

				Annotation annotation = optional.get();

				exchangeControllers.add(new ExchangeController(
						new TurtleHttpTarget(HttpMethod.getMethod(annotation.getClass()), HttpTarget.pathOfMethod(annotation)),
						new TurtleHttpPackage(method, requestClass, responseClass)
				));
			}
		}

		return new HttpExchangeComponent(this.factory, exchangeControllers);
	}

	private Optional<Annotation> getHttpAnnotation(Method method)
	{
		for (Annotation annotation : method.getDeclaredAnnotations())
		{
			if (ArrayUtils.contains(ExchangeComponentWrapper.httpMethods, annotation))
			{
				return Optional.of(annotation);
			}
		}

		return Optional.empty();
	}

}
