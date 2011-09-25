package org.duguo.xdir.core.internal.model;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.duguo.xdir.core.internal.utils.JcrNodeUtils;
import org.duguo.xdir.util.datetime.DateTimeUtil;

public abstract class AbstractActionModel extends AbstractPageModel
{
    private String action;
    private String status;
    private Map<String,String> updates;
    


    /*******************************************************
     * Helper methods
     *******************************************************/


    public void addUpdate(String key,String value) {
        getUpdates().put(key, value);
    }

    public void addStringUpdateIfChanged(String key,String value) {
        String oldValue=JcrNodeUtils.getPropertyIfExist(getNode(), key);
        if(oldValue==null || !oldValue.equals(value)){
            getUpdates().put(key, value);
        }
    }

    public String timestampForKey(long timestamp){
        if(timestamp==0){
            timestamp=Calendar.getInstance().getTimeInMillis();
        }
        return DateTimeUtil.timestampForKey(timestamp);
    }

    public String timestampForDisplay(long timestamp){
        if(timestamp==0){
            timestamp=Calendar.getInstance().getTimeInMillis();
        }
        return DateTimeUtil.timestampForDisplay( timestamp );
    }

    public String displayTimePassed(long timestamp){
        return DateTimeUtil.displayTimePassed( timestamp );
    }


    /*******************************************************
     * Setter and getters
     *******************************************************/

    public Map<String, String> getUpdates() {
        if(updates==null){
            updates=new HashMap<String, String>();
        }
        return updates;
    }

    public void setUpdates(Map<String, String> updates) {
        this.updates = updates;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction( String action )
    {
        this.action = action;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }
}
