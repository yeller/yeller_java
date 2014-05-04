package com.yellerapp.test.endtoend;

import org.junit.Test;

import com.yellerapp.client.YellerClient;
import com.yellerapp.client.YellerHTTPClient;

public class ActualApiRequestTest {
	private static String API_KEY = "mYlLzReGXgggS7YYvmvX9mH_8u3YlNgPBbKyjxxu4iE";
	@Test
	public void testAgainstActualApi() throws Exception {
		YellerClient client = YellerHTTPClient.withApiKey(API_KEY);
		try {
			throw new RuntimeException();
		} catch (Throwable t) {
			client.report(t);
		}
	}
}
