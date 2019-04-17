package com.fivenine.sharebutter.Rating;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.UserAccountSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    UserAccountSettings userAccountSettings;
    ValueEventListener accountListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        init();
    }

    private void init(){
        tbIvSupportAction = findViewById(R.id.tb_iv_support_action);
        tbTvTitle = findViewById(R.id.tb_tv_title);
        tbIvAction = findViewById(R.id.tb_iv_action);
        tbIvAction.setOnClickListener(this);

        ivSenderOffer = findViewById(R.id.iv_sender_offer_img);
        ivReceiverOffer = findViewById(R.id.iv_receiver_offer_img);
        tvSenderOffer = findViewById(R.id.tv_sender_name);
        tvReceiverOffer = findViewById(R.id.tv_receiver_name);

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
        userAccountSettings.setLikes(userAccountSettings.getLikes() + 1);
        finish();
    }

    private void dislikeClicked(){
        userAccountSettings.setLikes(userAccountSettings.getLikes() - 1);
        finish();
    }

    private void closeClicked(){
        finish();
    }

    private void getCurrentUserAccountSettings(){
        if(accountListener == null) {
            accountListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }

        databaseReference.child(getString(R.string.dbname_user_account_settings))
                .child(firebaseUser.getUid()).addValueEventListener(accountListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(accountListener != null){
            databaseReference.child(getString(R.string.dbname_user_account_settings))
                    .child(firebaseUser.getUid()).removeEventListener(accountListener);
        }
    }
}
