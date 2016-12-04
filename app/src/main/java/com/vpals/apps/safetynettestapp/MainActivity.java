package com.vpals.apps.safetynettestapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

/*import org.apache.commons.codec.binary.Base64;*/
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    Button btnCheck;
    String ctsProfileMatch;
    String safetyNetCallStatus;
    String responseValidationStatus;
    TextView txtCtsProfileMatch;
    TextView txtSafetyNetCallStatus;
    TextView txtResponseValidationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnCheck = (Button) findViewById(R.id.btnCheck);
        txtCtsProfileMatch = (TextView) findViewById(R.id.txtCtsProfileMatch);
        txtResponseValidationStatus = (TextView) findViewById(R.id.txtResponseValidationStatus);
        txtSafetyNetCallStatus = (TextView) findViewById(R.id.txtSafetyNetCallStatus);
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
    public void onClick(View v) {
        if (v.getId() == R.id.btnCheck) {
            SafetyNetHelper helperObj = new SafetyNetHelper();
            helperObj.buildGoogleApiClient(getApplicationContext());
            GoogleApiClient client = helperObj.getmGoogleApiClient();
            client.connect();
            if (helperObj.getResponseVO() != null) {
                if (helperObj.getResponseVO().isCtsProfileMatch())
                    ctsProfileMatch = "true";
                else
                    ctsProfileMatch = "false";
                if (helperObj.getSafetyNetCallStatus())
                    safetyNetCallStatus = "true";
                else
                    safetyNetCallStatus = "false";
                if (helperObj.getValidationStatus())
                    responseValidationStatus = "true";
                else
                    responseValidationStatus = "false";
            }
            txtCtsProfileMatch.setText("CTS Profile Match : " + ctsProfileMatch);
            txtSafetyNetCallStatus.setText("SafetyNet Call Success : " + safetyNetCallStatus);
            txtResponseValidationStatus.setText("Response Signature Valid : " + responseValidationStatus);
        }
    }
}
