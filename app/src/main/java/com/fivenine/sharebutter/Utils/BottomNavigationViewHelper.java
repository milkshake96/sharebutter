package com.fivenine.sharebutter.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.fivenine.sharebutter.AddOffer.AddOfferActivity;
import com.fivenine.sharebutter.Home.HomeActivity;
import com.fivenine.sharebutter.Location.LocationActivity;
import com.fivenine.sharebutter.Message.MessageActivity;
import com.fivenine.sharebutter.Profile.ProfileActivity;
import com.fivenine.sharebutter.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
}

    public static void enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.ic_home:
                        Intent intent1 = new Intent(context, HomeActivity.class);       //ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        break;

                    case R.id.ic_profile:
                        Intent intent2  = new Intent(context, ProfileActivity.class);   //ACTIVITY_NUM = 1
                        context.startActivity(intent2);
                        break;

                    case R.id.ic_add_img:
                        Intent intent3 = new Intent(context, AddOfferActivity.class);   //ACTIVITY_NUM = 2
                        context.startActivity(intent3);
                        break;

                    case R.id.ic_msg:
                        Intent intent4 = new Intent(context, MessageActivity.class);    //ACTIVITY_NUM = 3
                        context.startActivity(intent4);
                        break;

                    case R.id.ic_map:
                        Intent intent5 = new Intent(context, LocationActivity.class);   //ACTIVITY_NUM = 4
                        context.startActivity(intent5);
                        break;
                }

                return false;
            }
        });
    }
}
