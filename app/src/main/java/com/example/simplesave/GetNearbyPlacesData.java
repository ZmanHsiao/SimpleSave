package com.example.simplesave;

import android.graphics.Camera;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private static List<HashMap<String, String>> nearbyPlacesList = new ArrayList<>();
    private OnTaskCompleted listener;
    private String googlePlacesData;
    private GoogleMap mMap;
    private String url;

    public GetNearbyPlacesData(OnTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        final DataParser parser = new DataParser();
        final Object[] data =  parser.parse(s);
        nearbyPlacesList.addAll((List<HashMap<String, String>>) data[0]);
        showNearbyPlaces(nearbyPlacesList);
        // if there is more nearby restaurants, then start a new instance of async
        if ((String) data[1] != null ) {
            listener.moreData(buildUrl((String) data[1]));
        }
    }

    private String buildUrl(String nextToken) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("&key="+"AIzaSyCYWBfyhO7swPInMM5IKzd9cSuKVxfGuxY");
        googlePlaceUrl.append("&pagetoken=" + nextToken);
        return googlePlaceUrl.toString();
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {

        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);

            // get data of each place from Map
            String name = googlePlace.get("name");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            // plot it on the map, blue marker
            LatLng latlng = new LatLng(lat, lng);
            markerOptions.position(latlng);
            markerOptions.title(name);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            Marker m = mMap.addMarker(markerOptions);
            MapFragment.restaurantMarkers.add(m);
            listener.onTaskCompleted();
        }
    }

    public interface OnTaskCompleted {
        void onTaskCompleted();
        void moreData(String url);
    }

}
