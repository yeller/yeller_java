package com.yellerapp.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	@JsonSerialize
	@JsonProperty("causes")
	public List<Cause> causes;

	@JsonIgnore
	public Throwable originalError;

	@Override
	public String toString() {
		return "FormattedException{" +
				"type='" + type + '\'' +
				", message='" + message + '\'' +
				", stackTrace=" + stackTrace +
				", host='" + host + '\'' +
				", customData=" + customData +
				", clientVersion='" + clientVersion + '\'' +
				", applicationEnvironment='" + applicationEnvironment + '\'' +
				", url='" + url + '\'' +
				", location='" + location + '\'' +
				", dateTime='" + dateTime + '\'' +
				", causes=" + causes +
				'}';
	}

	public static class Cause {
		@JsonSerialize
		@JsonProperty("type")
		public String type;

		@JsonSerialize
		@JsonProperty("message")
		public String message;

		@JsonSerialize
		@JsonProperty("stacktrace")
		public ArrayList<ArrayList<Object>> stackTrace;

		public Cause(String type, String message, ArrayList<ArrayList<Object>> stackTrace) {
			this.type = type;
			this.message = message;
			this.stackTrace = stackTrace;
		}

		@Override
		public String toString() {
			return "Cause{" +
					"type='" + type + '\'' +
					", message='" + message + '\'' +
					", stackTrace=" + stackTrace +
					'}';
		}
	}
}
