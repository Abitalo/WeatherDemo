package com.abitalo.www.weatherdemo.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lancelot on 2015/12/4.
 */
public class Item_Weather {

    private String city;
    private String quality;
    private String weather;

    private int AQI;
    private int temperature_low;
    private int temperature_high;

    private long date;

    private String temperatureString=null;

    public Item_Weather(String city, String AQI, String quality, long date, String weather, String temperature){
        this.city=city;
        this.AQI=Integer.parseInt(AQI);
        this.quality=quality;
        this.date=date;
        this.weather=weather;
        temperatureString=temperature;
        this.temperature_low=Integer.parseInt(temperature.substring(0,temperature.indexOf("℃")));
        this.temperature_high=Integer.parseInt(temperature.substring(temperature.indexOf("~")+1,temperature.length()-1));
    }
    public Item_Weather(String city, int AQI, String quality, long date, String weather, int temperature_low, int temperature_high) {
        this.city=city;
        this.AQI=AQI;
        this.quality =quality;
        this.date=date;
        this.weather=weather;
        this.temperature_low=temperature_low;
        this.temperature_high=temperature_high;
        this.temperatureString=getTemperatureString();
    }
    private long getMills(String date){
        SimpleDateFormat form=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long mills=-1;
        try {
            mills=form.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mills;
    }

    public String getDateString(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(date));
    }

    public String getCity() {
        return city;
    }

    public String getQuality() {
        return quality;
    }

    public String getWeather() {
        return weather;
    }

    public int getAQI() {
        return AQI;
    }

    public int getTemperature_low() {
        return temperature_low;
    }

    public int getTemperature_high() {
        return temperature_high;
    }

    public long getDate() {
        return date;
    }

    public String getTemperatureString() {
        if(temperatureString==null)
            return temperature_low+"℃~"+temperature_high+"℃";
        else
            return temperatureString;
    }
}
