package com.yellerapp.test.unit;

import java.io.IOException;

import com.yellerapp.client.*;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class YellerReporterTest {
	public static class IgnoringSuccessHandler implements YellerSuccessHandler {
		public void errorSent(YellerSuccessResponse response) {
		}
	}

	public static final YellerSuccessResponse FAKE_YELLER_RESPONSE = new YellerSuccessResponse("dad2c5896777a2077e66700f0c2eb64a49f5af851ea00aca922e6d344b209cf8",
			"https://app.yellerapp.com/rails/rails/dad2c5896777a2077e66700f0c2eb64a49f5af851ea00aca922e6d344b209cf8");
	@Rule
	public JUnitRuleMockery mockery = new JUnitRuleMockery();
	protected YellerErrorHandler errorHandler = mockery.mock(YellerErrorHandler.class);
	protected YellerSuccessHandler successHandler = new IgnoringSuccessHandler();

	@Test
	public void itSendsExceptionsToASingleBackend() throws IOException, AuthorizationException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com" }, http, errorHandler, successHandler, null);
		mockery.checking(new Expectations() {
			{
				oneOf(http).post("http://api1.yellerapp.com/api-key-here", exception);
				will(returnValue(FAKE_YELLER_RESPONSE));
			}
		});
		reporter.report(exception);
	}

	@Test
	public void itRoundRobinsBetweenBackends() throws IOException, AuthorizationException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com", "http://api2.yellerapp.com" }, http, errorHandler, successHandler, null);
		mockery.checking(new Expectations() {
			{
				oneOf(http).post("http://api1.yellerapp.com/api-key-here", exception);
				will(returnValue(FAKE_YELLER_RESPONSE));
				oneOf(http).post("http://api2.yellerapp.com/api-key-here", exception);
				will(returnValue(FAKE_YELLER_RESPONSE));
			}
		});
		reporter.report(exception);
		reporter.report(exception);
	}

	@Test
	public void itRetriesWithADifferentBackendWhenAnExceptionIsThrown() throws IOException, AuthorizationException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com", "http://api2.yellerapp.com" }, http, errorHandler, successHandler, null);
		mockery.checking(new Expectations() {
			{
				allowing(http).post("http://api1.yellerapp.com/api-key-here", exception);
				will(throwException(new IOException()));
				oneOf(http).post("http://api2.yellerapp.com/api-key-here", exception);
				will(returnValue(FAKE_YELLER_RESPONSE));
				allowing(errorHandler).reportIOError(with(Matchers.any(String.class)), with(Matchers.any(Throwable.class)));
			}
		});
		reporter.report(exception);
	}

	@Test
	public void itReportsIOExceptionsToTheErrorHandler() throws IOException, AuthorizationException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com", "http://api2.yellerapp.com" }, http, errorHandler, successHandler, null);
		mockery.checking(new Expectations() {
			{
				allowing(http).post("http://api1.yellerapp.com/api-key-here", exception);
				will(throwException(new IOException()));
				allowing(http).post(with(Matchers.any(String.class)), with(Matchers.any(FormattedException.class)));
				will(returnValue(FAKE_YELLER_RESPONSE));
				oneOf(errorHandler).reportIOError(with(Matchers.is("http://api1.yellerapp.com")), with(Matchers.any(IOException.class)));
			}
		});
		reporter.report(exception);
	}

	@Test
	public void itReportsAuthorizationExceptions() throws IOException, AuthorizationException {
		final HTTPClient http = mockery.mock(HTTPClient.class);
		final FormattedException exception = new FormattedException();
		final Reporter reporter = new Reporter("api-key-here", new String[] { "http://api1.yellerapp.com", "http://api2.yellerapp.com" }, http, errorHandler, successHandler, null);
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
