package com.yellerapp.client;

public class SuccessResponse {
    public final String fingerprint;
    public final String url;

    public SuccessResponse(String fingerprint, String url) {
        this.fingerprint = fingerprint;
        this.url = url;
    }
}
