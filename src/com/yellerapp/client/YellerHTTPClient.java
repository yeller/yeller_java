package com.yellerapp.client;

public class YellerHTTPClient implements YellerClient {

	private String[] urls;
	private final String apiKey;
	private final ExceptionFormatter formatter;
	private Reporter reporter;

	public YellerHTTPClient(String apiKey) {
		this.apiKey = apiKey;
		this.formatter = new ExceptionFormatter();
		this.reporter = new Reporter(apiKey);
	}

	public static YellerClient withApiKey(String apiKey) {
		return new YellerHTTPClient(apiKey);
	}

	@Override
	public void report(Throwable t) {
		FormattedException formattedException = formatter.format(t);
		reporter.report(formattedException);
	}

	@Override
	public YellerClient setUrls(String... urls) {
		this.urls = urls;
		this.reporter = new Reporter(apiKey, urls);
		return this;
	}

}
