package com.fivenine.sharebutter.Home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.DisplayOfferAdapter;
import com.fivenine.sharebutter.Utils.LogOutDialog;
import com.fivenine.sharebutter.models.Item;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeFragment";
    public static final String SELECTED_ITEM = "selected_item";
    private static final int ACTIVITY_NUM = 0;

    //Fragment View
    View view;

    //Materials
    ImageView searchBtn;
    ImageView logoutBtn;
    ImageView categoriesBtn;
    ViewFlipper vfDisplay;
    GridView gvOffers;
    DisplayOfferAdapter displayOfferAdapter;
    AdapterView.OnItemClickListener onOfferClicked;

    ArrayList<String> sliderDisplayImgURLs;

    //Item
    private ArrayList<Item> offerItems;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener itemListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container,false);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            init();
        }


        return view;
    }

    private void init(){
        logoutBtn = view.findViewById(R.id.ivLoginbtn);
        logoutBtn.setOnClickListener(this);

        categoriesBtn = view.findViewById(R.id.ivCategoriesBtn);
        categoriesBtn.setOnClickListener(this);

        searchBtn = view.findViewById(R.id.ivSearchBtn);
        searchBtn.setOnClickListener(this);

        vfDisplay = view.findViewById(R.id.vf_home_display);
        sliderSetup();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(getContext().getString(R.string.dbname_items));
        itemListener = itemListener();
        databaseReference.addListenerForSingleValueEvent(itemListener);

        gvOffers = view.findViewById(R.id.gv_display_upload_offer);
        onOfferClicked = onOfferClickListener();
        gvOffers.setOnItemClickListener(onOfferClicked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivCategoriesBtn:
                categoriesOnClick();
                break;
            case R.id.ivLoginbtn:
                logoutOnClick();
                break;
            case R.id.ivSearchBtn:
                startActivity(new Intent(getActivity(),SearchActivity.class));
                break;
            default:
                break;
        }
    }

    public void categoriesOnClick(){
        Intent intent = new Intent(getActivity(), CategoriesActivity.class);
        startActivity(intent);
    }
    public void searchOnClick(){
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    public void logoutOnClick(){
        LogOutDialog logOutDialog = new LogOutDialog();
        getChildFragmentManager().beginTransaction().add(logOutDialog, "logOut").commit();
    }

    private void sliderSetup() {
        sliderDisplayImgURLs = new ArrayList<>();
        sliderDisplayImgURLs.add("https://clocktower-apartments.com/wp-content/uploads/2017/08/Eating-in-your-Seattle-Apartment-1000x400.jpg");
        sliderDisplayImgURLs.add("https://brooklynboulders.com/wp-content/uploads/2015/11/photo-1441123285228-1448e608f3d5-1000x400.jpeg");
        sliderDisplayImgURLs.add("https://www.thehealthyloaf.com.au/wp-content/uploads/2018/01/sharing-the-food-PSTU2CT-1000x400.jpg");
        sliderDisplayImgURLs.add("https://www.villa-athena-dday.com/wp-content/uploads/2018/06/IMG_20180504_090724-1000x400.jpg");
        sliderDisplayImgURLs.add("https://p.dam-img.rfdcontent.com/cms/001/306/575/1000x400_crop-north-force.jpg");
        sliderDisplayImgURLs.add("https://static1.squarespace.com/static/55f7166be4b02accbd8e3210/t/57f2e918579fb34674b8926f/1475537182819/");
        sliderDisplayImgURLs.add("https://cdn.filestackcontent.com/quality=value:100/output=compress:true,background:FFFFFF/resize=width:1000/DeBDfSAMR2SsrbybVfDW");

        for(String image : sliderDisplayImgURLs){
            flipperImages(image);
        }
    }

    private void flipperImages(String image){
        ImageView imageView = new ImageView(getContext());
        Picasso.get()
                .load(image)
                .placeholder(R.drawable.white_background)
                .fit()
                .into(imageView);

        vfDisplay.addView(imageView);
        vfDisplay.setFlipInterval(4000);
        vfDisplay.setAutoStart(true);

        //Animation
        vfDisplay.setInAnimation(getContext(), android.R.anim.slide_in_left);
        vfDisplay.setOutAnimation(getContext(), android.R.anim.slide_out_right);
    }

    private AdapterView.OnItemClickListener onOfferClickListener(){
        return new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new Gson();
                String selectedItem = gson.toJson(offerItems.get(position));

                Intent intent = new Intent(getContext(),ItemInfoActivity.class);
                intent.putExtra(SELECTED_ITEM, selectedItem);

                startActivity(intent);
            }
        };
    }

    private ValueEventListener itemListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offerItems = new ArrayList<>();

                // Get Post object and use the values to update the UI
                for(DataSnapshot existingUsers : dataSnapshot.getChildren()){
                    if(existingUsers.getKey().equals(firebaseUser.getUid())){
                        continue;
                    }

                    for(DataSnapshot uploadedItem : existingUsers.getChildren()){
                        Item item = uploadedItem.getValue(Item.class);
                        offerItems.add(item);
                    }
                }

                displayOfferAdapter = new DisplayOfferAdapter(getContext(), offerItems);
                gvOffers.setAdapter(displayOfferAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
    }
}
