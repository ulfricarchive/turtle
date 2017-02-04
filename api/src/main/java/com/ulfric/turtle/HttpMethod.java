package com.ulfric.turtle;

import java.lang.annotation.Annotation;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum HttpMethod {

	GET(com.ulfric.turtle.GET.class),
	POST(com.ulfric.turtle.POST.class),
	DELETE(com.ulfric.turtle.DELETE.class),
	PUT(com.ulfric.turtle.PUT.class),
	PATCH(com.ulfric.turtle.PATCH.class);

	private final Class<? extends Annotation> annotation;

	HttpMethod(Class<? extends Annotation> annotation)
	{
		this.annotation = annotation;
	}

	public Class<? extends Annotation> getAnnotation()
	{
		return annotation;
	}

	private static final Map<Class<? extends Annotation>, HttpMethod> valueMap =
			new IdentityHashMap<Class<? extends Annotation>, HttpMethod>()
	{{
		Stream.of(HttpMethod.values()).forEach(method -> this.put(method.annotation, method));
	}};

	public static HttpMethod getMethod(Class<? extends Annotation> annotation)
	{
		return HttpMethod.valueMap.get(annotation);
	}

}
