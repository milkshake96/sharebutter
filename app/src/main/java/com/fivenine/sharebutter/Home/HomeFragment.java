package com.fivenine.sharebutter.Home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.DisplayOfferAdapter;
import com.fivenine.sharebutter.Utils.LogOutDialog;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.UserAccountSettings;
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
import java.util.List;

import static android.view.View.GONE;

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
    SwipeRefreshLayout swipeRefreshLayout;

    ProgressBar homeProgressBar;

    ArrayList<String> sliderDisplayImgURLs;

    //Item
    private ArrayList<Item> offerItems;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private UserAccountSettings userAccountSettings;
    private List<UserAccountSettings> userAccountSettingsList;
    private List<String> userAccountSettingIDList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            init();
        }

        return view;
    }

    private void init() {
        swipeRefreshLayout = view.findViewById(R.id.srl_refresh);

        logoutBtn = view.findViewById(R.id.ivLoginbtn);
        logoutBtn.setOnClickListener(this);

        categoriesBtn = view.findViewById(R.id.ivCategoriesBtn);
        categoriesBtn.setOnClickListener(this);

        searchBtn = view.findViewById(R.id.ivSearchBtn);
        searchBtn.setOnClickListener(this);

        vfDisplay = view.findViewById(R.id.vf_home_display);
        sliderSetup();

        homeProgressBar = view.findViewById(R.id.homeProgressBar);
        homeProgressBar.setVisibility(GONE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        gvOffers = view.findViewById(R.id.gv_display_upload_offer);

        userAccountSettingsList = new ArrayList<>();
        userAccountSettingIDList = new ArrayList<>();
        getFilteredItem();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivCategoriesBtn:
                categoriesOnClick();
                break;
            case R.id.ivLoginbtn:
                logoutOnClick();
                break;
            case R.id.ivSearchBtn:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            default:
                break;
        }
    }

    public void categoriesOnClick() {
        Intent intent = new Intent(getActivity(), CategoriesActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void searchOnClick() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    public void logoutOnClick() {
        LogOutDialog logOutDialog = new LogOutDialog();
        getChildFragmentManager().beginTransaction().add(logOutDialog, "logOut").commit();
    }

    private void sliderSetup() {
        sliderDisplayImgURLs = new ArrayList<>();
        sliderDisplayImgURLs.add("https://d6prv7be4nrvy.cloudfront.net/wp-content/uploads/1000X500.jpg");
        sliderDisplayImgURLs.add("https://www.madison-fichtl.com/wp-content/uploads/2017/12/IMG_1713-1000x500.jpg");
        sliderDisplayImgURLs.add("http://sydney.edu.au/environment-institute/wp-content/uploads/2017/07/954_FoodImage-1000x500-c-center.jpg");
        sliderDisplayImgURLs.add("https://www.tradewindsins.com/wp-content/uploads/sites/61/2017/03/Pizza-and-Friends-1000x500.jpg");
        sliderDisplayImgURLs.add("http://annajones.co.uk/cms/wp-content/uploads/2017/09/Lunchbox-1000x500.jpg");
        sliderDisplayImgURLs.add("https://gomealprep.de/wp-content/uploads/2017/08/meal-prep-mit-fitprep-3fach-mit-essen-und-deckel-1000x500.png");
        sliderDisplayImgURLs.add("https://www.theprocessrecoverycenter.com/wp-content/uploads/2018/11/Christmas1-1000x500.jpg");

        for (String image : sliderDisplayImgURLs) {
            flipperImages(image);
        }
    }

    private void flipperImages(String image) {
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

    private AdapterView.OnItemClickListener onOfferClickListener() {
        if(onOfferClicked == null){
            onOfferClicked = new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Gson gson = new Gson();
                    String selectedItem = gson.toJson(offerItems.get(position));

                    Intent intent = new Intent(getContext(), ItemInfoActivity.class);
                    intent.putExtra(SELECTED_ITEM, selectedItem);

                    startActivity(intent);
                }
            };
        }

        return onOfferClicked;
    }

    private void getFilteredItem(){
        offerItems = new ArrayList<>();
        homeProgressBar.setVisibility(View.VISIBLE);

        databaseReference.child(getString(R.string.dbname_user_account_settings)).child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                        getListOfOtherUsers();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void getListOfOtherUsers(){
        databaseReference.child(getString(R.string.dbname_user_account_settings))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userAccountSettingsList.clear();

                        for(DataSnapshot user : dataSnapshot.getChildren()){
                            if(user.getKey().equals(firebaseUser.getUid())){
                                continue;
                            }
                            UserAccountSettings currentUser = user.getValue(UserAccountSettings.class);
                            userAccountSettingIDList.add(user.getKey());
                            userAccountSettingsList.add(currentUser);
                        }

                        compareState();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void compareState(){
        for(int i = 0; i < userAccountSettingsList.size(); i++){
            if(userAccountSettings.getState().isEmpty()){
                getItemFromDB(userAccountSettingIDList.get(i));
            } else {
                if(userAccountSettingsList.get(i).getState().isEmpty()){
                    getItemFromDB(userAccountSettingIDList.get(i));
                } else {
                    if(userAccountSettingsList.get(i).getState().equals(userAccountSettings.getState())){
                        getItemFromDB(userAccountSettingIDList.get(i));
                    }
                }
            }
        }
    }

    private void getItemFromDB(String curAccountId){
        homeProgressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        DatabaseReference itemDBRef = FirebaseDatabase.getInstance().getReference();

        itemDBRef.child(getString(R.string.dbname_items)).child(curAccountId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot curItem : dataSnapshot.getChildren()){
                            Item currentItem = curItem.getValue(Item.class);
                            if(currentItem.getTraded())
                                continue;

                            offerItems.add(currentItem);
                        }

                        homeProgressBar.setVisibility(View.GONE);
                        displayOfferAdapter = new DisplayOfferAdapter(getContext(), offerItems);
                        gvOffers.setAdapter(displayOfferAdapter);
                        gvOffers.setOnItemClickListener(onOfferClickListener());
                        swipeRefreshLayout.setRefreshing(false);

                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                getFilteredItem();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
