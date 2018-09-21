package com.example.asus.nfc_qr_app;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asus.nfc_qr_app.db.DBContract;
import com.example.asus.nfc_qr_app.db.DbHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = MainActivity2.class.getName();
    TextView text;
    Button btn_gen;
    ImageView image;
    String idnum;
    DbHelper dbHelper = new DbHelper(this);
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private String userurl = "http://192.168.8.100:8080/user/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readFromLocaleDataBase(db);

        String name = "";
        String regno = "";
        String nic = "";
        String photo = "";
        int sync_status;

        while (cursor.moveToNext()){
            name = cursor.getString(cursor.getColumnIndex(DBContract.NAME));
            regno = cursor.getString(cursor.getColumnIndex(DBContract.REGNO));
            nic = cursor.getString(cursor.getColumnIndex(DBContract.NIC));
            photo = cursor.getString(cursor.getColumnIndex(DBContract.PHOTO));
            sync_status = cursor.getInt(cursor.getColumnIndex(DBContract.SYNC_STATUS));
        }

        text = (TextView) findViewById(R.id.textViewidnum);
        text.setText(regno);

        cursor.close();
        dbHelper.close();


        btn_gen = (Button) findViewById(R.id.buttonGenerate);
        image = (ImageView) findViewById(R.id.imageid);

        btn_gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idnum = text.getText().toString().trim();
                String url = userurl + idnum;
                sendIdAndCheckValidity(url);
            }
        });
    }

    private void sendIdAndCheckValidity(final String url) {
        requestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (url != null){
                    Log.i(TAG,"Response :" + response.toString());
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try{
                        BitMatrix bitMatrix = multiFormatWriter.encode(idnum, BarcodeFormat.QR_CODE,200,200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        image.setImageBitmap(bitmap);
                        requestQueue.stop();
                    }catch (WriterException e){
                        e.printStackTrace();
                        requestQueue.stop();
                    }
                }
                else {
                    Log.i(TAG,"Inavalid User!!!");
                    requestQueue.stop();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,"Error :" + error.toString());
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

