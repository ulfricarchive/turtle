package com.ulfric.turtle.registry.model;

import com.ulfric.turtle.message.Response;

public class RegisterResponse extends Response {

	private boolean successful;

	public boolean isSuccessful()
	{
		return this.successful;
	}

	public void setSuccessful(boolean successful)
	{
		this.successful = successful;
	}

}
