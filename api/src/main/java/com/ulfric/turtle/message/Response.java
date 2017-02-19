package com.ulfric.turtle.message;

public interface Response extends Message {

	default String respond()
	{
		return "Hello, World!";
	}

}
