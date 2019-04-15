package com.fivenine.sharebutter.RecordActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener{

    //Toolbar
    private ImageView tbIvSupportAction;
    private TextView tbTvAction;
    private TextView tbTvTitle;

    //Other widgets
    //History Card
    private ImageView ivSenderOffer;
    private TextView tvSenderName;
    private ImageView ivReceiverOffer;
    private TextView tvReceiverName;

    //Descriptions
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvUserPhoneNumber;

    //Vars
    OfferRecordCard currentTradeOffer;

    //Firebase
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        init();
    }

    private void init(){
        currentTradeOffer = new Gson().fromJson(getIntent().getStringExtra(OfferRecordAdapter.CURRENT_TRADE), OfferRecordCard.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        tbIvSupportAction = findViewById(R.id.tb_iv_support_action);
        tbIvSupportAction.setOnClickListener(this);

        tbTvAction = findViewById(R.id.tb_tv_action);
        tbTvAction.setText("");

        tbTvTitle = findViewById(R.id.tb_tv_title);
        tbTvTitle.setText("History");

        ivSenderOffer = findViewById(R.id.iv_sender_offer_img);
        tvSenderName = findViewById(R.id.tv_sender_name);
        ivReceiverOffer = findViewById(R.id.iv_receiver_offer_img);
        tvReceiverName = findViewById(R.id.tv_receiver_name);

        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvUserPhoneNumber = findViewById(R.id.tv_user_phone_number);

        Picasso.get()
                .load(currentTradeOffer.getTraderItem().getImg1URL())
                .centerCrop()
                .fit()
                .into(ivSenderOffer);

        Picasso.get()
                .load(currentTradeOffer.getOwnerItem().getImg1URL())
                .centerCrop()
                .fit()
                .into(ivReceiverOffer);

        tvSenderName.setText(currentTradeOffer.getTraderItem().getItemOwnerName());
        tvReceiverName.setText(currentTradeOffer.getOwnerItem().getItemOwnerName());

        if(firebaseUser.getUid().equals(currentTradeOffer.getTradeOffer().getOwnerId())){
            tvUserName.setText(currentTradeOffer.getTradeOffer().getRequesterUsername());
            tvUserEmail.setText(currentTradeOffer.getTradeOffer().getRequesterEmail());
            tvUserPhoneNumber.setText(currentTradeOffer.getTradeOffer().getRequesterPhoneNumber());
        } else {
            tvUserName.setText(currentTradeOffer.getTradeOffer().getOwnerUsername());
            tvUserEmail.setText(currentTradeOffer.getTradeOffer().getOwnerEmail());
            tvUserPhoneNumber.setText(currentTradeOffer.getTradeOffer().getOwnerPhoneNumber());
        }
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
