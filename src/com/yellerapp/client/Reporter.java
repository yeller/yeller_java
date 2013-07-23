package com.yellerapp.client;

public class Reporter {

	private final String apiKey;
	private final String[] urls;

	public Reporter(String apiKey) {
		this.apiKey = apiKey;
		this.urls = new String[] { "api.yellerapp.com" };
	}

	public Reporter(String apiKey, String[] urls) {
		this.apiKey = apiKey;
		this.urls = urls;
	}

	public void report(FormattedException formattedException) {
	}

}
