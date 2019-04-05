package com.fivenine.sharebutter.Message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, ArrayList<Message> messages){
        super(context, 0, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        if(convertView == null){
            if(message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_message_item_right, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_message_item_left, parent, false);
            }
        }

        TextView tvMessage = convertView.findViewById(R.id.tv_message);
        TextView tvMesageTime = convertView.findViewById(R.id.tv_time_message_sent);
        ImageView ivProfilePic = convertView.findViewById(R.id.iv_chat_profile_pic);

        tvMesageTime.setText("");
        tvMesageTime.setVisibility(View.GONE);

        tvMessage.setText(message.getMessage());

        return convertView;
    }

    

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
