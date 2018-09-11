package com.example.jiahui.travelsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class Tab1Info extends Fragment {


    public DetailsActivity detailsActivity;

    public JSONObject placeItemObject;

    public View infoView;

    LinearLayout websiteLinearLayout;
    LinearLayout googleLinearLayout;
    LinearLayout ratingLinearLayout;
    LinearLayout addressLinearLayout;
    LinearLayout phoneLinearLayout;
    LinearLayout priceLinearLayout;

    TextView websiteTV;
    TextView googleTV;
    RatingBar ratingBar;
    TextView addressTV;
    TextView phoneTV;
    TextView priceTV;

    String websiteUrl;
    String googleUrl;
    String rating;
    String placeAddress;
    String phoneNumber;
    String price;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        infoView = inflater.inflate(R.layout.tab1_info, container, false);

        detailsActivity = (DetailsActivity) getActivity();

        placeItemObject = detailsActivity.json_object;

        System.out.println("----------------------->jsonItem from detail-----> " + placeItemObject.toString());

        //1. 获得控件

        websiteLinearLayout = infoView.findViewById(R.id.websiteLinearLayout);
        googleLinearLayout = infoView.findViewById(R.id.googleLinearLayout);
        ratingLinearLayout = infoView.findViewById(R.id.ratingLinearLayout);
        addressLinearLayout = infoView.findViewById(R.id.addressLinearLayout);
        phoneLinearLayout = infoView.findViewById(R.id.phoneLinearLayout);
        priceLinearLayout = infoView.findViewById(R.id.priceLinearLayout);

        websiteTV = infoView.findViewById(R.id.websiteTV);
        googleTV = infoView.findViewById(R.id.googleTV);
        ratingBar = infoView.findViewById(R.id.ratingBar);
        addressTV = infoView.findViewById(R.id.addressTV);
        phoneTV = infoView.findViewById(R.id.phoneTV);
        priceTV = infoView.findViewById(R.id.priceTV);

        //2. 从placeItemObject中提取需要参数
        //3. 判断参数是否存在
        //4. 赋值
        try {

            websiteUrl = placeItemObject.getString("website");
            websiteTV.setText(websiteUrl);

        } catch (JSONException e) {
            e.printStackTrace();
            websiteLinearLayout.setVisibility(View.INVISIBLE);
        }

        try {

            googleUrl = placeItemObject.getString("url");
            googleTV.setText(googleUrl);

        } catch (JSONException e) {
            e.printStackTrace();
            googleLinearLayout.setVisibility(View.INVISIBLE);
        }

        try {

            rating = placeItemObject.getString("rating");
            ratingBar.setRating(Float.parseFloat(rating));

        } catch (JSONException e) {
            e.printStackTrace();
            ratingLinearLayout.setVisibility(View.INVISIBLE);
        }

        try {

            placeAddress = placeItemObject.getString("formatted_address");
            addressTV.setText(placeAddress);

        } catch (JSONException e) {
            e.printStackTrace();
            addressLinearLayout.setVisibility(View.INVISIBLE);
        }

        try {

            phoneNumber = placeItemObject.getString("formatted_phone_number");
            phoneTV.setText(phoneNumber);

        } catch (JSONException e) {
            e.printStackTrace();
            phoneLinearLayout.setVisibility(View.INVISIBLE);
        }

        try {
            String priceTag;
            price = placeItemObject.getString("price_level");

            if(price.equals("1"))
                priceTag = "$";
            else if(price.equals("2"))
                priceTag = "$$";
            else if(price.equals("3"))
                priceTag = "$$$";
            else if(price.equals("4"))
                priceTag = "$$$$";
            else
                priceTag = "$$$$$";

            priceTV.setText(priceTag);

        } catch (JSONException e) {
            e.printStackTrace();
            priceLinearLayout.setVisibility(View.INVISIBLE);
        }


        return infoView;
    }

}
