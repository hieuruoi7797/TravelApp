package com.example.sonng266.travelapp.fragments.chat;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.adapter.MessageAdapter;
import com.example.sonng266.travelapp.fragments.BaseFragment;
import com.example.sonng266.travelapp.models.MessageModel;
import com.example.sonng266.travelapp.models.TripModel;
import com.example.sonng266.travelapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends BaseFragment {
    private static final String TAG = "";
//    @BindView(R.id.et_text_chat)
    EditText etTextChat;
    @BindView(R.id.rv_text_chat)
    RecyclerView rvTextChat;
    @BindView(R.id.bt_sent_chat)
    ImageView btSend;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private UserModel userModel;
    private String tId;
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private List<MessageModel> messageModels = new ArrayList<>();

    public ChatFragment() {
        // Required empty public constructor
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventTripId(String tId) {
        this.tId = tId;
    }
//    @Override
//    public void onStart() {
//        super.onStart();
//
//    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public String getPageTitle() {
        return "";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        setUI(view);
        return view;

    }

    private void setUI(final View view) {
        etTextChat = view.findViewById(R.id.et_text_chat);
        btSend = view.findViewById(R.id.bt_sent_chat);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Trip");
        firebaseUser = firebaseAuth.getCurrentUser();
        userModel = new UserModel();
        userModel.name = firebaseUser.getDisplayName();
        userModel.uid = firebaseUser.getUid();
        userModel.uri = firebaseUser.getPhotoUrl().toString();
        EventBus.getDefault().register(this);

        etTextChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    btSend.setImageResource(R.drawable.ic_sent_chat);
                }else{
                    btSend.setImageResource(R.drawable.ic_send);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        rvTextChat = view.findViewById(R.id.rv_text_chat);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvTextChat.setLayoutManager(linearLayoutManager);

        databaseReference.child(tId).child("messageList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageModels = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    MessageModel messageModel = dataSnapshot1.getValue(MessageModel.class);
                    messageModels.add(messageModel);
                }
                messageAdapter = new MessageAdapter(messageModels,view.getContext(),userModel.name);
                linearLayoutManager.scrollToPosition(messageModels.size() - 1);
                rvTextChat.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void handleFragmentClick(View view) {
        switch (view.getId()) {
            case R.id.bt_back:
                showBack();
                break;
            case R.id.bt_next:
                showNext(new ChatFragment());
                break;
            case R.id.bt_sent_chat:
                if (!etTextChat.getText().toString().equals("")){
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mm a");
                    String strDate = formatter.format(date);
                    Log.d(TAG, "handleFragmentClick: " +etTextChat.getText().toString());
                    Log.d(TAG, "handleFragmentClick: " +tId);
                    MessageModel messageModel = new MessageModel(userModel.name,etTextChat.getText().toString(),
                            strDate,userModel.uid,userModel.uri);
                    messageModels.add(messageModel);
                    databaseReference.child(tId).child("messageList").push().setValue(messageModel);
                    etTextChat.setText("");
                }
                break;
        }
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_chat;
    }
}
