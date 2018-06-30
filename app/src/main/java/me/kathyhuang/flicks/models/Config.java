package me.kathyhuang.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {

    //base url for loading images, default poster size, and backdrop size
    String imageBaseUrl;
    String posterSize;
    String backdropSize;


    public Config (JSONObject object) throws JSONException{

        JSONObject images = object.getJSONObject("images");

        //set image base url
        imageBaseUrl = images.getString("secure_base_url");

        //set poster size
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        posterSize = posterSizeOptions.optString(3, "w342");

        //set backdrop size
        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
        backdropSize = backdropSizeOptions.optString(1, "w780");

    }

    //helper method for creating urls
    public String getImageUrl(String size, String path){
        return String.format("%s%s%s", imageBaseUrl, size, path);
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }
}
