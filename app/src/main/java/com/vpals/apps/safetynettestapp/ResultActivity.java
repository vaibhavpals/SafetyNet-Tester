package com.vpals.apps.safetynettestapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ResultActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    String ctsProfileMatch = "";
    String safetyNetCallStatus = "";
    String responseValidationStatus = "";
    TextView txtCtsProfileMatch;
    TextView txtSafetyNetCallStatus;
    TextView txtResponseValidationStatus;
    GoogleApiClient mGoogleApiClient;
    int color;
    ProgressDialog prgDialogue;


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        executeSafetyNetCall();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        safetyNetCallStatus = "false";
        ctsProfileMatch = "unknown";
        responseValidationStatus = "unknown";
        performFinalActivities();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        txtCtsProfileMatch = (TextView) findViewById(R.id.txtCtsProfileMatch);
        txtResponseValidationStatus = (TextView) findViewById(R.id.txtResponseValidationStatus);
        txtSafetyNetCallStatus = (TextView) findViewById(R.id.txtSafetyNetCallStatus);
        prgDialogue = new ProgressDialog(this);
        prgDialogue.setMessage("Please wait...");
        prgDialogue.show();
        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    private void executeSafetyNetCall() {
        byte[] nonce = getRequestNonce();
        SafetyNet.SafetyNetApi.attest(mGoogleApiClient, nonce)
                .setResultCallback(new ResultCallback<SafetyNetApi.AttestationResult>() {
                    @Override
                    public void onResult(SafetyNetApi.AttestationResult result) {
                        Status status = result.getStatus();
                        if (status.isSuccess()) {
                            safetyNetCallStatus = "true";
                            Log.i("Safety Net Result", result.getJwsResult());
                            validateJwsResult(result.getJwsResult());
                            SafetyNetResponseVO responseVo = new SafetyNetResponseVO();
                            responseVo = parseJsonWebSignature(result.getJwsResult());
                            if(responseVo.isCtsProfileMatch())
                                ctsProfileMatch = "true";
                            else
                                ctsProfileMatch = "false";
                            Log.i("result: ", responseVo.toString());
                        } else {
                            safetyNetCallStatus = "false";
                            ctsProfileMatch = "unknown";
                            responseValidationStatus = "unknown";
                            performFinalActivities();
                            Log.i("Safety Net Error", "Oops!");
                        }

                    }
                });
    }

    private void validateJwsResult(String jwsResult) {
        doPost(this, jwsResult);
    }

    private byte[] getRequestNonce() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        Log.i("Nonce size", "length :" + bytes.length);
        Log.i("Nonce generated: ", new String(bytes));
        return bytes;
    }

    private SafetyNetResponseVO parseJsonWebSignature(@NonNull String jwsResult) {
        final String[] jwsMsgArr = jwsResult.split("\\.");
        if (jwsMsgArr.length == 3) {
            String decodedStr = new String(Base64.decode(jwsMsgArr[1], Base64.DEFAULT));
            return SafetyNetResponseVO.getSafetyNetObject(decodedStr);
        } else {
            return null;
        }
    }

    public void doPost(Context context, String jwsResult) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://www.googleapis.com/androidcheck/v1/attestations/verify?key=";
        String apiKey = "AIzaSyAQx9dRBNiz1BKRxGlY27rcM-A2YUWyhdQ";
        JSONObject jsonMsg = new JSONObject();
        try {
            jsonMsg.put("signedAttestation", jwsResult);
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
                if (statusCode == 200) {
                    String response = new String(responseBody);
                    Log.i("Response Received: ", response);
                    try {
                        JSONObject jsonRespose = new JSONObject(response);
                        if (jsonRespose.getBoolean("isValidSignature"))
                            responseValidationStatus = "true";
                        else
                            responseValidationStatus = "false";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    performFinalActivities();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Failure: ", "Rest call failed");
                safetyNetCallStatus = "true";
                ctsProfileMatch = "unknown";
                responseValidationStatus = "unknown";
                performFinalActivities();
            }
        });

    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

    public void performFinalActivities() {
        prgDialogue.hide();
        txtSafetyNetCallStatus.setText("SafetyNet Call Success : " + safetyNetCallStatus);
        txtResponseValidationStatus.setText("Response Signature Valid : " + responseValidationStatus);
        txtCtsProfileMatch.setText("CTS Profile Match : " + ctsProfileMatch);
        if(safetyNetCallStatus.equalsIgnoreCase("false") || responseValidationStatus.equalsIgnoreCase("false")
                || ctsProfileMatch.equalsIgnoreCase("false"))
            color = Color.RED;
        else if (safetyNetCallStatus.equalsIgnoreCase("true") && responseValidationStatus.equalsIgnoreCase("true")
                && ctsProfileMatch.equalsIgnoreCase("true"))
            color = Color.GREEN;
        else
            color = Color.YELLOW;
        setActivityBackgroundColor(color);
    }
}
