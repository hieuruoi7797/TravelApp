package com.example.sonng266.travelapp.ultis;

import com.example.sonng266.travelapp.models.ModelRealm;
import com.example.sonng266.travelapp.models.TripModel;
import com.example.sonng266.travelapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import io.realm.Realm;

/**
 * Created by cuonghx on 11/27/2017.
 */

public class Utils {
    private static Realm realm = Realm.getDefaultInstance();;

    public static void loginWithCuonghx2709(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("cuonghx2709@gmail.com", "27091998");
    }
    public static void addOrUpdate(ModelRealm modelRealm){
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(modelRealm);
        realm.commitTransaction();
    }
    public static ModelRealm getModelRealm(){
        return realm.where(ModelRealm.class).findFirst();
    }
    public static void delete(){
        realm.beginTransaction();
        realm.where(ModelRealm.class).findFirst().deleteFromRealm();
        realm.commitTransaction();
    }
    public static TripModel getTripById(List<TripModel> list, String id){
        for (TripModel u : list){
            if (u.tId.equals(id)){
                return u;
            }
        }
        return null;
    }

    public static String getStringById(List<String> list, String id){
        for (String u : list){
            if (u.equals(id)){
                return u;
            }
        }
        return null;
    }

}
