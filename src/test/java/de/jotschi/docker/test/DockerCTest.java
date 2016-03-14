package de.jotschi.docker.test;

import static org.junit.Assert.fail;

import org.junit.Test;

public class DockerCTest {

	@Test
	public void testA() throws InterruptedException {
		Thread.sleep(10000);
		fail("Lets test failing tests");
	}
}
