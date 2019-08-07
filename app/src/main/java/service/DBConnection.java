package service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.example.heartrate_android.UserPatient;

import java.io.File;

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
    public static final String TIMESTAMPCOL = "Timestamp";
    public static final String XCOL = "xVal";
    public static final String YCOL = "yVal";
    public static final String ZCOL = "zVal";
    //initialize the database
    private SQLiteDatabase dbUser = null;


    public void createDB(String tableName, File sdCardFile ){
        String filePath = Environment.getExternalStorageDirectory().toString()
                + "/Android/Data/CSE535_ASSIGNMENT2";
        File file = new File(filePath);
        //SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(sdCardFile+"/patientDB_team4.db", null);
        dbUser.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        file.mkdirs();
        //File file = new File(folder, "patientDB_team4.db");
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file+"/patientDB_team4.db", null);
        db.beginTransaction();
        try{
            db.execSQL("create table "+ tableName +" ("
                    + " timestamp DATETIME , "
                    + " xvalue REAL , "
                    + " yvalue REAL, "
                    + " zvalue REAL ); " );
            db.setTransactionSuccessful();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }

    //This function is used this to add entries to the database
    public void addHandler(String tableName,UserPatient patient) {
        ContentValues datapoints = new ContentValues();
        datapoints.put(TIMESTAMPCOL, patient.getTimestamp());
        datapoints.put(XCOL, patient.getXVal());
        datapoints.put(YCOL, patient.getYVal());
        datapoints.put(ZCOL, patient.getZVal());
        dbUser.insert(TABLE_NAME, null, datapoints);

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
}
