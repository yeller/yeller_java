package com.yellerapp.test.unit;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.yellerapp.client.AuthorizationException;
import com.yellerapp.client.FormattedException;
import com.yellerapp.client.HTTPClient;
import com.yellerapp.client.Reporter;
import com.yellerapp.client.YellerErrorHandler;

public class YellerReporterTest {
	@Rule
	public JUnitRuleMockery mockery = new JUnitRuleMockery();
	protected YellerErrorHandler errorHandler = mockery.mock(YellerErrorHandler.class);

	@Test
	public void itSendsExceptionsToASingleBackend() throws IOException, AuthorizationException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com" }, http, errorHandler);
		mockery.checking(new Expectations() {
			{
				oneOf(http).post("http://api1.yellerapp.com/api-key-here", exception);
			}
		});
		reporter.report(exception);
	}

	@Test
	public void itRoundRobinsBetweenBackends() throws IOException, AuthorizationException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com", "http://api2.yellerapp.com" }, http, errorHandler);
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
	public void itRetriesWithADifferentBackendWhenAnExceptionIsThrown() throws IOException, AuthorizationException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com", "http://api2.yellerapp.com" }, http, errorHandler);
		mockery.checking(new Expectations() {
			{
				allowing(http).post("http://api1.yellerapp.com/api-key-here", exception);
				will(throwException(new IOException()));
				oneOf(http).post("http://api2.yellerapp.com/api-key-here", exception);
				allowing(errorHandler).reportIOError(with(Matchers.any(String.class)), with(Matchers.any(Throwable.class)));
			}
		});
		reporter.report(exception);
	}

	@Test
	public void itReportsIOExceptionsToTheErrorHandler() throws IOException, AuthorizationException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com", "http://api2.yellerapp.com" }, http, errorHandler);
		mockery.checking(new Expectations() {
			{
				allowing(http).post("http://api1.yellerapp.com/api-key-here", exception);
				will(throwException(new IOException()));
				allowing(http).post(with(Matchers.any(String.class)), with(Matchers.any(FormattedException.class)));
				oneOf(errorHandler).reportIOError(with(Matchers.is("http://api1.yellerapp.com")), with(Matchers.any(IOException.class)));
			}
		});
		reporter.report(exception);
	}

	@Test
	public void itReportsAuthorizationExceptions() throws IOException, AuthorizationException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com", "http://api2.yellerapp.com" }, http, errorHandler);
		mockery.checking(new Expectations() {
			{
				allowing(http).post("http://api1.yellerapp.com/api-key-here", exception);
				will(throwException(new AuthorizationException("unauthorized")));
				oneOf(errorHandler).reportAuthError(with("http://api1.yellerapp.com"), with(Matchers.any(AuthorizationException.class)));
			}
		});
		reporter.report(exception);
	}
}
