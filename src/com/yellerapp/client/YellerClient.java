package com.yellerapp.client;

import java.util.HashMap;

public interface YellerClient {
	public void report(Throwable t);

	public YellerClient setUrls(String... urls);

	YellerHTTPClient setErrorHandler(YellerErrorHandler handler);

	public void report(Throwable t, HashMap<String, Object> custom);
}
