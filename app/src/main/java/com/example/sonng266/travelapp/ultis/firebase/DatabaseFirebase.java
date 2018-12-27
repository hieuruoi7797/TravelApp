package com.example.sonng266.travelapp.ultis.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseFirebase {

    private static final String TAG = "DatabaseFirebase";

    public static FirebaseDatabase firebaseDatabase;

    public static FirebaseDatabase getInstance() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
        }
        return firebaseDatabase;
    }

    public static <T> void pushDataToRoot(String root, T t) {
        DatabaseReference databaseReference = getInstance().getReference(root);
        databaseReference.push().setValue(t);
    }
}
