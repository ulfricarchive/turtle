package com.ulfric.turtle;

import com.ulfric.commons.cdi.ObjectFactory;
import com.ulfric.commons.cdi.initialize.Initialize;
import com.ulfric.commons.cdi.inject.Inject;

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
