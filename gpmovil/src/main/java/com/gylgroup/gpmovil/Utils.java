package com.gylgroup.gpmovil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gylgroup.gpmovil.model.Direccion;
import com.gylgroup.gpmovil.model.DireccionLocal;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import static android.R.attr.tag;
import static com.gylgroup.gpmovil.R.string.direccion;

/**
 * Created by gyl on 20/5/2017.
 */

public class Utils {
    public static Dictionary<String,DireccionLocal> getDireccionesLocales(Context activity) {
        Dictionary<String,DireccionLocal> direccionLocals=new Hashtable<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        for(String key : sharedPreferences.getAll().keySet()) {
            if (key.startsWith("dir_")) {
                String json = sharedPreferences.getString(key, null);
                direccionLocals.put(key.substring(4),json == null ? null : new Gson().fromJson(json, DireccionLocal.class));
            }
        }
        return direccionLocals;
    }
    public static void error (Context context,String mensaje) {
        Toast.makeText(context,mensaje,Toast.LENGTH_LONG).show();
    }
    public static String AddressToString(Address address) {

        ArrayList<String> addressFragments = new ArrayList<>();
        // Fetch the address lines using {@code getAddressLine},
        // join them, and send them to the thread. The {@link android.location.address}
        // class provides other options for fetching address details that you may prefer
        // to use. Here are some examples:
        // getLocality() ("Mountain View", for example)
        // getAdminArea() ("CA", for example)
        // getPostalCode() ("94043", for example)
        // getCountryCode() ("US", for example)
        // getCountryName() ("United States", for example)
        /* for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        } */
        addressFragments.add(address.getAddressLine(0));
        addressFragments.add(address.getLocality());
        return TextUtils.join(System.getProperty("line.separator"), addressFragments);
    }
    public static Location LocationFromDireccion(Direccion direccion) {
        String[] partes = direccion.getDireccion().split("[() ]");
        Location location=new Location("");
        location.setLatitude(Double.parseDouble(partes[1]));
        location.setLongitude(Double.parseDouble(partes[2]));
        return location;
    }
    public static String getWSURL(Context activity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPref.getString("wsurl", "");
    }
}
