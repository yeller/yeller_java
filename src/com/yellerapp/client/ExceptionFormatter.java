package com.yellerapp.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ExceptionFormatter {
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd'T'HHmmss'Z'");
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");
	protected final String[] applicationPackages;

	public ExceptionFormatter(String... applicationPackages) {
		this.applicationPackages = applicationPackages;
	}

	public FormattedException format(Throwable t, YellerExtraDetail detail,
			Map<String, Object> custom) {
		FormattedException e = new FormattedException();
		e.type = t.getClass().getSimpleName();
		e.message = t.getMessage();
		if (e.message != null) {
			e.message = e.message.substring(0,
					Math.min(1000, e.message.length()));
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

	private ArrayList<ArrayList<Object>> formatStackTrace(
			StackTraceElement[] stackTrace) {
		ArrayList<ArrayList<Object>> lines = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < stackTrace.length; i++) {
			if (i >= 1000) {
				break;
			}
			StackTraceElement elem = stackTrace[i];
			ArrayList<Object> line = new ArrayList<Object>();
			line.add(elem.getFileName());
			line.add(Integer.toString(elem.getLineNumber()));
			line.add(elem.getClassName() + "." + elem.getMethodName());
			if (this.applicationPackages != null) {
				boolean inApp = false;
				for (String appPackage : applicationPackages) {
					if (elem.getClassName().startsWith(appPackage)) {
						inApp = true;
					}
				}
				if (inApp) {
					Map<String, Object> opts = new HashMap<String, Object>();
					opts.put("in-app", new Boolean(true));
					line.add(opts);
				}
			}
			lines.add(line);
		}
		return lines;
	}
}
