package com.yellerapp.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class ExceptionFormatter {
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");

	public FormattedException format(Throwable t, YellerExtraDetail detail, HashMap<String,Object> custom) {
		FormattedException e = new FormattedException();
		e.type = t.getClass().getSimpleName();
		e.message = t.getMessage();
		if (e.message != null) {
			e.message = e.message.substring(0, Math.min(1000, e.message.length()));
		}

		e.stackTrace = formatStackTrace(t.getStackTrace());
		try {
			e.host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException u) {
		}
		e.customData = custom;
		e.applicationEnvironment = detail.applicationEnvironment;
		e.url = detail.url;
		e.location = detail.location;
		if (detail.dateTime != null) {
			e.dateTime = formatDate(detail.dateTime);
		} else {
			e.dateTime = formatDate(new Date());
		}
		return e;
	}

	private String formatDate(Date date) {
		SIMPLE_DATE_FORMAT.setTimeZone(TIME_ZONE);
		return SIMPLE_DATE_FORMAT.format(date);
	}

	private ArrayList<ArrayList<String>> formatStackTrace(StackTraceElement[] stackTrace) {
		ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();
		for(int i=0; i<stackTrace.length; i++) {
			if (i >= 1000) {
				break;
			}
			StackTraceElement elem = stackTrace[i];
			ArrayList<String> line = new ArrayList<String>();
			line.add(elem.getFileName());
			line.add(Integer.toString(elem.getLineNumber()));
			line.add(elem.getClassName() + "." + elem.getMethodName());
			lines.add(line);
		}
		return lines;
	}
}