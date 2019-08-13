package service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.example.heartrate_android.UserPatient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author amanjotsingh ,Sakshi Gautam
 * This is the database helper class to create DB for the patient
 * It also helps to add the accelerometer values in the database
 */

public class DBConnection extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    //WE need to store db to Eternal SD CARD
    public static final String DATABASE_PATH = Environment.getExternalStorageDirectory() + "/CSE535_ASSIGNMENT2/";
    public static final String DATABASE_NAME = "patientDB.db";
    //public static final String DATABASE_NAME = "group17.db";
    public static String TABLE_NAME = "Name_ID_Age_Sex";
    public static final String TIMESTAMPCOL = "timestamp";
    public static final String XCOL = "xvalue";
    public static final String YCOL = "yvalue";
    public static final String ZCOL = "zvalue";
    //initialize the database
    private String tableName;

    public DBConnection(Context context, String tableName, String dbFile) {
        super(context, dbFile, null, 1);
        this.tableName = tableName;
    }


    public DBConnection(Context context, String dbFile) {
        super(context, dbFile, null, 1);
    }


    public void createDB(String tableName, File sdCardFile) {
        String filePath = Environment.getExternalStorageDirectory().toString()
                + "/Android/Data/CSE535_ASSIGNMENT2";
        File file = new File(filePath);
        //SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(sdCardFile+"/patientDB_team4.db", null);
        //dbUser.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        file.mkdirs();
        //File file = new File(folder, "patientDB_team4.db");
        SQLiteDatabase dbUser = SQLiteDatabase.openOrCreateDatabase(file + "/patientDB_team4.db", null);
        dbUser.beginTransaction();
        try {
            dbUser.execSQL("create table " + tableName + " ("
                    + " timestamp DATETIME , "
                    + " xvalue FLOAT , "
                    + " yvalue FLOAT, "
                    + " zvalue FLOAT ); ");
            //dbUser.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbUser.endTransaction();
            dbUser.close();
        }
    }

    //This function is used this to add entries to the database
    public void addHandler(String tableName, UserPatient patient) {
        SQLiteDatabase dbUser = this.getWritableDatabase();//SQLiteDatabase.openOrCreateDatabase(file + "/patientDB_team4.db", null);
        ContentValues datapoints = new ContentValues();
        datapoints.put(TIMESTAMPCOL, patient.getTimestamp());
        datapoints.put(XCOL, patient.getXVal());
        datapoints.put(YCOL, patient.getYVal());
        datapoints.put(ZCOL, patient.getZVal());
        System.out.println(datapoints);
        dbUser.execSQL("CREATE TABLE IF NOT EXISTS "  + tableName + " ("
                + " timestamp LONG , "
                + " xvalue FLOAT , "
                + " yvalue FLOAT, "
                + " zvalue FLOAT ); ");
        dbUser.insert(tableName, null, datapoints);
    }


    public List<UserPatient> getValues(String tableName) {
        String filePath = Environment.getExternalStorageDirectory().toString()
                + "/Android/Data/CSE535_ASSIGNMENT2";
        File file = new File(filePath);
        SQLiteDatabase dbUser = SQLiteDatabase.openOrCreateDatabase(file + "/patientDB_team4.db", null);
        String query = "Select * FROM " + tableName;
        Cursor cursor = dbUser.rawQuery(query, null);
        List<UserPatient> patient = new ArrayList();

        while (cursor != null) {
            cursor.moveToFirst();
            UserPatient patientObj = new UserPatient(Long.parseLong(cursor.getString(0)), Float.parseFloat(cursor.getString(1)), Float.parseFloat(cursor.getString(2))
                    , Float.parseFloat(cursor.getString(3)));
            patient.add(patientObj);
        }
        cursor.close();
        return patient;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + tableName + "("
                + " timestamp DATETIME , "
                + " xvalue FLOAT , "
                + " yvalue FLOAT, "
                + " zvalue FLOAT ); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
