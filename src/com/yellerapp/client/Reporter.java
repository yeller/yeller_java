package com.yellerapp.client;

import java.io.IOException;

public class Reporter {

	private final String apiKey;
	private final String[] urls;
	private HTTPClient http;
	private int currentBackend = 0;
	private YellerErrorHandler handler;

	public Reporter(String apiKey, String[] urls, HTTPClient http, YellerErrorHandler handler) {
		this.apiKey = apiKey;
		this.urls = urls;
		this.http = http;
		this.handler = handler;
	}

	public void report(FormattedException exception) {
		report(exception, 0);
	}

	protected void report(FormattedException exception, int retryCount) {
		if (retryCount > (2 * urls.length)) {
			return;
		} else {
			try {
				http.post(this.urls[this.currentBackend] + "/" + this.apiKey,
						exception);
				this.currentBackend = (this.currentBackend + 1) % urls.length;
			} catch (AuthorizationException e) {
				this.handler.reportAuthError(this.urls[this.currentBackend], e);
			} catch (Exception e) {
				this.handler.reportIOError(this.urls[this.currentBackend], e);
				this.currentBackend = (this.currentBackend + 1) % urls.length;
				report(exception, retryCount + 1);
			}
		}
	}
}
