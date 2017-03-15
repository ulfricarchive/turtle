package com.ulfric.turtle.registry.model;

import com.ulfric.turtle.model.Response;

public interface RegisterResponse extends Response {

	boolean isSuccessful();

	void setSuccessful(boolean successful);

}
