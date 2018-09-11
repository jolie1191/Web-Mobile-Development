package com.example.jiahui.travelsearch;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class Tab2Photos extends Fragment {

    public DetailsActivity detailsActivity;
    public JSONObject placeItemObject;

    public String placeId;
    public int photoSize;

    public View photosView;
    LinearLayout photosLinearLayout;

    TextView noPhotos;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    PlacePhotoMetadataBuffer photoMetadataBuffer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        photosView = inflater.inflate(R.layout.tab2_photos, container, false);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

        photosLinearLayout = photosView.findViewById(R.id.photosLinearLayout);

        detailsActivity = (DetailsActivity) getActivity();

        placeItemObject = detailsActivity.json_object;

        noPhotos = photosView.findViewById(R.id.noPhotosTV);

        noPhotos.setVisibility(View.INVISIBLE);


        try {

            placeId = placeItemObject.getString("place_id");


            getPhotos(placeId);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return photosView;
    }

    private void getPhotos(String placeId) {

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);

        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {

                PlacePhotoMetadataResponse photos = task.getResult();

                photoMetadataBuffer = photos.getPhotoMetadata();

                photoSize = photoMetadataBuffer.getCount();

                if(photoSize == 0)
                    noPhotos.setVisibility(View.VISIBLE);

                for(int i = 0; i < photoSize; i++){

                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);

                    CharSequence attribution = photoMetadata.getAttributions();

                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);

                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();

                            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                            int height = (int) (width / bitmap.getWidth() * bitmap.getHeight());

                            Bitmap resize = Bitmap.createScaledBitmap(bitmap, width, height, true);

                            ImageView image = new ImageView(getActivity());

                            //image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(15, 10, 15, 10);
                            image.setLayoutParams(lp);

                            image.setImageBitmap(resize);

                            photosLinearLayout.addView(image);

                        }
                    });

                }


            }
        });
    }


}
