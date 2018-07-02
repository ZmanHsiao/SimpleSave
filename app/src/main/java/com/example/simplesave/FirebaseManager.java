package com.example.simplesave;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;

public class FirebaseManager {

    private  final String TAG = "GoogleActivity";
    private  final int RC_SIGN_IN = 9001;

    private  FirebaseFirestore db;
    private  User u;
    private  DocumentReference docRef;
    private String email;

    public FirebaseManager(){
        this.db = getFirestoreInstance();
        setUserFromFirestore();
        //this.docRef = getUserDoc(idToken);
        email = getEmail();
    }

    //private

    private  FirebaseFirestore getFirestoreInstance(){
        return FirebaseFirestore.getInstance();
    }

    private  FirebaseUser getFirebaseUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private  String getEmail(){
        return getFirebaseUser().getEmail();
    }

    private DocumentReference getUserDoc(){
        db.collection("users")
                .whereEqualTo("email", getEmail())
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                docRef = db.collection("users").document(document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            docRef = null;
                        }
                    }
                });
        return docRef;
    }


    //public

    public FirebaseManager setUser(User u){
        this.u = u;
        return this;
    }

    public FirebaseManager setUserFromFirestore(){
        db = getFirestoreInstance();
        if(docRef != null){
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            u = document.toObject(User.class);
                        } else {
                            Log.d(TAG, "user document does not exist");
                            u = null;
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        u = null;
                    }
                }
            });
        }
        return this;
    }


   // public User getUser(){
//        DocumentReference docRef = db.collection("users").document("BJ");
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                City city = documentSnapshot.toObject(City.class);
//            }
////        });
//    }




//    public void signin(){
//        setUserFromFirestore();
//        if(u == null || getUserDoc() == null){
//            //getIdToken();
//            setUser(new User(getEmail()));
//            //addUser();
////            setUser(new User(this.idToken));
////            addUser();
//        }
//        //else{System.out.println("user is not null:" + getIdToken());};
//    }
    
//    public  void pushUser(){
//        db = getFirestoreInstance();
//        setUserFromFirestore();
//        db.collection("users").document(u.getEmail()).set(u);
////        if(u != null){
////            updateUser();
////        }
////        else{
////            addUser();
////        }
//    }

    public static void pushUser(User u){
        String testdoc = u.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(testdoc).set(u, SetOptions.merge());
    }

    public static void pushUser(Map<String, Object> u){
        String testdoc = (String) u.get("email");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(testdoc).set(u, SetOptions.merge());
    }

}
