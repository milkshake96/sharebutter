package com.fivenine.sharebutter.Exchange;

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
import com.fivenine.sharebutter.models.TradeOffer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

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
            if(firebaseUser.getUid().equals(currentTradeOffer.getOwnerId())) {

                currentTradeOffer.setOwnerUsername(userName);
                currentTradeOffer.setOwnerEmail(userEmail);
                currentTradeOffer.setOwnerPhoneNumber(phoneNumber);

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
        databaseReference.child(getString(R.string.dbname_trade_offers)).child(firebaseUser.getUid())
                .child(String.valueOf(currentTradeOffer.getId())).setValue(currentTradeOffer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ConfirmationActivity.this, "Updated Item", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
