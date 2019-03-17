package com.fivenine.sharebutter.Authentication;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.fivenine.sharebutter.R;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout llSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        llSignUp = (LinearLayout) findViewById(R.id.llSignupMsg);
        llSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity();
            }
        });
    }

    public void SignUpActivity() {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }
}
