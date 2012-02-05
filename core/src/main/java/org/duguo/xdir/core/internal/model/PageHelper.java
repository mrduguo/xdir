package org.duguo.xdir.core.internal.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.duguo.xdir.core.internal.template.freemarker.StringTemplateUtils;
import org.duguo.xdir.jcr.utils.JcrNodeUtils;

public class PageHelper {

	public static final Pattern DIGITAL_PREFIX=Pattern.compile("\\d+_");
	public static final Pattern PLACE_HOLDER_PATTERN = Pattern.compile("\\$\\[\\w+\\]");
	public static String PLACEHOLDER_PAGE_PATH="${model.pagePath}";
	public static String PLACEHOLDER_PAGE_CONTEXT="${model.pageContext}";
	public static String PLACEHOLDER_FORMAT="${model.format}";
	public static final String TRUE_VALUE="true";

	public static String displayPropertyName(String propertyName) {
		Matcher matcher=DIGITAL_PREFIX.matcher(propertyName);
		if(matcher.find()){
			propertyName=propertyName.substring(matcher.end());
		}
		return JcrNodeUtils.getDisplayablePropertyName(propertyName);
	}
	
	public static String displayPropertyValue(String inputString,ModelImpl model) {
        if(inputString.indexOf("$")>=0){
            return StringTemplateUtils.render(inputString,model);
        } else{
            return inputString;
        }
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
