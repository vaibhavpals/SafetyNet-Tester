package com.vpals.apps.safetynettestapp;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Vaibhav on 04-12-2016.
 */

public class RestUtil {
    public static String doPost(Context context, String jwsResult) {
        final String[] retStr = {""};
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

        client.post(context, url + apiKey, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200) {
                    String response = new String(responseBody);
                    Log.i("Response Received: ", response);
                    try {
                        JSONObject jsonRespose = new JSONObject(response);
                        if(jsonRespose.getBoolean("isValidSignature"))
                        retStr[0] = "true";
                        else
                        retStr[0] = "false";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Failure: ", "Rest call failed");
            }
        });
        return retStr[0];
    }
}
