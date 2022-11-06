package com.application.e_scoots;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

public class GFBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent i) {
        GeofencingEvent gfEvent = GeofencingEvent.fromIntent(i) ;

        if (gfEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(gfEvent.getErrorCode()) ;
            Log.e(TAG, errorMessage) ;
            return ;
        }

        int GFtrans = gfEvent.getGeofenceTransition();

        AlertDialog.Builder b = new AlertDialog.Builder(context.getApplicationContext()) ;

        if (GFtrans == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Toast.makeText(context.getApplicationContext(), "You have entered a Geofence zone",Toast.LENGTH_SHORT).show();
        } else if (GFtrans == Geofence.GEOFENCE_TRANSITION_DWELL) {
//            b.setCancelable(true) ;
//            b.setTitle("This is an Alert Dialog") ;
//            b.setMessage("Want to End the Ride?") ;
//            b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    Intent intent = new Intent(context.getApplicationContext(), End.class) ;
//                }
//            }) ;
//            b.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.cancel();
//                }
//            }) ;
//            b.create() ;
//            b.show() ;
            Toast.makeText(context.getApplicationContext(), "End ride",Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(context.getApplicationContext(), End.class) ;
//            startActivity(intent) ;
        } else if (GFtrans == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Toast.makeText(context.getApplicationContext(), "You are leaving a Geofence zone",Toast.LENGTH_SHORT).show();
        }
    }
}