package uk.ac.cam.cl.interaction_design.group19.app.api;

import uk.ac.cam.cl.interaction_design.group19.app.WindDir;

public class DayData {
    public final int         temperature;
    public final int         low_temperature;
    public final int         high_temperature;
    public final int         precipitation_prob;
    public final int         frost_prob;
    public final WeatherType weather;
    public final WindDir     wind_direction;
    public final int         wind_speed;
    public final int         soil_moisture;
    public final int         soil_temperature;
    public final int         cloud_cover;
    
    public DayData(double temperature,
                   double low_temperature,
                   double high_temperature,
                   int precipitation_prob,
                   int frost_prob) {
        this.temperature = (int) Math.round(temperature);
        this.low_temperature = (int) Math.round(low_temperature);
        this.high_temperature = (int) Math.round(high_temperature);
        this.precipitation_prob = precipitation_prob;
        this.frost_prob = frost_prob;
        this.weather = null;
        cloud_cover = -1;
        wind_direction = null;
        wind_speed = -1;
        soil_moisture = -1;
        soil_temperature = -1;
    }
}