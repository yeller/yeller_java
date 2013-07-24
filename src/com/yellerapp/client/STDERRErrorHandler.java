package com.yellerapp.client;

public class STDERRErrorHandler implements YellerErrorHandler {

	@Override
	public void reportAuthError(String backend, Throwable e) {
		System.err.println(backend);
		e.printStackTrace(System.err);
	}

	@Override
	public void reportIOError(String backend, Throwable e) {
		// purposefully do nothing
	}

}
