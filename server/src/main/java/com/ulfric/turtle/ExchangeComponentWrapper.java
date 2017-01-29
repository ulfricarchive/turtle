package com.ulfric.turtle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.container.Component;
import com.ulfric.commons.cdi.container.ComponentWrapper;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.exception.Try;

class ExchangeComponentWrapper implements ComponentWrapper<Object> {

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
				Annotation annotation = optional.get();

				exchangeControllers.add(new ExchangeController(
						new HttpTarget(HttpMethod.getMethod(annotation.getClass()), HttpTarget.pathOfMethod(annotation)),
						request -> Try.to(() -> (Response) method.invoke(request))
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
