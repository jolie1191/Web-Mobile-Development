package com.example.jiahui.travelsearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Tab2Favorites extends Fragment {

    public View favsView;

    Button hasPreviewButton;
    Button hasNextButton;
    TextView noFavResultsTextView;
    ListView listView;

    public ArrayList<JSONArray> favPagesData;

    public int favCurrentPage;
    public JSONObject favJson_obj;
    public JSONArray favResultsArray;

    ArrayList<PlaceItem> list;

    JSONObject json_item;
    JSONObject placeItemDetail;
    String placeAdress;
    String catergoryImgUrl;
    String placeId;
    String placeName;

    public String key = "AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";

    RequestQueue requestQueue;
    ProgressDialog progressDialog;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        favsView =  inflater.inflate(R.layout.tab2favorites, container, false);

        requestQueue = Volley.newRequestQueue(getActivity());

        //1. 获取到所有的控件, 以及基本的初始设定
        //2.

        hasPreviewButton = (Button) favsView.findViewById(R.id.hasPreviewButton);
        hasNextButton = (Button) favsView.findViewById(R.id.hasNextButton);
        noFavResultsTextView = (TextView) favsView.findViewById(R.id.noFavResultsTV);
        listView = (ListView) favsView.findViewById(R.id.favsListView);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);
        if(sharedPreferences.getAll().size() == 0){

            noFavResultsTextView.setVisibility(View.VISIBLE);
            hasNextButton.setEnabled(false);
            hasPreviewButton.setEnabled(false);

            return favsView;

        } else if(sharedPreferences.getAll().size() <= 20){

            noFavResultsTextView.setVisibility(View.INVISIBLE);
            hasNextButton.setEnabled(false);
            hasPreviewButton.setEnabled(false);

        }else{

            noFavResultsTextView.setVisibility(View.INVISIBLE);
            hasNextButton.setEnabled(true);
            hasPreviewButton.setEnabled(false);

        }

        //System.out.println("-----------------Share preferences: " + sharedPreferences.getAll().toString());

        initView();

        return favsView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            System.out.println("------------------>page refresh");
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();

        }
    }


    public void onClick(View v) {
        switch(v.getId()){
            case R.id.hasPreviewButton:
                //goBack(v);
                break;
            case R.id.hasNextButton:
                //goNext(v);
                break;
        }

    }

    public void initView(){

        favPagesData = new ArrayList<>();
        favResultsArray = new JSONArray();

        favCurrentPage = 0;


//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);
//
//        Map<String,?> hashMap = sharedPreferences.getAll();
//        for(String key : hashMap.keySet()){
//
//            try {
//
//                JSONObject json_item = new JSONObject(hashMap.get(key).toString());
//                favResultsArray.put(json_item);
//
//                Log.e("jsonObject: ", json_item.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }

        //从sharePreference 中拿到所有favResultsArray数据
        getAllFavResultsArray();

        //favResultsArray may have over 35
        dividePagerHelper(favResultsArray);
        //System.out.println("favPagesData------------------------->" + favPagesData.toString());

        //当前页面的list， 18个item
        list = new ArrayList<>();
        if(favPagesData.size() == 0)
            noFavResultsTextView.setVisibility(View.VISIBLE);
        else{
            updateListPage(favPagesData.get(favCurrentPage));

            listView.setAdapter(new Tab2Favorites.MyAdapter());


        }

    }

    public void getAllFavResultsArray(){


        //从sharePreference 中拿到所有favResultsArray数据
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);

        if(sharedPreferences.getAll().size() == 0){

            noFavResultsTextView.setVisibility(View.VISIBLE);
            hasNextButton.setEnabled(false);
            hasPreviewButton.setEnabled(false);

            return;

        } else if(sharedPreferences.getAll().size() <= 20){

            noFavResultsTextView.setVisibility(View.INVISIBLE);
            hasNextButton.setEnabled(false);
            hasPreviewButton.setEnabled(false);

        }else{

            noFavResultsTextView.setVisibility(View.INVISIBLE);
            hasNextButton.setEnabled(true);
            hasPreviewButton.setEnabled(false);

        }


        Map<String,?> hashMap = sharedPreferences.getAll();
        for(String key : hashMap.keySet()){

            try {

                JSONObject json_item = new JSONObject(hashMap.get(key).toString());
                favResultsArray.put(json_item);

                Log.e("jsonObject: ", json_item.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void dividePagerHelper(JSONArray favResultsArray){
        //favPagesData
        int totalPage = (int) Math.ceil(favResultsArray.length()/20.0) ;
        for(int i = 0; i < totalPage; i++){
            JSONArray onePageJsonArr = new JSONArray();
            for(int j = i*20; j < favResultsArray.length() && j < 20; j++ ){
                //onePageJsonArr.put((JSONObject)favResultsArray.get(j));
                try {

                    JSONObject fav_item = (JSONObject) favResultsArray.get(j);
                    onePageJsonArr.put(fav_item);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            favPagesData.add(onePageJsonArr);
        }

    }


    public void updateListPage(JSONArray resultsArray){

        ArrayList<PlaceItem> tmpList = new ArrayList<>();
        for(int i = 0 ; i < resultsArray.length(); i++){
            PlaceItem item = new PlaceItem();
            try {
                json_item =  resultsArray.getJSONObject(i);
                placeAdress = json_item.getString("vicinity");
                catergoryImgUrl = json_item.getString("icon");
                placeId = json_item.getString("place_id");
                placeName = json_item.getString("name");

                System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + catergoryImgUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            item.setCatergoryImgUrl(catergoryImgUrl);
            item.setPlaceAddress(placeAdress);

//            //通过sharePreference 检查PlaceID是否存在
//            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);
//            //sharedPreferences.getString(placeId, "NODATA");
//
//            if(sharedPreferences.getString(placeId, "NODATA").equals("NODATA")){
//                item.setStarLike(false);
//            } else{
//                item.setStarLike(true);
//            }
            item.setStarLike(true);

            /**********************************/
            //item.setStarLike(false);
            item.setPlaceId(placeId);
            item.setPlaceName(placeName);

            item.setJson_item(json_item);

            tmpList.add(item);
            list = tmpList;
        }

    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            //把数据设置到resultsItem中的布局中
            //put results_item to 当前View中
            View itemView =  View.inflate(getActivity(), R.layout.results_item, null);
            ImageView catergoryImg = (ImageView) itemView.findViewById(R.id.catergoryImageView);
            TextView placeAddress = (TextView) itemView.findViewById(R.id.placeTextView);
            ImageView likeUnlikeImageView = (ImageView) itemView.findViewById(R.id.likeUnlikeImageView);
            TextView placeNameTextView = (TextView) itemView.findViewById(R.id.placeNameTextView);
            LinearLayout placeInfoLinearLayout = (LinearLayout) itemView.findViewById(R.id.placeInfoLinearLayout);


            likeUnlikeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(list.get(position).getStarLike() == false){
                        list.get(position).setStarLike(true);
                        //pagesData.get(currentPage).getJSONArray("results") = list;

                        listView.setAdapter(new Tab2Favorites.MyAdapter());
                        //SharePreference !! Add to
                        /**************************************************************************/
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString(list.get(position).getPlaceId(),list.get(position).getJson_item().toString()).apply();
                        //System.out.println("---------------------------------JsonObject: " + list.get(position).getJson_item().toString());

                    }else{
                        //list.get(position).setStarLike(false);

                        //listView.setAdapter(new Tab2Favorites.MyAdapter());
                        //Move item from share preference
                        /***************************************************************************/
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);
                        sharedPreferences.edit().remove(list.get(position).getPlaceId()).apply();

//                        getAllFavResultsArray();
//                        dividePagerHelper(favResultsArray);
//                        list = new ArrayList<>();
//                        updateListPage(favPagesData.get(favCurrentPage));
//                        listView.setAdapter(new Tab2Favorites.MyAdapter());
                        Toast.makeText(getActivity(), list.get(position).getPlaceName() + " was removed from favorites", Toast.LENGTH_SHORT).show();

                        getFragmentManager().beginTransaction().detach(Tab2Favorites.this).attach(Tab2Favorites.this).commit();

                    }

                }
            });


            placeInfoLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Fetching results");
                    progressDialog.show();

                    getOtherLatLngFromDetail(list.get(position).getPlaceId());

                }
            });

            //set Bitmap image to ImageView
            Picasso.get().load(list.get(position).getCatergoryImgUrl()).into(catergoryImg);
            placeAddress.setText(list.get(position).getPlaceAddress());

            placeNameTextView.setText((list.get(position).getPlaceName()));


            if(list.get(position).getStarLike() == false){

                likeUnlikeImageView.setImageResource(R.drawable.heart_outline_black);
            }else{
                likeUnlikeImageView.setImageResource(R.drawable.heart_fill_red);
            }


            return itemView;
        }
    }


    public void getOtherLatLngFromDetail(final String placeId){



        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + key;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    placeItemDetail = response.getJSONObject("result");

                    String fromFavorite = "true";

                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra("placeItemDetail", placeItemDetail.toString());

                    intent.putExtra("fromFavorite", fromFavorite);
                    intent.putExtra("placeId", placeId);
                    startActivity(intent);

                    progressDialog.dismiss();

                    System.out.println("---------------placeId PlaceDetail----------------->" + placeItemDetail.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("GeolocFromDetailError" , error.getLocalizedMessage());

            }
        });

        // Access the RequestQueue through your singleton class.
        requestQueue.add(jsonObjectRequest);

    }


    @Override
    public void onResume() {
        super.onResume();
        initView();

        System.out.println("--------------------go to favirte");
    }
}
