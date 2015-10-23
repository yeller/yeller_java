package com.yellerapp.client;

public class STDOUTSuccessHandler implements YellerSuccessHandler {
    public void errorSent(YellerSuccessResponse response) {
        System.out.println("Yeller: successfully reported error url=\"" + response.url + "\" type=\"" + response.formattedException.type + "\" message=\"" + response.formattedException.message + "\"");
    }
}
