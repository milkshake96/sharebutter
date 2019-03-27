package com.fivenine.sharebutter.AddOffer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.BottomNavigationViewHelper;
import com.fivenine.sharebutter.Utils.Permissions;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class AddOfferActivity extends AppCompatActivity {

    private static final String TAG = "AddOfferActivity";

    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private static final int ACTIVITY_NUM = 2;

    private Context mContext = AddOfferActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: successfully started");

//        if(checkPermissionsArray(Permissions.PERMISSIONS)){
//
//        }else{
//            verifyPermissions(Permissions.PERMISSIONS);
//        }
//        setupBottomNavigationView();
    }

//    public void verifyPermissions(String[] permissions){
//        Log.d(TAG, "verifyPermissions: verifying permissions.");
//
//        ActivityCompat.requestPermissions(
//                AddOfferActivity.this,
//                permissions,
//                VERIFY_PERMISSIONS_REQUEST
//        );
//    }
//
//    public boolean checkPermissionsArray(String[] permissions){
//        Log.d(TAG, "checkPermissionsArray: checking permissions array.");
//
//        for(int i = 0; i< permissions.length; i++){
//            String check = permissions[i];
//            if(!checkPermissions(check)){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public boolean checkPermissions(String permission){
//        Log.d(TAG, "checkPermissions: checking permission: " + permission);
//
//        int permissionRequest = ActivityCompat.checkSelfPermission(AddOfferActivity.this, permission);
//
//        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
//            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
//            return false;
//        }
//        else{
//            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
//            return true;
//        }
//    }
//
//
//    private void setupBottomNavigationView(){
//        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
//        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.btmNavViewBar);
//        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
//        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationViewEx);
//
//        Menu menu = bottomNavigationViewEx.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
//    }
}
