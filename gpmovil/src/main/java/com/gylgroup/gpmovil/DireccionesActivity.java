package com.gylgroup.gpmovil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.gylgroup.gpmovil.adapters.DireccionLocalAdapter;
import com.gylgroup.gpmovil.model.Direccion;
import com.gylgroup.gpmovil.model.DireccionLocal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DireccionesActivity extends BaseActivity {
    private ListView mDireccioLocalLV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direcciones);
        setupActionBar();
        mDireccioLocalLV=(ListView) findViewById(R.id.direccionLocalLV);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DireccionesActivity.this,DireccionActivity.class);
                intent.putExtra(Constants.ABM_MODE,Constants.ABM_ALTA);
                startActivity(intent);
            }
        });
        mDireccioLocalLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(DireccionesActivity.this,DireccionActivity.class);
                intent.putExtra(Constants.ABM_MODE,Constants.ABM_MODIFICACION);
                DireccionLocal local=(DireccionLocal) adapterView.getItemAtPosition(i);
                intent.putExtra(Constants.EDIT_ID,local.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDireccioLocalLV.setAdapter(new DireccionLocalAdapter(this, Collections.list(Utils.getDireccionesLocales(this).elements())));
    }
}
