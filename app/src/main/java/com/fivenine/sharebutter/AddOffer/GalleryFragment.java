package com.fivenine.sharebutter.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fivenine.sharebutter.Authentication.LoginActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.LogOutDialog;
import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int ACTIVITY_NUM = 0;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container,false);

        ImageView login_btn = (ImageView) view.findViewById(R.id.ivLoginbtn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOutDialog logOutDialog = new LogOutDialog();

                getChildFragmentManager().beginTransaction().add(logOutDialog, "logOut").commit();
            }
        });

        return view;
    }


}
