package com.yellerapp.client;

import java.io.IOException;

public interface HTTPClient {
	void post(String url, FormattedException exception) throws IOException,
			AuthorizationException;

	void close();
}
