package com.fivenine.sharebutter.Exchange;

import android.content.ClipData;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivenine.sharebutter.AddOffer.AddOfferActivity;
import com.fivenine.sharebutter.AddOffer.AddOfferFragment;
import com.fivenine.sharebutter.Home.ItemInfoActivity;
import com.fivenine.sharebutter.Message.MessageActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.TradeOffer;
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

import static android.view.View.GONE;
import static com.fivenine.sharebutter.AddOffer.AddOfferFragment.SELECTED_IMAGES;
import static com.fivenine.sharebutter.Home.HomeFragment.SELECTED_ITEM;
import static com.fivenine.sharebutter.Message.MessageActivity.CURRENT_TRADE_OFFER;

public class TraderExistingOffers extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "TraderExistingOffers";
    public static final String ITEM_TRADER = "item_trader";
    public static final String ITEM_SELECTED = "item_selected";
    private final int CODE_MULTIPLE_IMG_GALLERY = 2;
    private final int CODE_CONFIRM = 1;

    //Widgets
    GridView gvExistingTraderOffers;
    DisplayExistingItemAdapter displayExistingItemAdapter;
    AdapterView.OnItemClickListener onOfferClicked;

    //Toolbars
    ImageView ivSupportAction;
    TextView tvTitle;
    TextView tvAction;

    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ValueEventListener existingTraderItemListListener;

    //Var
    ArrayList<Item> existingTraderItems;
    Item currentSelectedItem;
    TradeOffer currentTradeOffer;
    User currentTrader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader_existing_offers);

        init();
    }

    private void init() {
        Gson gson = new Gson();
        currentTrader = gson.fromJson(getIntent().getStringExtra(MessageActivity.CURRENT_TRADER), User.class);
        currentTradeOffer = gson.fromJson(getIntent().getStringExtra(CURRENT_TRADE_OFFER), TradeOffer.class);

        gvExistingTraderOffers = findViewById(R.id.gv_trader_items);
        gvExistingTraderOffers.setNumColumns(3);

        ivSupportAction = findViewById(R.id.tb_iv_support_action);
        ivSupportAction.setOnClickListener(this);

        tvTitle = findViewById(R.id.tb_tv_title);
        String title = currentTrader.getUsername() + "'s Offers";
        tvTitle.setText(title);

        tvAction = findViewById(R.id.tb_tv_action);
        tvAction.setOnClickListener(this);
        tvAction.setText("");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        existingTraderItemListListener = getExistingItemListListener();

        if (firebaseUser.getUid().equals(currentTrader.getUser_id())) {
            onOfferClicked = getOnOfferClickTraderListener();
        } else {
            onOfferClicked = getOnOfferClickOwnerListener();
        }

        databaseReference.child(getString(R.string.dbname_items)).child(currentTrader.getUser_id())
                .addValueEventListener(existingTraderItemListListener);
    }

    private ValueEventListener getExistingItemListListener() {
        existingTraderItems = new ArrayList<>();


        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                existingTraderItems.clear();

                if (firebaseUser.getUid().equals(currentTrader.getUser_id())) {
                    Item addItem = new Item();
                    addItem.setImg1URL(String.valueOf(R.drawable.btn_corner_red));
                    existingTraderItems.add(addItem);
                }

                for (DataSnapshot item : dataSnapshot.getChildren()) {
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

    private AdapterView.OnItemClickListener getOnOfferClickOwnerListener() {
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(TraderExistingOffers.this, ItemInfoActivity.class);

                Gson gson = new Gson();
                String selectedItem = gson.toJson(existingTraderItems.get(position));
                intent.putExtra(ItemInfoActivity.VIEW_ONLY, true);
                intent.putExtra(SELECTED_ITEM, selectedItem);

                startActivity(intent);
            }
        };
    }

    private AdapterView.OnItemClickListener getOnOfferClickTraderListener() {
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    ImageView imageView = (ImageView) parent.getChildAt(i);
                    imageView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    imageView.setPadding(0, 0, 0, 0);
                }

                ImageView imageView = (ImageView) parent.getChildAt(position);
                imageView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                imageView.setPadding(2, 2, 2, 2);

                currentSelectedItem = (Item) parent.getItemAtPosition(position);
                tvAction.setText("NEXT");
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tb_iv_support_action:
                finish();
                break;
            case R.id.tb_tv_action:
                onAction();
                break;
            default:
                break;
        }
    }

    private void onAction() {
        if (tvAction.getText().toString().equals("NEXT")) {
            if (currentSelectedItem.getId() == null) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Selection various images"), CODE_MULTIPLE_IMG_GALLERY);
            } else {
                Intent intent = new Intent(TraderExistingOffers.this, ConfirmationActivity.class);
                Gson gson = new Gson();
                intent.putExtra(ITEM_SELECTED, gson.toJson(currentSelectedItem));
                intent.putExtra(CURRENT_TRADE_OFFER, gson.toJson(currentTradeOffer));
                startActivityForResult(intent, CODE_CONFIRM);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_MULTIPLE_IMG_GALLERY && resultCode == RESULT_OK) {
            ClipData clipData = data.getClipData();
            ArrayList<String> imgUri = new ArrayList<>();

            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    imgUri.add(clipData.getItemAt(i).getUri().toString());
                }
            }

            if(imgUri.size() > 3 || imgUri.size() < 1){
                Toast.makeText(this, "Must select > 1 or < 3 image", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(TraderExistingOffers.this, AddOfferActivity.class);
                intent.putStringArrayListExtra(SELECTED_IMAGES, imgUri);
                startActivity(intent);
            }
        } else if (requestCode == CODE_CONFIRM && resultCode == RESULT_OK){
            finish();
        }
    }
}
