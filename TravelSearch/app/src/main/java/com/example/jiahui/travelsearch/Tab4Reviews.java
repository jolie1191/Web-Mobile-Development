package com.example.jiahui.travelsearch;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Tab4Reviews extends Fragment {

    public DetailsActivity detailsActivity;

    public JSONObject placeItemObject;


    public RecyclerView recyclerView;
    View reviewsView;
    TextView noReviewsTextView;

    List<ReviewItem> yelpList;
    private MyAdapter adapter;


    Spinner reviewTypeSpinner;
    Spinner orderTypeSpinner;
    ArrayAdapter<String> spinnerAdapter;
    String selectedReviewType;
    String selectedOrderType;

    public final String[] reviewType = {"Google reviews", "Yelp reviews"};
    public final  String[] orderType = {"Default Order", "Highest Rating", "Lowest Rating", "Most Recent", "Least Recent"};



    JSONArray reviewObj;

    String author_url;
    String profile_photo_url;

    String placeName;
    List<String> elephantList;


    public String key = "AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";
    //other services
    RequestQueue requestQueue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reviewsView = inflater.inflate(R.layout.tab4_reviews, container, false);

        detailsActivity = (DetailsActivity) getActivity();

        placeItemObject = detailsActivity.json_object;


        try {
            initView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviewsView;
    }


    public void initView() throws JSONException {
        recyclerView = (RecyclerView) reviewsView.findViewById(R.id.reviewRecyclerView);

        noReviewsTextView = (TextView) reviewsView.findViewById(R.id.noReviewsTextView);

        reviewTypeSpinner = (Spinner) reviewsView.findViewById(R.id.reviewTypeSpinner);
        orderTypeSpinner = (Spinner) reviewsView.findViewById(R.id.orderTypeSpinner);

        //review type spinner
        spinnerAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, reviewType);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        reviewTypeSpinner.setAdapter(spinnerAdapter);

        //review order type spinner
        spinnerAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, orderType);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        orderTypeSpinner.setAdapter(spinnerAdapter);

        requestQueue = Volley.newRequestQueue(getActivity());


        noReviewsTextView.setVisibility((View.INVISIBLE));

        //review type listener
        reviewTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                //int selectedIndex = position;
                selectedReviewType = reviewType[position];

                adapter = new MyAdapter(getActivity(), getData());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                //Log.i("Selected position", catergory + "->" + keyword);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //order type spinner listener
        orderTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                //int selectedIndex = position;
                selectedOrderType = orderType[position];

                //System.out.println("---------------------->selected order tyoe" + selectedOrderType);

                adapter = new MyAdapter(getActivity(), getData());

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                //Log.i("Selected position", catergory + "->" + keyword);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        //adapter = new MyAdapter(getActivity(), getData());


//        try {
//            json_object = new JSONObject(getIntent().getStringExtra("firstPageResult"));
//
//
//            if(json_object.getString("status").equals("OK") && json_object.getString("results").length() != 0){
//                hasPreviewButton.setVisibility(View.VISIBLE);
//                hasNextButton.setVisibility(View.VISIBLE);
//                //加入pagesData
//                pagesData = new ArrayList<>();
//                pagesData.add(json_object);
//                currentPage = 0;
//                /*-----------Above for pagination-----------------*/
//
//                resultsArray = json_object.getJSONArray("results");
//                resultsSize = resultsArray.length();
//
//                Log.e("result:", json_object.getString("results"));
//            }else{
//                hasPreviewButton.setVisibility(View.INVISIBLE);
//                hasNextButton.setVisibility(View.INVISIBLE);
//                noResultsTextView.setVisibility((View.VISIBLE));
//
//                return;
//            }
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("intent get Error: ", e.getLocalizedMessage());
//        }

        //先初始化list中的数据
        //再设置Adapter
        //每次改变的都list中的数据再重设Adapter
        //list.remove(position)
        //list.setAdapter(new RecyclerView.MyAdapter());


//        list = new ArrayList<>();
//
//        //ReviewItem(String name, String time, String review, String imageUrl, String rating)
//        list.add(new ReviewItem("jiahui", "123", "better Me", "www.google.com", "5"));
//        list.add(new ReviewItem("weifei", "234", "hello world", "www.facebook.com", "5"));


        //updateListPage(resultsArray);

