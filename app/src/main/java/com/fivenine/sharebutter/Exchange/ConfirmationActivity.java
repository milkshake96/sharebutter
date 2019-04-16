package com.fivenine.sharebutter.Exchange;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivenine.sharebutter.Message.MessageActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.MessageChannel;
import com.fivenine.sharebutter.models.TradeOffer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Date;

public class ConfirmationActivity extends AppCompatActivity implements View.OnClickListener {

    //Firebase
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;

    //Toolbar
    ImageView tbIvSupportAction;
    TextView tbTvTitle;
    TextView tbTvAction;

    //Main Page
    EditText etUserName;
    EditText etUserEmail;
    EditText etPhoneNumber;
    Button btnConfirm;

    //Var
    Item selectedItem;
    Item targetItem;
    TradeOffer currentTradeOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        init();
    }

    private void init(){
        tbIvSupportAction = findViewById(R.id.tb_iv_support_action);
        tbTvTitle = findViewById(R.id.tb_tv_title);
        tbTvAction = findViewById(R.id.tb_tv_action);
        tbTvTitle.setText("Confirmation");
        tbTvAction.setText("");
        tbIvSupportAction.setOnClickListener(this);

        etUserName = findViewById(R.id.et_username);
        etUserEmail = findViewById(R.id.et_useremail);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);

        Gson gson = new Gson();
        selectedItem = gson.fromJson(getIntent().getStringExtra(TraderExistingOffers.ITEM_SELECTED), Item.class);
        targetItem = gson.fromJson(getIntent().getStringExtra(TraderExistingOffers.ITEM_TARGETED), Item.class);
        currentTradeOffer = gson.fromJson(getIntent().getStringExtra(MessageActivity.CURRENT_TRADE_OFFER), TradeOffer.class);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tb_iv_support_action:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btn_confirm:
                confirm();
                break;
            default:
                break;
        }
    }

    private void confirm(){
        String userName = etUserName.getText().toString();
        String userEmail = etUserEmail.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();

        if(userName.isEmpty() || userEmail.isEmpty() || phoneNumber.isEmpty()){
            Toast.makeText(this, "Must enter all fields", Toast.LENGTH_SHORT).show();
        } else {
            if(currentTradeOffer == null){

                currentTradeOffer = new TradeOffer(new Date().getTime(),
                        targetItem.getItemOwnerId(), firebaseUser.getUid(),
                        String.valueOf(targetItem.getId()), "No Item",
                        TradeOffer.TRADE_PENDING);

                MessageChannel currentSelfMessageChannel = new MessageChannel(currentTradeOffer.getId(),
                        firebaseUser.getUid(), currentTradeOffer.getOwnerId(),
                        "Offer Sent",
                        String.valueOf(new Date().getTime())  ,0);

                MessageChannel currentTargetMessageChannel = new MessageChannel(currentTradeOffer.getId(),
                        firebaseUser.getUid(), currentTradeOffer.getOwnerId(),
                        "New Offer",
                        String.valueOf(new Date().getTime())  ,1);

                //Store into self id
                //Trade Offer
                databaseReference.child(getString(R.string.dbname_trade_offers))
                        .child(currentTradeOffer.getOwnerId()).child(String.valueOf(currentTradeOffer.getId()))
                        .setValue(currentTradeOffer);

                //Message Channel
                databaseReference.child(getString(R.string.dbname_message_channels))
                        .child(currentTradeOffer.getOwnerId()).child(String.valueOf(currentSelfMessageChannel.getId()))
                        .setValue(currentSelfMessageChannel);

                //Store into target user id
                //Trade Offer
                databaseReference.child(getString(R.string.dbname_trade_offers))
                        .child(currentTradeOffer.getRequesterId()).child(String.valueOf(currentTradeOffer.getId()))
                        .setValue(currentTradeOffer);

                //Message Channel
                databaseReference.child(getString(R.string.dbname_message_channels))
                        .child(currentTradeOffer.getRequesterId()).child(String.valueOf(currentSelfMessageChannel.getId()))
                        .setValue(currentTargetMessageChannel);
            }

            if(firebaseUser.getUid().equals(currentTradeOffer.getOwnerId())) {

                currentTradeOffer.setOwnerUsername(userName);
                currentTradeOffer.setOwnerEmail(userEmail);
                currentTradeOffer.setOwnerPhoneNumber(phoneNumber);
                targetItem.setTraded(true);
                selectedItem.setTraded(true);
                currentTradeOffer.setStatus(TradeOffer.TRADE_SUCCESSFUL);

            } else if(firebaseUser.getUid().equals(currentTradeOffer.getRequesterId())){

                currentTradeOffer.setRequesterUsername(userName);
                currentTradeOffer.setRequesterEmail(userEmail);
                currentTradeOffer.setRequesterPhoneNumber(phoneNumber);
                currentTradeOffer.setRequesterItemId(String.valueOf(selectedItem.getId()));
            }

            updateConfirm();
        }
    }

    private void updateConfirm(){
        //Update Item Owner (trade offer)
        databaseReference.child(getString(R.string.dbname_trade_offers)).child(currentTradeOffer.getOwnerId())
                .child(String.valueOf(currentTradeOffer.getId())).setValue(currentTradeOffer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //Update Item Requester (trade offer)
                databaseReference.child(getString(R.string.dbname_trade_offers)).child(currentTradeOffer.getRequesterId())
                        .child(String.valueOf(currentTradeOffer.getId())).setValue(currentTradeOffer).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //Update Item Owner (Item)
                        databaseReference.child(getString(R.string.dbname_items)).child(currentTradeOffer.getOwnerId())
                                .child(String.valueOf(targetItem.getId())).setValue(targetItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //Update Item Owner (Item)
                                databaseReference.child(getString(R.string.dbname_items)).child(currentTradeOffer.getRequesterId())
                                        .child(String.valueOf(selectedItem.getId())).setValue(selectedItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ConfirmationActivity.this, "Updated Item", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.putExtra(MessageActivity.CURRENT_TRADE_OFFER, new Gson().toJson(currentTradeOffer));
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }


}
