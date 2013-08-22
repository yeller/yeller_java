package com.yellerapp.client;

public class YellerExtraDetail {
	public final String applicationEnvironment;
	public final String url;
	public final String location;
	
	public YellerExtraDetail(String applicationEnvironment, String url, String location) {
		this.applicationEnvironment = applicationEnvironment;
		this.url = url;
		this.location = location;
	}

	public YellerExtraDetail() {
		this.applicationEnvironment = null;
		this.url = null;
		this.location = null;
	}

	public YellerExtraDetail withApplicationEnvironment(String newApplicationEnvironment) {
		return new YellerExtraDetail(newApplicationEnvironment, this.url, this.location);
	}

	public YellerExtraDetail withUrl(String newUrl) {
		return new YellerExtraDetail(this.applicationEnvironment, newUrl, this.location);
	}

	public YellerExtraDetail withLocation(String newLocation) {
		return new YellerExtraDetail(this.applicationEnvironment, this.url, newLocation);
	}

}
