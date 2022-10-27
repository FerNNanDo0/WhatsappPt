package com.example.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Firebase {
    static FirebaseAuth Auth;
    static DatabaseReference Database;
    static StorageReference Storage;

    // ref FirebaseAuth
    public static FirebaseAuth getAuthRef() {
        Auth = FirebaseAuth.getInstance();
        return Auth;
    }
    // ref Database
    public static DatabaseReference getDatabaseRef() {
        Database = FirebaseDatabase.getInstance().getReference();
        return Database;
    }

    // ref Storage
    public static StorageReference getStorageRef() {
        Storage = FirebaseStorage.getInstance().getReference();
        return Storage;
    }
}
