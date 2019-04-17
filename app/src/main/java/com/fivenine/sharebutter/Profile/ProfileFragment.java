package com.fivenine.sharebutter.Profile;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fivenine.sharebutter.Home.HomeActivity;
import com.fivenine.sharebutter.Home.HomeFragment;
import com.fivenine.sharebutter.Home.ItemInfoActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.DisplayOfferAdapter;
import com.fivenine.sharebutter.Utils.FirebaseMethods;
import com.fivenine.sharebutter.Utils.GridImageAdapter;
import com.fivenine.sharebutter.Utils.UniversalImageLoader;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.User;
import com.fivenine.sharebutter.models.UserAccountSettings;
import com.fivenine.sharebutter.models.UserSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private static final String TAG = "ProfileFragment";
    private static final int ACTIVITY_NUM = 1;
    private CircleImageView profilePhoto;
    private GridView gridView;
    private static final int NUM_GRID_COLUMNS = 3;
    private TextView mOffers, mDisplayName,mDescription, mLikes, mState, mNoOfferMsg;
    private Context mContext;
    private ProgressBar mProgressBar;

    private DisplayOfferAdapter displayOfferAdapter;
    private ArrayList<Item> postedItemList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.d(TAG, "onCreate: successfully started");

        profilePhoto = (CircleImageView) view.findViewById(R.id.ivProfileImage);
        mOffers = (TextView) view.findViewById(R.id.tvOfferNum);
        mDisplayName = (TextView) view.findViewById(R.id.tvProfileUserName);
        mDescription = (TextView) view.findViewById(R.id.tvDecription);
        mLikes = (TextView) view.findViewById(R.id.tvLikesNum);
        mState = (TextView) view.findViewById(R.id.tv_state);

        gridView = (GridView) view.findViewById(R.id.gvProfileOfferUploaded);

        mNoOfferMsg = (TextView) view.findViewById(R.id.tv_no_offer_msg);
        mNoOfferMsg.setVisibility(View.GONE);
        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mContext = getActivity();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        TextView editProfile = (TextView) view.findViewById(R.id.tvEditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to 'EditProfileFragment'");
                // Create new fragment and transaction
                Fragment newFragment = new EditProfileFragment();
                // consider using Java coding conventions (upper first char class names!!!)
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.editProfileFragment, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }
        });

        postedItemList = new ArrayList<>();
        gridView.setOnItemClickListener(onOfferClickListener());
        setupFirebaseAuth();
        return view;
    }

    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());


        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), profilePhoto, null, "");

        mDisplayName.setText(settings.getDisplay_name());
        mState.setText(settings.getState());
        mLikes.setText(String.valueOf(settings.getLikes()));
        mOffers.setText(String.valueOf(settings.getOffers()));

        if(!settings.getDescription().isEmpty()){
            mDescription.setText(settings.getDescription());
        } else {
            mDescription.setText("Describe Yourself.");
        }
    }


     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null && user.isEmailVerified()) {
                    // User is signed in
                    getPostedItemListFromDB();
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
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
                //retrieve images for the user in question

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPostedItemListFromDB(){
        myRef.child(getString(R.string.dbname_items)).child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postedItemList.clear();
                        for(DataSnapshot curItem : dataSnapshot.getChildren()){
                            Item postedItem = curItem.getValue(Item.class);

                            if(postedItem.getDeleted() || postedItem.getTraded()){
                                continue;
                            }

                            postedItemList.add(postedItem);
                        }

                        if(postedItemList.size() > 0){
                            mNoOfferMsg.setVisibility(View.VISIBLE);
                        }

                        mProgressBar.setVisibility(View.GONE);
                        displayOfferAdapter = new DisplayOfferAdapter(getContext(), postedItemList);
                        gridView.setAdapter(displayOfferAdapter);
                        mOffers.setText(String.valueOf(postedItemList.size()));
                        monitorPostedItemChangesFromDB();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void monitorPostedItemChangesFromDB(){
        mProgressBar.setVisibility(View.VISIBLE);

        myRef.child(getString(R.string.dbname_items)).child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postedItemList.clear();
                        for(DataSnapshot curItem : dataSnapshot.getChildren()){
                            Item postedItem = curItem.getValue(Item.class);

                            if(postedItem.getDeleted() || postedItem.getTraded()){
                                continue;
                            }

                            postedItemList.add(postedItem);
                        }

                        if(postedItemList.size() <= 0){
                            mNoOfferMsg.setVisibility(View.VISIBLE);
                        } else {
                            mNoOfferMsg.setVisibility(View.GONE);
                        }

                        mProgressBar.setVisibility(View.GONE);
                        displayOfferAdapter = new DisplayOfferAdapter(getContext(), postedItemList);
                        gridView.setAdapter(displayOfferAdapter);
                        mOffers.setText(String.valueOf(postedItemList.size()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private AdapterView.OnItemClickListener onOfferClickListener(){
        return new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new Gson();
                String selectedItem = gson.toJson(postedItemList.get(position));

                Intent intent = new Intent(getContext(), ItemInfoActivity.class);
                intent.putExtra(HomeFragment.SELECTED_ITEM, selectedItem);

                startActivity(intent);
            }
        };
    }
}
