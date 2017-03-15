package com.ulfric.turtle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.verify.Verify;

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
		if (this.server.isRunning())
		{
			this.server.stop();
		}
	}

	@Test
	void testStart()
	{
		this.server.start();
	}

	@Test
	void testIsRunning()
	{
		Verify.that(this.server.isRunning()).isFalse();
		this.server.start();
		Verify.that(this.server.isRunning()).isTrue();
		this.server.stop();
		Verify.that(this.server.isRunning()).isFalse();
	}

}