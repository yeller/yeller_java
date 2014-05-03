package com.yellerapp.client;

import java.util.Map;
import java.util.HashMap;

public class YellerHTTPClient implements YellerClient {
	private static final Map<String, Object> NO_CUSTOM_DATA = new HashMap<String, Object>();
	private static final YellerExtraDetail NO_EXTRA_DETAIL = new YellerExtraDetail();

	public static String[] DEFAULT_URLS = new String[] {
		"http://collector1.yellerapp.com",
		"http://collector2.yellerapp.com",
		"http://collector3.yellerapp.com",
		"http://collector4.yellerapp.com",
		"http://collector5.yellerapp.com"
	};

	private final String apiKey;
	private String[] urls = DEFAULT_URLS;
	private YellerErrorHandler errorHandler = new STDERRErrorHandler();
	private final ExceptionFormatter formatter = new ExceptionFormatter();
	private Reporter reporter;
	private final HTTPClient http = new ApacheHTTPClient();

	public YellerHTTPClient(String apiKey) {
		this.apiKey = apiKey;
		this.reporter = new Reporter(apiKey, DEFAULT_URLS, http, errorHandler);
	}

	public static YellerHTTPClient withApiKey(String apiKey) {
		return new YellerHTTPClient(apiKey);
	}

	public void report(Throwable t) {
		report(t, NO_CUSTOM_DATA);
	}

	public void report(Throwable t, Map<String, Object> custom) {
		FormattedException formattedException = formatter.format(t, NO_EXTRA_DETAIL, custom);
		reporter.report(formattedException);
	}

	public void report(Throwable t, YellerExtraDetail extraDetail,
			Map<String, Object> custom) {
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
