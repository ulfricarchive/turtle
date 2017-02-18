package com.ulfric.turtle;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class TurtleConfigurationTest {

	private TurtleConfiguration configuration;

	@AfterEach
	void teardown()
	{
		this.configuration = null;
	}

	@Test
	void testLoadConfiguration()
	{
		this.configuration = TurtleConfiguration.loadConfiguration();
	}

}