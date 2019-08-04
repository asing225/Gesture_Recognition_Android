package com.example.heartrate_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.util.Random;

import static android.os.Build.ID;


public class NewContact {


}
class NewUserContact{
    SQLiteDatabase db;
    Context context;
    UserDBHelper userDBHelper;
    private String Name;
    private String Age;
    private String Gender;
    public void createDB(){
        userDBHelper = new UserDBHelper(context);
        db = userDBHelper.getWritableDatabase();
        userDBHelper.addInfo(ID,Name,Age,Gender,db);
        Toast.makeText(context, "Data saved", Toast.LENGTH_SHORT).show();
        userDBHelper.close();
    }
}


