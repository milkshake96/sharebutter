package com.fivenine.sharebutter.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.SearchListingAdapter;
import com.fivenine.sharebutter.models.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    private EditText mSearchField;
    private ImageView mSearchBtn;
    private RecyclerView mRecycleView;
    private SearchListingAdapter mAdapter;

    private ArrayList<Item> mItemList;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchField = findViewById(R.id.et_search_field);
        mSearchBtn = findViewById(R.id.iv_search_btn);
        mRecycleView = findViewById(R.id.result_list);

        getItemList();

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchFilter(s.toString());
            }
        });
    }

    private void searchFilter(String text) {
        ArrayList<Item> newList = new ArrayList<>();

        if (mItemList != null && mItemList.size() > 0) {
            for (Item item : mItemList) {
                if (item.getHashTag().toLowerCase().contains(text.toLowerCase())) {
                    newList.add(item);
                }
            }
            mAdapter.filteredList(newList);
        }
    }

    private void getItemList() {
        mItemList = new ArrayList<>();

        databaseRef = FirebaseDatabase.getInstance().getReference().child("items");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        Item item = s.getValue(Item.class);
                        Log.d(TAG, "onDataChange: Item : " + item.toString());
                        mItemList.add(item);
                    }
                }
                if (mItemList.size() > 0) {
                    initAdapter(mItemList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void initAdapter(ArrayList<Item> itemList) {
        mAdapter = new SearchListingAdapter(itemList);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnItemClickListener(new SearchListingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
    }

}
