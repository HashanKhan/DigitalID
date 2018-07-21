package com.example.asus.nfc_qr_app;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    NfcAdapter nfcAdapter;
    private Button btnvalidate;
    private EditText id;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    //private String usersurl = "http://192.168.8.101:8080/user/users";
    private String userurl = "http://192.168.8.100:8080/user/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = (EditText) findViewById(R.id.editTextID);
        btnvalidate = (Button) findViewById(R.id.buttonvalidate);
        btnvalidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regno = id.getText().toString();
                String url = userurl + regno;
                sendIdAndCheckValidity(url);
                id.setText("");
                //sendRequestAndPrintResponse();
            }
        });





        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter != null && nfcAdapter.isEnabled()){
            Toast.makeText(this,"NFC is Here!!!",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,"Sorryno NFC!!!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Toast.makeText(this,"NFC Intent Recieved!",Toast.LENGTH_LONG).show();
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] intentFilter = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilter,null);

        super.onResume();
    }

    @Override
    protected void onPause() {
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage){
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null){
                Toast.makeText(this,"Tag is not ndfe formatable",Toast.LENGTH_LONG).show();
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this,"Tag written.",Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Log.e("formatTag",e.getMessage());
        }
    }

    private void writeNdefMessage(Tag tag,NdefMessage ndefMessage){
        try {
            if (tag == null){
                Toast.makeText(this,"Tag Object can't be null.",Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if (ndef == null){
                formatTag(tag,ndefMessage);
            }else {
                ndef.connect();

                if (!ndef.isWritable()){
                    Toast.makeText(this,"Tag is not writable.",Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this,"Tag written.",Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Log.e("writeNdefMessage",e.getMessage());
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

    private void sendIdAndCheckValidity(final String url) {
        requestQueue = Volley.newRequestQueue(this);
        final TextView textViewstatus = findViewById(R.id.textViewValidity);
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (url != null){
                    Log.i(TAG,"Response :" + response.toString());
                    textViewstatus.setText("Valid User.");
                }
                else {
                    Log.i(TAG,"Inavalid User!!!");
                    textViewstatus.setText("Inavalid User!!!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,"Error :" + error.toString());
                textViewstatus.setText("Inavalid User!!!");
            }
        });
        requestQueue.add(stringRequest);
    }
}
