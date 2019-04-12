package com.fivenine.sharebutter.AddOffer;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Utils.DatePickerFragment;
import com.fivenine.sharebutter.Utils.ViewPagerAdapter;
import com.fivenine.sharebutter.models.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddOfferActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "AddOfferActivity";
    public static final String IMAGE_STORE_LOCATION = "itemImage";

    //Main Page Materials
    ViewPager vpImagesSelected;
    LinearLayout llCurrentImgPosition;

    ImageView ivCalendar;
    EditText etItemName;
    EditText etDescription;
    Spinner spCategory;
    EditText etHashTag;
    EditText etExpiredDate;

    ArrayAdapter<String> spinnerAdapter;
    ViewPagerAdapter viewPagerAdapter;
    ArrayList<String> selectedImages;

    //Toolbar Materials
    ImageView ivBack;
    TextView tvTitle;
    TextView tvAction;

    //Firebase
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    int uploadedItem = 0;

    //Upload item
    Item item;
    ArrayList<String> imgURLs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        init();
    }

    private void init() {
        vpImagesSelected = findViewById(R.id.vp_selected_upload_offer);

        selectedImages = getIntent().getStringArrayListExtra(AddOfferFragment.SELECTED_IMAGES);
        if (selectedImages != null || !selectedImages.isEmpty()) {
            List<Uri> imageUriList = new ArrayList<>();

            for (int i = 0; i < selectedImages.size(); i++) {
                imageUriList.add(Uri.parse(selectedImages.get(i)));
            }

            ViewPager.OnPageChangeListener onImageChange = pageChageListener();
            viewPagerAdapter = new ViewPagerAdapter(this, imageUriList);
            vpImagesSelected.setAdapter(viewPagerAdapter);
            vpImagesSelected.addOnPageChangeListener(onImageChange);
        }

        llCurrentImgPosition = findViewById(R.id.ll_current_img_position);
        for(int i = 0; i < llCurrentImgPosition.getChildCount(); i++){
            ImageView imageView = (ImageView) llCurrentImgPosition.getChildAt(i);
            if(i == 0)
                imageView.setImageResource(R.drawable.red_dot);
            else
                imageView.setImageResource(R.drawable.dot);
        }


        ivCalendar = findViewById(R.id.iv_get_calendar);
        ivCalendar.setOnClickListener(this);

        etItemName = findViewById(R.id.et_item_name);
        etDescription = findViewById(R.id.et_item_description);
        etHashTag = findViewById(R.id.et_hash_tag);
        etExpiredDate = findViewById(R.id.et_expired_date);
        spCategory = findViewById(R.id.sp_categories);

        ivBack = findViewById(R.id.tb_iv_support_action);
        tvAction = findViewById(R.id.tb_tv_action);
        tvTitle = findViewById(R.id.tb_tv_title);

        ivBack.setOnClickListener(this);
        tvAction.setOnClickListener(this);

        tvTitle.setText("Information");
        tvAction.setText("SEND");

        spinnerAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item_2, Item.CATEGORIES);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCategory.setAdapter(spinnerAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tb_tv_action:
                uploadFunction();
                break;
            case R.id.iv_get_calendar:
                displayCalendarFragment();
                break;
            case R.id.tb_iv_support_action:
                finish();
            default:
                break;
        }
    }

    private void uploadFunction() {
        String name = etItemName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String hashTag = etHashTag.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString().trim();
        String expDate = etExpiredDate.getText().toString().trim();
        Boolean isNotValid = false;
        String msg = "";



        if (name.isEmpty() || description.isEmpty() || hashTag.isEmpty() ||
                category.isEmpty() || expDate.isEmpty()) {
            isNotValid = true;
            msg = "Please fill in all item information.";
        } else {
            if (hashTag.charAt(0) != '#') {
                isNotValid = true;
                msg = "The first word of hashtage must be \"#\"";
            }
        }

        if (isNotValid) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            uploadedItem = 0;
            Toast.makeText(this, "Posting Item...", Toast.LENGTH_SHORT).show();
            item = new Item(new Date().getTime(), firebaseAuth.getCurrentUser().getUid(),
                    name, description, hashTag, category, expDate, false);

            imgURLs = new ArrayList<>();
            databaseReference = firebaseDatabase.getReference().child(AddOfferActivity.this.getString(R.string.dbname_items))
                    .child(firebaseAuth.getCurrentUser().getUid());

            for (int i = 0; i < selectedImages.size(); i++) {
                final String path = firebaseAuth.getCurrentUser().getUid() + "/" + IMAGE_STORE_LOCATION
                        + "/" + item.getId() + "/" + i + ".jpeg";
                storageReference = FirebaseStorage.getInstance().getReference().child(path);
                storageReference.putFile(Uri.parse(selectedImages.get(i))).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        uploadedItem++;
                        if(uploadedItem == selectedImages.size()){
                            uploadedItem = 0;

                            for(int i = 0; i < selectedImages.size(); i++){
                                final String path = firebaseAuth.getCurrentUser().getUid() + "/" + IMAGE_STORE_LOCATION
                                        + "/" + item.getId() + "/" + i + ".jpeg";
                                storageReference = FirebaseStorage.getInstance().getReference().child(path);
                                OnSuccessListener<Uri> onDownloadSuccess = getOnDownloadSuccessListener();
                                storageReference.getDownloadUrl().addOnSuccessListener(onDownloadSuccess);
                            }
                        }
                    }
                });
            }



        }
    }

    private OnSuccessListener<Uri> getOnDownloadSuccessListener(){
        return new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imgURLs.add(uri.toString());
                uploadedItem++;

                if(uploadedItem == selectedImages.size()) {
                    while (imgURLs.size() != 3){
                        imgURLs.add("No Image");
                    }

                    item.setImg1URL(imgURLs.get(0));
                    item.setImg2URL(imgURLs.get(1));
                    item.setImg3URL(imgURLs.get(2));

                    databaseReference.child(String.valueOf(item.getId())).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Upload Successful..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        };
    }

    private void displayCalendarFragment() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY");
        String currentDateString = simpleDateFormat.format(calendar.getTime());

        etExpiredDate.setText(currentDateString);
    }

    private ViewPager.OnPageChangeListener pageChageListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                updateCurrentSelectedPage(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
    }

    private void updateCurrentSelectedPage(final int selectedPage){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int counter = 0; counter < selectedImages.size(); counter++){
                    final ImageView imageView = (ImageView)llCurrentImgPosition.getChildAt(counter);
                    if(counter == selectedPage){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageResource(R.drawable.red_dot);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageResource(R.drawable.dot);
                            }
                        });
                    }
                }
            }
        }).start();
    }
}
