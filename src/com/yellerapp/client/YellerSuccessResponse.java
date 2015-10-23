package com.yellerapp.client;

public class YellerSuccessResponse {
    public final String fingerprint;
    public final String url;
    public final FormattedException formattedException;
    public final Throwable throwable;

    public YellerSuccessResponse(String fingerprint, String url, FormattedException exception) {
        this.fingerprint = fingerprint;
        this.url = url;
        this.formattedException = exception;
        this.throwable = exception.originalError;
    }

    @Override
    public String toString() {
        return "YellerSuccessResponse{" +
                "url='" + url + '\'' +
                ", fingerprint='" + fingerprint + '\'' +
                ", type='" + formattedException.type + '\'' +
                ", message='" + formattedException.message + '\'' +
                '}';
    }
}