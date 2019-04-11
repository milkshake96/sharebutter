package com.fivenine.sharebutter.Exchange;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.Home.ItemInfoActivity;
import com.fivenine.sharebutter.Message.MessageActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.DisplayOfferAdapter;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.fivenine.sharebutter.Home.HomeFragment.SELECTED_ITEM;

public class TraderExistingOffers extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "TraderExistingOffers";
    public static final String ITEM_TRADER = "item_trader";

    //Widgets
    GridView gvExistingTraderOffers;
    DisplayExistingItemAdapter displayExistingItemAdapter;
    AdapterView.OnItemClickListener onOfferClicked;

    ArrayList<Item> existingTraderItems;
    User currentTrader;

    //Toolbars
    ImageView ivSupportAction;
    TextView tvTitle;
    TextView tvAction;

    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ValueEventListener existingTraderItemListListener;

    Item currentSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader_existing_offers);

        init();
    }

    private void init(){
        Gson gson = new Gson();
        currentTrader = gson.fromJson(getIntent().getStringExtra(MessageActivity.CURRENT_TRADER), User.class);

        gvExistingTraderOffers = findViewById(R.id.gv_trader_items);
        gvExistingTraderOffers.setNumColumns(3);

        ivSupportAction = findViewById(R.id.tb_iv_support_action);
        ivSupportAction.setOnClickListener(this);

        tvTitle = findViewById(R.id.tb_tv_title);
        String title = currentTrader.getUsername() + "'s Offers";
        tvTitle.setText(title);

        tvAction = findViewById(R.id.tb_tv_action);
        tvAction.setText("");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        existingTraderItemListListener = getExistingItemListListener();

        onOfferClicked = getOnOfferClickListener();

        databaseReference.child(getString(R.string.dbname_items)).child(currentTrader.getUser_id())
                .addValueEventListener(existingTraderItemListListener);
    }

    private ValueEventListener getExistingItemListListener(){
        existingTraderItems = new ArrayList<>();


        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                existingTraderItems.clear();

                if(firebaseUser.getUid().equals(currentTrader.getUser_id())){
                    Item addItem = new Item();
                    addItem.setImg1URL(String.valueOf(R.drawable.btn_corner_red));
                    existingTraderItems.add(addItem);
                }

                for(DataSnapshot item : dataSnapshot.getChildren()){
                    Item currentItem = item.getValue(Item.class);
                    existingTraderItems.add(currentItem);
                }

                displayExistingItemAdapter = new DisplayExistingItemAdapter
                        (TraderExistingOffers.this, existingTraderItems, currentTrader);
                gvExistingTraderOffers.setAdapter(displayExistingItemAdapter);
                gvExistingTraderOffers.setOnItemClickListener(onOfferClicked);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private AdapterView.OnItemClickListener getOnOfferClickListener(){
        return new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(firebaseUser.getUid().equals(currentTrader.getUser_id())){
                    for(int i = 0; i < parent.getChildCount(); i++){
                        ImageView imageView = (ImageView) parent.getChildAt(i);
                        imageView.setBackgroundColor(Color.TRANSPARENT);
                        imageView.setPadding(0,0,0,0);
                    }

                    ImageView imageView = (ImageView) parent.getChildAt(position);
                    imageView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    imageView.setPadding(2,2,2,2);

                    currentSelectedItem = (Item)parent.getItemAtPosition(position);
                    tvAction.setText("NEXT");
                } else {
                    Intent intent = new Intent(TraderExistingOffers.this, ItemInfoActivity.class);

                    Gson gson = new Gson();
                    String selectedItem = gson.toJson(existingTraderItems.get(position));
                    intent.putExtra(ItemInfoActivity.VIEW_ONLY, true);
                    intent.putExtra(SELECTED_ITEM, selectedItem);

                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tb_iv_support_action:
                finish();
                break;
            default:
                break;
        }
    }
}
