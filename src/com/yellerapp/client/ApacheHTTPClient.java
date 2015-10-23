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

	public ApacheHTTPClient(ObjectMapper mapper) {
		this.http = buildHTTPClient();
		this.mapper = mapper;
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

	public SuccessResponse post(String url, FormattedException exception)
			throws IOException, AuthorizationException {
		HttpPost post = new HttpPost(url);
		try {
			final String encoded = encode(exception);
			post.setEntity(new StringEntity(encoded));
			HttpResponse response = http.execute(post);
			if (response.getStatusLine().getStatusCode() == 401) {
				throw new AuthorizationException("API key was invalid.");
			}
			return httpResponseToYellerResponse(response);
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

	public static SuccessResponse httpResponseToYellerResponse(HttpResponse response) {
		Header fingerprintHeader = response.getFirstHeader(YELLER_FINGERPRINT_HEADER_NAME);
		Header urlHeader = response.getFirstHeader(YELLER_URL_HEADER_NAME);
		if (fingerprintHeader != null && urlHeader != null) {
			return new SuccessResponse(fingerprintHeader.getValue(), urlHeader.getValue());
		} else {
			return new SuccessResponse("missing-fingerprint", "missing-url");
		}
	}

}
