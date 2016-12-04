package com.vpals.apps.safetynettestapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnClickListener {

    GoogleApiClient mGoogleApiClient;
    ProgressDialog prgDialog;
    Button btnCheck;
    int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prgDialog = new ProgressDialog(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(this)
                .build();
        btnCheck = (Button) findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(this);

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
    public void onConnected(@Nullable Bundle bundle) {
        byte[] nonce = getRequestNonce();
        SafetyNet.SafetyNetApi.attest(mGoogleApiClient, nonce)
                .setResultCallback(new ResultCallback<SafetyNetApi.AttestationResult>() {
                    @Override
                    public void onResult(SafetyNetApi.AttestationResult result) {
                        Status status = result.getStatus();
                        if (status.isSuccess()) {
                            Log.i("Safety Net Result", result.getJwsResult());
                            invokeWs(result.getJwsResult());
                        } else {
                            Log.i("Safety Net Error", "Oops!");
                        }
                    }
                });
    }

    private void invokeWs(String jwsResult){
        prgDialog.setMessage("Please wait...");
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://www.googleapis.com/androidcheck/v1/attestations/verify?key=";
        String apiKey = "AIzaSyAQx9dRBNiz1BKRxGlY27rcM-A2YUWyhdQ";
        JSONObject jsonMsg = new JSONObject();
        try {
            jsonMsg.put("signedAttestation",jwsResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonMsg.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(getApplicationContext(), url + apiKey, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                if(statusCode == 200) {
                    String response = new String(responseBody);
                    Log.i("Response Received: ", response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i("Failure: ", "Rest call failed");
            }
        });


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Safety Net Error", "Connection Suspended");
    }

    public byte[] getRequestNonce() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        Log.i("Nonce size", "length :" + bytes.length);
        Log.i("Nonce generated: ", bytes.toString());
        return bytes;
    }

    @Override
    public void onClick(View v) {
        counter++;
        if (v.getId() == R.id.btnCheck && counter % 2 != 0) {
            mGoogleApiClient.connect();
        }
        else
            mGoogleApiClient.disconnect();
    }
}
