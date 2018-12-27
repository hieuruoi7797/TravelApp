package com.example.sonng266.travelapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.activities.MainActivity;
import com.example.sonng266.travelapp.models.TripModel;
import com.example.sonng266.travelapp.models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<TripModel> list;
    private Context context;

    public TripAdapter(List<TripModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_recycleview_trips, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        holder.setUpdata(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private TextView id;
        private ImageView iv;
        private TextView tvCreator;
        private View view;

        public TripViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            tvCreator = itemView.findViewById(R.id.tv_name_trip);
            tvName = itemView.findViewById(R.id.tv_creator);
            id = itemView.findViewById(R.id.tv_id_trip);
            iv = itemView.findViewById(R.id.iv_item);
        }

        public void setUpdata(final TripModel tripModel){
            tvName.setText("Name: " + tripModel.name);
            id.setText("Id: "+tripModel.tId);

            FirebaseDatabase.getInstance().getReference("UserModel").orderByChild("uid").equalTo(tripModel.leaderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0){
                        for (DataSnapshot d: dataSnapshot.getChildren()) {
                            UserModel userModel = d.getValue(UserModel.class);
                            tvCreator.setText("Admin: " + userModel.name);
                            Log.d("cuonghx", "onDataChange: " + userModel.uri);
                            Picasso.with(context).load(Uri.parse(userModel.uri)).transform(new CropCircleTransformation()).into(iv);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("cuonghx", "onClick: " +  1123);
                    EventBus.getDefault().postSticky(tripModel.tId);
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
