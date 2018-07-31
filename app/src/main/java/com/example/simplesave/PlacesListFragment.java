package com.example.simplesave;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlacesListFragment extends Fragment implements GetNearbyPlacesData2.OnTaskCompleted{

    RecyclerView recyclerView;
    AsyncTask mTask;
    private BudgetPlan budgetplan;
    private static View view;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(37.827909, -122.135873); // joshs house
    private Location mLastKnownLocation;
    private LatLng myLocation = mDefaultLocation;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static final String TAG = PlacesListFragment.class.getSimpleName();

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
        updateLocationUI();
        getDeviceLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_places_list, container, false);
        return view;
    }

    private void getSurroundingPlaces(int price) {
        String url = getUrl(myLocation.latitude, myLocation.longitude, "restaurant", price);
        Object dataTransfer = url;
        GetNearbyPlacesData2 getNearbyPlacesData = new GetNearbyPlacesData2(this);
        mTask = getNearbyPlacesData.execute(dataTransfer);
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace, int price) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius="+ "3000");
        //googlePlaceUrl.append("&rankby=" + "distance");
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&opennow");
        //googlePlaceUrl.append("&minprice=" + price);
        //googlePlaceUrl.append("&maxprice=" + price);
        googlePlaceUrl.append("&key=" + "AIzaSyCYWBfyhO7swPInMM5IKzd9cSuKVxfGuxY");
        return googlePlaceUrl.toString();
    }

    @Override
    public void onTaskCompleted(final List<HashMap<String, String>> places) {
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
                            getSurroundingPlaces(1);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
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
        try {
            if (!mLocationPermissionGranted) {
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            mTask.cancel(true);
        } catch (Exception e) {
            System.out.println("failed");
        }

    }
}
