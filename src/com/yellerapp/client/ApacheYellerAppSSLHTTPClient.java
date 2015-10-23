package com.yellerapp.client;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;

public class ApacheYellerAppSSLHTTPClient {
	private static final String CERTIFICATE_PATH = "/ca.crt";
	private static final int CONNECTION_MAX_LIMIT = 64;
	private static final int MAX_CONNECTIONS_PER_ROUTE = 5;

	public static HTTPClient fromObjectMapper(ObjectMapper mapper) throws Exception {
		PoolingHttpClientConnectionManager connectionManager = buildConnectionManager();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return new ApacheHTTPClient(makeSecuredHTTPClient(connectionManager), connectionManager, mapper);
	}

	private static PoolingHttpClientConnectionManager buildConnectionManager() {
		PoolingHttpClientConnectionManager connectionManager  = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(CONNECTION_MAX_LIMIT);
		connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
		return connectionManager;
	}

	private static HttpClient makeSecuredHTTPClient(PoolingHttpClientConnectionManager connectionManager) throws Exception {
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(2000).setConnectTimeout(2000).build();
		SSLContext context = getComodoSSLContext();
		return HttpClients.custom().setConnectionManager(connectionManager)
				.setSslcontext(context).setDefaultRequestConfig(requestConfig)
				.build();
	}

	private static SSLContext getComodoSSLContext() throws Exception {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate ca = cf
				.generateCertificate(getResourceAsStream(CERTIFICATE_PATH));
		KeyStore ks = KeyStore.getInstance("jks");
		ks.load(null, null);
		ks.setCertificateEntry("ca", ca);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
		tmf.init(ks);

		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, tmf.getTrustManagers(), null);
		return context;
	}

	private static InputStream getResourceAsStream(String certificatePath) {
		return ApacheYellerAppSSLHTTPClient.class
				.getResourceAsStream(certificatePath);
	}
}