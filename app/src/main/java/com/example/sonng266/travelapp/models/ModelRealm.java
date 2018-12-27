package com.example.sonng266.travelapp.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by cuonghx on 12/7/2017.
 */

public class ModelRealm extends RealmObject{
    @PrimaryKey
    public int id;

    public String  tId;

    public ModelRealm(String tId) {
        this.id = 0;
        this.tId = tId;
    }

    public ModelRealm() {
    }
}
