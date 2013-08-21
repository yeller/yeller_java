package com.yellerapp.test.endtoend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class FakeServer {

	private final String path;
	List<HashMap<String, Object>> receivedExceptions = Collections
			.synchronizedList(new ArrayList<HashMap<String, Object>>());

	private HttpServer server;
	private final String host;
	private final int socket;

	public FakeServer(String host, int socket, String path) {
		this.host = host;
		this.socket = socket;
		this.path = path;
	}

	public void shouldHaveRecordedExceptionWithType(final String expectedType) {
		Checker checker = new Checker() {
			public boolean check(HashMap<String, Object> exception) {
				return exception.get("type").equals(expectedType);
			}
		};

		boolean received = shouldHaveRecordedException(0, checker);
		Assert.assertTrue("expected to have received an exception of type "
				+ expectedType, received);
	}

	public static interface Checker {
		public boolean check(HashMap<String, Object> exception);
	}

	public boolean shouldHaveRecordedException(int pollCount, Checker checker) {
		boolean received = false;
		for (HashMap<String, Object> o : receivedExceptions) {
			received = checker.check(o);
		}
		if (received) {
			return received;
		} else if (pollCount < 5) {
			try {
				Thread.sleep(100 * pollCount);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			shouldHaveRecordedException(pollCount + 1, checker);
		}
		return received;
	}

	public void start() throws IOException {
		int port = this.socket;
		InetSocketAddress address = new InetSocketAddress(host, port);
		this.server = HttpServer.create(address, 20);
		this.server.createContext(path, new YellerServerHandler(
				receivedExceptions));
		this.server.start();
	}

	static class YellerServerHandler implements HttpHandler {

		private final List<HashMap<String, Object>> receivedExceptions;

		public YellerServerHandler(
				List<HashMap<String, Object>> receivedExceptions) {
			this.receivedExceptions = receivedExceptions;
		}

		public void handle(HttpExchange http) throws IOException {
			InputStream requestBody = http.getRequestBody();
			JsonFactory factory = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(factory);
			TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
			};
			HashMap<String, Object> o = mapper.readValue(requestBody, typeRef);
			receivedExceptions.add(o);
			http.sendResponseHeaders(200, "success".length());
			OutputStream os = http.getResponseBody();
			os.write("success".getBytes());
			http.close();
		}

	}

	public void stop() {
		this.server.stop(0);
	}

	public void shouldHaveRecordedExceptionWithCustomData(final String key,
			final int i) {
		Checker checker = new Checker() {
			public boolean check(HashMap<String, Object> exception) {
				HashMap<String, Object> hashMap = (HashMap<String, Object>) exception
						.get("custom-data");
				return hashMap.get(key).equals(new Integer(i));
			}
		};

		boolean received = shouldHaveRecordedException(0, checker);
		Assert.assertTrue(
				"expected to receive an exception with custom data key: " + key
						+ " with value " + i, received);
	}
}
