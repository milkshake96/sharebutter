package com.fivenine.sharebutter.Profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "EditProfileFragment";

    Spinner spCity, spState;
    ArrayAdapter spinnerAdapterCity;
    ArrayAdapter spinnerAdapterState;
    View view;

    private final int GALLERY = 1;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //EditProfile Fragment widgets
    private EditText mDisplayName, mDescription;
    private TextView mChangeProfilePhoto, tvDone;
    private CircleImageView mProfilePhoto;

    private ProgressBar editProfileProgressBar;

    //vars
    private UserSettings mUserSettings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        init();

        return view;
    }

    private void init() {
        //back arrow for navigating back to "ProfileActivity"
        editProfileProgressBar = view.findViewById(R.id.editProfileProgressBar);
        editProfileProgressBar.setVisibility(View.GONE);
        ImageView backArrow = (ImageView) view.findViewById(R.id.ivBackBtn);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().onBackPressed();
            }
        });

        mProfilePhoto = (CircleImageView) view.findViewById(R.id.ivProfileImage);
        mDisplayName = (EditText) view.findViewById(R.id.etEditProfileUsername);
        mDescription = (EditText) view.findViewById(R.id.etEditProfileDescription);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.tv_changeProfilePhoto);
        tvDone = (TextView) view.findViewById(R.id.tvDone);
        spState = view.findViewById(R.id.sp_state);

        tvDone.setOnClickListener(this);
        mChangeProfilePhoto.setOnClickListener(this);


        spinnerAdapterState = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item_2, UserLocation.STATE);
        spinnerAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spState.setAdapter(spinnerAdapterState);


    }

    private void saveProfileSettings() {
        final String displayName = mDisplayName.getText().toString();
        final String description = mDescription.getText().toString();
        final String state = spState.getSelectedItem().toString();


        if (displayName.isEmpty() || description.isEmpty() || state.isEmpty()) {
            Toast.makeText(getContext(), "Must fill in all data", Toast.LENGTH_SHORT).show();
            return;
        } else {
            mUserSettings.getUser().setUsername(displayName);

            mUserSettings.getSettings().setDisplay_name(displayName);
            mUserSettings.getSettings().setDescription(description);
            mUserSettings.getSettings().setState(state);

            mFirebaseMethods.updateUserAccountSettings(mUserSettings);
            getActivity().onBackPressed();
        }
    }

    private void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        mUserSettings = userSettings;

        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

        mDisplayName.setText(settings.getDisplay_name());
        mDescription.setText(settings.getDescription());

        switch (settings.getState()) {
            case "Perak": {
                spState.setSelection(0);
            }
            case "Penang": {
                spState.setSelection(1);
            }
            case "Kuala Lumpur": {
                spState.setSelection(1);
            }
        }
    }

    private void changeProfilePhoto() {
        //Start Gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();

                uploadPhoto(contentURI);

//                mProfilePhoto.setImageURI(contentURI);
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadPhoto(final Uri contentURI) {
        editProfileProgressBar.setVisibility(View.VISIBLE);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("user_profile_photo");
        if (contentURI != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(contentURI));

            fileReference.putFile(contentURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        mUserSettings.getSettings().setProfile_photo(downloadUri.toString());
                        mUserSettings.getUser().setProfilePhoto(downloadUri.toString());

                        mFirebaseMethods.updateUserAccountSettings(mUserSettings);

                        Picasso.get()
                                .load(contentURI)
                                .fit()
                                .centerCrop()
                                .into(mProfilePhoto);
                        //add progress bar
                        editProfileProgressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_changeProfilePhoto:
                changeProfilePhoto();
                break;
            case R.id.tvDone:
                saveProfileSettings();
                break;
            default:
                break;
        }
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
        mFirebaseMethods = new FirebaseMethods(getActivity());

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
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

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
