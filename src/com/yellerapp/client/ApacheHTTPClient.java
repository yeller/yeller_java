package com.yellerapp.client;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApacheHTTPClient implements HTTPClient {
	private static final int MAX_CONNECTIONS_PER_ROUTE = 5;
	private static final int CONNECTION_MAX_LIMIT = 64;

	private final HttpClient http;
	private final ObjectMapper mapper;
	private PoolingHttpClientConnectionManager connectionManager;

	public ApacheHTTPClient() {
		this.http = buildHTTPClient();
		this.mapper = new ObjectMapper();
		this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	public HttpClient buildHTTPClient() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(2000).setConnectTimeout(2000).build();
		this.connectionManager = new PoolingHttpClientConnectionManager();
		this.connectionManager.setMaxTotal(CONNECTION_MAX_LIMIT);
		this.connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
		return HttpClients.custom().setConnectionManager(connectionManager)
				.setDefaultRequestConfig(requestConfig).build();
	};

	public void post(String url, FormattedException exception)
			throws IOException, AuthorizationException {
		HttpPost post = new HttpPost(url);
		try {
			final String encoded = encode(exception);
			post.setEntity(new StringEntity(encoded));
			HttpResponse response = http.execute(post);
			if (response.getStatusLine().getStatusCode() == 401) {
				throw new AuthorizationException("API key was invalid.");
			}
		} finally {
			post.releaseConnection();
		}
	}

	public void close() {
		this.connectionManager.close();
	};

	private String encode(FormattedException exception)
			throws JsonProcessingException {
		return this.mapper.writeValueAsString(exception);
	}
}
