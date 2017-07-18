package com.google.android.gms.samples.vision.barcodereader;

import java.util.Date;
import java.util.Map;

/**
 * Created by momchil on 03.07.17.
 */

public class MapWrapper {
    private Map<String, Object> map = null;
    private String comment;
    private Date timeAdded;
    private String author;

    public MapWrapper(){
        setComment(null);
        setTimeAdded(null);
        setAuthor(null);
    }
    public MapWrapper(Map<String, Object> map){
        setMap(map);
        setComment(null);
        setTimeAdded(null);
        setAuthor(null);
    }

    public void setMap(Map<String, Object> map){this.map = map;}
    public void setComment(String comment){this.comment = comment;}
    public void setTimeAdded(Date timeAdded){this.timeAdded = timeAdded;}
    public void setAuthor(String author){this.author = author;}

    public String getComment(){return comment;}
    public Date getTimeAdded(){return timeAdded;}
    public String getAuthor(){return author;}

    public String getId(){return  map.get("id").toString();}
    public String getName(){return map.get("name").toString();}
    public String getCompany(){return map.get("company").toString();}
    public String getPosition(){return map.get("position").toString();}
    public String getPhone(){return  map.get("phone").toString();}
}
