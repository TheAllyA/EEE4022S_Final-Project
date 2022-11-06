package com.application.e_scoots;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button ;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar ;

import com.google.android.material.textfield.TextInputEditText;

public class PaymentInfo extends AppCompatActivity {
    public TextInputEditText acc_no, exp_date, CVV ;
    public Button payment ;
    boolean check_acc_no, check_date, check_CVV ;
    public SQLiteDatabase SQLdb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);

        acc_no = (TextInputEditText) findViewById(R.id.acc_no_text) ;
        exp_date = (TextInputEditText) findViewById(R.id.exp_date_text) ;
        CVV = (TextInputEditText) findViewById(R.id.CVV_text) ;
        payment = (Button) findViewById(R.id.submit) ;
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentInfo.this) ;

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_acc_no = Acc_No_Check(acc_no) ;
                check_date = date_check(exp_date) ;
                check_CVV = CVV_check(CVV) ;

                if ((check_acc_no) && (check_date) && check_CVV ) {
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

                    Intent i = new Intent(PaymentInfo.this, QR_Code.class) ;
                    startActivity(i);

                } else {
                    builder.setCancelable(true) ;
                    builder.setTitle("This is an Alert Dialog") ;
                    builder.setMessage("Invalid entry of details") ;
                    builder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.create();
                    builder.show();
                    //Toast.makeText(PaymentInfo.this, "Invalid details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean Acc_No_Check(TextInputEditText acc_no) {
        boolean check = false ;
        String account = acc_no.getText().toString() ;

        if (account.length() == 16) {
            if (account.substring(0,1).equals("4") || account.substring(0,1).equals("5")) {
                if (check_sum(account)) {
                    check = true ;
                } else { Toast.makeText(this, "Invalid check sum", Toast.LENGTH_SHORT).show();}
            } else {
                Toast.makeText(this, "Invalid starting digit", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid account number length", Toast.LENGTH_SHORT).show();
        }

        return check ;
    }

    public boolean check_sum(String acc_num) {
        int sum = 0 ;
        boolean check = false ;

        for (int i = 1; i < acc_num.length(); i = i+2) {
            int digit = Integer.parseInt(acc_num.substring(i,i+1))*2 ;

            if (digit > 9) {
                String str_digit = Integer.toString(digit) ;
                int new_digit = Integer.parseInt(str_digit.substring(0,1)) + Integer.parseInt(str_digit.substring(1,2)) ;

                sum = sum + new_digit ;
            } else {
                sum = sum + digit ;
            }
        }

        for (int i = 0; i < acc_num.length(); i = i+2) {
            sum = sum + Integer.parseInt(acc_num.substring(i,i+1)) ;
        }

        if (sum%10 == 0) {
            check = true ;
        }

        return check ;
    }

    public boolean date_check(TextInputEditText exp_date) {
        String expiry = exp_date.getText().toString() ;
        boolean check = false ;

        Calendar c = Calendar.getInstance() ;
        int curr_month = c.get(Calendar.MONTH)+1 ;
        int curr_year = c.get(Calendar.YEAR) ;
        int month = Integer.parseInt(expiry.substring(0,2)) ;
        int year = Integer.parseInt(expiry.substring(3,7)) ;

        if (curr_year < year) {
            check = true;
            return check ;
        }
        if (curr_year ==  year) {
            if (curr_month <= month) {
                check = true;
            }
        }
         else {
            Toast.makeText(this, "Invalid expiry date", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, Integer.toString(curr_year), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, Integer.toString(curr_month), Toast.LENGTH_SHORT).show();
        }

        return check ;
    }

    public boolean CVV_check(TextInputEditText CVV) {
        String str_CVV = CVV.getText().toString() ;
        boolean check = false ;

        if (str_CVV.length() == 3) {
            check = true ;
        } else {
            Toast.makeText(this, "Invalid CVV number", Toast.LENGTH_SHORT).show();
        }

        return check ;
    }

    public void insert() {
        File path = this.getDatabasePath("E_Scoots.db") ;
        SQLdb = SQLiteDatabase.openDatabase(path.getAbsolutePath(),null,SQLiteDatabase.OPEN_READWRITE) ;
        ContentValues cv = new ContentValues() ;

        cv.put("Card_Num", acc_no.getText().toString()) ;
        cv.put("Expiry_Date", exp_date.getText().toString() ) ;
        cv.put("CVV", CVV.getText().toString()) ;
        SQLdb.insert("UCT_Member_Information", null, cv) ;

        SQLdb.close();
    }
}