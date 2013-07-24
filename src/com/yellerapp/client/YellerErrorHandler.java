package com.yellerapp.client;

public interface YellerErrorHandler {

	void reportAuthError(String backend, Throwable e);

	void reportIOError(String backend, Throwable e);
}
