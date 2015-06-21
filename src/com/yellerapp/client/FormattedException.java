package com.yellerapp.client;

import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class FormattedException {

	@JsonSerialize
	public String type;

	@JsonSerialize
	public String message;

	@JsonSerialize
	@JsonProperty("stacktrace")
	public ArrayList<ArrayList<Object>> stackTrace;

	@JsonSerialize
	public String host;

	@JsonSerialize
	@JsonProperty("custom-data")
	public Map<String, Object> customData;

	@JsonSerialize
	@JsonProperty("client-version")
	public String clientVersion;

	@JsonSerialize
	@JsonProperty("application-environment")
	public String applicationEnvironment;

	@JsonSerialize
	public String url;

	@JsonSerialize
	public String location;

	@JsonSerialize
	@JsonProperty("datetime")
	public String dateTime;
}
