package com.buststudios.pare;

import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// turns json string into list of hashmaps
// each hashmap is one place/restaurant's information
public class DataParser {

    // breaks down each place from json object to hashmap
    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlacesMap = new HashMap<>();
        String name = "-NA-";
        String latitude = "";
        String longitude = "";
        String rating = "";

        try {
            if (!googlePlaceJson.isNull("name")) {
                name = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("rating")) {
                rating = googlePlaceJson.getString("rating");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            googlePlacesMap.put("name", name);
            googlePlacesMap.put("lat", latitude);
            googlePlacesMap.put("lng", longitude);
            googlePlacesMap.put("rating", rating);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlacesMap;
    }

    // takes array of all places and sends to getplace to dissect
    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        int count = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        for (int i = 0; i < count; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    // takes the string of data, converts to json array, sends to getplaces
    public Object[] parse (String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        Object[] data = new Object[2];
        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
            if (jsonObject.has("next_page_token")) {
                data[1] = jsonObject.getString("next_page_token");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        data[0] = getPlaces(jsonArray);
        return data;
    }
}
