package com.yellerapp.client;

import java.io.IOException;

import org.apache.http.Header;
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
	public static final String YELLER_FINGERPRINT_HEADER_NAME = "X-Yeller-Fingerprint";
	private static final String YELLER_URL_HEADER_NAME = "X-Yeller-URL";

	private final HttpClient http;
	private final ObjectMapper mapper;
	private PoolingHttpClientConnectionManager connectionManager;

	public static HTTPClient fromObjectMapper(ObjectMapper mapper) {
		PoolingHttpClientConnectionManager connectionManager = buildConnectionManager();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return new ApacheHTTPClient(buildHTTPClient(connectionManager), connectionManager, mapper);
	}

	private static PoolingHttpClientConnectionManager buildConnectionManager() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(CONNECTION_MAX_LIMIT);
		connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
		return connectionManager;
	}

	public ApacheHTTPClient(HttpClient httpClient, PoolingHttpClientConnectionManager connectionManager, ObjectMapper mapper) {
		this.http = httpClient;
		this.connectionManager = connectionManager;
		this.mapper = mapper;
	}

	public static HttpClient buildHTTPClient(PoolingHttpClientConnectionManager connectionManager) {
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(2000).setConnectTimeout(2000).build();
		return HttpClients.custom().setConnectionManager(connectionManager)
				.setDefaultRequestConfig(requestConfig).build();
	};

	public YellerSuccessResponse post(String url, FormattedException exception)
			throws IOException, AuthorizationException {
		HttpPost post = new HttpPost(url);
		try {
			final String encoded = encode(exception);
			post.setEntity(new StringEntity(encoded));
			HttpResponse response = http.execute(post);
			if (response.getStatusLine().getStatusCode() == 401) {
				throw new AuthorizationException("API key was invalid.");
			}
			return httpResponseToYellerResponse(response, exception);
		} finally {
			post.releaseConnection();
		}
	}

	public void close() {
		this.connectionManager.close();
	};

	protected String encode(FormattedException exception)
			throws JsonProcessingException {
		return this.mapper.writeValueAsString(exception);
	}

	protected YellerSuccessResponse httpResponseToYellerResponse(HttpResponse response, FormattedException exception) {
		Header fingerprintHeader = response.getFirstHeader(YELLER_FINGERPRINT_HEADER_NAME);
		Header urlHeader = response.getFirstHeader(YELLER_URL_HEADER_NAME);
		if (fingerprintHeader != null && urlHeader != null) {
			return new YellerSuccessResponse(fingerprintHeader.getValue(), urlHeader.getValue(), exception);
		} else {
			return new YellerSuccessResponse("missing-fingerprint", "missing-url", exception);
		}
	}
}
