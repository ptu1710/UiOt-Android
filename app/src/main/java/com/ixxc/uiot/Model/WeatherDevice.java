package com.ixxc.uiot.Model;

import com.google.gson.Gson;

public class WeatherDevice extends Device {
    public String id;
    public String name;

    public Attribute temperature;
    public Attribute humidity;
    public Attribute windSpeed;
    public Attribute windDirection;
    public Attribute rainfall;
    public Attribute rainTomorrow;
    public Attribute uVIndex;
    public Attribute maxTemperature;
    public Attribute minTemperature;
    public Attribute description;

    public String rainPredictValue = "1";

    public WeatherDevice() {
        super();

        this.id = "";
        this.name = "";

        this.temperature = new Attribute("temperature", null);
        this.humidity = new Attribute("humidity", null);
        this.rainfall = new Attribute("rainfall", null);
        this.rainTomorrow = new Attribute("rainTomorrow", null);
        this.uVIndex = new Attribute("uVIndex", null);
        this.windDirection = new Attribute("windDirection", null);
        this.windSpeed = new Attribute("windSpeed", null);
    }

    public WeatherDevice(Device device) {
        super(device.type);

        if (!device.type.equals("WeatherAsset")) {
            throw new IllegalArgumentException("Device type is not WeatherAsset");
        }

        this.id = device.id;
        this.name = device.name;

        this.temperature = new Gson().fromJson(device.attributes.get("temperature"), Attribute.class);
        this.maxTemperature = new Gson().fromJson(device.attributes.get("maxTemperature"), Attribute.class);
        this.minTemperature = new Gson().fromJson(device.attributes.get("minTemperature"), Attribute.class);
        this.humidity = new Gson().fromJson(device.attributes.get("humidity"), Attribute.class);
        this.rainfall = new Gson().fromJson(device.attributes.get("rainfall"), Attribute.class);
        this.rainTomorrow = new Gson().fromJson(device.attributes.get("rainTomorrow"), Attribute.class);
        this.uVIndex = new Gson().fromJson(device.attributes.get("uVIndex"), Attribute.class);
        this.windDirection = new Gson().fromJson(device.attributes.get("windDirection"), Attribute.class);
        this.windSpeed = new Gson().fromJson(device.attributes.get("windSpeed"), Attribute.class);
        this.description = new Gson().fromJson(device.attributes.get("weatherDescription"), Attribute.class);
    }
}
