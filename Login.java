package com.application.e_scoots;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button ;
import android.content.Intent;
import android.view.View;
import android.database.sqlite.SQLiteDatabase ;
import android.database.sqlite.SQLiteOpenHelper ;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

public class Login extends AppCompatActivity {
    public Button sign_in, register ;
    SQLiteDatabase SQLdb ;
    public TextInputEditText std_no, std_pass ;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sign_in = (Button) findViewById(R.id.Sign_In) ;
        register = (Button) findViewById(R.id.Register) ;
        std_no = (TextInputEditText) findViewById(R.id.sno_text) ;
        std_pass = (TextInputEditText) findViewById(R.id.password)  ;
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search()) {
                    builder.setCancelable(true) ;
                    builder.setTitle("Welcome!") ;
                    builder.setMessage("Successful sign in.") ;
                    builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.create();
                    builder.show();

                    Intent i = new Intent(Login.this, QR_Code.class);
                    startActivity(i);
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Register.class) ;
                startActivity(i);

            }
        });
    }

    public boolean search() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        File path = this.getDatabasePath("E_Scoots.db") ;
        SQLdb = SQLiteDatabase.openDatabase(path.getAbsolutePath(),null,SQLiteDatabase.OPEN_READONLY) ;
        String search_std = "SELECT * FROM UCT_Member_Information WHERE Student_Num IN ('" + std_no.getText().toString() + "')" ;

        Cursor cursor_std = SQLdb.rawQuery(search_std, null);

        if (cursor_std.moveToFirst()) {
            String search_pass = "SELECT * FROM UCT_Member_Information WHERE Student_Pass IN ('" + std_pass.getText().toString() + "')";
            Cursor cursor_pass = SQLdb.rawQuery(search_pass,null) ;

            if (cursor_pass.moveToFirst()) {
                return true ;
            } else {
                builder.setCancelable(true) ;
                builder.setTitle("This is an Alert Dialog") ;
                builder.setMessage("Incorrect password") ;
                builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create();
                builder.show();
                //Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }

            cursor_pass.close();
        } else {
            builder.setCancelable(true) ;
            builder.setTitle("This is an Alert Dialog") ;
            builder.setMessage("Incorrect student number") ;
            builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create();
            builder.show();
            //Toast.makeText(this, "Incorrect student number", Toast.LENGTH_SHORT).show();
        }

        cursor_std.close();
        SQLdb.close();
        return false ;

    }

}