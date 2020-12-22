package com.example.touahmed.wiz_of_oz_controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by takter on 3/26/17.
 */

public class Controller extends AppCompatActivity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String WEAR_MESSAGE_PATH = "/message";
    private static final String TAG = "Send Data";
    private static final String COUNT_KEY = "com.example.key.count";
    DBController_status controller = new DBController_status(this);



    private GoogleApiClient mApiClient;
    private int activity_id=0;
    private String userPID="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Intent myIntent = getIntent(); // gets the previously created intent
        userPID = myIntent.getStringExtra("userPID");

        initizlize_inc_dec_buttons();
        addUpdateButton();
        addAlertButton();

        initGoogleApiClient();

        finishButton();
        //Intent myIntent = getIntent(); // gets the previously created intent
        //userPID = myIntent.getStringExtra("userPID");





    }
    private void sendMessage( String value ) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count");
        putDataMapReq.getDataMap().putString(COUNT_KEY, value);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mApiClient, putDataReq);
    }
    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: " + bundle);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: " + i);

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);

    }

    public void finishButton()
    {
        Button button = (Button) findViewById(R.id.finish);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final List<String> regions = new ArrayList<>(Arrays.asList("front", "right", "rear", "left"));
                String strShareValue = "F:";
                for (int i = 0; i < regions.size(); i++) {
                    int resID = getResources().getIdentifier(regions.get(i) + "_count", "id", getPackageName());
                    TextView x = (TextView) findViewById(resID);
                    int count = (Integer.parseInt(x.getText().toString()));
                    strShareValue += Integer.toString(count);
                    strShareValue += ",";
                }

                Log.d(TAG, "Update " + strShareValue);
                Toast.makeText(Controller.this,
                        strShareValue, Toast.LENGTH_SHORT).show();


                sendMessage(strShareValue);

                try {
                    HashMap<String, String> queryValues = new HashMap<String, String>();
                    queryValues.put("userID", userPID);
                    queryValues.put("log", strShareValue);

                    System.out.println("before query insert");
                    controller.insertUser(queryValues);
                    System.out.println("After insertion...");
                } catch (Exception ex) {
                    System.out.println(ex.getMessage().toString());
                }
                syncSQLiteMySQLDB();
                Intent intent = new Intent(Controller.this, MainActivity.class);
                //intent.putExtra("userPID", upid);
                startActivity(intent);

            }
        });

    }


    public void addUpdateButton()
    {
        Button button = (Button) findViewById(R.id.update);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final List<String> regions = new ArrayList<>(Arrays.asList("front", "right", "rear", "left"));
                String strShareValue = "0:";
                for (int i = 0; i < regions.size(); i++) {
                    int resID = getResources().getIdentifier(regions.get(i) + "_count", "id", getPackageName());
                    TextView x = (TextView) findViewById(resID);
                    int count = (Integer.parseInt(x.getText().toString()));
                    strShareValue += Integer.toString(count);
                    strShareValue += ",";
                }

                Log.d(TAG, "Update " + strShareValue);
                Toast.makeText(Controller.this,
                        strShareValue, Toast.LENGTH_SHORT).show();


                sendMessage(strShareValue);

                try {
                    HashMap<String, String> queryValues = new HashMap<String, String>();
                    queryValues.put("userID", userPID);
                    queryValues.put("log", strShareValue);

                        System.out.println("before query insert");
                        controller.insertUser(queryValues);
                        System.out.println("After insertion...");
                } catch (Exception ex) {
                    System.out.println(ex.getMessage().toString());
                }
                syncSQLiteMySQLDB();

            }
        });

    }

    // Alert Button Activity
    public void addAlertButton() {
        Button button = (Button) findViewById(R.id.alert);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final List<String> regions = new ArrayList<>(Arrays.asList("front", "right", "rear", "left"));
                String strShareValue = "1:";
                for (int i = 0; i < regions.size(); i++) {
                    int resID = getResources().getIdentifier(regions.get(i) + "_count", "id", getPackageName());
                    TextView x = (TextView) findViewById(resID);
                    int count = (Integer.parseInt(x.getText().toString()));
                    strShareValue += Integer.toString(count);
                    strShareValue += ",";
                }

                Log.d(TAG, "Alert " + strShareValue);
                Toast.makeText(Controller.this,
                        strShareValue, Toast.LENGTH_SHORT).show();


                sendMessage(strShareValue);

                try {
                    HashMap<String, String> queryValues = new HashMap<String, String>();
                    queryValues.put("userID", userPID);
                    queryValues.put("log", strShareValue);

                    System.out.println("before query insert");
                    controller.insertUser(queryValues);
                    System.out.println("After insertion...");
                } catch (Exception ex) {
                    System.out.println(ex.getMessage().toString());
                }
                syncSQLiteMySQLDB();

            }
        });
    }

    public void initizlize_inc_dec_buttons()
    {
        final List<String> regions = new ArrayList<>(Arrays.asList("front", "right","left","rear"));
        List<String> funcs= new ArrayList<>(Arrays.asList("plus","minus"));

        for(int i=0;i<regions.size();i++)
        {
            for(int j=0;j<funcs.size();j++)
            {
                String name=regions.get(i)+"_"+funcs.get(j);
                int button_id = getResources().getIdentifier(name, "id", getPackageName());

                Button button = (Button) findViewById(button_id);
                final int type=j;
                final int r=i;

                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        inc_dec(regions.get(r), type);




                    }
                });
            }
        }

    }

    public void inc_dec(String s,int type)
    {

        int resID = getResources().getIdentifier(s+"_count", "id", getPackageName());
        TextView x = (TextView) findViewById(resID);
        int count=(Integer.parseInt(x.getText().toString()));
        if(type==0)
            count++;
        else
            count--;
        if(count<0)
            count=0;

        x.setText(Integer.toString(count));

    }

    public void syncSQLiteMySQLDB(){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> userList =  controller.getAllUsers();
        System.out.println(userList);
        if(userList.size()!=0){
            if(controller.dbSyncCount() != 0){
                //prgDialog.show();
                params.put("usersJSON", controller.composeJSONfromSQLite());
                //params.put("usersJSON", "");

                System.out.println(params);
                client.post("https://juhu.soic.indiana.edu/vipwizofoz/controller/insertuser.php",params,new AsyncHttpResponseHandler() {
                    //System.out.println("Inside Client Post...");
                    @Override
                    public void onSuccess(String response) {
                        System.out.println("Inside Success....");
                        System.out.println(response);
                        //prgDialog.hide();
                        try {
                            System.out.println("Inside update functions");
                            JSONArray arr = new JSONArray(response);
                            System.out.println(arr.length());
                            for(int i=0; i<arr.length();i++){
                                JSONObject obj = (JSONObject)arr.get(i);
                                System.out.println(obj.get("id"));
                                System.out.println(obj.get("status"));
                                controller.updateSyncStatus(obj.get("id").toString(),obj.get("status").toString());
                            }
                            Toast.makeText(getApplicationContext(), "DB Sync completed!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // TODO Auto-generated method stub
                        //prgDialog.hide();
                        System.out.println("Inside Failure....");
                        if(statusCode == 404){
                            Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        }else if(statusCode == 500){
                            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "SQLite and Remote MySQL DBs are in Sync!", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No data in SQLite DB, please do enter User name to perform Sync action", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

