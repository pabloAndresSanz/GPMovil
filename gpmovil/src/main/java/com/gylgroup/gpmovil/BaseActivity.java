package com.gylgroup.gpmovil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    protected final int MY_PERMISSIONS_REQUEST =10;
    protected String[] permissions=new String[] {};
    int[] grantResults=new int[] {};

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    protected void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_direcciones) {
            Intent intent=new Intent(this,DireccionesActivity.class);
            startActivity(intent);
        }

        if (id == android.R.id.home) {
            /*Intent intent=new Intent(this,SignInActivity.class);
            startActivity(intent);*/
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean checkPermissions() {
        boolean faltaPermiso=false;
        for(String permission : permissions)  {
            if (checkSelfPermission(permission)!= PackageManager.PERMISSION_GRANTED) {
                faltaPermiso=true;
            }
        }
        if (faltaPermiso) {
            requestPermissions(permissions,MY_PERMISSIONS_REQUEST);
        }
        return !faltaPermiso;
    }

    protected void dale() {
        // que hace si están los permisos
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissions()) dale();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                this.grantResults=grantResults;
                dale();
                return;
            }
        }
    }
}
