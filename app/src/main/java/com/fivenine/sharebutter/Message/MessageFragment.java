package com.fivenine.sharebutter.Message;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.MessageChannel;
import com.fivenine.sharebutter.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MessageFragment extends Fragment {

    private static final String TAG = "MessageFragment";
    public static final String MESSAGE_CHANNEL = "message_channel";
//    private Button btnLogin;

    View view;
    //Widgets
    //MessageChannel List
    RecyclerView rvMessageList;
    MessageAdapter messageAdapter;

    //Toolbar
    ImageView ivSupportAction;
    TextView tvTitle;
    TextView tvAction;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ValueEventListener messageChannelListener;
    ValueEventListener userListListener;

    FirebaseUser firebaseUser;

    List<User> userList;
    List<MessageChannel> messageChannelList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);

        init();
        return view;
    }

    private void init(){
        //Widgets
        ivSupportAction = view.findViewById(R.id.tb_iv_support_action);
        tvTitle = view.findViewById(R.id.tb_tv_title);
        tvAction = view.findViewById(R.id.tb_tv_action);

        ivSupportAction.setVisibility(View.GONE);
        tvAction.setText("");
        tvTitle.setText("Message");

        rvMessageList = view.findViewById(R.id.rv_message_list);
        rvMessageList.setHasFixedSize(true);
        rvMessageList.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Firebase
        userList = new ArrayList<>();
        messageChannelList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        messageChannelListener = getMessageChannelListener();

        databaseReference.child(getString(R.string.dbname_message_channels))
                .child(firebaseUser.getUid()).addListenerForSingleValueEvent(messageChannelListener);

        userListListener = getUserListListener();
    }

    private ValueEventListener getMessageChannelListener(){
        messageChannelList.clear();

        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot existingMessageChannel : dataSnapshot.getChildren()) {
                    MessageChannel messageChannel = existingMessageChannel.getValue(MessageChannel.class);
                    messageChannelList.add(messageChannel);
                }

                databaseReference.child(getString(R.string.dbname_users)).addListenerForSingleValueEvent(userListListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener getUserListListener(){
        userList.clear();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot currentUser : dataSnapshot.getChildren()){
                    User tempUser = currentUser.getValue(User.class);
                    if(tempUser.getUser_id().equals(firebaseUser.getUid())){
                        continue;
                    }

                    for(int i = 0; i < messageChannelList.size(); i++){
                        if(messageChannelList.get(i).getSenderId().equals(tempUser.getUser_id()) ||
                                messageChannelList.get(i).getReceiverId().equals(tempUser.getUser_id())){
                            userList.add(tempUser);
                        }
                    }
                }

                messageAdapter = new MessageAdapter(getActivity(), messageChannelList, userList);
                rvMessageList.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
