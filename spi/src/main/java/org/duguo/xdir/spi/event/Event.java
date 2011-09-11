package org.duguo.xdir.spi.event;

import java.util.Map;

public interface Event{
	
	public String getName();
	public Object getSource();
	public Map<Object,Object> getAttributes();
	
}