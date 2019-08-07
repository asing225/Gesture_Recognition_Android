package service;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import java.io.File;

/**
 * @author amanjotsingh
 * This is the database helper class to create DB for the patient*/

public class DBConnection {

    public void createDB(String tableName, File sdCardFile){

        //SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(sdCardFile+"/patientDB_team4.db", null);
        String filePath = Environment.getExternalStorageDirectory().toString()
                + "/Android/Data/CSE535_ASSIGNMENT2";
        File file = new File(filePath);
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
}