//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<ReviewItem> getData(){

        noReviewsTextView.setVisibility(View.INVISIBLE);

        List<ReviewItem> list = new ArrayList<>();

        if(selectedReviewType.equals("Google reviews")){

            try {
                reviewObj = placeItemObject.getJSONArray("reviews");

                //System.out.println("---------------------------json Object" + reviewObj.toString());

                for(int i = 0; i < reviewObj.length(); i++){

                    author_url = reviewObj.getJSONObject(i).getString("author_url");
                    profile_photo_url = reviewObj.getJSONObject(i).getString("profile_photo_url");
                    String author_name = reviewObj.getJSONObject(i).getString("author_name");
                    String author_rating = reviewObj.getJSONObject(i).getString("rating");
                    String review_time = reviewObj.getJSONObject(i).getString("time");
                    String review_text = reviewObj.getJSONObject(i).getString("text");

                    ReviewItem reviewItem = new ReviewItem(author_name, review_time, review_text, profile_photo_url, author_rating, author_url );

                    list.add(reviewItem);
                }

            } catch (JSONException e) {
                e.printStackTrace();

                noReviewsTextView.setVisibility(View.VISIBLE);

                System.out.println("---------------------------json error");
            }

            list = sortListByOrderType(list, selectedOrderType);
            return list;

        }else{

            getYelpReviews();
            //sortListByOrderType(list, selectedOrderType);

            //list = yelpList;



            System.out.println("---------------------------yelp data" + yelpList.toString());

            return yelpList;
        }


        //return list;

    }

    public void getYelpReviews(){
        noReviewsTextView.setVisibility(View.INVISIBLE);
        yelpList = new ArrayList<>();

       System.out.println("----------------------------------->getYelpReviews");

        try {

            placeName = placeItemObject.getString("name");
            String str = placeItemObject.getString("formatted_address");
            elephantList = Arrays.asList(str.split(","));

            //System.out.println(placeName + "  " + elephantList.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("------------------------------elephant----------------"+ elephantList.toString());
        String url;
        if(elephantList.size() != 4){
            url = "http://placesearchnode-env.us-east-2.elasticbeanstalk.com/yelp/" + placeName + "/" + elephantList.get(0);
        }else
            url = "http://placesearchnode-env.us-east-2.elasticbeanstalk.com/yelp/" + placeName + "/"
                + elephantList.get(0) + "/" + elephantList.get(2) + "/" + elephantList.get(1) + "/" + elephantList.get(2) + "/" + elephantList.get(3);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONObject response) {

                //System.out.println("-------------------->json Data yelp" + response.toString());

                try {
                    JSONArray reviewsArray = response.getJSONArray("reviews");
                    for(int i = 0; i < reviewsArray.length(); i++){

                        String yelp_author_url = reviewsArray.getJSONObject(i).getString("url");

                        String yelp_profile_photo_url = reviewsArray.getJSONObject(i).getJSONObject("user").getString("image_url");


                        String yelp_author_name = reviewsArray.getJSONObject(i).getJSONObject("user").getString("name");
                        String yelp_author_rating = reviewsArray.getJSONObject(i).getString("rating");

                        String yelp_review_time = reviewsArray.getJSONObject(i).getString("time_created");
                        String yelp_review_text = reviewsArray.getJSONObject(i).getString("text");

                        //System.out.println("-------------------->json Data yelp" + yelp_review_text);

                        ReviewItem reviewItem = new ReviewItem(yelp_author_name, yelp_review_time, yelp_review_text, yelp_profile_photo_url, yelp_author_rating, yelp_author_url );

                        yelpList.add(reviewItem);

                        yelpList = sortListByOrderType(yelpList, selectedOrderType);

                        //处理异步问题
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    System.out.println("-----------------------yelp error when get reponse");
                    noReviewsTextView.setVisibility(View.VISIBLE);
                    yelpList = new ArrayList<>();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.e("yelp error", error.getLocalizedMessage());

                //Log.e("yelp error", "error");
                noReviewsTextView.setVisibility(View.VISIBLE);
                yelpList = new ArrayList<>();

            }
        });

                // Access the RequestQueue through your singleton class.
                requestQueue.add(jsonObjectRequest);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<ReviewItem> sortListByOrderType(List<ReviewItem> list, String orderType){

        //List<ReviewItem> tmpList = list;

        if( orderType == null || orderType.equals("Default Order")){

            return list;

        }else if(orderType.equals("Highest Rating")){

            list.sort((a, b)->b.getRating().compareTo(a.getRating()));

            //Collections.sort(list, (ReviewItem a, ReviewItem b)->b.getRating().compareTo(a.getRating()));

        }else if(orderType.equals("Lowest Rating")){

            list.sort((a, b)->a.getRating().compareTo(b.getRating()));

        }else if(orderType.equals("Most Recent")){

            list.sort((a, b)->b.getTime().compareTo(a.getTime()));

        }else if(orderType.equals("Least Recent")){

            list.sort((a, b)->a.getTime().compareTo(b.getTime()));
        }

        System.out.println("------------------->sorted list" + list.toString() );

        return list;
    }




    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        private final LayoutInflater inflater;
        List<ReviewItem> list = Collections.emptyList();

        public MyAdapter(Context context, List<ReviewItem> list){

            inflater = LayoutInflater.from(context);
            this.list = list;

        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.review_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            if (list.size() != 0) {

                holder.nameTV.setText(list.get(position).getName());

                if(selectedReviewType.equals("Google reviews")){

                    long epoch = Long.parseLong(list.get(position).getTime());
                    String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date (epoch*1000));
                    holder.timeTV.setText(date);

                }else{
                    holder.timeTV.setText(list.get(position).getTime());
                }


                holder.reviewTV.setText(list.get(position).getReview());
                holder.ratingBar2.setRating(Float.parseFloat(list.get(position).getRating()));
                //holder.authorImageView.setImageResource();
                Picasso.get().load(list.get(position).getImageUrl()).into(holder.authorImageView);

            }

            holder.reviewLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).getAuthorUrl()));
                    startActivity(browserIntent);

                    System.out.println("----------------------------------------->click listener here!!");

                }
            });




        }

        @Override
        public int getItemCount() {
            return list.size();
        }



        class MyViewHolder extends RecyclerView.ViewHolder{

            LinearLayout reviewLinearLayout;
            TextView nameTV;
            TextView timeTV;
            TextView reviewTV;
            ImageView authorImageView;
            RatingBar ratingBar2;

            public MyViewHolder(View itemView) {
                super(itemView);

                reviewLinearLayout = (LinearLayout) itemView.findViewById(R.id.reviewLinearLayout);
                nameTV = (TextView) itemView.findViewById(R.id.nameTV);
                timeTV = (TextView) itemView.findViewById(R.id.timeTV);
                reviewTV = (TextView) itemView.findViewById(R.id.reviewTV);
                authorImageView = (ImageView) itemView.findViewById(R.id.authorImageView);
                ratingBar2 = (RatingBar) itemView.findViewById(R.id.ratingBar2);

            }
        }
    }

}
