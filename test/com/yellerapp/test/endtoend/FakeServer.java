package com.yellerapp.test.endtoend;

import java.io.IOException;
import java.io.InputStream;
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

	private final String url;
	private final String path;
	List<String> receivedExceptions = Collections.synchronizedList(new ArrayList<String>());

	private HttpServer server;

	public FakeServer(String url, String path) {
		this.url = url;
		this.path = path;
	}

	public void shouldHaveRecordedExceptionWithType(String expectedType) {
		boolean received = false;
		for(String type : receivedExceptions) {
			if (type.equals(expectedType)) {
				received = true;
			}
		}
		Assert.assertTrue("expected to have received an exception of type " + expectedType, received);
	}

	public void start() throws IOException {
		String[] split = url.split(":");
		int port = Integer.parseInt(split[1]);
		InetSocketAddress address = new InetSocketAddress(split[0], port);
		this.server = HttpServer.create(address, 20);
		this.server.createContext(path, new YellerServerHandler(receivedExceptions));
	}
	
	static class YellerServerHandler implements HttpHandler {

		private final List<String> receivedExceptions;

		public YellerServerHandler(List<String> receivedExceptions) {
			this.receivedExceptions = receivedExceptions;
		}

		@Override
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
		}
		
	}
}
