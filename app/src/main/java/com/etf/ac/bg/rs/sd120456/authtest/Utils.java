package com.etf.ac.bg.rs.sd120456.authtest;

import com.google.firebase.database.FirebaseDatabase;

class Utils {
    private static FirebaseDatabase mDatabase;

    static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}