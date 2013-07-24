package com.yellerapp.client;

public class STDERRErrorHandler implements YellerErrorHandler {

	@Override
	public void reportYellerError(String string, Throwable e) {
		System.err.println(string);
		e.printStackTrace(System.err);
	}

}
