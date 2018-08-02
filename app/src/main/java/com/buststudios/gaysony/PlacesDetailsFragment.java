package com.buststudios.gaysony;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PlacesDetailsFragment extends Fragment implements GetNearbyPlacesData2.OnTaskCompleted {

    RecyclerView recyclerView;
    private BudgetPlan budgetplan;
    private static View view;

    private MapView mMapView;
    private static GoogleMap mMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(37.827909, -122.135873); // joshs house
    private Location mLastKnownLocation;
    private LatLng myLocation = mDefaultLocation;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static final String TAG = PlacesDetailsFragment.class.getSimpleName();

    public PlacesDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ((PlacesAdapter) recyclerView.getAdapter()).onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        budgetplan = MainActivity.budgetplan;
        mGeoDataClient = Places.getGeoDataClient(getActivity());
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_places_details, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                updateLocationUI();
                getDeviceLocation(); //call get surrounding places in this method, because async
            }
        });
        return view;
    }

    private void getSurroundingPlaces(int price) {
        String url = getUrl(myLocation.latitude, myLocation.longitude, "restaurant", price);
        Object dataTransfer = url;
        GetNearbyPlacesData2 getNearbyPlacesData = new GetNearbyPlacesData2(this);
        getNearbyPlacesData.execute(dataTransfer);
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace, int price) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius="+ "1000");
        //googlePlaceUrl.append("&rankby=" + "distance");
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&opennow");
        //googlePlaceUrl.append("&minprice=" + price);
        //googlePlaceUrl.append("&maxprice=" + price);
        googlePlaceUrl.append("&key=" + "AIzaSyCYWBfyhO7swPInMM5IKzd9cSuKVxfGuxY");
        System.out.println(googlePlaceUrl.toString());
        return googlePlaceUrl.toString();
    }

    @Override
    public void onTaskCompleted(final List<HashMap<String, String>> places) {
        addPlaceMarkers(places);
        recyclerView = (RecyclerView) view.findViewById(R.id.restaurantList);
        TextView none = ((TextView) view.findViewById(R.id.none));
        if (places.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            none.setVisibility(View.GONE);
            recyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));
            PlacesAdapter adapter = new PlacesAdapter(getActivity(), initData(places));
            adapter.setParentClickableViewAnimationDefaultDuration();
            adapter.setParentAndIconExpandOnClick(true);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            none.setVisibility(View.VISIBLE);
            none.setText("No Open Restaurants Near You");
        }
    }

    private void addPlaceMarkers(List<HashMap<String, String>> places) {
        for (int i = 0; i < places.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = places.get(i);
            String name = googlePlace.get("name");
            String rating = googlePlace.get("rating");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            LatLng latlng = new LatLng(lat, lng);
            markerOptions.position(latlng);
            markerOptions.title(name + " (" + rating + ")");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            Marker m = mMap.addMarker(markerOptions);
            MapFragment.restaurantMarkers.add(m);
        }
    }

    private List<ParentObject> initData(List<HashMap<String, String>> places) {
        List<ParentObject> parentObject = new ArrayList<>();
        for (int i = 0; i < places.size(); i++) {
            PlacesTitleParent parent = new PlacesTitleParent(places.get(i).get("name"), places.get(i).get("place_id"));
            List<Object> childList = new ArrayList<>();
            childList.add(new PlacesTitleChild("", "", "", "",0, 0));
            parent.setChildObjectList(childList);
            parentObject.add(parent);
        }
        return parentObject;
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener( new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // set camera and marker for current location
                            mLastKnownLocation = task.getResult();
                            myLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(myLocation).title("I am here!"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( myLocation, DEFAULT_ZOOM));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                            getSurroundingPlaces(1);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
