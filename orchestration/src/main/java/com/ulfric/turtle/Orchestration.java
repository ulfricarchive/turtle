package com.ulfric.turtle;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.inject.Inject;
import com.ulfric.commons.cdi.scope.Shared;

@Shared
public class Orchestration {

	public static void main(String[] args)
	{
		ObjectFactory factory = ObjectFactory.newInstance();
		Orchestration orchestration = factory.requestExact(Orchestration.class);
		orchestration.start();
	}

	@Inject private ObjectFactory factory;

	private void start()
	{

	}

}
