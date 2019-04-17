package com.fivenine.sharebutter.Message;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<Chat> chatList;
    private FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_message_item_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_message_item_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, int position) {
        Chat currentChat;
        Chat previousChat = null;
        if (position != 0) {
            previousChat = chatList.get(position - 1);
        }
        currentChat = chatList.get(position);
        viewHolder.tvMessage.setText(currentChat.getMessage());

        if(previousChat != null) {
            if (currentChat.getTimeInMillis() - previousChat.getTimeInMillis() > (1000L * 60L * 5L)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, h:mm a");
                String currentDate = simpleDateFormat.format(new Date(currentChat.getTimeInMillis()));
                viewHolder.tvMessageTimeSent.setText(currentDate);
            } else {
                viewHolder.tvMessageTimeSent.setVisibility(View.GONE);
            }
        } else {
            viewHolder.tvMessageTimeSent.setVisibility(View.GONE);
        }

        //Add Profile Picture Here
        if(!currentChat.getSenderImgUrl().isEmpty()){
            Picasso.get()
                    .load(currentChat.getSenderImgUrl())
                    .centerCrop()
                    .fit()
                    .into(viewHolder.ivChatProfilePic);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMessage;
        public TextView tvMessageTimeSent;
        public ImageView ivChatProfilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessageTimeSent = itemView.findViewById(R.id.tv_time_message_sent);
            tvMessage = itemView.findViewById(R.id.tv_message);
            ivChatProfilePic = itemView.findViewById(R.id.iv_chat_profile_pic);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
