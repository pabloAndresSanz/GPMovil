package com.gylgroup.gpmovil;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.gylgroup.gpmovil.adapters.MedicoAdapter;
import com.gylgroup.gpmovil.model.Medico;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Build.VERSION_CODES.M;

public class EspecialistasActivity extends BaseActivity  {
    ListView especialistaslv;
    public boolean puedeLlamar=true;

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected final int MY_PERMISSIONS_REQUEST =10;
    int[] grantResults=new int[] {PackageManager.PERMISSION_GRANTED,PackageManager.PERMISSION_GRANTED};
    Location locationLocal;
    public boolean puedeLlamar() {
        return grantResults[0]==PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                this.grantResults=grantResults;

                /*
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    puedeLlamar=false;
                    Toast.makeText(this,"El usuario no habilitó el permiso de llamada", Toast.LENGTH_LONG);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
 */
                dale();
                return;
            }
        }
    }
    protected void dale() {
        try {
            showProgressDialog();
            locationLocal=(Location)getIntent().getParcelableExtra("local");
            Long provincia = getIntent().getLongExtra("provincia", 0);
            Long especialidad = getIntent().getLongExtra("especialidad", 0);
            Boolean sinCobertura = getIntent().getBooleanExtra("sinCobertura", false);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String coberturas = sharedPref.getString("cobertura", "1");
            if (sinCobertura) {
                coberturas += ",1";
            }
            especialistaslv = (ListView) findViewById(R.id.especialistaslv);
            Retrofit restAdapter = new Retrofit.Builder()
                    .baseUrl(sharedPref.getString("wsurl", ""))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            GPAdimWS service = restAdapter.create(GPAdimWS.class);
            Call<List<Medico>> callMedicos = service.listMedico(coberturas, provincia, especialidad);
            callMedicos.enqueue(new Callback<List<Medico>>() {
                @Override
                public void onResponse(Call<List<Medico>> call, Response<List<Medico>> response) {
                    hideProgressDialog();
                    if (response.isSuccessful()) {
                        especialistaslv.setAdapter(new MedicoAdapter(EspecialistasActivity.this, response.body(),locationLocal));

                    } else {
                        // error response, no access to resource?
                        Utils.error(EspecialistasActivity.this,"Respondio con error");
                    }
                }

                @Override
                public void onFailure(Call<List<Medico>> call, Throwable t) {
                    hideProgressDialog();
                    // something went completely south (like no internet connection)
                    Utils.error(EspecialistasActivity.this,"No se pudo conectar al server");
                }
            });
        }
        catch(Exception e) {
            hideProgressDialog();
            Utils.error(this,e.getMessage());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_especialistas);
        setupActionBar();
        permissions=new String[] {Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_FINE_LOCATION};
    }
}
