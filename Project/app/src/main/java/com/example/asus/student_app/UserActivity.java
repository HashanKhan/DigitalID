package com.example.asus.student_app;

import android.content.Context;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.asus.student_app.db.DBContract;
import com.example.asus.student_app.db.DbHelper;
import com.example.asus.student_app.db.MySingleton;
import com.example.asus.student_app.db.RecyclerAdapter;
import com.example.asus.student_app.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.asus.student_app.db.DbHelper.PASS_PHRASE;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = UserActivity.class.getName();
    RecyclerView recyclerView;
    EditText editText;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    ArrayList<User> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        SQLiteDatabase.loadLibs(this);
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        editText = (EditText) findViewById(R.id.editTextNum);
        adapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        readFromLocaleStorage();
    }

    public void submitNumber(View view) {
        String regno = editText.getText().toString();
        String url1 = DBContract.SERVER_URL + regno;
        readFromAppServer(url1);
        editText.setText("");
    }

    public void readFromLocaleStorage(){
        arrayList.clear();
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase(PASS_PHRASE);

        Cursor cursor = dbHelper.readFromLocaleDataBase(database);

        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(DBContract.NAME));
            String regno = cursor.getString(cursor.getColumnIndex(DBContract.REGNO));
            String nic = cursor.getString(cursor.getColumnIndex(DBContract.NIC));
            String photo = cursor.getString(cursor.getColumnIndex(DBContract.PHOTO));
            String username = cursor.getString(cursor.getColumnIndex(DBContract.USERNAME));
            String password = cursor.getString(cursor.getColumnIndex(DBContract.PASSWORD));
            int sync_status = cursor.getInt(cursor.getColumnIndex(DBContract.SYNC_STATUS));
            arrayList.add(new User(name,regno,nic,photo,username,password,sync_status));
        }
        adapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();
    }

    private void readFromAppServer(final String url1){
        if (checkNetworkConnection()){
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url1,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (url1 != null){
                            saveToLocaleStorage(response.getString("name"),response.getString("regno"),
                                    response.getString("nic"),response.getString("photo"),response.getString("username"),response.getString("password"),DBContract.sync_status_ok);
                        }
                        else {
                            Log.i(TAG,"Not an Instance!!!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG,"Error :" + error.toString());
                }
            });
            MySingleton.getInstance(UserActivity.this).addToRequestQue(jsonObjectRequest);
        }
        else {
            Toast.makeText(this,"Sorry no Network Connection!!!",Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void saveToLocaleStorage(String name, String regno, String nic, String photo, String username, String password, int sync_status){
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase(PASS_PHRASE);
        dbHelper.saveToLocaleDataBase(name,regno,nic,photo,username,password,sync_status,database);
        readFromLocaleStorage();
        dbHelper.close();
    }
}
