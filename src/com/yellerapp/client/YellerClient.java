package com.yellerapp.client;

public interface YellerClient {
	public void report(Throwable t);

	public YellerClient setUrls(String... urls);

	YellerHTTPClient setErrorHandler(YellerErrorHandler handler);
}
