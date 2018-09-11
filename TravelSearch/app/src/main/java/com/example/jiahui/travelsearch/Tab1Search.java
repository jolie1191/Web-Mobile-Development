package com.example.jiahui.travelsearch;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class Tab1Search extends Fragment implements View.OnClickListener {

    //get all view
    public View searchView;
    Spinner spinner;
    //spinner array adapter
    ArrayAdapter<String> adapter;
    EditText keywordEditText;
    //TextView noKeywordTextView;
    EditText distanceEditText;
    RadioGroup locationGroupRadio;
    RadioButton checkedRadio;
    //TextView noOtherLocationTextView;
    PlacesAutocompleteTextView placesAutocompleteTextView;
    Button searchButton;
    Button clearButton;

    TextView noKeywordTV;
    TextView noLocationTV;

    //all info
    public String keyword;
    public String catergory;
    public String distance;
    //public Boolean locationHereOrOther;
    public String choice; //selected Radio: "here", "other"
    public String locationOtherName;
    public  LatLng otherLocationLatLng;
    public LatLng userLocation;

    public String otherPlaceId;

    public String otherLat;
    public String otherLng;

    public static JSONObject firstPageresult;




    public final String[] catergoryValues = {"Default", "Airport", "Amusement Park", " Aquarium", " Art Gallery",
            " Bakery", " Bar", " Beauty Salon", " Bowling Alley", " Bus Station", " Cafe",
            " Campground", " Car Rental", " Casino", " Lodging", " Movie Theater",
            "Museum ", " Night Club", " Park", " Parking", " Restaurant",
            " Shopping Mall", " Stadium", " Subway Station", "Taxi Stand ", "Train Station ",
            " Transit Station", "Travel Agency ", " Zoo"};

    public String[] catergories = {"default", "airport", "amusement_park", "aquarium", "art_gallery",
            "bakery", "bar", "beauty_salon", "bowling_alley", "bus_station", "cafe",
            "campground", "car_rental", "casino", "lodging", "movie_theater",
            "museum ", "night_club", "park", "parking", "restaurant",
            "shopping_mall", "stadium", "subway_station", "taxi_stand", "train_station",
            "transit_station", "travel_agency ", "zoo"};


    public String key = "AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";
    //other services
    RequestQueue requestQueue;

    ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        searchView = inflater.inflate(R.layout.tab1search, container, false);

        //获取到所有view
        //keyword
        keywordEditText = (EditText) searchView.findViewById(R.id.keywordEditText);
        noKeywordTV = (TextView) searchView.findViewById(R.id.noKeywordTextView);
        noLocationTV = (TextView) searchView.findViewById(R.id.noOtherLocationTextView);

        //Set spinner
        spinner = (Spinner) searchView.findViewById(R.id.spinner);

        //distance view
        distanceEditText = (EditText) searchView.findViewById(R.id.distanceEditText);

        //location radio group
        locationGroupRadio = (RadioGroup) searchView.findViewById(R.id.locationGroupRadio);
        noLocationTV = (TextView) searchView.findViewById(R.id.noOtherLocationTextView);

        //place autocompelete
        placesAutocompleteTextView = (PlacesAutocompleteTextView) searchView.findViewById(R.id.places_autocomplete);

        //Button group
        searchButton = (Button) searchView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        clearButton = (Button) searchView.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(this);

        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, catergoryValues);

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spinner.setAdapter(adapter);

        //set Error message invisiable
        noKeywordTV.setVisibility(View.INVISIBLE);
        noLocationTV.setVisibility(View.INVISIBLE);

        //set placeAutocompelete
        placesAutocompleteTextView.setEnabled(false);

        requestQueue = Volley.newRequestQueue(getActivity());

        return searchView;

    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //==========keyword text=====================//
        //keyword = keywordEditText.getText().toString();

        //=====================set category spinner selected listener==========================//


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                //int selectedIndex = position;
                catergory = catergories[position];

                Log.i("Selected position", catergory + "->" + keyword);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locationGroupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //checkedRadio = (RadioButton)group.findViewById(checkedId);
                switch(checkedId){
                    case R.id.hereRadio:
                        //placesAutocompleteTextView.clearComposingText();
                        noLocationTV.setVisibility(View.INVISIBLE);
                        placesAutocompleteTextView.setText("");
                        placesAutocompleteTextView.setEnabled(false);
                        break;
                    case R.id.otherRadio:
                        placesAutocompleteTextView.setEnabled(true);
                        break;
                }

            }
        });

        placesAutocompleteTextView.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        // do something awesome with the selected place

                        otherPlaceId = place.place_id;
                        //System.out.println("place Id--------------------------------  " +  place.place_id);
                    }
                }
        );




    }


    public void onSearch(View view){

        //get Here coordinate
        MainActivity mainActivity = (MainActivity) getActivity();
        userLocation = mainActivity.userLocation;

        //reset
        noKeywordTV.setVisibility(View.INVISIBLE);
        noLocationTV.setVisibility(View.INVISIBLE);

        keyword = keywordEditText.getText().toString().trim();
        //catergory
        distance = distanceEditText.getText().toString().trim();
        //
        int selectedId=locationGroupRadio.getCheckedRadioButtonId();
        RadioButton locationHereOrOther = (RadioButton) searchView.findViewById(selectedId);
        choice = (String) locationHereOrOther.getTag();

        locationOtherName = (String) placesAutocompleteTextView.getText().toString().trim();


        //检查form是否为empty
        Boolean isValid = checkValidation(view);

        if(isValid == false){
            Toast.makeText(getActivity(), "Please fix all fields with errors ", Toast.LENGTH_SHORT).show();
            return;
        }else {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching results");
            progressDialog.show();

            if(choice.equals("other")){
                getOtherLatLngFromDetail(otherPlaceId);
            }else{
                getAllPlacesAndJump();
            }

            //get firstPageresult + 跳转到下一页
            //getAllPlacesAndJump();


            System.out.println("keyword: " + keyword);
            System.out.println("distance: " + distance);
            System.out.println("catergory: " + catergory);
            System.out.println("choice: " + choice);
            System.out.println("locationOtherName: " + locationOtherName);
            System.out.println("userLocation: " + userLocation.latitude + "  " + userLocation.longitude);


        }


    }

    public void onClear(View v){

        /*
        *
        * EditText keywordEditText;
        TextView noKeywordTextView;
        EditText distanceEditText;
        RadioGroup locationGroupRadio;
        RadioButton checkedRadio;
        TextView noOtherLocationTextView;
        PlacesAutocompleteTextView placesAutocompleteTextView;
        Button searchButton;
        Button clearButton;

        TextView noKeywordTV;
        TextView noLocationTV;


        //all info
        public String keyword;
        public String catergory;
        public String distance;
        //public Boolean locationHereOrOther;
        public String choice; //selected Radio: "here", "other"
        public String locationOtherName;
        public  LatLng otherLocationLatLng;
        public LatLng userLocation;

        public String otherPlaceId;

        public static JSONObject firstPageresult;
            *
        * */

        keywordEditText.setText("");
        noKeywordTV.setVisibility(View.INVISIBLE);
        distanceEditText.setText("");
        RadioButton hereRadio = (RadioButton) searchView.findViewById(R.id.hereRadio);
        hereRadio.setChecked(true);
        noLocationTV.setVisibility(View.INVISIBLE);
        placesAutocompleteTextView.setText("");

        spinner.setSelection(adapter.getPosition("Default"));


        keyword = "";
        catergory = "default";
        distance = "";
        choice = "";
        locationOtherName = "";

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.searchButton:
                onSearch(v);
                break;
            case R.id.clearButton:
                onClear(v);
                break;
        }

    }

    public Boolean checkValidation(View view){
        Boolean rs = true;

        if(keyword.length() == 0){
            noKeywordTV.setVisibility(View.VISIBLE);
            rs = false;
        }

        if(choice.equals("other") && locationOtherName.length() == 0){
            noLocationTV.setVisibility(View.VISIBLE);
            rs = false;
        }

        return rs;
    }


    LatLng res;
    public void getOtherLatLngFromDetail(final String placeId){

        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + key;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("jaso: " , response.toString());
                //System.out.println("-------------------------------->" + response.toString());
                try {
                    JSONObject result = response.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                    //res = new LatLng(result.);
                    otherLat = String.valueOf(result.getDouble("lat"));
                    otherLng = String.valueOf(result.getDouble("lng"));
                    //res = new LatLng(lat, lng);

                    getAllPlacesAndJump();

                    System.out.println("---------------placeId----------------->" + otherLat + "," + otherLng);

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

    //http://localhost:3000/showresults?catergory=default&distance=&geolocation=34.0674,-118.2423&keyword=coffee&location=0

    public JSONObject getAllPlacesAndJump(){

        //http://travelsearchnode-env.us-east-2.elasticbeanstalk.com/showresults?catergory=default&distance=&geolocation=34.0266,-118.2831&keyword=pizza&location=0
        String choiceTmp;
        String url;
        if(choice.equals("here")){
            choiceTmp = "0";
            url = "http://placesearchnode-env.us-east-2.elasticbeanstalk.com/showresults?catergory=" + catergory
                    + "&distance=" + distance + "&geolocation=" + userLocation.latitude + "," + userLocation.longitude
                    + "&keyword=" + keyword + "&location=" + choiceTmp;


        } else{
            choiceTmp = "1";
            url = "http://placesearchnode-env.us-east-2.elasticbeanstalk.com/showresults?catergory=" + catergory
                    + "&distance=" + distance + "&geolocation="  + otherLat + "," + otherLng +  "&keyword=" + keyword +
                    "&location=" + choiceTmp + "&locationOther=" + locationOtherName;

        }

        //url = "http://travelsearchnode-env.us-east-2.elasticbeanstalk.com/showresults?catergory=default&distance=&geolocation=&keyword=coffee&location=1&locationOther=USC+University+Club,+West+34th+Street,+Los+Angeles,+CA,+USA";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                firstPageresult = response;

                System.out.println("-------------------------------->" + response.toString());

                //progressDialog.dismiss();

                Intent intent = new Intent(getActivity(), ResultsActivity.class);
                intent.putExtra("firstPageResult", firstPageresult.toString());
                startActivity(intent);

                progressDialog.dismiss();



            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Volley error: ", error.getLocalizedMessage());
                progressDialog.dismiss();
            }
        });

        // Access the RequestQueue through your singleton class.
        requestQueue.add(jsonObjectRequest);

//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Fetching results");
//        progressDialog.show();


        return null;
    }
}
