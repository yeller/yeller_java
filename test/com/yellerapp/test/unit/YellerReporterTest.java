package com.yellerapp.test.unit;

import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.yellerapp.client.FormattedException;
import com.yellerapp.client.HTTPClient;
import com.yellerapp.client.Reporter;

public class YellerReporterTest {
	@Rule
	public JUnitRuleMockery mockery = new JUnitRuleMockery();

	@Test
	public void itSendsExceptionsToASingleBackend() throws IOException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com" }, http);
		mockery.checking(new Expectations() {
			{
				oneOf(http).post("http://api1.yellerapp.com/api-key-here", exception);
			}
		});
		reporter.report(exception);
	}

	@Test
	public void itRoundRobinsBetweenBackends() throws IOException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com", "http://api2.yellerapp.com" }, http);
		mockery.checking(new Expectations() {
			{
				oneOf(http).post("http://api1.yellerapp.com/api-key-here", exception);
				oneOf(http).post("http://api2.yellerapp.com/api-key-here", exception);
			}
		});
		reporter.report(exception);
		reporter.report(exception);
	}

	@Test
	public void itRetriesWithADifferentBackendWhenAnExceptionIsThrown() throws IOException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com", "http://api2.yellerapp.com" }, http);
		mockery.checking(new Expectations() {
			{
				allowing(http).post("http://api1.yellerapp.com/api-key-here", exception);
				will(throwException(new IOException()));
				oneOf(http).post("http://api2.yellerapp.com/api-key-here", exception);
			}
		});
		reporter.report(exception);
	}
}
