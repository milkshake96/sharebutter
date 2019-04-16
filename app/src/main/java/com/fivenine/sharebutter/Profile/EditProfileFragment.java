package com.fivenine.sharebutter.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.FirebaseMethods;
import com.fivenine.sharebutter.Utils.UniversalImageLoader;
import com.fivenine.sharebutter.models.User;
import com.fivenine.sharebutter.models.UserAccountSettings;
import com.fivenine.sharebutter.models.UserLocation;
import com.fivenine.sharebutter.models.UserSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    Spinner spCity, spState;
    ArrayAdapter spinnerAdapterCity;
    ArrayAdapter spinnerAdapterState;
    View view;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //EditProfile Fragment widgets
    private EditText mDisplayName, mDescription;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;

    //vars
    private UserSettings mUserSettings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_editprofile, container, false);


        mProfilePhoto = (CircleImageView) view.findViewById(R.id.ivProfileImage);
        mDisplayName = (EditText) view.findViewById(R.id.etEditProfileUsername);
        mDescription = (EditText) view.findViewById(R.id.etEditProfileDescription);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.tv_changeProfilePhoto);

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.ivBackBtn);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().onBackPressed();
            }
        });

        //setProfileImage();
//        setupFirebaseAuth();
        init();
//        setProfileWidgets();

        return view;
    }

    private void setProfileImage() {
        Log.d(TAG, "setProfileImage: setting profile image.");
        String imgURL = "www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72IeLf";
//        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "https://");

        Picasso.get()
                .load(imgURL)
                .fit()
                .centerCrop()
                .into(mProfilePhoto);
    }

    private void init() {

//        spCity = view.findViewById(R.id.sp_city);
        spState = view.findViewById(R.id.sp_state);

//        spinnerAdapterCity = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item_2, UserLocation.CITY);
//        spinnerAdapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAdapterState = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item_2, UserLocation.STATE);
        spinnerAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        spCity.setAdapter(spinnerAdapterCity);
        spState.setAdapter(spinnerAdapterState);
    }

    private void saveProfileSettings() {
        final String displayName = mDisplayName.getText().toString();
        final String description = mDescription.getText().toString();
        final String state = mDescription.getText().toString();



        if(displayName.isEmpty() || description.isEmpty() || state.isEmpty()){
            Toast.makeText(getContext(), "Must fill in all data", Toast.LENGTH_SHORT).show();
        } else {
            mUserSettings.getSettings().setDisplay_name(displayName);
            mUserSettings.getSettings().setDescription(description);
            mUserSettings.getSettings().setState(state);

            mFirebaseMethods.updateUserAccountSettings(mUserSettings.getSettings());
        }
    }

    private void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
//        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getUser().getEmail());
//        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getUser().getPhone_number());

        mUserSettings = userSettings;
        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mDescription.setText(settings.getDescription());

    }

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve user information from the database
//                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

                //retrieve images for the user in question

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        setupFirebaseAuth();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
