package com.yellerapp.client;

public class Reporter {

	private final String apiKey;
	private final String[] urls;
	private HTTPClient http;
	private int currentBackend = 0;
	private YellerErrorHandler handler;

	public Reporter(String apiKey, String[] urls, HTTPClient http,
			YellerErrorHandler handler) {
		this.apiKey = apiKey;
		this.urls = urls;
		this.http = http;
		this.handler = handler;
	}

	public void report(FormattedException exception) {
		if (exception.applicationEnvironment != null
				&& (exception.applicationEnvironment.equals("test")
				|| exception.applicationEnvironment.equals("development"))) {
			// ignore
		} else {
			report(exception, 0);
		}
	}

	protected void report(FormattedException exception, int retryCount) {
		if (retryCount > (2 * urls.length)) {
			return;
		} else {
			try {
				http.post(this.urls[this.currentBackend] + "/" + this.apiKey,
						exception);
				this.cycleBackend();
			} catch (AuthorizationException e) {
				this.handler.reportAuthError(this.urls[this.currentBackend], e);
			} catch (Exception e) {
				this.handler.reportIOError(this.urls[this.currentBackend], e);
				this.cycleBackend();
				report(exception, retryCount + 1);
			}
		}
	}

	protected synchronized void cycleBackend() {
		this.currentBackend = (this.currentBackend + 1) % urls.length;
	}
}
