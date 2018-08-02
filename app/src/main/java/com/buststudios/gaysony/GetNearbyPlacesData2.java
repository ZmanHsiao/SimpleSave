package com.buststudios.gaysony;

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

public class GetNearbyPlacesData2 extends AsyncTask<Object, String, String> {

    private static List<HashMap<String, String>> nearbyPlacesList = new ArrayList<>();
    private OnTaskCompleted listener;
    private String googlePlacesData;
    private String url;

    public GetNearbyPlacesData2(OnTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Object... objects) {
        url = (String) objects[0];
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
        final DataParser2 parser = new DataParser2();
        listener.onTaskCompleted(parser.parse(s));
    }

    private String buildUrl(String nextToken) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("&key="+"AIzaSyCYWBfyhO7swPInMM5IKzd9cSuKVxfGuxY");
        googlePlaceUrl.append("&pagetoken=" + nextToken);
        return googlePlaceUrl.toString();
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(List<HashMap<String, String>> places);
    }

}
