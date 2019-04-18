package com.fivenine.sharebutter.Profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fivenine.sharebutter.Home.HomeFragment;
import com.fivenine.sharebutter.Home.ItemInfoActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.DisplayOfferAdapter;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.User;
import com.fivenine.sharebutter.models.UserAccountSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OtherProfileActivity extends AppCompatActivity implements View.OnClickListener{

    //Widgets
    //Toolbar
    private ImageView tbIvSupportAction;
    private TextView tbTvTitle;
    private TextView tbTvAction;

    //Profile
    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvDescription;
    private TextView tvOfferNum;
    private TextView tvLikeNum;
    private TextView tvState;

    //Item
    private GridView gvProfileOfferUploaded;
    private TextView noOfferMsg;
    private ProgressBar progressBar;

    //Firebase + Vars
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    UserAccountSettings itemOwnerAccountSetting;
    User itemOwnerUser;
    DisplayOfferAdapter displayOfferAdapter;
    ArrayList<Item> postedItem;
    AdapterView.OnItemClickListener postedItemClickListener;

    ValueEventListener userListener;
    ValueEventListener itemListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        init();
    }

    private void init() {
        tbIvSupportAction = findViewById(R.id.tb_iv_support_action);
        tbIvSupportAction.setOnClickListener(this);

        tbTvTitle = findViewById(R.id.tb_tv_title);
        tbTvAction = findViewById(R.id.tb_tv_action);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvOfferNum = findViewById(R.id.tvOfferNum);
        tvUserName = findViewById(R.id.tvProfileUserName);
        tvDescription = findViewById(R.id.tvDecription);
        tvLikeNum = findViewById(R.id.tvLikesNum);
        tvState = findViewById(R.id.tv_state);

        gvProfileOfferUploaded = findViewById(R.id.gvProfileOfferUploaded);

        noOfferMsg = findViewById(R.id.tv_no_offer_msg);
        noOfferMsg.setVisibility(View.GONE);
        progressBar = findViewById(R.id.profileProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        postedItem = new ArrayList<>();
        postedItemClickListener = onOfferClickListener();
        getUserProfile();
    }

    private void getUserProfile() {
        progressBar.setVisibility(View.VISIBLE);

        itemOwnerUser = new Gson().fromJson(getIntent().getStringExtra(ItemInfoActivity.ITEM_OWNER), User.class);
        databaseReference.child(getString(R.string.dbname_user_account_settings))
                .child(itemOwnerUser.getUser_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemOwnerAccountSetting = dataSnapshot.getValue(UserAccountSettings.class);
                databaseReference.child(getString(R.string.dbname_items))
                        .child(itemOwnerUser.getUser_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot curItem : dataSnapshot.getChildren()) {
                            Item newItem = curItem.getValue(Item.class);

                            if (newItem.getTraded() || newItem.getDeleted())
                                continue;

                            postedItem.add(newItem);
                        }

                        displayOfferAdapter = new DisplayOfferAdapter(OtherProfileActivity.this, postedItem);
                        gvProfileOfferUploaded.setAdapter(displayOfferAdapter);
                        gvProfileOfferUploaded.setOnItemClickListener(postedItemClickListener);

                        if(postedItem.size() <= 0 )
                            noOfferMsg.setVisibility(View.VISIBLE);
                        else
                            noOfferMsg.setVisibility(View.GONE);

                        if(itemOwnerAccountSetting != null){
                            initializeValue();
                            monitorUserStatus();
                            monitorItemStatus();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeValue() {
        tbTvTitle.setText(itemOwnerAccountSetting.getDisplay_name() + "'s Profile");
        tbTvAction.setText("");

        if (!itemOwnerAccountSetting.getProfile_photo().isEmpty()) {
            Picasso.get()
                    .load(itemOwnerAccountSetting.getProfile_photo())
                    .fit()
                    .centerCrop()
                    .into(ivProfileImage);
        }

        tvOfferNum.setText(String.valueOf(itemOwnerAccountSetting.getOffers()));
        tvUserName.setText(itemOwnerAccountSetting.getDisplay_name());

        if (itemOwnerAccountSetting.getDescription().isEmpty()) {
            tvDescription.setText(itemOwnerAccountSetting.getDisplay_name() + " did not left any description.");
        } else {
            tvDescription.setText(itemOwnerAccountSetting.getDescription());
        }

        tvLikeNum.setText(String.valueOf(itemOwnerAccountSetting.getLikes()));
        tvState.setText(itemOwnerAccountSetting.getState());
    }

    private void monitorUserStatus() {
        databaseReference.child(getString(R.string.dbname_user_account_settings))
                .child(itemOwnerUser.getUser_id()).removeEventListener(userListener());

        databaseReference.child(getString(R.string.dbname_user_account_settings))
                .child(itemOwnerUser.getUser_id()).addValueEventListener(userListener());
    }

    private void monitorItemStatus() {
        databaseReference.child(getString(R.string.dbname_items))
                .child(itemOwnerUser.getUser_id()).removeEventListener(itemListener());

        databaseReference.child(getString(R.string.dbname_items))
                .child(itemOwnerUser.getUser_id()).addValueEventListener(itemListener());
    }

    private ValueEventListener userListener() {
        if (userListener == null) {
            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    initializeValue();
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }

        return userListener;
    }

    private ValueEventListener itemListener() {
        if (itemListener == null) {
            itemListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    postedItem.clear();
                    for (DataSnapshot curItem : dataSnapshot.getChildren()) {
                        Item newItem = curItem.getValue(Item.class);

                        if (newItem.getTraded() || newItem.getDeleted())
                            continue;

                        postedItem.add(newItem);
                    }

                    displayOfferAdapter = new DisplayOfferAdapter(OtherProfileActivity.this, postedItem);
                    gvProfileOfferUploaded.setAdapter(displayOfferAdapter);
                    gvProfileOfferUploaded.setOnItemClickListener(postedItemClickListener);

                    if(postedItem.size() <= 0 )
                        noOfferMsg.setVisibility(View.VISIBLE);
                    else
                        noOfferMsg.setVisibility(View.GONE);

                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }

        return itemListener;
    }

    private AdapterView.OnItemClickListener onOfferClickListener(){
        return new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new Gson();
                String selectedItem = gson.toJson(postedItem.get(position));

                Intent intent = new Intent(OtherProfileActivity.this, ItemInfoActivity.class);
                intent.putExtra(HomeFragment.SELECTED_ITEM, selectedItem);

                startActivity(intent);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseReference.child(getString(R.string.dbname_user_account_settings))
                .child(itemOwnerUser.getUser_id()).removeEventListener(userListener());

        databaseReference.child(getString(R.string.dbname_items))
                .child(itemOwnerUser.getUser_id()).removeEventListener(itemListener());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tb_iv_support_action:
                finish();
                break;
            default:
                break;
        }
    }
}
