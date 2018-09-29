package com.example.asus.student_app;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.example.asus.student_app.db.DBContract;
import com.example.asus.student_app.db.DbHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import static com.example.asus.student_app.db.DbHelper.PASS_PHRASE;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = MainActivity2.class.getName();
    NfcAdapter nfcAdapter;
    TextView id,nm,nc;
    ImageView image,image1;
    String idnum;
    DbHelper dbHelper = new DbHelper(this);
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private String userurl = "http://192.168.8.101:8080/user/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        SQLiteDatabase.loadLibs(this);

        //Database Section
        SQLiteDatabase db = dbHelper.getReadableDatabase(PASS_PHRASE);
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

        id = (TextView) findViewById(R.id.textViewidnum);
        nm = (TextView) findViewById(R.id.textViewName);
        nc = (TextView) findViewById(R.id.textViewNIC);

        id.setText(regno);
        nm.setText(name);
        nc.setText(nic);

        image1 = (ImageView) findViewById(R.id.imageViewUser);

        Bitmap bm = StringToBitMap(photo);
        image1.setImageBitmap(bm);

        cursor.close();
        dbHelper.close();


        image = (ImageView) findViewById(R.id.imageid);

        idnum = id.getText().toString().trim();
        String url = userurl + idnum;
        sendIdAndCheckValidity(url);

        //NFC Section
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter != null && nfcAdapter.isEnabled()){
            Toast.makeText(this,"NFC is Here!!!",Toast.LENGTH_LONG).show();
            NdefMessage ndefMessage = createNdefMessage(id.getText()+"");
            nfcAdapter.setNdefPushMessage(ndefMessage,this);
        }
        else {
            Toast.makeText(this,"Sorry No NFC!!!",Toast.LENGTH_LONG).show();
        }
    }

    //Converting the byte array to bitmap.
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    //NFC Methods
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
        Intent intent = new Intent(this,MainActivity2.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
    }

    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }

    //NFC Tag formatting. Will needed for future work.
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

    //NFC Tag writing will needed for future work.
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

    private NdefRecord createTextRecord(String content){
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language,0,languageSize);
            payload.write(text,0,textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT,new byte[0],payload.toByteArray());

        }catch (UnsupportedEncodingException e){
            Log.e("createTextRecord",e.getMessage());
        }
        return null;
    }

    private NdefMessage createNdefMessage(String content){
        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});
        return  ndefMessage;
    }

    //QR code generation request.
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

