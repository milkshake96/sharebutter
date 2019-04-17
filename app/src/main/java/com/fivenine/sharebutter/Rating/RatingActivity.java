package com.fivenine.sharebutter.Rating;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivenine.sharebutter.Message.MessageActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.RecordActivity.OfferRecordCard;
import com.fivenine.sharebutter.models.UserAccountSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {

    //Widgets
    //ToolBar
    ImageView tbIvSupportAction;
    TextView tbTvTitle;
    ImageView tbIvAction;

    //CardView
    ImageView ivSenderOffer;
    TextView tvSenderOffer;
    ImageView ivReceiverOffer;
    TextView tvReceiverOffer;

    //Others
    ImageView ivLike;
    ImageView ivDislike;

    //Firebase + Vars
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    OfferRecordCard currentTradeOffer;

    UserAccountSettings userOwnAccountSettings;
    UserAccountSettings userTargetAccountSettings;
    ValueEventListener accountListener;
    ValueEventListener targetAccountListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        init();
    }

    private void init(){
        currentTradeOffer = new Gson().fromJson(getIntent().getStringExtra(MessageActivity.COMPLETED_TRADE_OFFER), OfferRecordCard.class);

        tbIvSupportAction = findViewById(R.id.tb_iv_support_action);
        tbTvTitle = findViewById(R.id.tb_tv_title);
        tbIvAction = findViewById(R.id.tb_iv_action);
        tbIvAction.setOnClickListener(this);

        ivSenderOffer = findViewById(R.id.iv_sender_offer_img);
        ivReceiverOffer = findViewById(R.id.iv_receiver_offer_img);
        tvSenderOffer = findViewById(R.id.tv_sender_name);
        tvReceiverOffer = findViewById(R.id.tv_receiver_name);

        Picasso.get()
                .load(currentTradeOffer.getTraderItem().getImg1URL())
                .fit()
                .centerCrop()
                .into(ivSenderOffer);

        Picasso.get()
                .load(currentTradeOffer.getOwnerItem().getImg1URL())
                .fit()
                .centerCrop()
                .into(ivReceiverOffer);

        tvSenderOffer.setText(currentTradeOffer.getTraderItem().getItemOwnerName());
        tvReceiverOffer.setText(currentTradeOffer.getOwnerItem().getItemOwnerName());

        ivDislike = findViewById(R.id.iv_dislike);
        ivDislike.setOnClickListener(this);

        ivLike = findViewById(R.id.iv_like);
        ivLike.setOnClickListener(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getCurrentUserAccountSettings();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_dislike:
                dislikeClicked();
                break;
            case R.id.iv_like:
                likeClicked();
                break;
            case R.id.tb_iv_action:
                closeClicked();
                break;
            default:
                break;
        }
    }

    private void likeClicked(){
        long likes = userOwnAccountSettings.getLikes() + 1;
        updateLikes(likes);
    }

    private void dislikeClicked(){
        long likes = userOwnAccountSettings.getLikes() - 1;
        updateLikes(likes);
    }

    private void closeClicked(){
        finish();
    }

    private void getCurrentUserAccountSettings(){
        if(accountListener == null) {
            accountListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userOwnAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                    targetUserAccountSettingListener();

                    if(firebaseUser.getUid().equals(currentTradeOffer.getOwnerItem().getItemOwnerId())){
                        databaseReference.child(getString(R.string.dbname_user_account_settings))
                                .child(currentTradeOffer.getTraderItem().getItemOwnerId())
                                .addValueEventListener(targetAccountListener);
                    } else {
                        databaseReference.child(getString(R.string.dbname_user_account_settings))
                                .child(currentTradeOffer.getOwnerItem().getItemOwnerId())
                                .addValueEventListener(targetAccountListener);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }
        databaseReference.child(getString(R.string.dbname_user_account_settings))
                .child(firebaseUser.getUid()).removeEventListener(accountListener);

        databaseReference.child(getString(R.string.dbname_user_account_settings))
                .child(firebaseUser.getUid()).addValueEventListener(accountListener);
    }

    private ValueEventListener targetUserAccountSettingListener(){
        if(targetAccountListener == null){
            targetAccountListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userTargetAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }

        return targetAccountListener;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(accountListener != null){
            databaseReference.child(getString(R.string.dbname_user_account_settings))
                    .child(firebaseUser.getUid()).removeEventListener(accountListener);
        }

        if(targetAccountListener != null){
            if(firebaseUser.getUid().equals(currentTradeOffer.getOwnerItem().getItemOwnerId())){
                databaseReference.child(getString(R.string.dbname_user_account_settings))
                        .child(currentTradeOffer.getTraderItem().getItemOwnerId())
                        .removeEventListener(targetAccountListener);
            } else {
                databaseReference.child(getString(R.string.dbname_user_account_settings))
                        .child(currentTradeOffer.getOwnerItem().getItemOwnerId())
                        .removeEventListener(targetAccountListener);
            }
        }
    }

    private void updateLikes(long numberOfLike){
        userTargetAccountSettings.setLikes(numberOfLike);

        if(firebaseUser.getUid().equals(currentTradeOffer.getOwnerItem().getItemOwnerId())){
            databaseReference.child(getString(R.string.dbname_user_account_settings))
                    .child(currentTradeOffer.getTraderItem().getItemOwnerId())
                    .setValue(userTargetAccountSettings).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RatingActivity.this, "Thank You.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        } else {
            databaseReference.child(getString(R.string.dbname_user_account_settings))
                    .child(currentTradeOffer.getOwnerItem().getItemOwnerId())
                    .setValue(userTargetAccountSettings).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RatingActivity.this, "Thank You.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }
}
