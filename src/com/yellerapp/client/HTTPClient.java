package com.yellerapp.client;

import java.io.IOException;

public interface HTTPClient {
	YellerSuccessResponse post(String url, FormattedException exception) throws IOException,
			AuthorizationException;

	void close();
}
