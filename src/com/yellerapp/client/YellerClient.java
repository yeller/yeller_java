package com.yellerapp.client;

import java.util.Map;

public interface YellerClient {
	public void report(Throwable t);

	public void close();

	public YellerClient setUrls(String... urls) throws Exception;

	YellerHTTPClient setErrorHandler(YellerErrorHandler handler);

	public void report(Throwable t, Map<String, Object> custom);

	public void report(Throwable t, YellerExtraDetail extraDetail,
			Map<String, Object> custom);

	public void report(Throwable t, YellerExtraDetail extraDetail);
}
