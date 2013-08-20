package com.yellerapp.client;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApacheHTTPClient implements HTTPClient {
	private final HttpClient http;
	private final ObjectMapper mapper;

	public ApacheHTTPClient() {
		this.http = new DefaultHttpClient();
		http.getParams().setParameter("http.socket.timeout", new Integer(2000));
		this.mapper = new ObjectMapper();
	}

	public void post(String url, FormattedException exception)
			throws IOException, AuthorizationException {
		HttpPost post = new HttpPost(url);
		final String encoded = encode(exception);
		post.setEntity(new StringEntity(encoded));
		HttpResponse response = http.execute(post);
		if (response.getStatusLine().getStatusCode() == 401) {
			throw new AuthorizationException();
		}
	}

	private String encode(FormattedException exception) throws JsonProcessingException {
		return this.mapper.writeValueAsString(exception);
	}
}
