package com.buststudios.gaysony;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlacesAdapter extends ExpandableRecyclerAdapter<PlacesTitleParentViewHolder, PlacesTitleChildViewHolder> {

    private GeoDataClient mGeoDataClient;
    BudgetPlan budgetplan = MainActivity.budgetplan;
    Context mCtx;
    LayoutInflater inflater;
    List<PlacesTitleChildViewHolder> childViews;
    List<PlacesTitleParentViewHolder> parentViews;

    public PlacesAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);
        inflater = LayoutInflater.from(context);
        mGeoDataClient = Places.getGeoDataClient(context);
        childViews = new ArrayList<>();
        parentViews = new ArrayList<>();
        mCtx = context;
    }

    @Override
    public PlacesTitleParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.places_parent_card, viewGroup, false);
        PlacesTitleParentViewHolder pView = new PlacesTitleParentViewHolder(view);
        parentViews.add(pView);
        return pView;
    }

    @Override
    public PlacesTitleChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.places_child_layout, viewGroup, false);
        PlacesTitleChildViewHolder cView = new PlacesTitleChildViewHolder(view);
        childViews.add(cView);
        return cView;
    }

    @Override
    public void onBindParentViewHolder(final PlacesTitleParentViewHolder placesTitleParentViewHolder, final int i, Object o) {
        final PlacesTitleParent parent = (PlacesTitleParent) o;
        final List<Object> childList =  parent.getChildObjectList();
        placesTitleParentViewHolder._textView.setText(parent.getTitle());
        final String id = parent.getPlace_id();
        placesTitleParentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // only loads if it is not expanded yet and the data has not loaded before
                if (!placesTitleParentViewHolder.isExpanded() && ((PlacesTitleChild) childList.get(0)).getName().equals("")){
                    placeBufferResponseOnCompleteListener detailsListener = new placeBufferResponseOnCompleteListener(childList);
                    photosOnCompleteListener photoListener = new photosOnCompleteListener(childList);
                    mGeoDataClient.getPlaceById(id).addOnCompleteListener(detailsListener);
                    Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(id);
                    photoMetadataResponse.addOnCompleteListener(photoListener);
                }
                placesTitleParentViewHolder.onClick(placesTitleParentViewHolder.itemView);
            }
        });
    }

    @Override
    public void onBindChildViewHolder(PlacesTitleChildViewHolder placesTitleChildViewHolder, int i, Object o) {
        PlacesTitleChild child = (PlacesTitleChild) o;
        placesTitleChildViewHolder.name.setText(child.getName());
        placesTitleChildViewHolder.address.setText(child.getAddress());
        placesTitleChildViewHolder.rating.setRating(child.getRating());
        placesTitleChildViewHolder.image.setImageBitmap(child.getImage());
        TextView website = placesTitleChildViewHolder.website;
        String url = child.getWebsite();
        String link = "<a href=\"" + url + "\">Website</a>";
        website.setText(Html.fromHtml(link));
        website.setMovementMethod(LinkMovementMethod.getInstance());

        TextView phone = placesTitleChildViewHolder.phonenum;
        phone.setText(child.getPhonenum());
        Linkify.addLinks(phone, Linkify.ALL);

        String price = "$";
        for (int j=0; j < child.getPriceLevel()-1; j++) {
            price += "$";
        }
        placesTitleChildViewHolder.addTrans.setText(price);
        addTransOnClickListener addTransOnClickListener = new addTransOnClickListener(child.getName());
        placesTitleChildViewHolder.addTrans.setOnClickListener(addTransOnClickListener);

    }

    class placeBufferResponseOnCompleteListener implements OnCompleteListener<PlaceBufferResponse> {
        List<Object> childData;
        public placeBufferResponseOnCompleteListener(List<Object> childData) {
            this.childData = childData;
        }
        @Override
        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
            if (task.isSuccessful()) {
                PlacesTitleChild child = (PlacesTitleChild) childData.get(0);
                PlaceBufferResponse places = task.getResult();
                Place myPlace = places.get(0);

                child.setName((String) myPlace.getName());
                child.setAddress((String) myPlace.getAddress());
                child.setRating(myPlace.getRating());
                child.setPhonenum((String)myPlace.getPhoneNumber());
                child.setPriceLevel(myPlace.getPriceLevel());
                if (myPlace.getWebsiteUri() != null) {
                    child.setWebsite(myPlace.getWebsiteUri().toString());
                }
            } else {
                System.out.println("Place Not Found");
            }
        }
    }

    class photosOnCompleteListener implements OnCompleteListener<PlacePhotoMetadataResponse> {

        List<Object> childData;
        public photosOnCompleteListener(List<Object> childData) {
            this.childData = childData;
        }
        @Override
        public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
            try {
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                CharSequence attribution = photoMetadata.getAttributions();
                Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        Bitmap bitmap = photo.getBitmap();
                        //bitmap = Bitmap.createScaledBitmap(bitmap, 700, 400, false);
                        PlacesTitleChild child = (PlacesTitleChild) childData.get(0);
                        child.setImage(bitmap);
                        notifyDataSetChanged();
                    }
                });
                photoMetadataBuffer.release();
            } catch (Exception e) {
                System.out.println("No Photos Found");
            }

        }
    }


    class addTransOnClickListener implements View.OnClickListener {
        String restaurantName;
        Timestamp transactionDate = new Timestamp(new Date());

        public addTransOnClickListener(String restaurantName) {
            this.restaurantName = restaurantName;
        }

        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
            View mView = inflater.inflate(R.layout.add_transaction_dialog, null);
            final EditText price = (EditText) mView.findViewById(R.id.price);
            price.requestFocus();

            final EditText title = (EditText) mView.findViewById(R.id.title);
            title.setText(restaurantName);
            Button timestampButton = (Button) mView.findViewById(R.id.timestampButton);
            Button addButton = (Button) mView.findViewById(R.id.addTrans);
            builder.setView(mView);
            builder.create();
            final AlertDialog display = builder.show();

            final Spinner dropdown = (Spinner) mView.findViewById(R.id.categoryDropdown);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mCtx, android.R.layout.simple_list_item_1,
                    budgetplan.getCategories().toArray(new String[0]));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown.setAdapter(adapter);
            dropdown.setSelection(adapter.getPosition("Restaurant"));

            timestampButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                    View mView = inflater.inflate(R.layout.dialog_time_picker, null);
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
    }
}


