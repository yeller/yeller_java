package com.yellerapp.client;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;

public class ApacheYellerAppSSLHTTPClient implements HTTPClient {
	private static final String CERTIFICATE_PATH = "/ca.crt";
    private static final int CONNECTION_MAX_LIMIT = 64;
	private final HttpClient http;
	private final ObjectMapper mapper;

	public ApacheYellerAppSSLHTTPClient() throws Exception {
		this.http = makeSecuredHTTPClient();
		this.mapper = new ObjectMapper();
		this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

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

	private HttpClient makeSecuredHTTPClient() throws Exception {
		RequestConfig requestConfig = RequestConfig.custom().
				setSocketTimeout(2000).
				setConnectTimeout(2000).build();
		SSLContext context = getComodoSSLContext();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(CONNECTION_MAX_LIMIT);
		return HttpClients.custom().
				setSslcontext(context).
				setDefaultRequestConfig(requestConfig).
				build();
	}

	private SSLContext getComodoSSLContext() throws Exception {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate ca = cf.generateCertificate(getResourceAsStream(CERTIFICATE_PATH));
		KeyStore ks = KeyStore.getInstance("jks");
		ks.load(null, null);
		ks.setCertificateEntry("ca", ca);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
		tmf.init(ks);

		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, tmf.getTrustManagers(), null);
		return context;
	}

	private InputStream getResourceAsStream(String certificatePath) {
		return ApacheYellerAppSSLHTTPClient.class.getResourceAsStream(certificatePath);
	}

	private String encode(FormattedException exception) throws JsonProcessingException {
		return this.mapper.writeValueAsString(exception);
	}

}
