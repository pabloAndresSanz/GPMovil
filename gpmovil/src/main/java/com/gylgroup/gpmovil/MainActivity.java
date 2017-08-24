package com.gylgroup.gpmovil;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.gylgroup.gpmovil.adapters.SimpleItemAdapter;
import com.gylgroup.gpmovil.adapters.StringAdapter;
import com.gylgroup.gpmovil.model.DireccionLocal;
import com.gylgroup.gpmovil.model.Especialidad;
import com.gylgroup.gpmovil.model.Provincia;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.gylgroup.gpmovil.Utils.getDireccionesLocales;

public class MainActivity extends BaseActivity {
    final String UBICACION_ACTUAL="Ubicación actual";
    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    Spinner localeslv;
    Spinner provinciaslv;
    Spinner especialidadeslv;
    CheckBox sincoberturachk;
    String url;
    public boolean puedeAccederUbicacion() {
        return grantResults[0]==PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        permissions=new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
        grantResults=new int[] {PackageManager.PERMISSION_GRANTED};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupActionBar();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void dale() {
        try {
            getLastLocation();
            localeslv = (Spinner) findViewById(R.id.localeslv);
            provinciaslv = (Spinner) findViewById(R.id.provinciaslv);
            especialidadeslv = (Spinner) findViewById(R.id.especialidadeslv);
            sincoberturachk = (CheckBox) findViewById(R.id.sincoberturachk);
            Button submitBtn=(Button) findViewById(R.id.submit);
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submit_click();
                }
            });
            //Snackbar.make(provinciaslv,"url:"+url,Snackbar.LENGTH_INDEFINITE).show();
            List<String> locales=Collections.list(getDireccionesLocales(this).keys());
            if (puedeAccederUbicacion()) {
                locales.add(0,UBICACION_ACTUAL);
            }
            localeslv.setAdapter(new StringAdapter(this,locales));
            provinciaslv.setAdapter(new SimpleItemAdapter<Provincia>(MainActivity.this, DB.getProvincias()));
            especialidadeslv.setAdapter(new SimpleItemAdapter<Especialidad>(MainActivity.this, DB.getEspecialidades()));
        }
        catch (Exception e) {
            Snackbar.make(provinciaslv,e.getMessage(),Snackbar.LENGTH_INDEFINITE).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();


                        } else {
                            mLastLocation=new Location("");
                            mLastLocation.setLatitude(-34.606209);
                            mLastLocation.setLongitude(-58.435789);
                            Utils.error(MainActivity.this,"No se encontró ubicación actual");
                        }
                    }
                });
    }

    protected void submit_click() {
        Intent intent=new Intent(this,EspecialistasActivity.class);
        String selectedLocal=(String)localeslv.getSelectedItem();
        Location location;
        if (selectedLocal.equals(UBICACION_ACTUAL)) {
            location=mLastLocation;
        }
        else {
            DireccionLocal local = Utils.getDireccionesLocales(this).get(selectedLocal);
            location = new Location("");
            location.setLatitude(local.getAddress().getLatitude());
            location.setLongitude(local.getAddress().getLongitude());
        }
        intent.putExtra("local",location);
        intent.putExtra("provincia",provinciaslv.getSelectedItemId());
        intent.putExtra("especialidad",especialidadeslv.getSelectedItemId());
        intent.putExtra("sinCobertura",sincoberturachk.isChecked());
        startActivity(intent);
    }
}


