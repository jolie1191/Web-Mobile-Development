package com.example.jiahui.travelsearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import org.w3c.dom.Text;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    ListView listView;
    Button hasPreviewButton;
    Button hasNextButton;
    TextView noResultsTextView;



    ArrayList<PlaceItem> list;
    JSONObject json_object;
    JSONArray resultsArray;
    JSONObject json_item;

    String placeAdress;
    String catergoryImgUrl;
    String firstPageNextToken;
    String placeId;
    String placeName;
    int currentPage;

    ArrayList<JSONObject> pagesData;

    int resultsSize;

    //volley service
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    public String key = "AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";

    JSONObject placeItemDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        requestQueue = Volley.newRequestQueue(this);
        //找到控件， 并初始化listView


        try {
            initView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void initView() throws JSONException {
        listView = (ListView) findViewById(R.id.resultsListView);
        hasPreviewButton = (Button) findViewById(R.id.hasPreviewButton);
        hasNextButton = (Button) findViewById(R.id.hasNextButton);
        noResultsTextView = (TextView) findViewById(R.id.noResultsTextView);

        hasPreviewButton.setEnabled(false);
        hasNextButton.setEnabled(false);
        hasPreviewButton.setVisibility(View.INVISIBLE);
        hasNextButton.setVisibility(View.INVISIBLE);
        noResultsTextView.setVisibility((View.INVISIBLE));


        try {
            json_object = new JSONObject(getIntent().getStringExtra("firstPageResult"));


            if(json_object.getString("status").equals("OK") && json_object.getString("results").length() != 0){
                hasPreviewButton.setVisibility(View.VISIBLE);
                hasNextButton.setVisibility(View.VISIBLE);
                //加入pagesData
                pagesData = new ArrayList<>();
                pagesData.add(json_object);
                currentPage = 0;
                /*-----------Above for pagination-----------------*/

                resultsArray = json_object.getJSONArray("results");
                resultsSize = resultsArray.length();

                Log.e("result:", json_object.getString("results"));
            }else{
                hasPreviewButton.setVisibility(View.INVISIBLE);
                hasNextButton.setVisibility(View.INVISIBLE);
                noResultsTextView.setVisibility((View.VISIBLE));

                return;
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("intent get Error: ", e.getLocalizedMessage());
        }

        //先初始化list中的数据
        //再设置Adapter
        //每次改变的都list中的数据再重设Adapter
        //list.remove(position)
        //list.setAdapter(new MyAdapter());
        if(json_object.has("next_page_token")){
            firstPageNextToken = json_object.getString("next_page_token");
            hasPreviewButton.setEnabled(false);
            hasNextButton.setEnabled(true);
        }else{
            hasPreviewButton.setEnabled(false);
            hasNextButton.setEnabled(false);
        }

        list = new ArrayList<>();

        if(resultsSize != 0){
            updateListPage(resultsArray);


        }else{

            list = new ArrayList<>();
        }
        listView.setAdapter(new MyAdapter());


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

            //通过sharePreference 检查PlaceID是否存在
            SharedPreferences sharedPreferences = getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);
            //sharedPreferences.getString(placeId, "NODATA");

            if(sharedPreferences.getString(placeId, "NODATA").equals("NODATA")){
                item.setStarLike(false);
            } else{
                item.setStarLike(true);
            }

            /**********************************/
            //item.setStarLike(false);
            item.setPlaceId(placeId);
            item.setPlaceName(placeName);

            item.setJson_item(json_item);

            tmpList.add(item);
            list = tmpList;
        }

    }


    public class MyAdapter extends BaseAdapter{

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
            View itemView =  View.inflate(ResultsActivity.this, R.layout.results_item, null);
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

                        listView.setAdapter(new MyAdapter());
                        //SharePreference !! Add to
                        /**************************************************************************/
                        SharedPreferences sharedPreferences = getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString(list.get(position).getPlaceId(),list.get(position).getJson_item().toString()).apply();
                        //System.out.println("---------------------------------JsonObject: " + list.get(position).getJson_item().toString());

                        Toast.makeText(ResultsActivity.this, list.get(position).getPlaceName() + " was added to favorites", Toast.LENGTH_SHORT).show();

                    }else{
                        list.get(position).setStarLike(false);


                        listView.setAdapter(new MyAdapter());
                        //Move item from share preference
                        /***************************************************************************/
                        SharedPreferences sharedPreferences = getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);
                        sharedPreferences.edit().remove(list.get(position).getPlaceId()).apply();

                        Toast.makeText(ResultsActivity.this, list.get(position).getPlaceName() + " was removed from favorites", Toast.LENGTH_SHORT).show();


                    }

                }
            });


            placeInfoLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //JSONObject placeItemDetail;

                    getOtherLatLngFromDetail(list.get(position).getPlaceId(), list.get(position).getStarLike(), list.get(position).getJson_item());


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


    public void getOtherLatLngFromDetail(final String placeId, Boolean likeUnlike, JSONObject json_item){



        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + key;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    placeItemDetail = response.getJSONObject("result");

                    Intent intent = new Intent(ResultsActivity.this, DetailsActivity.class);
                    intent.putExtra("placeItemDetail", placeItemDetail.toString());
                    intent.putExtra("likeUnlike", likeUnlike);
                    intent.putExtra("placeId", placeId);
                    intent.putExtra("json_item", json_item.toString());
                    startActivity(intent);

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


    public void goNext(View view){

        System.out.println("-------------------------------->CurrentPage: " + currentPage + "-->go Page: " + (currentPage + 1));
        currentPage++;

        //list = new ArrayList<>();
        //1. 通过volley 得到行的jsonOBject
        //2. 确认是否有nexttoken，有-赋值，没有firsttoken=""
        //3. 更新list， Adapter
        //4. 把当前object加入pages
        //5. 判断下次next和preview

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching results");
        progressDialog.show();

        try {
            json_object = pagesData.get(currentPage);
            resultsArray = json_object.getJSONArray("results");
            resultsSize = resultsArray.length();

            currentPage = currentPage;

            updateListPage(resultsArray);
            listView.setAdapter(new MyAdapter());

            if(currentPage == pagesData.size() - 1 && firstPageNextToken.equals("")){
                hasNextButton.setEnabled(false);
                hasPreviewButton.setEnabled(true);

            }else{

                hasNextButton.setEnabled(true);
                hasPreviewButton.setEnabled(true);
            }
            progressDialog.dismiss();

        } catch ( IndexOutOfBoundsException e ) {
            //list.add( index, new Object() );
            getJsonObjectByToken(firstPageNextToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //getJsonObjectByToken(firstPageNextToken);



    }

    public void getJsonObjectByToken(String token){

        //json_object =

        String url = "http://travelsearchnode-env.us-east-2.elasticbeanstalk.com/pages/" + firstPageNextToken;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("jaso: " , response.toString());
                //System.out.println("-------------------------------->" + response.toString());
                try {
                    json_object = response;

                    resultsArray = json_object.getJSONArray("results");
                    resultsSize = resultsArray.length();

                    currentPage = currentPage;

                    pagesData.add(json_object);

                    updateListPage(resultsArray);
                    listView.setAdapter(new MyAdapter());

                    if(json_object.has("next_page_token")){
                        firstPageNextToken = json_object.getString("next_page_token");
                        hasPreviewButton.setEnabled(true);
                        hasNextButton.setEnabled(true);
                    }else{
                        firstPageNextToken = "";
                        hasPreviewButton.setEnabled(true);
                        hasNextButton.setEnabled(false);
                    }


                    progressDialog.dismiss();

                    System.out.println("---------------json_object----------------->" + json_object.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("GoNextPageError" , error.getLocalizedMessage());

            }
        });

        // Access the RequestQueue through your singleton class.
        requestQueue.add(jsonObjectRequest);
    }


    public void goBack(View view){
        System.out.println("-------------------------------->CurrentPage: " + currentPage + "-->go Page: " + (currentPage - 1));
        currentPage--;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching results");
        progressDialog.show();

        try {
            json_object = pagesData.get(currentPage);
            resultsArray = json_object.getJSONArray("results");
            resultsSize = resultsArray.length();

            currentPage = currentPage;

            updateListPage(resultsArray);
            listView.setAdapter(new MyAdapter());

            if(currentPage == 0){
                hasNextButton.setEnabled(true);
                hasPreviewButton.setEnabled(false);

            }else{

                hasNextButton.setEnabled(true);
                hasPreviewButton.setEnabled(true);
            }
            progressDialog.dismiss();

        } catch ( IndexOutOfBoundsException e ) {
            //list.add( index, new Object() );
            //getJsonObjectByToken(firstPageNextToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Go Back");
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(resultsSize != 0)
            updateListPage(resultsArray);
        else
            list = new ArrayList<>();

        listView.setAdapter(new MyAdapter());


    }
}
