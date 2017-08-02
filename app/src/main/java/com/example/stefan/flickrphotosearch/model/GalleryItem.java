package com.example.stefan.flickrphotosearch.model;

/**
 * Created by Stefan on 7/28/2017.
 */

public class GalleryItem {

    private String id;
    private String secret;
    private String server;
    private String farm;
    private String title;

    public GalleryItem(String id, String secret, String server, String farm,String title) {
        this.id = id;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        this.title = title;
    }

    public String getId(){
        return id;
    }

    public String getURL(){
        return "http://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }

    public String getTitle() {
        return title;
    }
}
