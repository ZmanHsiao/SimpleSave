package com.example.simplesave;

import android.content.Intent;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class UpdateTestUser {

    public static void updateTestUser(User u){
        String testdoc = "GCfZaNPhVfeLEgqfXSrz";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(testdoc).set(u, SetOptions.merge());
    }

}
