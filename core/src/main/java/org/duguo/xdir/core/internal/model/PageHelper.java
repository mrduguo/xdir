package org.duguo.xdir.core.internal.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.duguo.xdir.jcr.utils.JcrNodeUtils;

public class PageHelper {

	public static final Pattern DIGITAL_PREFIX=Pattern.compile("\\d+_");
	public static final Pattern PLACE_HOLDER_PATTERN = Pattern.compile("\\$\\[\\w+\\]");
	public static String PLACEHOLDER_PAGE_PATH="$[pagePath]";
	public static String PLACEHOLDER_PAGE_CONTEXT="$[pageContext]";
	public static String PLACEHOLDER_FORMAT="$[format]";
	public static final String TRUE_VALUE="true";

	public static String displayPropertyName(String propertyName) {
		Matcher matcher=DIGITAL_PREFIX.matcher(propertyName);
		if(matcher.find()){
			propertyName=propertyName.substring(matcher.end());
		}
		return JcrNodeUtils.getDisplayablePropertyName(propertyName);
	}
	
	public static String displayPropertyValue(String inputString,ModelImpl model) {
		Matcher matcher = PLACE_HOLDER_PATTERN.matcher(inputString);
		StringBuffer output = new StringBuffer();
		while (matcher.find()) {
			String match = matcher.group();
			if (PLACEHOLDER_PAGE_PATH.equals(match)) {
				matcher.appendReplacement(output, model.getPagePath());
			}else if (PLACEHOLDER_PAGE_CONTEXT.equals(match)) {
					matcher.appendReplacement(output, model.getPageContext().toString());
			}else if (PLACEHOLDER_FORMAT.equals(match)) {
				matcher.appendReplacement(output, model.getFormat());
			}
		}
		matcher.appendTail(output);
		return output.toString();
	}
	
	public static boolean isRequestTrue(HttpServletRequest request,String parameterName) {
		String value=request.getParameter(parameterName);
		if(value!=null && TRUE_VALUE.equals(value)){
			return true;
		}
		return false;
	}

	public static int getIntParameter(HttpServletRequest request, String parameterName, int defaultValue) {
		String value=request.getParameter(parameterName);
		if(value!=null){
			return Integer.parseInt(value);
		}else{
			return defaultValue;
		}
	}

	public static long getLongParameter(HttpServletRequest request, String parameterName, long defaultValue) {
		String value=request.getParameter(parameterName);
		if(value!=null){
			return Long.parseLong(value);
		}else{
			return defaultValue;
		}
	}
}
