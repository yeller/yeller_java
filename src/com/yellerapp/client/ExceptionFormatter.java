package com.yellerapp.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExceptionFormatter {

	public FormattedException format(Throwable t, HashMap<String,Object> custom) {
		FormattedException e = new FormattedException();
		e.type = t.getClass().getSimpleName();
		e.message = t.getMessage();
		if (e.message != null) {
			e.message = e.message.substring(0, Math.min(1000, e.message.length()));
			System.out.println(e.message.length());
		}

		e.stackTrace = formatStackTrace(t.getStackTrace());
		try {
			e.host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException u) {
		}
		e.customData = custom;
		return e;
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