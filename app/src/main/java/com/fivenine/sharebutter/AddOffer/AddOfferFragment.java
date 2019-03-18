package com.fivenine.sharebutter.AddOffer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivenine.sharebutter.R;

public class AddOfferFragment extends Fragment {

    private static final String TAG = "AddOfferFragment";
//    private Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addoffer, container, false);

        return view;

//        btnLogin = (Button) findViewById(R.id.llLoginButton);
//        btnLogin.setOnClickListener();
    }

}
