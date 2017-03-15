package com.ulfric.turtle.json;

import com.google.gson.Gson;
import com.ulfric.dragoon.scope.Shared;

@Shared
public class GsonProvider {

	private final Gson gson = new Gson();

	public Gson getGson()
	{
		return this.gson;
	}

}
