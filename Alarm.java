package com.example.smsapp;

public class Alarm
{
    public String number;
    public String name;
    public String severity;
    public String description;
    public String location;
    public String date;
    public String time;

    public Alarm(){}

    public Alarm(String number, String name, String severity, String description, String location, String date, String time)
    {
        this.number = number;
        this.name = name;
        this.severity = severity;
        this.description = description;
        this.location = location;
        this.date = date;
        this.time = time;
    }

}
