package com.example.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {

    //Base url for images
    String imgBaseUrl;
    //Image poster size
    String posterSize;

    public Config(JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images");
        imgBaseUrl = images.getString("secure_base_url");
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        posterSize = posterSizeOptions.optString(3, "w342");
    }

    public String getImageUrl(String size, String path){
        return String.format("%s%s%s", imgBaseUrl, size, path);
    }

    public String getImgBaseUrl() {
        return imgBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }
}
