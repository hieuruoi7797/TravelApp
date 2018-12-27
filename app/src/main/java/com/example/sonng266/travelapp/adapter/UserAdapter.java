package com.example.sonng266.travelapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder>{

    private List<UserModel> list;
    private Context context;

    public void setIdLeader(String idLeader) {
        this.idLeader = idLeader;
    }

    private String idLeader;

    public UserAdapter(List<UserModel> list, Context context){
        this.list = list;
        this.context = context;
    }
    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_user, parent, false);


        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        holder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserHolder  extends RecyclerView.ViewHolder{

        private ImageView ivUser;
        private TextView tvName;
        private TextView mail;
        private ImageView ivMore;

        public UserHolder(View itemView) {
            super(itemView);
            this.ivUser = itemView.findViewById(R.id.iv_item_user);
            this.tvName = itemView.findViewById(R.id.tv_namedisplay);
            this.mail = itemView.findViewById(R.id.tv_mail_user);
            this.ivMore = itemView.findViewById(R.id.iv_more_user);
        }

        public void setData(UserModel userModel){
            tvName.setText(userModel.name);
            Picasso.with(context).load(userModel.uri).transform(new CropCircleTransformation()).into(ivUser);
            Log.d("cuonghx", "setData: " + idLeader +FirebaseAuth.getInstance().getCurrentUser().getUid() );
            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(idLeader)) {
                ivMore.setVisibility(View.VISIBLE);
                ivMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("cuonghx", "onClick: ");
                        PopupMenu popupMenu = new PopupMenu(context, ivMore);
                        popupMenu.inflate(R.menu.pop_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.kick:
                                        break;
                                    case R.id.change_admin:
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }else {
                ivMore.setVisibility(View.GONE);
            }
            if (userModel.uid.equals(idLeader)){
                mail.setText("Leader");
                ivMore.setVisibility(View.GONE);
            }
        }
    }
}
