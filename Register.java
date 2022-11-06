package com.application.e_scoots;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout ;
import com.google.android.material.textfield.TextInputEditText ;

import java.io.File;

public class Register extends AppCompatActivity {
    public TextInputEditText R_sno_text, R_password, R_password_confirm ;
    public Button R_sign_in ;
    boolean check_std_no, check_std_passw ;
    public SQLiteDatabase SQLdb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        R_sno_text = (TextInputEditText) findViewById(R.id.R_sno_text) ;
        R_password = (TextInputEditText) findViewById(R.id.R_password) ;
        R_password_confirm = (TextInputEditText) findViewById(R.id.R_password_confirm) ;
        R_sign_in = (Button) findViewById(R.id.R_Sign_In) ;
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);

        R_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_std_no = Std_No_Check(R_sno_text);
                check_std_passw = Std_Pass_Check(R_password);

                if (check_std_no && check_std_passw) {
                    if (R_password.getText().toString().equals(R_password_confirm.getText().toString())) {
                        insert() ;
                        builder.setCancelable(true) ;
                        builder.setTitle("This is an Alert Dialog") ;
                        builder.setMessage("Successfully added!") ;
                        builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        builder.create();
                        builder.show();

                        Intent i = new Intent(Register.this, PaymentInfo.class);
                        startActivity(i);
                    } else {
                        builder.setCancelable(true) ;
                        builder.setTitle("This is an Alert Dialog") ;
                        builder.setMessage("Password confirmation does not match") ;
                        builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        builder.create();
                        builder.show();
                        //Toast.makeText(Register.this, "Password confirmation does not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    builder.setCancelable(true) ;
                    builder.setTitle("This is an Alert Dialog") ;
                    builder.setMessage("Invalid entry for student number/password") ;
                    builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.create() ;
                    builder.show() ;
                    //Toast.makeText(Register.this, "Invalid entry for student number/password", Toast.LENGTH_SHORT).show();
                }
            }
        }) ;
    }

    public boolean Std_No_Check(TextInputEditText std_no) {
        File path = this.getDatabasePath("E_Scoots.db") ;
        SQLdb = SQLiteDatabase.openDatabase(path.getAbsolutePath(),null,SQLiteDatabase.OPEN_READONLY) ;
        boolean check = false;
        String R_std_no = std_no.getText().toString();

        if (R_std_no.length() == 9) {
            if (R_std_no.matches("[a-zA-Z0-9]*")) {
                String search_std = "SELECT * FROM UCT_Member_Information WHERE Student_Num IN ('" + R_std_no + "')" ;

                Cursor cursor_std = SQLdb.rawQuery(search_std, null);

                if (cursor_std.moveToFirst()) {
                   Toast.makeText(this, "This student already exists in the database", Toast.LENGTH_SHORT).show();
                } else {
                    check = true;
                    //Toast.makeText(this, "Perfect", Toast.LENGTH_SHORT).show();
                }
                SQLdb.close();

            } else {
                Toast.makeText(this, "Invalid characters", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid student number length", Toast.LENGTH_SHORT).show();
        }
        return check ;
        // Don't forget to check for student number in database
    }

    public boolean Std_Pass_Check(TextInputEditText pass) {
        File path = this.getDatabasePath("E_Scoots.db") ;
        SQLdb = SQLiteDatabase.openDatabase(path.getAbsolutePath(),null,SQLiteDatabase.OPEN_READONLY) ;
        boolean check = false ;
        String R_std_pass = pass.getText().toString() ;

        if (R_std_pass.length() < 8) {
            Toast.makeText(this, "Weak Password (minimum of 8 characters & a number)", Toast.LENGTH_SHORT).show();
        } else if (R_std_pass.length() > 8 && R_std_pass.matches("[a-zA-Z0-9]*")){
            String search_pass = "SELECT * FROM UCT_Member_Information WHERE Student_Pass IN ('" + R_std_pass + "')";
            Cursor cursor_pass = SQLdb.rawQuery(search_pass,null) ;

            if (cursor_pass.moveToFirst()) {
                Toast.makeText(this, "This password already exists in the database", Toast.LENGTH_SHORT).show();
            } else {
                check = true;
                //Toast.makeText(this, "Strong password", Toast.LENGTH_SHORT).show();
            }

            SQLdb.close();
        }

        return check ;
//        // Don't forget to check for password in database
    }

    public void insert() {
        File path = this.getDatabasePath("E_Scoots.db") ;
        SQLdb = SQLiteDatabase.openDatabase(path.getAbsolutePath(),null,SQLiteDatabase.OPEN_READWRITE) ;
        ContentValues cv = new ContentValues() ;

        //String insert = "INSERT INTO UCT_Member_Information (Student_Num, Student_Pass) VALUES ('" + R_sno_text + "', '" + R_password + "')" ;
        cv.put("Student_Num", R_sno_text.getText().toString());
        cv.put("Student_Pass", R_password.getText().toString()) ;
        SQLdb.insert("UCT_Member_Information",null, cv) ;

        SQLdb.close();
    }
}