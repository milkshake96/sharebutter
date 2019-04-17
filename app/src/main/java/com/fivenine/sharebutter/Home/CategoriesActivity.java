package com.fivenine.sharebutter.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fivenine.sharebutter.Authentication.LoginActivity;
import com.fivenine.sharebutter.Authentication.SignUpActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.RecordActivity.OfferRecordsActivity;
import com.fivenine.sharebutter.models.Item;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "CategoriesActivity";
    public static final int RC_OFFER_RECORD = 1;
    public static final int RC_OFFER_HISTORY = 2;
    public static final int RC_CATEGORIES = 3;

    ImageView ivCategory1;
    ImageView ivCategory2;
    ImageView ivCategory3;

    ImageView ivOfferRecords;
    ImageView ivHistory;

    ProgressBar categoryProgressBar;

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
        ivCategory1 = findViewById(R.id.ic_next);
        ivCategory1.setOnClickListener(this);

        ivCategory2 = findViewById(R.id.ic_next2);
        ivCategory2.setOnClickListener(this);

        ivCategory3 = findViewById(R.id.ic_next3);
        ivCategory3.setOnClickListener(this);

        ivOfferRecords = findViewById(R.id.ic_next4);
        ivOfferRecords.setOnClickListener(this);

        ivHistory = findViewById(R.id.ic_next5);
        ivHistory.setOnClickListener(this);

        categoryProgressBar = findViewById(R.id.categoryProgressBar);
        categoryProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ic_next:
                onCategoryClicked(0);
                break;
            case R.id.ic_next2:
                onCategoryClicked(1);
                break;
            case R.id.ic_next3:
                onCategoryClicked(2);
                break;
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

    private void onCategoryClicked(int category){
        Intent categoryIntent = new Intent(CategoriesActivity.this, FilteredCategoryActivity.class);
        categoryIntent.putExtra(TAG, Item.CATEGORIES[category]);
        startActivityForResult(categoryIntent, RC_CATEGORIES);
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
