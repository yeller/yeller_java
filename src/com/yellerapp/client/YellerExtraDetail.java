package com.yellerapp.client;

import java.util.Date;

public class YellerExtraDetail {
	public final String applicationEnvironment;
	public final String url;
	public final String location;
	public final Date dateTime;

	public YellerExtraDetail(String applicationEnvironment, String url, String location, Date dateTime) {
		this.applicationEnvironment = applicationEnvironment;
		this.url = url;
		this.location = location;
		this.dateTime = dateTime;
	}

	public YellerExtraDetail() {
		this.applicationEnvironment = "production";
		this.url = null;
		this.location = null;
		this.dateTime = null;
	}

	public YellerExtraDetail withApplicationEnvironment(String newApplicationEnvironment) {
		return new YellerExtraDetail(newApplicationEnvironment, this.url, this.location, this.dateTime);
	}

	public YellerExtraDetail withUrl(String newUrl) {
		return new YellerExtraDetail(this.applicationEnvironment, newUrl, this.location, this.dateTime);
	}

	public YellerExtraDetail withLocation(String newLocation) {
		return new YellerExtraDetail(this.applicationEnvironment, this.url, newLocation, this.dateTime);
	}

	public YellerExtraDetail withDate(Date newDate) {
		return new YellerExtraDetail(this.applicationEnvironment, this.url, this.location, newDate);
	}

}
