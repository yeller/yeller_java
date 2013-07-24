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
	public void itTrimsTheMessageToOneThousandCharacters() {
		FormattedException formatted;
		try {
			StringBuilder sb = new StringBuilder(2000);
			for (int i=0; i<2000; i++) {
				sb.append('a');
			}
			String reallyLongMessage = sb.toString();
			throw new RuntimeException(reallyLongMessage);
		} catch (Throwable t) {
			formatted = exceptionFormatter.format(t);
			assertThat(formatted.message.length(), is(1000));
		}
	}

	@Test
	public void itPullsTheStacktraceFromTheMessage() {
		assertThat(formatDefault().stackTrace.get(0).get(0), is("ExceptionFormatterTest.java"));
	}

	@Test
	public void itTrimsTheStracktraceToOneThousandLines() {
		FormattedException formatted;
		try {
			throw new RuntimeException();
		} catch (Throwable t) {
			StackTraceElement[] newStackTrace = new StackTraceElement[2000];
			for(int i=0; i<newStackTrace.length; i++) {
				newStackTrace[i] = t.getStackTrace()[0];
			}
			t.setStackTrace(newStackTrace);
			formatted = exceptionFormatter.format(t);
			assertThat(formatted.stackTrace.size(), is(1000));
		}
	}

	@Test
	public void itAttachesTheCurrentHostName() throws UnknownHostException {
		assertThat(formatDefault().host, is(InetAddress.getLocalHost().getHostName()));
	}

	@Test
	public void itGrabsTheCurrentTime() {
	}
}
