package com.example.asus.student_app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;

public class MainActivity3 extends AppCompatActivity {
    private static final String TAG = MainActivity3.class.getName();
    NfcAdapter nfcAdapter;
    private Button button;
    private TextView textView;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    //private String usersurl = "http://192.168.8.101:8080/user/users";
    private String userurl = "http://192.168.8.101:8080/user/";
    private String idnum;
    private TextView id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        textView = (TextView) findViewById(R.id.textViewValidity);
        button = (Button) findViewById(R.id.buttonQRscanner);
        final Activity activity = this;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        //NFC Part
        id = (TextView) findViewById(R.id.textviewID);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter != null && nfcAdapter.isEnabled()){
            Toast.makeText(this,"NFC is Here!!!",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,"Sorry No NFC!!!",Toast.LENGTH_LONG).show();
        }
    }

    //NFC Reader Part
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this,"NFC Intent Recieved!",Toast.LENGTH_LONG).show();
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (parcelables != null && parcelables.length > 0){
                readTextFromMessage((NdefMessage) parcelables[0]);
            }else {
                Toast.makeText(this,"No NDEF Message Found!!!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this,MainActivity3.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
    }

    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord){
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        }catch (UnsupportedEncodingException e){
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    private void readTextFromMessage(NdefMessage ndefMessage){
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0){
            NdefRecord ndefRecord = ndefRecords[0];
            String tagcontent = getTextFromNdefRecord(ndefRecord);
            id.setText(tagcontent);

            String regno = id.getText().toString();
            String url = userurl + regno;
            sendIdAndCheckValidity(url);
            id.setText("");
        }else {
            Toast.makeText(this,"No NDEF records found!!!",Toast.LENGTH_SHORT).show();
        }
    }

    /*private void sendRequestAndPrintResponse() {
        requestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.GET, usersurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG,"Response :" + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,"Error :" + error.toString());
            }
        });

        //10000 is the time in milliseconds adn is equal to 10 sec
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this,"You cancelled the scan!!!",Toast.LENGTH_LONG).show();
            }else {
                idnum = result.getContents();
                String url = userurl + idnum;
                sendIdAndCheckValidity(url);
                id.setText("");
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendIdAndCheckValidity(final String url) {
        requestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (url != null){
                    Log.i(TAG,"Response :" + response.toString());
                    textView.setText("Valid User");
                    requestQueue.stop();
                }
                else {
                    Log.i(TAG,"Inavalid User!!!");
                    textView.setText("Inavalid User!!!");
                    requestQueue.stop();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,"Error :" + error.toString());
                textView.setText("Error!!!");
                requestQueue.stop();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }
}
