package com.yellerapp.client;

public class YellerHTTPClient implements YellerClient {
	public static String[] DEFAULT_URLS = new String[] {
		"http://api1.yellerapp.com",
		"http://api2.yellerapp.com",
		"http://api3.yellerapp.com",
		"http://api4.yellerapp.com",
		"http://api5.yellerapp.com"
	};

	private String[] urls;
	private final String apiKey;
	private final ExceptionFormatter formatter;
	private Reporter reporter;
	private final HTTPClient http;

	public YellerHTTPClient(String apiKey) {
		this.apiKey = apiKey;
		this.formatter = new ExceptionFormatter();
		this.http = new HTTPClient() {
			@Override
			public void post(String url, FormattedException exception) {
				// TODO Auto-generated method stub
			}
		};
		this.reporter = new Reporter(apiKey, DEFAULT_URLS, http);
	}

	public static YellerHTTPClient withApiKey(String apiKey) {
		return new YellerHTTPClient(apiKey);
	}

	@Override
	public void report(Throwable t) {
		FormattedException formattedException = formatter.format(t);
		reporter.report(formattedException);
	}

	@Override
	public YellerHTTPClient setUrls(String... urls) {
		this.urls = urls;
		this.reporter = new Reporter(apiKey, urls, http);
		return this;
	}

}
