package com.example.asus.nfc_qr_app;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.nfc_qr_app.db.DBContract;
import com.example.asus.nfc_qr_app.db.DbHelper;
import com.example.asus.nfc_qr_app.entities.User;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.asus.nfc_qr_app.db.DbHelper.PASS_PHRASE;

public class MainActivity1 extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    private TextView id;
    private ImageView image;
    DbHelper dbHelper = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

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

        id = (TextView) findViewById(R.id.textViewid);
        id.setText(regno);

        image = (ImageView) findViewById(R.id.imageViewUser);

        Bitmap bm = StringToBitMap(photo);
        image.setImageBitmap(bm);

        cursor.close();
        dbHelper.close();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter != null && nfcAdapter.isEnabled()){
            Toast.makeText(this,"NFC is Here!!!",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,"Sorryno NFC!!!",Toast.LENGTH_LONG).show();
        }
    }

    protected void onNewIntent(View view) {
        NdefMessage ndefMessage = createNdefMessage(id.getText()+"");
        nfcAdapter.setNdefPushMessage(ndefMessage,this);
    }

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
        Intent intent = new Intent(this,MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
    }

    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
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
}
