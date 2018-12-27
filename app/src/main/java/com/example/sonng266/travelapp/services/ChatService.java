package com.example.sonng266.travelapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.sonng266.travelapp.adapter.MessageAdapter;
import com.example.sonng266.travelapp.eventBus.MessageEvent;
import com.example.sonng266.travelapp.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ChatService extends Service {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String tId;
    private List<MessageModel> messageModels = new ArrayList<>();
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventTripId(String tId) {
        this.tId = tId;
    }

    public ChatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Trip");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseReference.child("26697").child("messageList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageModels = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    MessageModel messageModel = dataSnapshot1.getValue(MessageModel.class);
                    messageModels.add(messageModel);
                }
                Log.d("TAG", "onDataChange: " + messageModels.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
