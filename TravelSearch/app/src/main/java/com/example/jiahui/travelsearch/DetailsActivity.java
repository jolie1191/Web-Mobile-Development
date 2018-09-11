package com.example.jiahui.travelsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static JSONObject json_object;

    String placeName;
    String placeAddress;
    String twUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setSupportActionBar(toolbar);
        //Your toolbar is now an action bar and you can use it like you always do, for example:
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        try {

            json_object = new JSONObject(getIntent().getStringExtra("placeItemDetail"));
            placeName = json_object.getString("name");
            toolbar.setTitle(json_object.getString("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    //Change the ImageView image source depends on menu item click
//                    case R.id.action_favorite:
//                        //item.setIcon(R.drawable.elephant);
//                        return true;
//                    case R.id.action_share:
//                        //item.setImageResource(R.drawable.dog);
//                        return true;
//
//                }
//
//                return false;
//            }
//        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);


        MenuItem action_favorite = (MenuItem) menu.findItem(R.id.action_favorite);
        if(getIntent().getStringExtra("fromFavorite") != null){

            action_favorite.setIcon(R.drawable.heart_fill_white);

        }else if(getIntent().getBooleanExtra("likeUnlike", false)){

            action_favorite.setIcon(R.drawable.heart_fill_white);

        } else{
            action_favorite.setIcon(R.drawable.heart_outline_white);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {

            if(getIntent().getStringExtra("fromFavorite") != null){

                //remove item from share prefereence

                SharedPreferences sharedPreferences = getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);
                sharedPreferences.edit().remove(getIntent().getStringExtra("placeId")).apply();

                Toast.makeText(DetailsActivity.this, placeName + " was removed from favorites", Toast.LENGTH_SHORT).show();

                item.setIcon(R.drawable.heart_outline_white);

            }else if(getIntent().getBooleanExtra("likeUnlike", false)){
                //remove item from share preference

                SharedPreferences sharedPreferences = getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);
                sharedPreferences.edit().remove(getIntent().getStringExtra("placeId")).apply();

                Toast.makeText(DetailsActivity.this, placeName + " was removed from favorites", Toast.LENGTH_SHORT).show();

                item.setIcon(R.drawable.heart_outline_white);

            } else{

                //add item to share preference

                SharedPreferences sharedPreferences = getSharedPreferences("com.example.jiahui.travelsearch", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString(getIntent().getStringExtra("placeId"),getIntent().getStringExtra("json_item")).apply();
                //System.out.println("---------------------------------JsonObject: " + list.get(position).getJson_item().toString());

                Toast.makeText(DetailsActivity.this, placeName + " was added to favorites", Toast.LENGTH_SHORT).show();

                item.setIcon(R.drawable.heart_fill_white);
            }


            return true;
        }else if(id == R.id.action_share){

            //"https://twitter.com/intent/tweet?text=" + text + "&url=" + twUrl + "&hashtags=TravelAndEntertainmentSearch";
            try {

                placeAddress = json_object.getString("formatted_address");
                twUrl = json_object.getString("website");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String text = "Check out " + placeName +" located at " + placeAddress + ". Website: ";


            String tweetUrl = "https://twitter.com/intent/tweet?text=" + text + twUrl;

            Uri uri = Uri.parse(tweetUrl);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));

            //System.out.println("--------------------->" + json_object.toString());

        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        public PlaceholderFragment() {
//        }
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.tab1_info, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//            return rootView;
//        }
//    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    Tab1Info tab1 = new Tab1Info();
                    return tab1;
                case 1:
                    Tab2Photos tab2 = new Tab2Photos();
                    return tab2;
                case 2:
                    Tab3Map tab3 = new Tab3Map();
                    return tab3;
                case 3:
                    Tab4Reviews tab4 = new Tab4Reviews();
                    return tab4;

            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0:
                    Drawable image = getBaseContext().getDrawable(R.drawable.info_outline);
                    image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                    SpannableString sb = new SpannableString("  "+ "Info        ");
                    ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb;
                case 1:
                    image = getBaseContext().getDrawable(R.drawable.photos);
                    image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                    sb = new SpannableString("  "+ "Photos      ");
                    imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb;
                case 2:
                    image = getBaseContext().getDrawable(R.drawable.maps);
                    image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                    sb = new SpannableString("  "+ "Map         ");
                    imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb;
                case 3:
                    image = getBaseContext().getDrawable(R.drawable.review);
                    image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                    sb = new SpannableString("  "+ "Reviews     ");
                    imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return sb;
            }

            return null;
        }
    }
}
