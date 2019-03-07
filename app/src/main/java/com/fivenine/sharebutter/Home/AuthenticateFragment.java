package com.fivenine.sharebutter.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fivenine.sharebutter.R;

public class AuthenticateFragment extends Fragment {

    private static final String TAG = "AuthenticateFragment";
//    private Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authenticate, container, false);

        return view;

//        btnLogin = (Button) findViewById(R.id.llLoginButton);
//        btnLogin.setOnClickListener();
    }

}
