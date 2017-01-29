package com.ulfric.turtle;

import java.util.function.Function;

@FunctionalInterface
public interface HttpFunction<T extends Request, R extends Response> extends Function<T, R> {

}
