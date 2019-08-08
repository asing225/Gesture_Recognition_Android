package service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.heartrate_android.UserPatient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author amanjotsingh ,Sakshi Gautam
 * This is the database helper class to create DB for the patient
 * It also helps to add the accelerometer values in the database*/

public class DBConnection   {
    private static final int DATABASE_VERSION = 1;
    //WE need to store db to Eternal SD CARD
    public static final String DATABASE_PATH = Environment.getExternalStorageDirectory()+"/CSE535_ASSIGNMENT2/";
    public static final String DATABASE_NAME = "patientDB.db";
    //public static final String DATABASE_NAME = "group17.db";
    public static String TABLE_NAME = "Name_ID_Age_Sex";
    public static final String TIMESTAMPCOL = "timestamp";
    public static final String XCOL = "xvalue";
    public static final String YCOL = "yvalue";
    public static final String ZCOL = "zvalue";
    //initialize the database
    private SQLiteDatabase dbUser = null;

    public void createDB(String tableName, File sdCardFile ){
        String filePath = Environment.getExternalStorageDirectory().toString()
                + "/Android/Data/CSE535_ASSIGNMENT2";
        File file = new File(filePath);
        //SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(sdCardFile+"/patientDB_team4.db", null);
        //dbUser.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        file.mkdirs();
        //File file = new File(folder, "patientDB_team4.db");
        SQLiteDatabase dbUser = SQLiteDatabase.openOrCreateDatabase(file+"/patientDB_team4.db", null);
        dbUser.beginTransaction();
        try{
            dbUser.execSQL("create table "+ tableName +" ("
                    + " timestamp DATETIME , "
                    + " xvalue FLOAT , "
                    + " yvalue FLOAT, "
                    + " zvalue FLOAT ); " );
            dbUser.setTransactionSuccessful();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            dbUser.endTransaction();
        }
    }

 /*   public UserPatient getxyz(String tableName){
        String query = "Select * from "+ tableName+ " ORDER BY TIMESTAMP DESC limit 10";
        Cursor cursor = dbUser.rawQuery(query, null);\
        UserPatient patient = new UserPatient();
        int i=0;
        int j=10;
        if (cursor.moveToFirst() ){
             do{
                 patient.getTimestamp(Long.parseLong(cursor.getString(0)));
                 patient.getXVal(Float.parseFloat(cursor.getString(1)));
                 patient.getYVal(Float.parseFloat(cursor.getString(2)));
                 patient.getZVal(Float.parseFloat(cursor.getString(3)));
                }while (cursor.moveToNext());
            }

            cursor.close();
            return patient;
    }*/



    //This function is used this to add entries to the database
    public void addHandler(String tableName,UserPatient patient) {
        String filePath = Environment.getExternalStorageDirectory().toString()
                + "/Android/Data/CSE535_ASSIGNMENT2";
        File file = new File(filePath);
        SQLiteDatabase dbUser = SQLiteDatabase.openOrCreateDatabase(file+"/patientDB_team4.db", null);
        dbUser.beginTransaction();
        ContentValues datapoints = new ContentValues();
        datapoints.put(TIMESTAMPCOL, patient.getTimestamp());
        datapoints.put(XCOL, patient.getXVal());
        datapoints.put(YCOL, patient.getYVal());
        datapoints.put(ZCOL, patient.getZVal());
        dbUser.insert(tableName, null, datapoints);
        dbUser.endTransaction();
    }
    //This function is use this to check if a timestamp already exists in the database.
    public UserPatient findHandler(long timestamp) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + TIMESTAMPCOL + "=" + timestamp;
        Cursor cursor = dbUser.rawQuery(query, null);
        UserPatient patient = new UserPatient();
        if (cursor!=null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            patient.setTimestamp(Long.parseLong(cursor.getString(0)));
            patient.setXVal(Float.parseFloat(cursor.getString(1)));
            patient.setYVal(Float.parseFloat(cursor.getString(2)));
            patient.setZVal(Float.parseFloat(cursor.getString(3)));
            cursor.close();
        } else {
            patient = null;
        }
        return patient;
    }

    public List<UserPatient> getValues(String tableName) {
        String filePath = Environment.getExternalStorageDirectory().toString()
                + "/Android/Data/CSE535_ASSIGNMENT2";
        File file = new File(filePath);
        SQLiteDatabase dbUser = SQLiteDatabase.openOrCreateDatabase(file+"/patientDB_team4.db", null);
        String query = "Select * FROM " + tableName;
        Cursor cursor = dbUser.rawQuery(query, null);
        List<UserPatient> patient = new ArrayList();

        while (cursor!=null) {
            cursor.moveToFirst();
            UserPatient patientObj = new UserPatient(Long.parseLong(cursor.getString(0)), Float.parseFloat(cursor.getString(1)), Float.parseFloat(cursor.getString(2))
                    , Float.parseFloat(cursor.getString(3)));
            patient.add(patientObj);
        }
        cursor.close();
        return patient;
    }

}
