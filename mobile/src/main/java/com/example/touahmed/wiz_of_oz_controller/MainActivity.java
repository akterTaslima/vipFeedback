package com.example.touahmed.wiz_of_oz_controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DBController controller = new DBController(this);
    private EditText editTextPID;
    private EditText editTextDate;
    private CheckBox chkRestaurant, chkOfc, chkGrocery, chkDoctors, chkTransit, chkATM;

    private Button buttonRegister;
    private Button buttonSync;
    ProgressDialog prgDialog;
    public String scenario="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        scenario="";
        editTextPID = (EditText) findViewById(R.id.editPid);
        //editTextDate = (EditText) findViewById(R.id.editDate);
        // check boxes
        chkRestaurant = (CheckBox) findViewById(R.id.chkRestaurant);
        chkRestaurant.setOnClickListener(this);
        chkOfc = (CheckBox) findViewById(R.id.chkOfc);
        chkOfc.setOnClickListener(this);
        chkGrocery = (CheckBox) findViewById(R.id.chkGrocery);
        chkGrocery.setOnClickListener(this);
        chkDoctors = (CheckBox) findViewById(R.id.chkDoctors);
        chkDoctors.setOnClickListener(this);
        chkTransit = (CheckBox) findViewById(R.id.chkTransit);
        chkTransit.setOnClickListener(this);
        chkATM = (CheckBox) findViewById(R.id.chkATM);
        chkATM.setOnClickListener(this);

        //check button
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonSync = (Button) findViewById(R.id.buttonSync);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
        prgDialog.setCancelable(false);


    }

    public void register(View v) {

        try {
            HashMap<String, String> queryValues = new HashMap<String, String>();
            queryValues.put("userID", editTextPID.getText().toString());
            //queryValues.put("date", editTextDate.getText().toString());

            String upid = editTextPID.getText().toString();
            System.out.println("Scenario:: " + scenario);
            queryValues.put("scenario", scenario);

            if(upid.matches("")){
            //if (editTextPID.getText().toString() == ""  ) {

                Toast.makeText(getApplicationContext(), "Please enter User name",
                        Toast.LENGTH_LONG).show();


            }
            else if((chkRestaurant.isChecked()==false) && (chkGrocery.isChecked()==false) && (chkATM.isChecked()==false) && (chkDoctors.isChecked()==false)
                    && (chkOfc.isChecked()==false) && (chkTransit.isChecked()==false)){

                Toast.makeText(getApplicationContext(), "Choose at least one scenario!",
                        Toast.LENGTH_LONG).show();
            }
            else {

                System.out.println("before query insert");
                controller.insertUser(queryValues);
                System.out.println("After insertion...");
                // Sync with remote DB
                syncSQLiteMySQLDB();

                if (chkRestaurant.isChecked()){
                    chkRestaurant.setChecked(false);
                }
                if (chkGrocery.isChecked()){
                    chkGrocery.setChecked(false);
                }

                if (chkATM.isChecked()){
                    chkATM.setChecked(false);
                }

                if (chkDoctors.isChecked()){
                    chkDoctors.setChecked(false);
                }

                if (chkOfc.isChecked()){
                    chkOfc.setChecked(false);
                }

                if (chkTransit.isChecked()){
                    chkTransit.setChecked(false);
                }
                editTextPID.setText("");
                //editTextDate.setText("");

                // Start new activity
                Intent intent = new Intent(this, Controller.class);
                intent.putExtra("userPID", upid);
                startActivity(intent);

            }

          /*  if (editTextPID.getText().toString() != null
                    && editTextPID.getText().toString().trim().length() != 0 && editTextDate.getText().toString() != null) {
                System.out.println("before query insert");
                controller.insertUser(queryValues);
                System.out.println("After insertion...");
                // Sync with remote DB
                syncSQLiteMySQLDB();

                editTextPID.setText("");
                editTextDate.setText("");

                // Start new activity
                Intent intent = new Intent(this, Controller.class);
                intent.putExtra("userPID", upid);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Please enter User name",
                        Toast.LENGTH_LONG).show();
            }*/

        } catch (Exception ex) {
            System.out.println(ex.getMessage().toString());
            //infotext.setText(ex.getMessage().toString());
        }



    }
    public void sync(View v) {
        syncSQLiteMySQLDB();
        //Intent intent = new Intent(this, Controller.class);
        //intent.putExtra("userPID", editTextPID.getText().toString());

        //startActivity(intent);
        Toast.makeText(MainActivity.this,
                "Synchronization Complete", Toast.LENGTH_SHORT).show();


    }

   @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.chkRestaurant:
                if (chkRestaurant.isChecked())
                    scenario=scenario + "Restaurant, ";
                    break;
            case R.id.chkGrocery:
                if (chkGrocery.isChecked())
                    scenario=scenario + "Grocery, ";
                break;
            case R.id.chkOfc:
                if (chkOfc.isChecked())
                    scenario=scenario + "Office, ";
                break;
            case R.id.chkATM:
                if (chkATM.isChecked())
                    scenario=scenario + "ATM, ";
                break;
            case R.id.chkDoctors:
                if (chkDoctors.isChecked())
                    scenario=scenario + "Doctors Office, ";
                break;
            case R.id.chkTransit:
                if (chkTransit.isChecked())
                    scenario=scenario + "Transit, ";
                break;

        }
    }

    public void syncSQLiteMySQLDB(){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> userList =  controller.getAllUsers();
        System.out.println(userList);
        if(userList.size()!=0){
            if(controller.dbSyncCount() != 0){
                prgDialog.show();
                params.put("usersJSON", controller.composeJSONfromSQLite());
                //params.put("usersJSON", "");

                System.out.println(params);
                client.post("https://juhu.soic.indiana.edu/vipwizofoz/insertuser.php",params,new AsyncHttpResponseHandler() {
                //client.post("http://149.160.229.237/wiz_of_oz/insertuser.php",params,new AsyncHttpResponseHandler() {
                    //System.out.println("Inside Client Post...");
                    @Override
                    public void onSuccess(String response) {
                        System.out.println("Inside Success....");
                        System.out.println(response);
                        prgDialog.hide();
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
                        prgDialog.hide();
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
