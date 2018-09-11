package com.example.jiahui.travelsearch;

import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlaceItem {

    public String catergoryImgUrl;
    public String placeAddress;
    public Boolean starLike;
    public  String placeId;
    public JSONObject json_item;
    public  String placeName;

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }


    public JSONObject getJson_item() {
        return json_item;
    }

    public void setJson_item(JSONObject json_item) {
        this.json_item = json_item;
    }


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public PlaceItem() {
    }

    public PlaceItem(Boolean starLike) {
        this.starLike = starLike;
    }


    public String getCatergoryImgUrl() {
        return catergoryImgUrl;
    }

    public void setCatergoryImgUrl(String catergoryImgUrl) {
        this.catergoryImgUrl = catergoryImgUrl;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public Boolean getStarLike() {
        return starLike;
    }

    public void setStarLike(Boolean starLike) {
        this.starLike = starLike;
    }


}
