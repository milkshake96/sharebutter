package com.fivenine.sharebutter.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fivenine.sharebutter.Authentication.LoginActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.LogOutDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticateFragment extends Fragment {

    private static final String TAG = "AuthenticateFragment";

    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authenticate, container, false);

        openDialog();

        return view;
    }

    public void openDialog() {
//        LogOutDialog logoutDialog = new LogOutDialog();
//        logoutDialog.show(getSupportFragmentManager(), "log out dialog");
    }

}
