package org.duguo.xdir.spi.model.support;

import java.util.HashMap;
import java.util.Map;

import org.duguo.xdir.spi.model.GetAndPut;


/**
 * @author mrduguo
 *
 */
public abstract class AbstractGetAndPut implements GetAndPut
{

    private Map<Object, Object> map;

    public Object get(Object key) {
        if(map!=null){
            return map.get(key);
        }else{
        	return null;
        }
    }

    public void put(Object key, Object value) {
        if(map==null){
            map = new HashMap<Object, Object>(1);
        }
        map.put(key, value);
    }

    public Map<Object, Object> getMap()
    {
        if(map==null){
            map = new HashMap<Object, Object>(1);
        }
        return map;
    }

    public void setMap(Map<Object, Object> map)
    {
        this.map=map;
    }
    
}
