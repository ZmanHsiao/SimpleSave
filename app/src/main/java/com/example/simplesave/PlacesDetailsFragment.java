package com.example.simplesave;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

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
        googlePlaceUrl.append("&minprice=" + price);
        googlePlaceUrl.append("&maxprice=" + price);
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
        if (places.size() != 0) {
            String[] list = new String[places.size()];
            for (int i = 0; i < places.size(); i++) {
                list[i] = places.get(i).get("name");
            }
            ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
            ListView lv = (ListView) view.findViewById(R.id.restaurantList);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                            String placeId = places.get(i).get("place_id");
                            currentSelect = places.get(i).get("name");

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            mView = getLayoutInflater().inflate(R.layout.places_details_dialog, null);
                            builder.setView(mView);
                            builder.create();

                            mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(placeBufferResponseOnCompleteListener);
                            Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
                            photoMetadataResponse.addOnCompleteListener(placePhotosOnCompleteListener);

                            Button button = (Button) mView.findViewById(R.id.add);
                            button.setOnClickListener(addTransListener);
                            AlertDialog display = builder.show();
                        }
                    }
            );
        } else {
            String[] none = {"No Open Restaurants"};
            ListAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, none);
            ListView lv = (ListView) view.findViewById(R.id.restaurantList);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(null);
        }

    }

    private OnCompleteListener<PlaceBufferResponse> placeBufferResponseOnCompleteListener = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
            if (task.isSuccessful()) {
                PlaceBufferResponse places = task.getResult();
                Place myPlace = places.get(0);

                ((TextView)mView.findViewById(R.id.name)).setText(myPlace.getName());
                ((TextView)mView.findViewById(R.id.address)).setText(myPlace.getAddress());
                ((TextView)mView.findViewById(R.id.phonenum)).setText(myPlace.getPhoneNumber());
                ((TextView)mView.findViewById(R.id.rating)).setText(myPlace.getRating() + " rating");

                if (myPlace.getWebsiteUri() != null) {
                    ((TextView)mView.findViewById(R.id.website)).setText(myPlace.getWebsiteUri().toString());
                }


                String types = "";
                for (int i = 0; i < myPlace.getPlaceTypes().size(); i++) {
                    try {
                        types += getPlaceTypeForValue(myPlace.getPlaceTypes().get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ((TextView)mView.findViewById(R.id.types)).setText(types);
                places.release();

            } else {
                Log.e(TAG, "Place not found.");
            }
        }
    };

    private OnCompleteListener placePhotosOnCompleteListener = new OnCompleteListener<PlacePhotoMetadataResponse>() {
        @Override
        public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
            // Get the list of photos.
            PlacePhotoMetadataResponse photos = task.getResult();
            // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
            PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
            // Get the first photo in the list.
            PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
            // Get the attribution text.
            CharSequence attribution = photoMetadata.getAttributions();
            // Get a full-size bitmap for the photo.
            Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
            photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                    PlacePhotoResponse photo = task.getResult();
                    Bitmap bitmap = photo.getBitmap();
                    ImageView mImg = (ImageView) mView.findViewById(R.id.image);
                    mImg.setImageBitmap(bitmap);
                }
            });
        }
    };

    private String getPlaceTypeForValue(int value) throws Exception {
        Field[] fields = Place.class.getDeclaredFields();
        String name;
        for (Field field : fields) {
            name = field.getName().toLowerCase();
            if (name.startsWith("type_") && field.getInt(null) == value) {
                return name.replace("type_", "");
            }
        }
        throw new IllegalArgumentException("place value " + value + " not found.");
    }

    private View.OnClickListener addTransListener = new View.OnClickListener() {
        Timestamp transactionDate = new Timestamp(new Date());
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View mView = getLayoutInflater().inflate(R.layout.add_transaction_dialog, null);
            final EditText price = (EditText) mView.findViewById(R.id.price);
            
            price.requestFocus();

            final EditText title = (EditText) mView.findViewById(R.id.title);
            title.setText(currentSelect);
            Button timestampButton = (Button) mView.findViewById(R.id.timestampButton);
            Button addButton = (Button) mView.findViewById(R.id.addTrans);
            builder.setView(mView);
            builder.create();
            final AlertDialog display = builder.show();

            final Spinner dropdown = (Spinner) mView.findViewById(R.id.categoryDropdown);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,
                    budgetplan.getCategories().toArray(new String[0]));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown.setAdapter(adapter);
            dropdown.setSelection(adapter.getPosition("Food"));

            timestampButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    View mView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
                    final DatePicker datePicker = (DatePicker) mView.findViewById(R.id.datePicker);
                    datePicker.setMinDate(budgetplan.getStartDate().toDate().getTime());
                    datePicker.setMaxDate(budgetplan.getEndDate().toDate().getTime());
                    final TimePicker timePicker = (TimePicker) mView.findViewById(R.id.timePicker);
                    Button doneButton = (Button) mView.findViewById(R.id.doneButton);
                    builder.setView(mView);
                    builder.create();
                    final AlertDialog display = builder.show();

                    doneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar c = Calendar.getInstance();
                            c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            transactionDate = new Timestamp(c.getTime());
                            display.dismiss();
                        }
                    });
                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String category = dropdown.getSelectedItem().toString();
                    String name = title.getText().toString();
                    float val = Float.valueOf(price.getText().toString());
                    budgetplan.addTransaction(category, name, val, transactionDate);
                    display.dismiss();
                    AppLibrary.pushUser(MainActivity.user);
                }
            });
        }
    };


}
