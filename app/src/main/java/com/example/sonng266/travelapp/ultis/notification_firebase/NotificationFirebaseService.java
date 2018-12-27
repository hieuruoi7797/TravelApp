package com.example.sonng266.travelapp.ultis.notification_firebase;

import android.util.Log;

import com.example.sonng266.travelapp.eventBus.ShowDialogSOSEvent;
import com.example.sonng266.travelapp.eventBus.ShowDialogTripStartedEvent;
import com.example.sonng266.travelapp.eventBus.ShowDialogWrongWayEvent;
import com.example.sonng266.travelapp.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by sonng266 on 13/12/2017.
 */

public class NotificationFirebaseService extends FirebaseMessagingService {
    private static final String TAG = "Amen";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String type = remoteMessage.getData().get("messageType");
        String uid = remoteMessage.getData().get("uIdSending");
        String nameSender = remoteMessage.getNotification().getBody();

        if (!uid.equals(FirebaseAuth.getInstance().getUid())) {
            switch (type) {
                case HomeFragment.SOS_TYPE:
                    EventBus.getDefault().postSticky(new ShowDialogSOSEvent(true, nameSender));
                    break;
                case HomeFragment.STARTING_TYPE:
                    EventBus.getDefault().postSticky(new ShowDialogTripStartedEvent(true));
                    break;
                case HomeFragment.SOMEBODY_WRONG_WAY:
                    EventBus.getDefault().postSticky(new ShowDialogWrongWayEvent(true, nameSender));
                    break;
                default:
                    break;
            }
        }

    }

}
