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
                    + " xvalue REAL NOT NULL , "
                    + " yvalue REAL NOT NULL, "
                    + " zvalue REAL NOT NULL ); " );
            dbUser.setTransactionSuccessful();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            dbUser.endTransaction();
        }
    }
/*

    float[] xVals = new float[10];
    float[] yVals = new float[10];
    float[] zVals = new float[10];
    public ArrayList<String>[]  getxyz(String tableName){
        String query = "Select * from "+ tableName+ " ORDER BY TIMESTAMP DESC limit 10";
        Cursor cursor = dbUser.rawQuery(query, null);\
        UserPatient patient = new UserPatient();
        int i=0;
        int j=10;
        if (cursor.moveToFirst() ){
             do{
                 String x = cursor.getString(cursor.getColumnIndex("XValues"));
                 String y = cursor.getString(cursor.getColumnIndex("YValues"));
                 String z = cursor.getString(cursor.getColumnIndex("ZValues"));
                 Float x = Float.parseFloat(x);
                 Float y = Float.parseFloat(y);
                 Float z = Float.parseFloat(z);
                 xVals[j] = x;
                 yVals[j] = y;
                 zVals[j] = z;

                }while (cursor.moveToNext());
            }cursor.close();
            ArrayList<String>[] arr = new ArrayList<String>[3];
                 arr[0] = xVals[j];
                 arr[1] = yVals[j];
                 arr[2] = zVals[j]
             return arr;
    }

*/


    //This function is used this to add entries to the database
    //This function is used this to add entries to the database
    //This function is used this to add entries to the database
    public void addHandler(String tableName,UserPatient patient) {
        String filePath = Environment.getExternalStorageDirectory().toString()
                + "/Android/Data/CSE535_ASSIGNMENT2";
        File file = new File(filePath);
        SQLiteDatabase dbUser = SQLiteDatabase.openOrCreateDatabase(file+"/patientDB_team4.db", null);
        dbUser.beginTransaction();
        dbUser.execSQL("INSERT INTO " + tableName + " VALUES (" + patient.getTimestamp()
                + "," + patient.getXVal() + "," + patient.getYVal() + "," + patient.getZVal() + ");");
        dbUser.endTransaction();
        dbUser.close();
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

        while (cursor!=null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            UserPatient patientObj = new UserPatient(Long.parseLong(cursor.getString(0)), Float.parseFloat(cursor.getString(1)), Float.parseFloat(cursor.getString(2))
                    , Float.parseFloat(cursor.getString(3)));
            patient.add(patientObj);
        }
        cursor.close();
        return patient;
    }

}
