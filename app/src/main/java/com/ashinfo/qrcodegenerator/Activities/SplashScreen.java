package com.ashinfo.qrcodegenerator.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.ashinfo.qrcodegenerator.R;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    SharedPreferences sharedPreferences;
    String RegistrationStat, permissionStat;
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        permissionStat = sharedPreferences.getString("permissionStat", "NotGranted");
        permissionStat = decrypt(permissionStat);

        if (isOnline()){
            if (Build.VERSION.SDK_INT >= 23) {
                if (permissionStat.equals("Granted")) {
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            /* Create an Intent that will start the Menu-Activity. */
                            Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                    }, SPLASH_DISPLAY_LENGTH);
                } else {
                    permissionManager = new PermissionManager() {};
                    permissionManager.checkAndRequestPermissions(this);
                }
            } else {
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        /* Create an Intent that will start the Menu-Activity. */
                        Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                }, SPLASH_DISPLAY_LENGTH);
            }
        } else {
            Toast.makeText(SplashScreen.this, "Your Internet Connection is Not Working, Try Again Later.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String encrypt(String input) {
        // This is base64 encoding, which is not an encryption
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String input) {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);

        ArrayList<String> granted = permissionManager.getStatus().get(0).granted;
        ArrayList<String> denied = permissionManager.getStatus().get(0).denied;

        for (String item:granted)
        {
            SharedPreferences.Editor editor = getSharedPreferences("myPref", MODE_PRIVATE).edit();
            editor.putString("permissionStat", encrypt("Granted"));
            editor.apply();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            //goNextPage();
        }

        for (String item:denied)
        {
            SharedPreferences.Editor editor = getSharedPreferences("myPref", MODE_PRIVATE).edit();
            editor.putString("permissionStat", encrypt("NotGranted"));
            editor.apply();
        }
    }
}
