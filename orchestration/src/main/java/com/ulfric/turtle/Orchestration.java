package com.ulfric.turtle;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.inject.Inject;

public class Orchestration {

	public static void main(String[] args)
	{
		ObjectFactory factory = ObjectFactory.newInstance();
		factory.request(Orchestration.class);
	}

	@Inject private ObjectFactory factory;

	@Initialize
	private void init()
	{

	}

}
