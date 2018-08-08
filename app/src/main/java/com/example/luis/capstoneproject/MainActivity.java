package com.example.luis.capstoneproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private Context ctx;
    private FusedLocationProviderClient mFusedLocationClient;
    private String list_order = "headlines";
    private Parcelable layoutManagerSavedState = null;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_headlines:
                    setTitle(R.string.app_name);
                    list_order = "headlines";
                    getTopHeadlines(null, null);
                    return true;
                case R.id.navigation_local:
                    list_order = "local";
                    getLocalNews();
                    return true;
                case R.id.navigation_categories:
                    setTitle(R.string.app_name);
                    list_order = "categories";
                    getCategories();
                    return true;
                case R.id.navigation_favourites:
                    list_order = "favorites";
                    setTitle(R.string.app_name);

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null)
            list_order = savedInstanceState.getString("list_order");

        Log.v("ds", list_order);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ctx = this;

        recyclerView = findViewById(R.id.headlines_recycler_view);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        switch (list_order){
            case "headlines":
                getTopHeadlines(null, null);
                break;
            case "local":
                getLocalNews();
                break;
            case "categories":
                getCategories();
                break;
            case "favorites":
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("SAVED_LAYOUT_MANAGER", recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putString("list_order", list_order);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState instanceof Bundle) {
            layoutManagerSavedState = (savedInstanceState).getParcelable("SAVED_LAYOUT_MANAGER");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void restoreLayoutManagerPosition() {
        if (layoutManagerSavedState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
        }
    }

    void getTopHeadlines(String source, String locale) {

        String link;

        if (source == null)
            link = "https://newsapi.org/v2/top-headlines?language=en&apiKey=" +
                    BuildConfig.NewsApiKey;
        else
            link = "https://newsapi.org/v2/top-headlines?sources=" + source + "&apiKey=" +
                    BuildConfig.NewsApiKey;

        if(locale != null)
            link = link + "&country=" + locale;



        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, link, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ArrayList<Headline> headlineArrayList = new ArrayList<>();

                try {
                    JSONArray articles = response.getJSONArray("articles");

                    for (int i = 0; i < articles.length(); i++) {
                        JSONObject article = articles.getJSONObject(i);


                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();

                        //Source source = gson.fromJson(String.valueOf(article.getJSONObject("source")), Source.class);
                        Headline headline = gson.fromJson(article.toString(), Headline.class);

                        headlineArrayList.add(headline);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
                recyclerView.setAdapter(new HeadlinesAdapter(ctx, headlineArrayList));
                restoreLayoutManagerPosition();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //TODO: handle failure
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    void getCategories() {
        String link = "https://newsapi.org/v2/sources?apiKey=" +
                BuildConfig.NewsApiKey;


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, link, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ArrayList<Category> categoryArrayList = new ArrayList<>();

                try {
                    JSONArray categories = response.getJSONArray("sources");

                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject categoriesJSONObject = categories.getJSONObject(i);


                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();

                        //Source source = gson.fromJson(String.valueOf(article.getJSONObject("source")), Source.class);
                        Category category = gson.fromJson(categoriesJSONObject.toString(), Category.class);

                        categoryArrayList.add(category);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                recyclerView.setLayoutManager(new GridLayoutManager(ctx, numberOfColumns()));
                recyclerView.setAdapter(new CategoriesAdapter(ctx, categoryArrayList));
                restoreLayoutManagerPosition();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //TODO: handle failure
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);

    }

    void getLocalNews() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 200);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v("fds", "fail");

            return;
        }else {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.

                            Log.v("fds", location.toString());

                            if (location != null) {

                                Log.v("loca", String.valueOf(location.getLatitude()));
                                Geocoder gcd = new Geocoder(ctx, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                    if (addresses.size() > 0) {
                                        String countryName = addresses.get(0).getCountryName();

                                        Map<String, String> countries = new HashMap<>();
                                        for (String iso : Locale.getISOCountries()) {
                                            Locale l = new Locale("", iso);
                                            countries.put(l.getDisplayCountry(), iso);
                                        }


                                        getTopHeadlines(null, countries.get(countryName));

                                        setTitle(countryName);

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setMessage(R.string.permissions_message);
            builder.setCancelable(false);
            builder.setIcon(R.drawable.ic_warning_black_24dp);
            builder.setTitle(R.string.permissions_title);

            builder.setPositiveButton(
                    R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                            }
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2; //to keep the grid aspect
        return nColumns;
    }

}
