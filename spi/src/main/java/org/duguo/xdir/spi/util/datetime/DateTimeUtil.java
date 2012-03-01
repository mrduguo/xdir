package org.duguo.xdir.spi.util.datetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil
{
    public static DateFormat TIMESTAMP_DATE_FORMAT_YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyyMMdd-HHmmss");
    public static DateFormat TIMESTAMP_DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String currentTimestampKey() {
        return TIMESTAMP_DATE_FORMAT_YYYYMMDD_HHMMSS.format(new Date());
    }

    public static String timestampForKey(long time) {
        return TIMESTAMP_DATE_FORMAT_YYYYMMDD_HHMMSS.format(new Date(time));
    }

    public static String timestampForDisplay(long time) {
        return TIMESTAMP_DATE_FORMAT_YYYY_MM_DD_HH_MM_SS.format(new Date(time));
    }
    
    public static String displayTimePassed(long time) {
        if(time==0){
            return "moment";
        }
        
        long smallUnit=Calendar.getInstance().getTimeInMillis()-time;
        long bigUnit=smallUnit/1000;
        if(bigUnit<=1){
            return smallUnit+" milliseconds";
        }
        smallUnit=bigUnit;
        bigUnit=smallUnit/60;
        if(bigUnit<=1){
            return smallUnit+" seconds";
        }
        
        smallUnit=bigUnit;
        bigUnit=smallUnit/60;
        if(bigUnit<=1){
            return smallUnit+" minutes";
        }
        
        smallUnit=bigUnit;
        bigUnit=smallUnit/24;
        if(bigUnit<=1){
            return smallUnit+" hours";
        }
        
        smallUnit=bigUnit;
        long days=smallUnit;
        bigUnit=smallUnit/7;
        if(bigUnit<=1){
            return smallUnit+" days";
        }
        
        smallUnit=bigUnit;
        bigUnit=smallUnit/5;
        if(bigUnit<=1){
            return smallUnit+" weeks";
        }

        
        smallUnit=days/30;
        bigUnit=days/365;
        if(bigUnit<=1){
            return smallUnit+" months";
        }
        
        return bigUnit+" years";
    }
}
