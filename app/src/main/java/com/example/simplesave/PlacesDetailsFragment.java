package com.example.simplesave;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class PlacesDetailsFragment extends Fragment implements GetNearbyPlacesData2.OnTaskCompleted {

    RecyclerView recyclerView;

    private BudgetPlan budgetplan;
    private String currentSelect;
    private static View view;
    private View mView;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(37.827909, -122.135873); // joshs house
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private LatLng myLocation = mDefaultLocation;
    private static final String TAG = MapFragment.class.getSimpleName();

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
        getDeviceLocation();
        getSurroundingPlaces(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_places_details, container, false);
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
        //googlePlaceUrl.append("&radius="+ "15000");
        googlePlaceUrl.append("&rankby=" + "distance");
        googlePlaceUrl.append("&type=" + nearbyPlace);
        //googlePlaceUrl.append("&opennow");
        //googlePlaceUrl.append("&minprice=" + price);
        //googlePlaceUrl.append("&maxprice=" + price);
        googlePlaceUrl.append("&key=" + "AIzaSyCYWBfyhO7swPInMM5IKzd9cSuKVxfGuxY");
        System.out.println(googlePlaceUrl.toString());
        return googlePlaceUrl.toString();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // set camera and marker for current location
                            mLastKnownLocation = task.getResult();
                            myLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

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

    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
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
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
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
            childList.add(new PlacesTitleChild("", "", "", "", "",0));
            parent.setChildObjectList(childList);
            parentObject.add(parent);
        }
        return parentObject;
    }
}
