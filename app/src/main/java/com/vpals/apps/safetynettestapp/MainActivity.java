package com.vpals.apps.safetynettestapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

/*import org.apache.commons.codec.binary.Base64;*/
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.safetynet.SafetyNet;
import android.provider.Settings.Secure;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    Button btnCheck;
    /*private String android_id = "";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MobileAds.initialize(getApplicationContext(), String.valueOf(R.string.banner_ad_unit_id));
        //android_id = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
        //123b2b1e6f8df9b5
        btnCheck = (Button) findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(this);
        /*Log.i("DEVICE ID :" , android_id);*/
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("123b2b1e6f8df9b5").build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
