package com.fivenine.sharebutter.Home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.DisplayOfferAdapter;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.UserAccountSettings;
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

import static com.fivenine.sharebutter.Home.HomeFragment.SELECTED_ITEM;

public class FilteredCategoryActivity extends AppCompatActivity implements View.OnClickListener{

    //Widgets
    //Toolbar
    ImageView tbIvSupportAction;
    TextView tbTvTitle;
    TextView tbTvAction;

    GridView gvFilteredItem;

    //Firebase + Vars
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    String filterConditions;
    UserAccountSettings userAccountSettings;
    List<String> userAccountSettingIDList;
    List<UserAccountSettings> userAccountSettingsList;
    ArrayList<Item> filteredItem;

    DisplayOfferAdapter displayOfferAdapter;
    AdapterView.OnItemClickListener onOfferClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_category);

        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tb_iv_support_action:
                finish();
                break;
            default:
                break;
        }
    }

    private void init(){
        filteredItem = new ArrayList<>();
        userAccountSettingsList = new ArrayList<>();
        userAccountSettingIDList = new ArrayList<>();
        filterConditions = getIntent().getStringExtra(CategoriesActivity.TAG);

        tbIvSupportAction = findViewById(R.id.tb_iv_support_action);
        tbIvSupportAction.setOnClickListener(this);

        tbTvAction = findViewById(R.id.tb_tv_action);
        tbTvAction.setText("");

        tbTvTitle = findViewById(R.id.tb_tv_title);
        tbTvTitle.setText(filterConditions);

        gvFilteredItem = findViewById(R.id.gv_filtered_item_list);
        gvFilteredItem.setNumColumns(3);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getFilteredItem();
    }

    private void getFilteredItem(){
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
        DatabaseReference itemDBRef = FirebaseDatabase.getInstance().getReference();

        itemDBRef.child(getString(R.string.dbname_items)).child(curAccountId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot curItem : dataSnapshot.getChildren()){
                            Item currentItem = curItem.getValue(Item.class);
                            if(currentItem.getTraded() || currentItem.getDeleted() || !currentItem.getCategory().equals(filterConditions))
                                continue;

                            filteredItem.add(currentItem);
                        }

                        displayOfferAdapter = new DisplayOfferAdapter(FilteredCategoryActivity.this, filteredItem);
                        gvFilteredItem.setAdapter(displayOfferAdapter);
                        gvFilteredItem.setOnItemClickListener(onOfferClickListener());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private AdapterView.OnItemClickListener onOfferClickListener() {
         if(onOfferClicked == null){
             onOfferClicked = new AdapterView.OnItemClickListener() {

                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     Gson gson = new Gson();
                     String selectedItem = gson.toJson(filteredItem.get(position));

                     Intent intent = new Intent(FilteredCategoryActivity.this, ItemInfoActivity.class);
                     intent.putExtra(SELECTED_ITEM, selectedItem);

                     startActivity(intent);
                 }
             };
         }

        return onOfferClicked;
    }
}
