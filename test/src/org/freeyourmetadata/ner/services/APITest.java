package org.freeyourmetadata.ner.services;

import org.slf4j.Logger;
import org.testng.annotations.BeforeSuite;

public class APITest {

	protected Logger logger;

	@BeforeSuite
	public void init() {
		System.setProperty("log4j.configuration", "tests.log4j.properties");
	}
}
