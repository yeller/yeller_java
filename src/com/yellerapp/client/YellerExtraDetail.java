package com.yellerapp.client;

import java.util.Date;

public class YellerExtraDetail {
	public final String applicationEnvironment;
	public final String url;
	public final String location;
	public final Date dateTime;
    public final String clientVersion;

	public YellerExtraDetail(String applicationEnvironment, String url, String location, Date dateTime, String clientVersion) {
		this.applicationEnvironment = applicationEnvironment;
		this.url = url;
		this.location = location;
		this.dateTime = dateTime;
        this.clientVersion = clientVersion;
	}

	public YellerExtraDetail() {
		this.applicationEnvironment = "production";
		this.url = null;
		this.location = null;
		this.dateTime = null;
        this.clientVersion = null;
	}

	public YellerExtraDetail withApplicationEnvironment(String newApplicationEnvironment) {
		return new YellerExtraDetail(newApplicationEnvironment, this.url, this.location, this.dateTime, this.clientVersion);
	}

	public YellerExtraDetail withUrl(String newUrl) {
		return new YellerExtraDetail(this.applicationEnvironment, newUrl, this.location, this.dateTime, this.clientVersion);
	}

	public YellerExtraDetail withLocation(String newLocation) {
		return new YellerExtraDetail(this.applicationEnvironment, this.url, newLocation, this.dateTime, this.clientVersion);
	}

	public YellerExtraDetail withDate(Date newDate) {
		return new YellerExtraDetail(this.applicationEnvironment, this.url, this.location, newDate, this.clientVersion);
	}

	public YellerExtraDetail withClientVersion(String newClientVersion) {
		return new YellerExtraDetail(this.applicationEnvironment, this.url, this.location, this.dateTime, newClientVersion);
	}

}
