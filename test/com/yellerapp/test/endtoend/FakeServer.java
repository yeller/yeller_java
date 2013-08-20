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
	List<String> receivedExceptions = Collections.synchronizedList(new ArrayList<String>());

	private HttpServer server;
	private final String host;
	private final int socket;

	public FakeServer(String host, int socket, String path) {
		this.host = host;
		this.socket = socket;
		this.path = path;
	}

	public void shouldHaveRecordedExceptionWithType(String expectedType) {
		boolean received = shouldHaveRecordedExceptionWithType(expectedType, 0);
		Assert.assertTrue("expected to have received an exception of type " + expectedType, received);
	}

	public boolean shouldHaveRecordedExceptionWithType(String expectedType, int pollCount) {
		boolean received = false;
		for(String type : receivedExceptions) {
			if (type.equals(expectedType)) {
				received = true;
			}
		}
		if (received) {
			return received;
		} else if (pollCount < 5) {
			try {
				Thread.sleep(100 * pollCount);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			shouldHaveRecordedExceptionWithType(expectedType, pollCount + 1);
		}
		return received;
	}

	public void start() throws IOException {
		int port = this.socket;
		InetSocketAddress address = new InetSocketAddress(host, port);
		this.server = HttpServer.create(address, 20);
		this.server.createContext(path, new YellerServerHandler(receivedExceptions));
		this.server.start();
	}
	
	static class YellerServerHandler implements HttpHandler {

		private final List<String> receivedExceptions;

		public YellerServerHandler(List<String> receivedExceptions) {
			this.receivedExceptions = receivedExceptions;
		}

		public void handle(HttpExchange http) throws IOException {
			InputStream requestBody = http.getRequestBody();
		    JsonFactory factory = new JsonFactory(); 
		    ObjectMapper mapper = new ObjectMapper(factory); 
		    TypeReference<HashMap<String,Object>> typeRef 
		          = new TypeReference< 
		                 HashMap<String,Object> 
		               >() {}; 
		    HashMap<String,Object> o 
		         = mapper.readValue(requestBody, typeRef); 
		    receivedExceptions.add((String) o.get("type"));
		    http.sendResponseHeaders(200, "success".length());
		    OutputStream os = http.getResponseBody();
		    os.write("success".getBytes());
		    http.close();
		}
		
	}

	public void stop() {
		this.server.stop(0);
	}
}
