package com.example.nearme;

public class Post {
   private String name;
    private String occupation;
    private String phno;
    private Double lat;

    public Double getLat() {
        return lat;
    }

    public Double getLongi() {
        return longi;
    }

    private Double  longi;
   // private Location location;
//    public Location getLocation() {
//        return location;
//    }


    String isAvailable = null;

    public String getName() {
        return name;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getPhno() {
        return phno;
    }

    public String getIsAvailable() {
        return isAvailable;
    }
}
