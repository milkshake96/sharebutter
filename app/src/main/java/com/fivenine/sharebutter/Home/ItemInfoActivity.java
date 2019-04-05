package com.fivenine.sharebutter.Home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.Message.MessageActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.Item;
import com.google.gson.Gson;

public class ItemInfoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "ItemInfoActivity";

    //Materials

    //ToolBar
    ImageView ivBack;
    TextView tvTitle;
    TextView tvAction;

    Item currentSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        init();
    }

    private void init() {
        ivBack = findViewById(R.id.tb_iv_support_action);
        tvTitle = findViewById(R.id.tb_tv_title);
        tvAction = findViewById(R.id.tb_tv_action);

        Gson gson = new Gson();
        currentSelectedItem = gson.fromJson(getIntent().getStringExtra(HomeFragment.SELECTED_ITEM), Item.class);

        tvTitle.setText(currentSelectedItem.getName());
        tvAction.setText("Trade");

        ivBack.setOnClickListener(this);
        tvAction.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tb_iv_support_action:
                onBackClicked();
                break;
            case R.id.tb_tv_action:
                onTradeClicked();
                break;
            default:
                break;
        }
    }

    public void onBackClicked(){
        finish();
    }

    public void onTradeClicked(){
        Intent intent = new Intent(ItemInfoActivity.this, MessageActivity.class);
        intent.putExtra(HomeFragment.SELECTED_ITEM, getIntent().getStringExtra(HomeFragment.SELECTED_ITEM));
        startActivity(intent);
    }
}
