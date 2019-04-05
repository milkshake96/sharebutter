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
import com.fivenine.sharebutter.models.Message;
import com.github.library.bubbleview.BubbleTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<Message> messageList;
    private FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Message> messageList){
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.layout_message_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_message_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int position) {

        Message message = messageList.get(position);
        viewHolder.tvMessage.setText(message.getMessage());
        viewHolder.tvMessageTimeSent.setVisibility(View.GONE);

        //Add Profile Picture Here
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

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
        if(messageList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
