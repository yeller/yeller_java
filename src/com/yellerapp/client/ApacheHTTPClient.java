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
    private static final int CONNECTION_MAX_LIMIT = 64;

	private final HttpClient http;
	private final ObjectMapper mapper;

	public ApacheHTTPClient() {
		this.http = buildHTTPClient();
		http.getParams().setParameter("http.socket.timeout", new Integer(2000));
		this.mapper = new ObjectMapper();
		this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

    public HttpClient buildHTTPClient() {
		RequestConfig requestConfig = RequestConfig.custom().
				setSocketTimeout(2000).
				setConnectTimeout(2000).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(CONNECTION_MAX_LIMIT);
		return HttpClients.custom().
				setDefaultRequestConfig(requestConfig).
				build();
    };

	public void post(String url, FormattedException exception)
			throws IOException, AuthorizationException {
		HttpPost post = new HttpPost(url);
		final String encoded = encode(exception);
		post.setEntity(new StringEntity(encoded));
		HttpResponse response = http.execute(post);
		if (response.getStatusLine().getStatusCode() == 401) {
			throw new AuthorizationException("API key was invalid.");
		}
	}

	private String encode(FormattedException exception) throws JsonProcessingException {
		return this.mapper.writeValueAsString(exception);
	}
}
