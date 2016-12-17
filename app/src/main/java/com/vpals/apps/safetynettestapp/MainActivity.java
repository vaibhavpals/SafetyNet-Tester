package com.vpals.apps.safetynettestapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    FancyButton btnCheck;
    TextView introTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MobileAds.initialize(getApplicationContext(), String.valueOf(R.string.banner_ad_unit_id));
        btnCheck = (FancyButton) findViewById(R.id.btnCheck);
        introTxt = (TextView) findViewById(R.id.introTxt);
        introTxt.setText("The SafetyNet API" +
                " provides access to Google services that" +
                " help you assess the health and safety of an Android device.");
        btnCheck.setText("Run SafetyNet Test");
        btnCheck.setBackgroundColor(Color.parseColor("#3b5998"));
        btnCheck.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btnCheck.setTextSize(17);
        btnCheck.setRadius(5);
        btnCheck.setOnClickListener(this);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("123b2b1e6f8df9b5").build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCheck) {
            startNewActivity();
        }
    }

    private void startNewActivity() {
        Intent intent = new Intent(this,ResultActivity.class);
        startActivity(intent);
    }
}
