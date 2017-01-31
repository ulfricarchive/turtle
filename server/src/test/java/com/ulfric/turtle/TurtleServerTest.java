package com.ulfric.turtle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.commons.cdi.ObjectFactory;

@RunWith(JUnitPlatform.class)
public class TurtleServerTest {

	private ObjectFactory factory;
	private TurtleServer server;

	@BeforeEach
	void init()
	{
		this.factory = ObjectFactory.newInstance();
		this.server = this.factory.requestExact(TurtleServer.class);
	}

	@AfterEach
	void teardown()
	{
		this.server.stop();
	}

	@Test
	void testStart()
	{
		this.server.start();
	}

}