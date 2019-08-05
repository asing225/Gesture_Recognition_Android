package com.example.heartrate_android;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
public class DataBaseAcc extends SQLiteOpenHelper  {
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
    public DataBaseAcc(Context context)
    {
        super(context, DATABASE_PATH+DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //called only after getReadable or getWritableDatabase() is invoked
        dbUser = db;
        //if required add code here when db is first created
        Log.d("Sakshi","OnCreate of db called");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        dbUser.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(dbUser);
    }
    public void createPatientTable(String table)
    {
        if(dbUser==null)
            dbUser = getWritableDatabase();
        if(!TABLE_NAME.equals(table)) {
            //Integer is 8 bytes long, as is a long, so timestamp can be stored in an integer
            String create_table = "CREATE TABLE IF NOT EXISTS " + table + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TIMESTAMPCOL + " INTEGER," +
                    XCOL + " REAL NOT NULL," + YCOL + " REAL NOT NULL," + ZCOL + " REAL NOT NULL);";
            TABLE_NAME = table;
            dbUser.execSQL(create_table);
            Log.d("Sakshi", "Tabel created : " + table);
        }
    }
    //Use this to add entries to the database
    public void addHandler(UserPatient patient) {
        ContentValues values = new ContentValues();
        //Integer is 8 bytes long, as is a long, so timestamp can be stored in an integer
        values.put(TIMESTAMPCOL, patient.getTimestamp());
        values.put(XCOL, patient.getXValues());
        values.put(YCOL, patient.getYValues());
        values.put(ZCOL, patient.getZValues());
        dbUser.insert(TABLE_NAME, null, values);

        //database.close();
    }
    //Use this to check if a timestamp already exists in the database.
    public UserPatient findHandler(long timestamp) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + TIMESTAMPCOL + "=" + timestamp;
        Cursor cursor = dbUser.rawQuery(query, null);
        UserPatient patient = new UserPatient();
        if (cursor!=null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            patient.setTimestamp(Long.parseLong(cursor.getString(0)));
            patient.setXValues(Float.parseFloat(cursor.getString(1)));
            patient.setYValues(Float.parseFloat(cursor.getString(2)));
            patient.setZValues(Float.parseFloat(cursor.getString(3)));
            cursor.close();
        } else {
            patient = null;
        }
        return patient;
    }
}
