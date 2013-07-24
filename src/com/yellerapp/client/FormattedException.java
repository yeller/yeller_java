package com.yellerapp.client;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class FormattedException {

	@JsonSerialize
	public String type;
	@JsonSerialize
	public String message;
	@JsonSerialize
	public ArrayList<ArrayList<String>> stackTrace;
	@JsonSerialize
	public String host;

}
