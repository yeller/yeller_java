package com.yellerapp.test.endtoend;

import java.io.IOException;

import org.junit.Test;

import com.yellerapp.client.YellerClient;
import com.yellerapp.client.YellerHTTPClient;

public class EndToEndTest {
	@Test
	public void itReportsAnExceptionToYeller() throws IOException {
		FakeServer server = new FakeServer("localhost", 6666, "/sample-api-key");
		server.start();
		YellerClient client = YellerHTTPClient.withApiKey("sample-api-key").setUrls("http://localhost:6666");
		try {
			throw new RuntimeException();
		} catch (Throwable t) {
			client.report(t);
		}
		server.shouldHaveRecordedExceptionWithType("RuntimeException");
		server.stop();
	}

	@Test
	public void itCanReportAnExceptionWithExtraDetail () {
		// attach: custom data
		// attach: location
		// attach: application-environment
		// attach: custom time
		// attach: custom type
		// attach: custom message
		System.out.println("TODO: itCanReportAnExceptionWithExtraDetail");
	}
}