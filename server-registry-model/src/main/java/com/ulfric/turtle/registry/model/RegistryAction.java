package com.ulfric.turtle.registry.model;

public enum RegistryAction {

	START,
	REMOVE;

	public boolean toStart()
	{
		return this == RegistryAction.START;
	}

	public boolean toRemove()
	{
		return this == RegistryAction.REMOVE;
	}

}
