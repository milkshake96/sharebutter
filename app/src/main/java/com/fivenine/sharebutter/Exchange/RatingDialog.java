package com.fivenine.sharebutter.Exchange;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivenine.sharebutter.Message.MessageActivity;
import com.fivenine.sharebutter.R;
import com.fivenine.sharebutter.Rating.RatingActivity;
import com.google.gson.Gson;

public class RatingDialog extends AppCompatDialogFragment implements View.OnClickListener{

    ImageView ivMessage;
    ImageView ivClose;
    ImageView ivRating;
    AlertDialog.Builder builder;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_congratulations_dialogbox, null);

        builder.setView(view)
                .setTitle("");

        ivClose = view.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(this);

        ivMessage = view.findViewById(R.id.iv_message);
        ivMessage.setOnClickListener(this);

        ivRating = view.findViewById(R.id.iv_rate);
        ivRating.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_message:
                messageClicked();
                break;
            case R.id.iv_close:
                closeClicked();
            case R.id.iv_rate:
                ratingClicked();
                break;
            default:
                break;
        }
    }

    private void messageClicked(){
        dismiss();
    }

    private void closeClicked(){
        dismiss();
    }

    private void ratingClicked(){
        dismiss();

        Intent intent = new Intent(getContext(), RatingActivity.class);
        MessageActivity messageActivity = (MessageActivity) getActivity();
        intent.putExtra(MessageActivity.COMPLETED_TRADE_OFFER, new Gson().toJson(messageActivity.completedTradeOffer));
        startActivity(intent);
    }
}
