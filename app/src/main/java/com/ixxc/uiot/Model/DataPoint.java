package com.ixxc.uiot.Model;

import java.util.ArrayList;
import java.util.List;

public class DataPoint {
    public Long x;
    public Double y;

    private static List<DataPoint> dataPointList;

    public static List<DataPoint> getDataPointList() { return dataPointList; }

    public static void setDataPointList(List<DataPoint> list) {
        dataPointList = list;
    }
}
