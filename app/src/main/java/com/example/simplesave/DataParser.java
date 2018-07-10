package com.example.simplesave;

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
        String reference = "";

        try {
            if (!googlePlaceJson.isNull("name")) {
                name = googlePlaceJson.getString("name");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");

            googlePlacesMap.put("name", name);
            googlePlacesMap.put("lat", latitude);
            googlePlacesMap.put("lng", longitude);
            googlePlacesMap.put("reference", reference);

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
    public List<HashMap<String, String>> parse (String jsonData, GoogleMap mMap) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

            if (jsonObject.has("next_page_token")) {
                Thread.sleep(2000); // places api needs 2 seconds between calls
                String apiKey = "AIzaSyCYWBfyhO7swPInMM5IKzd9cSuKVxfGuxY";
                String nextpagetoken = jsonObject.getString("next_page_token");
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?&key="
                                + apiKey + "&pagetoken=" + nextpagetoken;
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(dataTransfer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }
}
