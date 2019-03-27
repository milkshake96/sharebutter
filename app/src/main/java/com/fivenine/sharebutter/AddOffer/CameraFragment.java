package com.fivenine.sharebutter.AddOffer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.LogOutDialog;
import com.google.firebase.auth.FirebaseAuth;

public class CameraFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int ACTIVITY_NUM = 0;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container,false);

        return view;
    }


}
