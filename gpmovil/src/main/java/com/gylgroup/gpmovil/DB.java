package com.gylgroup.gpmovil;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.gylgroup.gpmovil.model.Cobertura;
import com.gylgroup.gpmovil.model.Especialidad;
import com.gylgroup.gpmovil.model.Provincia;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gyl on 11/5/2017.
 */

public class DB {
    static final int PROVINCIAS=0;
    static final int ESPECIALIDADES=1;
    static final int COBERTURAS=2;
    static boolean[] estados=new boolean[3]; //false no terminado true terminado
    static SplashActivity splash;
    static boolean terminado=false;
    synchronized static void termine(int proceso) {
        termine(proceso,false);
    }
    synchronized static void termine(int proceso,boolean conerror) {
        if (!terminado) {
            estados[proceso] = true;
            if (conerror) {
                terminado = true;
                splash.seguirConSettings();
                return;
            }
            if (estados[0] && estados[1] && estados[2]) {
                splash.seguirConMain();
            }
        }

    }
    public static void init(SplashActivity activity) {
        try {
            terminado=false;
            estados=new boolean[]{false,false,false};
            splash=activity;
            splash.getSharedPreferences("GPMovil", Context.MODE_PRIVATE);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
            String url = sharedPref.getString("wsurl", "");
            Retrofit restAdapter = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            GPAdimWS service = restAdapter.create(GPAdimWS.class);
            Call<List<Provincia>> callProvincias = service.listProvincia();
            callProvincias.enqueue(new Callback<List<Provincia>>() {
                @Override
                public void onResponse(Call<List<Provincia>> call, Response<List<Provincia>> response) {
                    if (response.isSuccessful()) {
                        provincias = response.body();
                        termine(DB.PROVINCIAS);
                    } else {
                        // error response, no access to resource?
                        Log.d("Error", response.message());
                        termine(DB.PROVINCIAS,true);
                    }
                }

                @Override
                public void onFailure(Call<List<Provincia>> call, Throwable t) {
                    // something went completely south (like no internet connection)
                    termine(DB.PROVINCIAS,true);
                }
            });
            Call<List<Especialidad>> callEspecialidades = service.listEspecialidad();
            callEspecialidades.enqueue(new Callback<List<Especialidad>>() {
                @Override
                public void onResponse(Call<List<Especialidad>> call, Response<List<Especialidad>> response) {
                    if (response.isSuccessful()) {
                        especialidades = response.body();
                        termine(DB.ESPECIALIDADES);
                    } else {
                        // error response, no access to resource?
                        Log.d("Error", response.message());
                        termine(DB.ESPECIALIDADES,true);
                    }
                }

                @Override
                public void onFailure(Call<List<Especialidad>> call, Throwable t) {
                    // something went completely south (like no internet connection)

                    termine(DB.ESPECIALIDADES,true);
                }
            });
            Call<List<Cobertura>> callCoberturas = service.listCobertura();
            callCoberturas.enqueue(new Callback<List<Cobertura>>() {
                @Override
                public void onResponse(Call<List<Cobertura>> call, Response<List<Cobertura>> response) {
                    if (response.isSuccessful()) {
                        coberturas = response.body();
                        termine(DB.COBERTURAS);
                    } else {
                        // error response, no access to resource?
                        Log.d("Error", response.message());
                        termine(DB.COBERTURAS,true);
                    }
                }

                @Override
                public void onFailure(Call<List<Cobertura>> call, Throwable t) {
                    // something went completely south (like no internet connection)
                    termine(DB.COBERTURAS,true);
                }
            });
        }
        catch(Exception e) {
            Toast.makeText(activity,e.getMessage(),Toast.LENGTH_LONG).show();
            termine(0,true);
        }

    }
    private static List<Provincia> provincias;
    private static List<Especialidad> especialidades;

    public static List<Provincia> getProvincias() {
        return provincias;
    }

    public static List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public static List<Cobertura> getCoberturas() {
        return coberturas;
    }

    public static String getCoberturaDescripcion(String id) {
        return getCoberturaDescripcion(Integer.parseInt(id));
    }
    public static String getCoberturaDescripcion(int id) {
        for(Cobertura c: coberturas) {
            if (c.getId()==id) return c.getDescripcion();
        }
        return "";
    }

    private static List<Cobertura> coberturas;
}
