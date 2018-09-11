package com.example.jiahui.travelsearch;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tab3Map extends Fragment {

    public DetailsActivity detailsActivity;

    public JSONObject placeItemObject;

    public View mapView;

    Spinner spinner;

    ArrayAdapter<String> adapter;

    PlacesAutocompleteTextView placesAutocompleteTextView;


    String fromLocationName;

    String fromLocationPlaceId;

    double targetLat;
    double targetLng;
    double fromLat;
    double fromLng;

    String targetTitle;

    String[] travelModes ={"driving", "walking", "bicyling", "transit"};
    String[] travelModeValue = {"Driving", "Walking", "Bicycling", "Transit"};
    String selectedTravelMode;

    private GoogleMap map;

    SupportMapFragment supportMapFragment;
    PolylineOptions polylineOptions;
    Route route;
    Leg leg;
    LatLng targetLocation;

    //for Tansit
    List<Step> stepList;
    ArrayList<PolylineOptions> polylineOptionList;

    ArrayList<LatLng> directionPositionList;

    RequestQueue requestQueue;

    public String key = "AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mapView = inflater.inflate(R.layout.tab3_map, container, false);

        //place autocompelete
        placesAutocompleteTextView = (PlacesAutocompleteTextView) mapView.findViewById(R.id.places_autocomplete);

        //Set spinner
        spinner = (Spinner) mapView.findViewById(R.id.spinnerView);

        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, travelModeValue);

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spinner.setAdapter(adapter);

        detailsActivity = (DetailsActivity) getActivity();

        placeItemObject = detailsActivity.json_object;

        requestQueue = Volley.newRequestQueue(getActivity());

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);


        try {

            targetLat = Double.parseDouble(placeItemObject.getJSONObject("geometry").getJSONObject("location").getString("lat"));
            targetLng = Double.parseDouble(placeItemObject.getJSONObject("geometry").getJSONObject("location").getString("lng"));
            targetTitle = placeItemObject.getString("name");

        } catch (JSONException e) {
            e.printStackTrace();
        }



        placesAutocompleteTextView.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        // do something awesome with the selected place

                        fromLocationPlaceId = place.place_id;

                        fromLocationName = (String) placesAutocompleteTextView.getText().toString().trim();

                        getOtherLatLngFromDetail(fromLocationPlaceId);

                        System.out.println("place Id--------------------------------  " +  place.place_id);
                    }
                }
        );

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                //int selectedIndex = position;
                selectedTravelMode = travelModes[position];

                if(fromLocationName != null){

                    getDirectionAndShowMap();

                }


                Log.i("Selected position", selectedTravelMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //map

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (googleMap != null) {
                    targetLocation = new LatLng(targetLat, targetLng);
                    googleMap.addMarker(new MarkerOptions().position(targetLocation)
                            .title(targetTitle));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(targetLocation));

                }
            }
        });

        return mapView;
    }


    public void getOtherLatLngFromDetail(final String placeId){

        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + key;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //System.out.println("-------------------------------->" + response.toString());
                try {
                    JSONObject result = response.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");

                    fromLat = result.getDouble("lat");
                    fromLng = result.getDouble("lng");

                    //getAllPlacesAndJump();
                    getDirectionAndShowMap();

                    System.out.println("---------------placeId----------------->" + fromLat + "," + fromLng);

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


    public void getDirectionAndShowMap(){


        GoogleDirection.withServerKey("AIzaSyDZMrVdoWetR9i4e7mxdU22pni85irVsZw")
                .from(new LatLng(fromLat, fromLng))
                .to(new LatLng(targetLat,targetLng))
                .transportMode(selectedTravelMode)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if(direction.isOK()) {

                            if(selectedTravelMode.equals("transit")){
                                stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                                polylineOptionList = DirectionConverter.createTransitPolyline(getActivity(), stepList, 5, Color.BLUE, 3, Color.BLUE);

                            }else{

                            }
                            route = direction.getRouteList().get(0);
                            leg = route.getLegList().get(0);
                            directionPositionList = leg.getDirectionPoint();
                            polylineOptions = DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.BLUE);

                            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    if (googleMap != null) {
                                        googleMap.clear();

                                        targetLocation = new LatLng(targetLat, targetLng);
                                        googleMap.addMarker(new MarkerOptions().position(targetLocation)
                                                .title(targetTitle));

                                        googleMap.addMarker(new MarkerOptions().position(new LatLng(fromLat, fromLng)));

                                        //添加路线
                                        if(selectedTravelMode.equals("transit")){

                                            for (PolylineOptions polylineOption : polylineOptionList) {
                                                googleMap.addPolyline(polylineOption);
                                            }

                                        }else{

                                            googleMap.addPolyline(polylineOptions);

                                        }


                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 12));

                                    }
                                }
                            });




                            // Do something
                        } else {
                            // Do something
                            Log.e("Direction error", direction.getErrorMessage());
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                    }
                });

    }


    @Override
    public void onStop() {
        super.onStop();


    }
}
