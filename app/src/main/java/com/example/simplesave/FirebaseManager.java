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

import java.util.Map;

public class FirebaseManager {

    private  final String TAG = "GoogleActivity";
    private  final int RC_SIGN_IN = 9001;

    private  FirebaseFirestore db;
    private  User u;
    private  DocumentReference docRef;
    private  String idToken;
    private String email;
    private boolean wait;


    public FirebaseManager(){
        this.db = getFirestoreInstance();
        //this.idToken = getIdToken();
        setUserFromFirestore();
        //this.docRef = getUserDoc(idToken);
        wait = true;
        email = getEmail();
    }

    //private

    private  FirebaseFirestore getFirestoreInstance(){
        return FirebaseFirestore.getInstance();
    }

    private  FirebaseUser getFirebaseUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setIdToken(String idToken){
        this.idToken = idToken;
    }

    private void setWait(boolean wait){
        this.wait = wait;
    }

    private boolean getWait(){
        return wait;
    }

    private  String getIdToken(){
        FirebaseUser mUser = getFirebaseUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            setIdToken(task.getResult().getToken());
                        } else {
                            System.out.println("exception: " + task.getException().getMessage());
                        }
                        setUser(new User(idToken));
                        addUser();
                    }
                });
        setWait(true);
        System.out.println("idtoken after: " + this.idToken);
        return this.idToken;
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

    private  String getEmail(){
        return getFirebaseUser().getEmail();
    }



    private  void addUser(){
        db.collection("users").document().set(u);
    }

    private  void updateUser(){
        docRef.set(u);
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

    public User getTestUser(final Activity ac){
        String testdoc = "GCfZaNPhVfeLEgqfXSrz";

        DocumentReference docRef = db.collection("users").document(testdoc);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
//                setUser(u);
//                System.out.println("user");
//                System.out.println("user: " + documentSnapshot.toString());
//                Intent intent = new Intent(ac, TransactionsActivity.class);
//                startActivity(intent);
            }
        });
        return u;
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

//    public User getUser(){
//        DocumentReference docRef = db.collection("users").document("BJ");
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                City city = documentSnapshot.toObject(City.class);
//            }
//        });
//    }

    public void signin(){
        setUserFromFirestore();
        if(u == null || getUserDoc() == null){
            //getIdToken();
            setUser(new User(getEmail()));
            addUser();
//            setUser(new User(this.idToken));
//            addUser();
        }
        //else{System.out.println("user is not null:" + getIdToken());};
    }
    
    public  void pushUser(){
        db = getFirestoreInstance();
        setUserFromFirestore();
        if(u != null){
            updateUser();
        }
        else{
            addUser();
        }
    }

}
