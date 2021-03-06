package com.example.touahmed.wiz_of_oz_controller;

/**
 * Created by takter on 3/27/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class DBController extends SQLiteOpenHelper {
    private static final String tablename = "wiz_of_oz";  // tablename
    private static final String date = "date";  // column name
    private static final String id = "PID";  // User inserted ID column
    private static final String scenario = "Scenarios"; // column name
    private static final String scenario2 = "Grocery"; // column name
    private static final String scenario3 = "Transit"; // column name
    private static final String scenario4 = "ATM"; // column name
    private static final String scenario5 = "Office"; // column name
    private static final String scenario6 = "Doctor Ofiice"; // column name
    private static final String status = "UpdateStatus"; // column name
    private static final String databasename = "participantinfo"; // Dtabasename
    private static final int versioncode = 1; //versioncode of the database

    public DBController(Context applicationcontext) {
        super(applicationcontext, databasename, null, 1);
    }
    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        System.out.println("Inside OnCreate...");
        query = "CREATE TABLE IF NOT EXISTS " + tablename + "(" + id + " text, " + scenario + " text, " + status + " text)";
        //query = "CREATE TABLE users ( userId INTEGER PRIMARY KEY, userName TEXT, udpateStatus TEXT)";
        database.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS " + tablename;
        database.execSQL(query);
        onCreate(database);
    }

    /*@Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE IF NOT EXISTS " + tablename + "(" + id + " integer primary key, " + place + " text, " + country + " text)";
        database.execSQL(query);
    }
*/
    /*@Override
    public void onUpgrade(SQLiteDatabase database, int version_old,
                          int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS " + tablename;
        database.execSQL(query);
        onCreate(database);
    }*/
    public void insertUser(HashMap<String, String> queryValues) {
        System.out.println("Inside insertUser...");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id, queryValues.get("userID"));
        //values.put(date, queryValues.get("date"));
        values.put(scenario, queryValues.get("scenario"));
        //values.put(scenario2, queryValues.get("chkGrocery"));
        //values.put("userName", queryValues.get("userName"));
        values.put(status, "no");
        database.insert(tablename, null, values);
        database.close();
    }

    public ArrayList<HashMap<String, String>> getAllUsers() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + tablename;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("userId", cursor.getString(0));
                //map.put("date", cursor.getString(1));
                map.put("scenario", cursor.getString(2));
                //map.put("chkGrocery", cursor.getString(3));
                //System.out.println(map);
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }

    /**
     * Compose JSON out of SQLite records
     * @return
     */
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + tablename + " where " + status + " = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("userId", cursor.getString(0));
               // map.put("userDate", cursor.getString(1));
                map.put("userScenario", cursor.getString(2));
                //map.put("chkGrocery", cursor.getString(3));
                //System.out.println(map);
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

    /**
     * Get Sync status of SQLite
     * @return
     */
    public String getSyncStatus(){
        String msg = null;
        if(this.dbSyncCount() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync needed\n";
        }
        return msg;
    }

    /**
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public int dbSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM " + tablename +" where " + status + " = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }

    /**
     * Update Sync status against each User ID
     * @param uid
     * @param Upstatus
     */
    public void updateSyncStatus(String uid, String Upstatus){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update "+ tablename + " set " + status + " = '"+ Upstatus +"' where " + id + " = " + "'"+ uid +"'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);

        database.close();
    }

}