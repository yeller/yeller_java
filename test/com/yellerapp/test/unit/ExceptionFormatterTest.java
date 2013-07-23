package com.yellerapp.test.unit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import com.yellerapp.client.ExceptionFormatter;
import com.yellerapp.client.FormattedException;

public class ExceptionFormatterTest {
	private final ExceptionFormatter exceptionFormatter = new ExceptionFormatter();

	protected FormattedException formatDefault() {
		FormattedException formatted;
		try {
			throw new RuntimeException("an error message");
		} catch (Throwable t) {
			formatted = exceptionFormatter.format(t);
		}
		return formatted;
	}

	@Test
	public void itPullsTheExceptionTypeOutOfTheException() {
		assertThat(formatDefault().type, is("RuntimeException"));
	}

	@Test
	public void itPullsTheExceptionMessageFromTheException() {
		assertThat(formatDefault().message, is("an error message"));
	}

	@Test
	public void itPullsTheStacktraceFromTheMessage() {
		assertThat(formatDefault().stackTrace.get(0).get(0), is("ExceptionFormatterTest.java"));
	}

	@Test
	public void itAttachesTheCurrentHostName() throws UnknownHostException {
		assertThat(formatDefault().host, is(InetAddress.getLocalHost().getHostName()));
	}

	@Test
	public void itGrabsTheCurrentTime() {
	}
}
