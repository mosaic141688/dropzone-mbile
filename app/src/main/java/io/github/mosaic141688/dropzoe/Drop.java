package io.github.mosaic141688.dropzoe;

/**
 * Created by mosaic on 10/12/17.
 */

public class Drop {
    String id;
    double lat;
    double lng;
    long time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Drop(){}

    public Drop(String id, double lat, double lng, long time) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }


}
