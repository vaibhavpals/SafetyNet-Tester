package com.vpals.apps.safetynettestapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

import java.security.SecureRandom;

/**
 * Created by Vaibhav on 04-12-2016.
 */

public class SafetyNetHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static GoogleApiClient mGoogleApiClient;
    private static SafetyNetResponseVO responseVO = null;
    private static Boolean validationStatus = false;
    private static Boolean safetyNetCallStatus = false;
    ProgressDialog prgDialog;
    private SafetyNetHelper()
    {}

    private static SafetyNetHelper helperInstance = new SafetyNetHelper();

    public static SafetyNetHelper getInstance(){
        return helperInstance;
    }

    public static SafetyNetResponseVO getResponseVO() {
        return responseVO;
    }

    public static void setResponseVO(SafetyNetResponseVO responseVO) {
        SafetyNetHelper.responseVO = responseVO;
    }

    public static Boolean getValidationStatus() {
        return validationStatus;
    }

    public static void setValidationStatus(Boolean validationStatus) {
        SafetyNetHelper.validationStatus = validationStatus;
    }

    public static Boolean getSafetyNetCallStatus() {
        return safetyNetCallStatus;
    }

    public static void setSafetyNetCallStatus(Boolean safetyNetCallStatus) {
        SafetyNetHelper.safetyNetCallStatus = safetyNetCallStatus;
    }

    public static GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        SafetyNetHelper.mGoogleApiClient = mGoogleApiClient;
    }

    public synchronized void buildGoogleApiClient(Context context) {
        setmGoogleApiClient(new GoogleApiClient.Builder(context)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        executeSafetyNetCall();
    }

    @Override
    public void onConnectionSuspended(int i) {
        setSafetyNetCallStatus(false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        setSafetyNetCallStatus(false);
    }

    private void executeSafetyNetCall() {
        byte[] nonce = getRequestNonce();
        SafetyNet.SafetyNetApi.attest(getmGoogleApiClient(), nonce)
                .setResultCallback(new ResultCallback<SafetyNetApi.AttestationResult>() {
                    @Override
                    public void onResult(SafetyNetApi.AttestationResult result) {
                        Status status = result.getStatus();
                        if (status.isSuccess()) {
                            setSafetyNetCallStatus(true);
                            Log.i("Safety Net Result", result.getJwsResult());
                            validateJwsResult(result.getJwsResult());
                            setResponseVO(parseJsonWebSignature(result.getJwsResult()));
                            Log.i("result: " , getResponseVO().toString());
                        } else {
                            setSafetyNetCallStatus(false);
                            Log.i("Safety Net Error", "Oops!");
                        }
                    }
                });
    }

    private byte[] getRequestNonce() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        Log.i("Nonce size", "length :" + bytes.length);
        Log.i("Nonce generated: ", new String(bytes));
        return bytes;
    }

    @Nullable
    private SafetyNetResponseVO parseJsonWebSignature(@NonNull String jwsResult) {
        final String[] jwsMsgArr = jwsResult.split("\\.");
        if (jwsMsgArr.length == 3) {
            String decodedStr = new String(Base64.decode(jwsMsgArr[1], Base64.DEFAULT));
            return SafetyNetResponseVO.getSafetyNetObject(decodedStr);
        } else {
            return null;
        }
    }

    private void validateJwsResult (String jwsResult) {

        RestUtil.doPost(getmGoogleApiClient().getContext(),jwsResult);

    }
}
