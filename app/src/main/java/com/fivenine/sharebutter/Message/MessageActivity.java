package com.fivenine.sharebutter.Message;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fivenine.sharebutter.Home.HomeFragment;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.BottomNavigationViewHelper;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.Message;
import com.fivenine.sharebutter.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Date;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MessageActivity";

    //Materials view all offers
    LinearLayout llViewAllOffers;
    TextView tvViewAllOffers;
    ImageView ivViewAllOffers;

    //Materials (When item to offer does not exist)
    RelativeLayout rlNoOfferPage;
    ImageView ivUploadPhoto;

    //Materials (When item to offer exist)
    LinearLayout llOfferExistPage;
    ImageView ivOfferPhoto;
    TextView tvItemName;
    TextView tvItemDescription;
    TextView tvItemExpDate;

    //Chat Box
    RecyclerView rvChatList;
    ArrayList<Message> messageArrayList;
    MessageAdapter messageAdapter;

    RelativeLayout rlChatBox;
    EditText etChatInput;
    ImageView ivSendChat;

    //Toolbar
    ImageView ivSupportAction;
    TextView tvTitle;
    TextView tvAction;

    //Firebase
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ValueEventListener messageListener;
    ValueEventListener userListener;

    User targetItemUser;
    Item targetItem;
    Item ownedItem;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        init();
    }

    private void init() {
        //Firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Initialize Target Item
        Gson gson = new Gson();
        targetItem = gson.fromJson(getIntent().getStringExtra(HomeFragment.SELECTED_ITEM), Item.class);

        //Initialize Firebase
        userListener = userListener();
        messageListener = messageListener();

        databaseReference.child(MessageActivity.this.getString(R.string.dbname_users))
                .child(targetItem.getItemOwnerId()).addListenerForSingleValueEvent(userListener);

        databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats))
                .child(firebaseUser.getUid()).addValueEventListener(messageListener);

        //View All Offers
        llViewAllOffers = findViewById(R.id.ll_view_all_offer);
        tvViewAllOffers = findViewById(R.id.tv_view_all_offer);
        ivViewAllOffers = findViewById(R.id.iv_view_all_offer);

        //When no item offered for trade
        rlNoOfferPage = findViewById(R.id.rl_no_offer_exist_page);
        ivUploadPhoto = findViewById(R.id.iv_upload_photo);

        //When selected an item for trade
        llOfferExistPage = findViewById(R.id.ll_offer_exist_page);
        ivOfferPhoto = findViewById(R.id.iv_uploaded_photo);
        tvItemName = findViewById(R.id.tv_item_name);
        tvItemDescription = findViewById(R.id.tv_item_description);
        tvItemExpDate = findViewById(R.id.tv_item_exp_date);

        //Chat box
        rlChatBox = findViewById(R.id.rl_chat_box);
        rvChatList = findViewById(R.id.rv_chat_list);
        rvChatList.setHasFixedSize(true);
        messageArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getApplicationContext()));
        linearLayoutManager.setStackFromEnd(true);
        rvChatList.setLayoutManager(linearLayoutManager);

        etChatInput = findViewById(R.id.et_chat_input);

        ivSendChat = findViewById(R.id.iv_send_chat);
        ivSendChat.setOnClickListener(this);

        //Toolbar
        ivSupportAction = findViewById(R.id.tb_iv_support_action);
        tvTitle = findViewById(R.id.tb_tv_title);
        tvTitle.setText("");
        tvAction = findViewById(R.id.tb_tv_action);
        tvAction.setText("");
    }

    private void initializeData(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_upload_photo:
                uploadOfferToTrade();
                break;
            case R.id.iv_uploaded_photo:
                break;
            case R.id.iv_send_chat:
                sendChat();
                break;
            case R.id.tb_iv_support_action:
                backButtonClicked();
                break;
            case R.id.tb_tv_action:
                break;
            default:
                break;
        }
    }

    private void uploadOfferToTrade(){

    }

    private void sendChat(){
        String message = etChatInput.getText().toString();

        if(message.isEmpty()){
//            Toast.makeText(this, "You can't send empty message", Toast.LENGTH_SHORT).show();
        } else {
            Message newMessage = new Message(message, firebaseUser.getUid(), targetItem.getItemOwnerId(), new Date().getTime());
            databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats))
                    .child(newMessage.getSender()).push().setValue(newMessage);

            databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats))
                    .child(newMessage.getReceiver()).push().setValue(newMessage);

            etChatInput.setText("");
        }
    }

    private void backButtonClicked(){
        finish();
    }

    private ValueEventListener messageListener(){
        messageArrayList = new ArrayList<>();

        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageArrayList.clear();
                for(DataSnapshot chat : dataSnapshot.getChildren()){
                    Message message = chat.getValue(Message.class);
                    messageArrayList.add(message);
                }

                messageAdapter = new MessageAdapter(MessageActivity.this, messageArrayList);
                rvChatList.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener userListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                targetItemUser = user;

                tvTitle.setText(targetItemUser.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseReference.child(MessageActivity.this.getString(R.string.dbname_users))
                .child(targetItem.getItemOwnerId()).removeEventListener(userListener);

        databaseReference.child(MessageActivity.this.getString(R.string.dbname_chats))
                .child(firebaseUser.getUid()).removeEventListener(messageListener);
    }

    //    private void setupBottomNavigationView(){
//        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
//        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.btmNavViewBar);
//        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
//        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationViewEx);
//
//        Menu menu = bottomNavigationViewEx.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
//    }
}
