package com.fivenine.sharebutter.AddOffer;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivenine.sharebutter.Home.HomeActivity;
import com.fivenine.sharebutter.Home.HomeFragment;
import com.fivenine.sharebutter.Profile.ProfileFragment;
import com.fivenine.sharebutter.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

public class AddOfferFragment extends Fragment implements View.OnClickListener {
    public static final String SELECTED_IMAGES = "selected_images";
    private static final String TAG = "AddOfferFragment";
    private   final int RC_ADD_NEW_OFFER = 3;
    private final int CODE_MULTIPLE_IMG_GALLERY = 2;

    private View view;
    private ImageView ivCurrentImage;
    private GridLayout glOfferImages;
    private ImageView ivBack, ivAddSign;
    private TextView tvAction;
    private TextView tvTitle;

    private ArrayList<String> imgUri;

    private ViewGroup container;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addoffer, container, false);
        this.container = container;

        init();
        return view;
    }

    public void init() {
        ivCurrentImage = view.findViewById(R.id.iv_current_selected_img_offer);
        glOfferImages = view.findViewById(R.id.gl_other_upload_img_offer);
        ivBack = view.findViewById(R.id.tb_iv_support_action);
        ivAddSign = view.findViewById(R.id.iv_add_sign);
        tvAction = view.findViewById(R.id.tb_tv_action);
        tvTitle = view.findViewById(R.id.tb_tv_title);

        ivBack.setVisibility(GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Add Offer");
        tvAction.setOnClickListener(this);
        tvAction.setText("");
        tvAction.setVisibility(View.INVISIBLE);

        glOfferImages.setUseDefaultMargins(true);
        ivCurrentImage.setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_ADD_NEW_OFFER && resultCode == RESULT_OK){
            HomeActivity mHomeActivity = (HomeActivity)getActivity();
            mHomeActivity.viewPager.arrowScroll(View.FOCUS_LEFT);

            tvAction.setText("");
            ivAddSign.setVisibility(View.VISIBLE);
            ivCurrentImage.setImageDrawable(view.getResources().getDrawable(R.drawable.layout_add_offer_sign));

            glOfferImages.removeAllViews();
        }

        if (requestCode == CODE_MULTIPLE_IMG_GALLERY && resultCode == RESULT_OK) {
            ClipData clipData = data.getClipData();
            imgUri = new ArrayList<>();
            glOfferImages.removeAllViews();

            if (clipData != null) {
                ivCurrentImage.setImageURI(clipData.getItemAt(0).getUri());

                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setImageURI(clipData.getItemAt(i).getUri());
                    imageView.setOnClickListener(this);
                    imageView.setDrawingCacheEnabled(true);
                    imageView.setTag(i);
                    imgUri.add(clipData.getItemAt(i).getUri().toString());
                    glOfferImages.addView(imageView, glOfferImages.getWidth() / 3, glOfferImages.getWidth() / 3);
                }
            }
        }


        if (glOfferImages.getChildCount() > 3) {
            tvAction.setText("");
            tvAction.setEnabled(false);
            Toast.makeText(getContext(), "Maximum 3 Pictures only.", Toast.LENGTH_SHORT).show();
            ivAddSign.setVisibility(View.GONE);
        } else if (glOfferImages.getChildCount() > 0 && glOfferImages.getChildCount() <= 3) {
            tvAction.setVisibility(View.VISIBLE);
            tvAction.setText("NEXT");
            tvAction.setEnabled(true);
            ivAddSign.setVisibility(View.GONE);
        } else {
            tvAction.setText("");
            tvAction.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.iv_current_selected_img_offer) {
            //Start Gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Selection various images"), CODE_MULTIPLE_IMG_GALLERY);
        } else if (v.getId() == R.id.tb_tv_action) {
            if(tvAction.getText().toString().equals("NEXT")){
                Intent intent = new Intent(getContext(), AddOfferActivity.class);
                intent.putStringArrayListExtra(SELECTED_IMAGES, imgUri);
                startActivityForResult(intent, RC_ADD_NEW_OFFER);
            }
        } else {

            if (v.getTag() != null) {
                for (int i = 0; i < glOfferImages.getChildCount(); i++) {
                    if (glOfferImages.getChildAt(i).getTag().equals(v.getTag())) {
                        ImageView ivCurrent = (ImageView) glOfferImages.getChildAt(i);

                        ivCurrentImage.setImageBitmap(ivCurrent.getDrawingCache(true));
                    }
                }
            }
        }
    }
}
