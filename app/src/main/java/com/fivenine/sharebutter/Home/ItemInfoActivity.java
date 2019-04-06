package com.fivenine.sharebutter.Home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fivenine.sharebutter.AddOffer.AddOfferFragment;
import com.fivenine.sharebutter.Message.MessageActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.ViewPagerAdapter;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ItemInfoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "ItemInfoActivity";

    //Materials (Item Image)
    ViewPager vpSelectedItemImages;
    LinearLayout llImagePosition;
    ViewPagerAdapter viewPagerAdapter;
    List<Uri> imageUriList;

    //Materials (Item Description)
    TextView tvHashTag;
    TextView tvItemName;
    TextView tvDescription;
    TextView tvExpDate;

    //Materials (User Description)
    ImageView ivProfileImage;
    TextView tvUserName;

    //ToolBar
    ImageView ivBack;
    TextView tvTitle;
    TextView tvAction;

    Item currentSelectedItem;
    FirebaseUser firebaseUser;
    User itemOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        init();
    }

    private void init() {
        //Get Data From previous page
        Gson gson = new Gson();
        currentSelectedItem = gson.fromJson(getIntent().getStringExtra(HomeFragment.SELECTED_ITEM), Item.class);

        //Item Img
        vpSelectedItemImages = findViewById(R.id.vp_selected_upload_offer);
        imageUriList = new ArrayList<>();

        if(!currentSelectedItem.getImg1URL().equals("No Image")) {
            imageUriList.add(Uri.parse(currentSelectedItem.getImg1URL()));
        }

        if(!currentSelectedItem.getImg2URL().equals("No Image")){
            imageUriList.add(Uri.parse(currentSelectedItem.getImg2URL()));
        }

        if(!currentSelectedItem.getImg3URL().equals("No Image")){
            imageUriList.add(Uri.parse(currentSelectedItem.getImg3URL()));
        }

        ViewPager.OnPageChangeListener onImageChanged = addOnPageChangeListener();
        viewPagerAdapter = new ViewPagerAdapter(this, imageUriList);
        vpSelectedItemImages.addOnPageChangeListener(onImageChanged);
        vpSelectedItemImages.setAdapter(viewPagerAdapter);

        llImagePosition = findViewById(R.id.ll_image_position);

        //Item Info
        tvItemName = findViewById(R.id.tv_item_name);
        tvDescription = findViewById(R.id.tv_description);
        tvHashTag = findViewById(R.id.tv_hash_tag);
        tvExpDate = findViewById(R.id.tv_expired_date);

        //User Info
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvUserName = findViewById(R.id.tv_user_name);

        //Toolbar
        ivBack = findViewById(R.id.tb_iv_support_action);
        tvTitle = findViewById(R.id.tb_tv_title);
        tvAction = findViewById(R.id.tb_tv_action);


        tvTitle.setText(currentSelectedItem.getName());
        tvAction.setText("");

        ImageView iv = new ImageView(this);
        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.handshake);
        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 80, 80, true);
        iv.setImageBitmap(bMapScaled);

        tvAction.setBackground(iv.getDrawable());

        ivBack.setOnClickListener(this);
        tvAction.setOnClickListener(this);

        //User
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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

    private ViewPager.OnPageChangeListener addOnPageChangeListener(){
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                updateCurrentSelectedPage(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
    }

    private void updateCurrentSelectedPage(final int selectedPage){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int counter = 0; counter < imageUriList.size(); counter++){
                    final ImageView imageView = (ImageView)llImagePosition.getChildAt(counter);
                    if(counter == selectedPage){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageResource(R.drawable.red_dot);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageResource(R.drawable.dot);
                            }
                        });
                    }
                }
            }
        }).start();
    }
}
