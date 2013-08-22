package com.yellerapp.client;

import java.util.HashMap;

public class YellerHTTPClient implements YellerClient {
	private static final HashMap<String, Object> NO_CUSTOM_DATA = new HashMap<String, Object>();
	private static final YellerExtraDetail NO_EXTRA_DETAIL = new YellerExtraDetail();

	public static String[] DEFAULT_URLS = new String[] {
		"http://api1.yellerapp.com",
		"http://api2.yellerapp.com",
		"http://api3.yellerapp.com",
		"http://api4.yellerapp.com",
		"http://api5.yellerapp.com"
	};

	private final String apiKey;
	private String[] urls;
	private YellerErrorHandler errorHandler = new STDERRErrorHandler();
	private final ExceptionFormatter formatter;
	private Reporter reporter;
	private final HTTPClient http;

	public YellerHTTPClient(String apiKey) {
		this.apiKey = apiKey;
		this.formatter = new ExceptionFormatter();
		this.http = new ApacheHTTPClient();
		this.reporter = new Reporter(apiKey, DEFAULT_URLS, http, errorHandler);
	}

	public static YellerHTTPClient withApiKey(String apiKey) {
		return new YellerHTTPClient(apiKey);
	}

	public void report(Throwable t) {
		report(t, NO_CUSTOM_DATA);
	}

	public void report(Throwable t, HashMap<String, Object> custom) {
		FormattedException formattedException = formatter.format(t, NO_EXTRA_DETAIL, custom);
		reporter.report(formattedException);
	}

	public void report(Throwable t, YellerExtraDetail extraDetail,
			HashMap<String, Object> custom) {
		FormattedException formattedException = formatter.format(t, extraDetail, custom);
		reporter.report(formattedException);
	}

	public void report(Throwable t, YellerExtraDetail extraDetail) {
		FormattedException formattedException = formatter.format(t, extraDetail, NO_CUSTOM_DATA);
		reporter.report(formattedException);
	}


	public YellerHTTPClient setUrls(String... urls) {
		this.reporter = new Reporter(apiKey, urls, http, errorHandler);
		this.urls = urls;
		return this;
	}

	public YellerHTTPClient setErrorHandler(YellerErrorHandler handler) {
		this.reporter = new Reporter(apiKey, this.urls, http, handler);
		return this;
	}

}
