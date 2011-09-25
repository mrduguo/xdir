package org.duguo.xdir.core.internal.model;

import javax.jcr.Property;

import org.duguo.xdir.core.internal.utils.JcrNodeUtils;

public class TextProperty {
	private Property property;
	private String propertyName;
	private ModelImpl model;

	public TextProperty(ModelImpl model,Property property,String propertyName) {
	    this.model=model;
		this.property = property;
		this.propertyName = propertyName;
	}

	public String getName() throws Exception {
		return model.displayPropertyName(propertyName);
	}

	public String getValue() throws Exception {
		if(model.isAdvancedView() || propertyName.charAt( 0 )=='_'){
			return getRawValue();
		}else{
			return model.displayPropertyValue(getRawValue());
		}
	}
	
	public String getRawValue() throws Exception {
		return JcrNodeUtils.getPropertyStringValue(property);
	}

	public String getDisplayableValue() throws Exception {
		return model.displayPropertyValue(getRawValue());
	}

	public Property getProperty() {
		return property;
	}


}
