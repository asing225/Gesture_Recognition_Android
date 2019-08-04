package com.example.heartrate_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Team4.DB";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY =
     "CREATE TABLE " + UserContract.NewUserInfo.TABLE_NAME+ "(" + UserContract.NewUserInfo.Time_Stamp + "TEXT,"+
             UserContract.NewUserInfo.X_Values+ "TEXT," + UserContract.NewUserInfo.Y_Values+ "TEXT," + UserContract.NewUserInfo.Z_Values+ "TEXT);";


    public UserDBHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DATABASE OPERATIONS", "DB created/opened");

    }

    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL(CREATE_QUERY);
        Log.e("DATABASE OPERATIONS", "Table created");
    }

    public void addInfo(String TimeStamp, String x_values, String y_values, String z_values, SQLiteDatabase db){
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserContract.NewUserInfo.Time_Stamp,TimeStamp);
        contentValues.put(UserContract.NewUserInfo.X_Values,x_values);
        contentValues.put(UserContract.NewUserInfo.Y_Values,y_values);
        contentValues.put(UserContract.NewUserInfo.Z_Values,z_values);
        db.insert(UserContract.NewUserInfo.TABLE_NAME,null,contentValues);
        Log.e("DATABASE OPERATIONS", "One row inserted");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int OldVersion, int NewVersion){

    }
}

