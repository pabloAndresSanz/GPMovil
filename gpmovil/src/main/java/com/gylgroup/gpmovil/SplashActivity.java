package com.gylgroup.gpmovil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            DB.init(this);
        }
        catch(Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    protected void seguirConMain() {
        Intent intent;
        intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    protected void seguirConSettings() {
        Toast.makeText(this,"Corregir URL",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


}
