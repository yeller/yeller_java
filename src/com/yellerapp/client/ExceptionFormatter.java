package com.yellerapp.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ExceptionFormatter {

	public FormattedException format(Throwable t) {
		FormattedException e = new FormattedException();
		e.type = t.getClass().getSimpleName();
		e.message = t.getMessage();
		e.stackTrace = formatStackTrace(t.getStackTrace());
		try {
			e.host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException u) {
		}
		return e;
	}

	private ArrayList<ArrayList<String>> formatStackTrace(StackTraceElement[] stackTrace) {
		ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();
		for(StackTraceElement elem : stackTrace) {
			ArrayList<String> line = new ArrayList<String>();
			line.add(elem.getFileName());
			line.add(Integer.toString(elem.getLineNumber()));
			line.add(elem.getClassName() + "." + elem.getMethodName());
			lines.add(line);
		}
		return lines;
	}
}