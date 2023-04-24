package com.ixxc.uiot.Model;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataPoint {
    public Long x;
    public Float y;

    private static List<DataPoint> dataPointList;

    public static List<DataPoint> getDataPointList() { return dataPointList; }

    public static void setDataPointList(List<DataPoint> list) {
        dataPointList = list;
    }
    Date now = new Date();
    String date = new SimpleDateFormat("dd/MM/yyyy").format(now);
    public  int getDateNow(String s){
        switch (s){
            case "year" :
                return Integer.parseInt(date.split("/")[2]);
            case "month" :
                return Integer.parseInt(date.split("/")[1]);
            case "day" :
                return Integer.parseInt(date.split("/")[0]);
            case "hour" :
                return now.getHours();
            case "minute" :
                return now.getMinutes();
        }
        return 0;
    }

    public Long getTimestamp(String d){
        Log.d("AAA", "d: "+ d);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy' 'KK:mm");
        Date date = null;
        try {
            date = (Date) ((SimpleDateFormat) formatter).parse(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date.getTime();
    }


}
