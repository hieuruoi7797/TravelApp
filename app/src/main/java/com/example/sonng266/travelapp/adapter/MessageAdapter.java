package com.example.sonng266.travelapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sonng266.travelapp.R;
import com.example.sonng266.travelapp.models.MessageModel;
import com.example.sonng266.travelapp.models.UserModel;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<MessageModel> messageModels;
    private Context context;
    private String userName;
    private int viewType;
    public MessageAdapter(List<MessageModel> messageModels, Context context,String userName) {
        this.messageModels = messageModels;
        this.context = context;
        this.userName = userName;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == 1){
           View itemView = layoutInflater.inflate(R.layout.item_rv_chat,parent,false);
            return new MessageViewHolder(itemView);
        }else if (viewType == 2){
           View itemView = layoutInflater.inflate(R.layout.item_rv_reply,parent,false);
            return new MessageViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.setData(messageModels.get(position));
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
       if (userName.equals(messageModels.get(position).userName)){
           viewType = 1;
           return 1;
       }else{
           viewType =2;
           return 2;
       }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        ImageView ivAvatar;
        TextView repUserName;
        TextView reply;
        TextView mycontent;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar_reply);
            repUserName = itemView.findViewById(R.id.tv_name_reply);
            reply = itemView.findViewById(R.id.tv_reply);
            mycontent = itemView.findViewById(R.id.tv_text_chat);
        }

        public void setData(MessageModel messageModel){
            if (viewType == 2){
                Picasso.with(context).load(messageModel.Uri)
                        .transform(new CropCircleTransformation())
                        .into(ivAvatar);
                repUserName.setText(messageModel.userName);
                reply.setText(messageModel.content);
            }else if (viewType == 1){
                Log.d("TAG", "setData: " + messageModel.content);
                mycontent.setText(messageModel.content);
            }
        }
    }
}
