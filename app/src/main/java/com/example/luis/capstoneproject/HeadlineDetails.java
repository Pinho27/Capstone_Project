package com.example.luis.capstoneproject;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

public class HeadlineDetails extends AppCompatActivity {

    private WebView myWebView;
    Context ctx;

    Headline headline2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headline_details);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ctx = this;

        final Headline headline = getIntent().getParcelableExtra("headline");

        headline2 = headline;

        myWebView =findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());

        if (savedInstanceState == null)
            myWebView.loadUrl(headline.getUrl());

        FloatingActionButton fab = findViewById(R.id.fab_previous_step);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Link: " + headline.getUrl() + "\n\n" + headline.getTitle() + "\n\n" + headline.getDescription());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        myWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        myWebView.restoreState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favourite_action_menu, menu);

        Thread thread = new Thread(){
            public void run(){
                AppDatabase database = AppDatabase.getAppDatabase(getApplicationContext());

                if(database.headlineDAO().getHeadline(headline2.getUrl()) != null){
                    HeadlineDetails.this.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Snackbar.make(myWebView, "Headline is in your favorites!", Snackbar.LENGTH_LONG).show();
                            MenuItem menuItem = menu.findItem(R.id.action_favorite);
                            MenuItemCompat.getActionView(menuItem);
                            menuItem.setIcon(R.drawable.ic_star_white_24dp);
                        }});
                    super.run();
                }
            }
        };
        thread.start();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_favorite:
                Thread thread = new Thread(){
                    public void run(){
                        AppDatabase database = AppDatabase.getAppDatabase(getApplicationContext());

                        if(database.headlineDAO().getHeadline(headline2.getUrl()) != null){
                            if(database.headlineDAO().deleteHeadline(headline2) == 1){
                                HeadlineDetails.this.runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        Snackbar.make(myWebView, "Removed from favorites successfully!", Snackbar.LENGTH_LONG).show();
                                        item.setIcon(R.drawable.ic_star_border_white_24dp);
                                    }});
                                super.run();

                            }

                        }else {
                            if (database.headlineDAO().insertHeadline(headline2) != -1) {

                                //Update Widget
                                SharedPreferences.Editor editor = getSharedPreferences("prefs", MODE_PRIVATE).edit();
                                editor.putString("widget_headline_url", headline2.getUrl());
                                editor.apply();

                                Intent intent = new Intent(ctx, AppWidget.class);
                                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                                int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), AppWidget.class));
                                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
                                sendBroadcast(intent);

                                HeadlineDetails.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(myWebView, "Added successfully to favorites!", Snackbar.LENGTH_LONG).show();
                                        item.setIcon(R.drawable.ic_star_white_24dp);
                                    }
                                });
                                super.run();

                            }
                        }
                    }
                };
                thread.start();
                break;

            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
