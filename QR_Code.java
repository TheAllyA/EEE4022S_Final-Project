package com.application.e_scoots;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
import android.view.View ;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QR_Code extends AppCompatActivity {
    public TextView qr_textview ;
    public ImageButton qr_button ;
    public Button map_button ;
    public IntentIntegrator intentIntegrator = new IntentIntegrator(this);
    public SQLiteDatabase SQLdb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        ActivityCompat.requestPermissions(QR_Code.this, new String[]{Manifest.permission.CAMERA}, 1011);
    }

   @Override
   public void onRequestPermissionsResult(int reqCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            qr_textview = (TextView) findViewById(R.id.qr_scan);
            qr_button = (ImageButton) findViewById(R.id.qr_button);
//            map_button = (Button) findViewById(R.id.map_button);
            ArrayList<String> barcode = new ArrayList<>();
            barcode.add(IntentIntegrator.QR_CODE);

            qr_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intentIntegrator.setDesiredBarcodeFormats(barcode);
                    intentIntegrator.initiateScan(barcode);
                }
            });

//            map_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(QR_Code.this, Map.class);
//                    startActivity(i);
//                }
//            });
        }
        else {
            Toast.makeText(QR_Code.this, "Permisssion denied to use Camera", Toast.LENGTH_SHORT).show() ;
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        final IntentResult result = IntentIntegrator.parseActivityResult(resCode,data);

        if (result != null) {
            String contents = result.getContents();
            //qr_textview.setText("Scan has succesfully connected");
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            //DialogInterface.OnClickListener listener ;
            File path = this.getDatabasePath("E_Scoots.db");
            SQLdb = SQLiteDatabase.openDatabase(path.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
            String update = "UPDATE Scooter_Information SET Status = 'Available' WHERE Student_Num = 'ADMALI004'";
            SQLdb.execSQL(update);
            SQLdb.close();

            if (search()) {
                build.setMessage("You have connected to an e-scooter!"); //Would you like to go to ${result.contents}?");
                build.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Intent in = new Intent(Intent.ACTION_WEB_SEARCH);
//                                in.putExtra(SearchManager.QUERY, contents);
//                                startActivity(in);
                                Intent intent = new Intent(QR_Code.this, Map.class);
                                startActivity(intent);
                            }
                        }
                );

                build.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface1, int i1) {
                                finish();
                            }
                        }
                );

                build.create();
                build.show();

                File path1 = this.getDatabasePath("E_Scoots.db");
                SQLdb = SQLiteDatabase.openDatabase(path1.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
                String update1 = "UPDATE Scooter_Information SET Status = 'Unavailable' WHERE Student_Num = 'ADMALI004'";
                SQLdb.execSQL(update1);
                SQLdb.close();

//                Intent i = new Intent(QR_Code.this, Map.class);
//                startActivity(i);
            } else {
                //Toast.makeText(this, "Please retry", Toast.LENGTH_SHORT).show();
                //qr_textview.setText("Please retry - Scan unsuccessful");
                build.setMessage("Scan unsuccessful - There are no available e-scooters.");
                build.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                build.create();
                build.show();
            }
        }
    }

    public boolean search() {
        File path = this.getDatabasePath("E_Scoots.db") ;
        SQLdb = SQLiteDatabase.openDatabase(path.getAbsolutePath(),null,SQLiteDatabase.OPEN_READONLY) ;
        String search_status = "SELECT * FROM Scooter_Information WHERE Status IN ('Available')" ;

        Cursor cursor_std = SQLdb.rawQuery(search_status, null);

        if (cursor_std.moveToFirst()) {
            cursor_std.close();
            SQLdb.close();
            return true ;
        } else {
            cursor_std.close();
            SQLdb.close();
            return false ;
        }

    }
}