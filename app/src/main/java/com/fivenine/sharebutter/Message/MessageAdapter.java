package com.fivenine.sharebutter.Message;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivenine.sharebutter.Home.ItemInfoActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.MessageChannel;
import com.fivenine.sharebutter.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private Context context;
    private List<MessageChannel> messageChannelList;
    private List<User> userList;

    public MessageAdapter(Context context, List<MessageChannel> messageChannelList, List<User> userList) {
        this.context = context;
        this.messageChannelList = messageChannelList;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_message_item, viewGroup, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int position) {

        MessageChannel currentMessageChannel = messageChannelList.get(position);

        for(int i = 0; i < userList.size(); i++){
            if(currentMessageChannel.getSenderId().equals(userList.get(i).getUser_id()) ||
                    currentMessageChannel.getReceiverId().equals(userList.get(i).getUser_id())){
                viewHolder.tvUserName.setText(userList.get(i).getUsername());
            }
        }

        //viewHolder.ivProfilePic;
//        for(int i = 0; i <userList.size(); i++){
//        }

        Date latestDate = new Date(Long.parseLong(currentMessageChannel.getLatestMessageTime()));
        String time = new SimpleDateFormat("h:mm a").format(latestDate);

        viewHolder.tvLatestMessage.setText(currentMessageChannel.getLatestMessage());
        viewHolder.tvLatestMessageTime.setText(time);
        viewHolder.tvUnseenMessages.setText(String.valueOf(currentMessageChannel.getUnSeenMessages()));
    }

    @Override
    public int getItemCount() {
        return messageChannelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public CircleImageView ivProfilePic;
        public TextView tvUserName;
        public TextView tvLatestMessage;
        public TextView tvLatestMessageTime;
        public TextView tvUnseenMessages;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePic = itemView.findViewById(R.id.iv_message_item_profile_pic);
            tvUserName = itemView.findViewById(R.id.tv_message_item_user_name);
            tvLatestMessage = itemView.findViewById(R.id.tv_message_item_last_message);
            tvLatestMessageTime = itemView.findViewById(R.id.tv_message_item_message_time);
            tvUnseenMessages = itemView.findViewById(R.id.tv_message_item_unseen_messages);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, MessageActivity.class);
            Gson gson = new Gson();

            MessageChannel messageChannel = messageChannelList.get(getAdapterPosition());
            String currentMessageChannel = gson.toJson(messageChannel);

            intent.putExtra(MessageFragment.MESSAGE_CHANNEL, currentMessageChannel);
            v.getContext().startActivity(intent);
        }
    }
}