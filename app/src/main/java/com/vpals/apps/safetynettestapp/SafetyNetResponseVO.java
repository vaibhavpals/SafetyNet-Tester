package com.vpals.apps.safetynettestapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Vaibhav on 04-12-2016.
 */

public class SafetyNetResponseVO {
    private String nonce;
    private long timestampMs;
    private String apkPackageName;
    private String[] apkCertificateDigestSha256;
    private String apkDigestSha256;
    private boolean ctsProfileMatch;


    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(long timestampMs) {
        this.timestampMs = timestampMs;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    public String[] getApkCertificateDigestSha256() {
        return apkCertificateDigestSha256;
    }

    public void setApkCertificateDigestSha256(String[] apkCertificateDigestSha256) {
        this.apkCertificateDigestSha256 = apkCertificateDigestSha256;
    }

    public String getApkDigestSha256() {
        return apkDigestSha256;
    }

    public void setApkDigestSha256(String apkDigestSha256) {
        this.apkDigestSha256 = apkDigestSha256;
    }

    public boolean isCtsProfileMatch() {
        return ctsProfileMatch;
    }

    public void setCtsProfileMatch(boolean ctsProfileMatch) {
        this.ctsProfileMatch = ctsProfileMatch;
    }

    public static @Nullable
    SafetyNetResponseVO getSafetyNetObject(@NonNull String decodedJWSResult) {
        Log.d("decodedJWSResult json:",decodedJWSResult);
        SafetyNetResponseVO responseObj = new SafetyNetResponseVO();
        try {
            JSONObject rootElement = new JSONObject(decodedJWSResult);
            if(rootElement.has("nonce")) {
                responseObj.nonce = rootElement.getString("nonce");
            }
            if(rootElement.has("apkCertificateDigestSha256")) {
                JSONArray jsonArray = rootElement.getJSONArray("apkCertificateDigestSha256");
                if(jsonArray!=null){
                    String[] certDigests = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        certDigests[i]=jsonArray.getString(i);
                    }
                    responseObj.apkCertificateDigestSha256 = certDigests;
                }
            }
            if(rootElement.has("apkDigestSha256")) {
                responseObj.apkDigestSha256 = rootElement.getString("apkDigestSha256");
            }
            if(rootElement.has("apkPackageName")) {
                responseObj.apkPackageName = rootElement.getString("apkPackageName");
            }
            if(rootElement.has("ctsProfileMatch")) {
                responseObj.ctsProfileMatch = rootElement.getBoolean("ctsProfileMatch");
            }
            if(rootElement.has("timestampMs")) {
                responseObj.timestampMs = rootElement.getLong("timestampMs");
            }
            return responseObj;
        } catch (JSONException e) {
            Log.e("problem parsing: ", e.getMessage(), e);
        }
        return null;
    }

    @Override

    public String toString() {
        return "SafetyNetResponseVO{" +
                "nonce='" + nonce + '\'' +
                ", timestampMs=" + timestampMs +
                ", apkPackageName='" + apkPackageName + '\'' +
                ", apkCertificateDigestSha256=" + Arrays.toString(apkCertificateDigestSha256) +
                ", apkDigestSha256='" + apkDigestSha256 + '\'' +
                ", ctsProfileMatch=" + ctsProfileMatch +
                '}';
    }
}
