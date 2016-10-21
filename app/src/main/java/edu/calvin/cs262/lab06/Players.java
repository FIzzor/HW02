package edu.calvin.cs262.lab06;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Players forecast object (POJO), one per day, based on openweathermap's RESTful API.
 * Based on Deitel's WeatherViewer app (chapter 17).
 *
 * @author deitel
 * @author kvlinden
 * @version spring, 2017
 */
public class Players {

    private String emailaddress, name;
    private int id;

    public Players(int id, String emailaddress, String name) {
        this.id = id;
        this.emailaddress = emailaddress;
        this.name = name;
    }

    public String getId() { return id + ""; }
    public String getEmailaddress() {
        return emailaddress;
    }
    public String getName() {
        return name;
    }

}