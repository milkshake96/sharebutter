package com.fivenine.sharebutter.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.Authentication.LoginActivity;
import com.fivenine.sharebutter.Authentication.SignUpActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.RecordActivity.OfferRecordsActivity;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "CategoriesActivity";
    public static final int RC_OFFER_RECORD = 1;
    public static final int RC_OFFER_HISTORY = 2;

    ImageView ivOfferRecords;
    ImageView ivHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        ImageView close = (ImageView) findViewById(R.id.redclose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriesActivity.this, HomeActivity.class);
                startActivity(intent);

                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        init();
    }

    private void init(){
        ivOfferRecords = findViewById(R.id.ic_next4);
        ivOfferRecords.setOnClickListener(this);

        ivHistory = findViewById(R.id.ic_next5);
        ivHistory.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ic_next4:
                onOfferRecordClicked();
                break;
            case R.id.ic_next5:
                onHistoryClicked();
                break;
            default:
                break;
        }
    }

    private void onOfferRecordClicked(){
        Intent recordOffer = new Intent(CategoriesActivity.this, OfferRecordsActivity.class);
        recordOffer.putExtra(TAG, RC_OFFER_RECORD);
        startActivityForResult(recordOffer, RC_OFFER_RECORD);
    }

    private void onHistoryClicked(){
        Intent recordOffer = new Intent(CategoriesActivity.this, OfferRecordsActivity.class);
        recordOffer.putExtra(TAG, RC_OFFER_HISTORY);
        startActivityForResult(recordOffer, RC_OFFER_HISTORY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
