package com.fivenine.sharebutter.AddOffer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.Item;

import java.util.ArrayList;

public class AddOfferActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddOfferActivity";

    //Main Page Materials
    ViewPager vpImagesSelected;
    ImageView ivCurrentImgPosition;
    ImageView ivCalendar;
    EditText etItemName;
    EditText etDescription;
    EditText etHashTag;
    EditText etCity;
    EditText etState;
    EditText etExpiredDate;

    ArrayAdapter<String> spinnerAdapter;

    //Toolbar Materials
    ImageView ivBack;
    TextView tvTitle;
    TextView tvAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        init();
    }

    private void init(){
        vpImagesSelected = findViewById(R.id.vp_selected_upload_offer);
        ivCurrentImgPosition = findViewById(R.id.iv_current_img_position);
        ivCalendar = findViewById(R.id.iv_get_calendar);
        etItemName = findViewById(R.id.et_item_name);
        etDescription = findViewById(R.id.et_item_description);
        etHashTag = findViewById(R.id.et_hash_tag);
        etCity = findViewById(R.id.et_city);
        etState = findViewById(R.id.et_state);
        etExpiredDate = findViewById(R.id.et_expired_date);

        ivBack = findViewById(R.id.tb_iv_support_action);
        tvAction = findViewById(R.id.tb_tv_action);
        tvTitle = findViewById(R.id.tb_tv_title);

        ivBack.setOnClickListener(this);
        tvAction.setOnClickListener(this);

        tvTitle.setText("Information");
        tvAction.setText("SEND");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tb_tv_action:
                uploadFunction();
                break;
            default:
                break;
        }
    }

    private void uploadFunction(){

    }
}
