package com.yellerapp.test.unit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.junit.Test;

import com.yellerapp.client.ExceptionFormatter;
import com.yellerapp.client.FormattedException;
import com.yellerapp.client.Version;
import com.yellerapp.client.YellerExtraDetail;

public class ExceptionFormatterTest {
	private final ExceptionFormatter exceptionFormatter = new ExceptionFormatter(
			null);
	private static final HashMap<String, Object> NO_CUSTOM_DATA = new HashMap<String, Object>();
	private static final YellerExtraDetail NO_DETAIL = new YellerExtraDetail();

	protected FormattedException formatDefault() {
		FormattedException formatted;
		try {
			throw new RuntimeException("an error message");
		} catch (Throwable t) {
			formatted = exceptionFormatter.format(t, NO_DETAIL, NO_CUSTOM_DATA);
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
			for (int i = 0; i < 2000; i++) {
				sb.append('a');
			}
			String reallyLongMessage = sb.toString();
			throw new RuntimeException(reallyLongMessage);
		} catch (Throwable t) {
			formatted = exceptionFormatter.format(t, NO_DETAIL, NO_CUSTOM_DATA);
			assertThat(formatted.message.length(), is(1000));
		}
	}

	@Test
	public void itPullsTheStacktraceFromTheMessage() {
		assertThat((String) formatDefault().stackTrace.get(0).get(0),
				is("ExceptionFormatterTest.java"));
	}

	@Test
	public void itTrimsTheStracktraceToOneThousandLines() {
		FormattedException formatted;
		try {
			throw new RuntimeException();
		} catch (Throwable t) {
			StackTraceElement[] newStackTrace = new StackTraceElement[2000];
			for (int i = 0; i < newStackTrace.length; i++) {
				newStackTrace[i] = t.getStackTrace()[0];
			}
			t.setStackTrace(newStackTrace);
			formatted = exceptionFormatter.format(t, NO_DETAIL, NO_CUSTOM_DATA);
			assertThat(formatted.stackTrace.size(), is(1000));
		}
	}

	@Test
	public void itAttachesTheCurrentHostName() throws UnknownHostException {
		assertThat(formatDefault().host, is(InetAddress.getLocalHost()
				.getHostName()));
	}

	@Test
	public void itGrabsTheCurrentTime() {
		System.out.println("TODO: itGrabsTheCurrentTime");
	}

	@Test
	public void itIncludesGivenCustomData() {
		FormattedException formatted;
		try {
			throw new RuntimeException();
		} catch (Throwable t) {
			HashMap<String, Object> custom = new HashMap<String, Object>();
			custom.put("user_id", 1);
			formatted = exceptionFormatter.format(t, NO_DETAIL, custom);
			assertThat((Integer) formatted.customData.get("user_id"), is(1));
		}
	}

	@Test
	public void itIncludesExtraDetail() {
		FormattedException formatted;
		try {
			throw new RuntimeException();
		} catch (Throwable t) {
			YellerExtraDetail detail = NO_DETAIL
					.withApplicationEnvironment("production")
					.withUrl("http://example.com/error")
					.withLocation("UserServlet");
			formatted = exceptionFormatter.format(t, detail, NO_CUSTOM_DATA);
			assertThat(formatted.applicationEnvironment, is("production"));
			assertThat(formatted.url, is("http://example.com/error"));
			assertThat(formatted.location, is("UserServlet"));
		}
	}

	@Test
	public void itIncludesIfInAppInTheStacktrace() {
		FormattedException formatted;
		ExceptionFormatter formatterWithPackage = new ExceptionFormatter(
				"com.yellerapp");
		try {
			throw new RuntimeException();
		} catch (Throwable t) {
			formatted = formatterWithPackage.format(t, NO_DETAIL,
					NO_CUSTOM_DATA);
			assertThat(
					(Boolean) ((java.util.Map<String, Object>) formatted.stackTrace
							.get(0).get(3)).get("in-app"),
					is(new Boolean(true)));
		}
	}

	@Test
	public void itIncludesClientVersionIfPassed() {
		FormattedException formatted;
		final String newClientVersion = "test-client-version: 1.0.0";
		ExceptionFormatter formatterWithPackage = new ExceptionFormatter(
				"com.yellerapp");
		try {
			throw new RuntimeException();
		} catch (Throwable t) {
			formatted = formatterWithPackage.format(t, NO_DETAIL.withClientVersion(newClientVersion),
					NO_CUSTOM_DATA);
			assertThat(
					formatted.clientVersion,
					is(newClientVersion));
		}
	}

	@Test
	public void itDefaultsClientVersionToGlobal() {
		FormattedException formatted;
		ExceptionFormatter formatterWithPackage = new ExceptionFormatter(
				"com.yellerapp");
		try {
			throw new RuntimeException();
		} catch (Throwable t) {
			formatted = formatterWithPackage.format(t, NO_DETAIL,
					NO_CUSTOM_DATA);
			assertThat(
					formatted.clientVersion,
					is(Version.VERSION));
		}
	}


	@Test
	public void itIncludesNestedExceptionsTest() {
		FormattedException formatted;
		ExceptionFormatter formatterWithPackage = new ExceptionFormatter(
				"com.yellerapp");
		try {
			try {
				throw new RuntimeException();
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		} catch (Throwable t) {
			formatted = formatterWithPackage.format(t, NO_DETAIL,
					NO_CUSTOM_DATA);
			assertThat(
					formatted.causes.size(),
					is(1));
		}
	}

	@Test
	public void nestedExceptionsUsesTheRootCauseAsTheToplevel() {
		FormattedException formatted;
		ExceptionFormatter formatterWithPackage = new ExceptionFormatter(
				"com.yellerapp");
		try {
			try {
				throw new RuntimeException("root");
			} catch (Throwable t) {
				throw new RuntimeException("child", t);
			}
		} catch (Throwable t) {
			formatted = formatterWithPackage.format(t, NO_DETAIL,
					NO_CUSTOM_DATA);
			assertThat(
					formatted.message,
					is("root"));
		}
	}
}
