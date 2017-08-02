package com.example.stefan.flickrphotosearch.model;

/**
 * Created by Stefan on 7/28/2017.
 */

public class Movie {

    private String name;
    private String year;

    public Movie(String name, String year) {
        this.name = name;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    @Override
    public String toString() {
        return String.format("%s - %s",this.name,this.year);
    }
}
