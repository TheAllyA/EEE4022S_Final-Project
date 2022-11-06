package com.application.e_scoots;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.io.File;

public class End extends AppCompatActivity {
    SQLiteDatabase SQLdb ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        File path = this.getDatabasePath("E_Scoots.db") ;
        SQLdb = SQLiteDatabase.openDatabase(path.getAbsolutePath(),null,SQLiteDatabase.OPEN_READWRITE) ;
        String update = "UPDATE Scooter_Information SET Status = 'Available' WHERE Student_Num = 'ADMALI004'" ;
        SQLdb.execSQL(update);
        SQLdb.close();
    }
}