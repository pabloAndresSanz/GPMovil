package com.gylgroup.gpmovil.adapters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gylgroup.gpmovil.EspecialistasActivity;
import com.gylgroup.gpmovil.R;
import com.gylgroup.gpmovil.TurnosActivity;
import com.gylgroup.gpmovil.Utils;
import com.gylgroup.gpmovil.model.Cobertura;
import com.gylgroup.gpmovil.model.Direccion;
import com.gylgroup.gpmovil.model.Medico;
import com.gylgroup.gpmovil.model.Telefono;

import java.util.List;

import static android.R.attr.tag;
import static android.media.CamcorderProfile.get;

/**
 * Created by Administrator on 5/8/2017.
 */

public class MedicoAdapter extends BaseAdapter {
    protected EspecialistasActivity activity;
    protected static LayoutInflater layoutInflater = null;
    protected List<Medico> lst;
    private int cobertura;
    private Location locationLocal;

    public MedicoAdapter(EspecialistasActivity activity, List<Medico> lst,Location locationLocal) {
        super();
        this.activity = activity;
        this.lst = lst;
        this.locationLocal=locationLocal;
        layoutInflater = LayoutInflater.from(activity);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        this.cobertura = Integer.parseInt(sharedPref.getString("cobertura", "0"));

    }
    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public Object getItem(int position) {
        return lst.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_especialista, null);
        TextView nombre = (TextView) convertView.findViewById(R.id.nombre);
        boolean cubre=false;
        for(Cobertura c:lst.get(position).getCoberturaCollection()) {
            if(c.getId()==cobertura) {
                cubre=true;
                break;
            }
        }
        nombre.setText(lst.get(position).getNombre()+(!cubre?"\nSIN COBERTURA": ""));
        LinearLayout medicodirecionlv = (LinearLayout) convertView.findViewById(R.id.medicodireccionlv);
        for(Direccion direccion : lst.get(position).getDireccionCollection()) {
            View md = layoutInflater.inflate(R.layout.item_medicodireccion, null);
            ImageView direccioniv = (ImageView) md.findViewById(R.id.direccionDireccion);
            ImageView turnosiv = (ImageView) md.findViewById(R.id.turnos);
            TextView descripciontv = (TextView) md.findViewById(R.id.direccionDescripcion);
            TextView distanciatv = (TextView) md.findViewById(R.id.distancia);
            LinearLayout telefonoslv = (LinearLayout) md.findViewById(R.id.telefonos);
            Location location= Utils.LocationFromDireccion(direccion);
            distanciatv.setText(String.format("%d m",Math.round(location.distanceTo(locationLocal))));
            descripciontv.setText(direccion.getDescripcion());
            direccioniv.setTag(direccion);
            direccioniv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Direccion direccion=(Direccion) v.getTag();
                    Location loc= Utils.LocationFromDireccion(direccion);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(String.format("geo:%s,%s?q=%s", loc.getLatitude(), loc.getLongitude(), direccion.getDescripcion())));
                    activity.startActivity(intent);
                }
            });
            turnosiv.setTag(new Object[] {direccion,lst.get(position)});
            turnosiv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, TurnosActivity.class);
                    Object[] params=(Object[]) v.getTag();
                    Direccion md=(Direccion) params[0];
                    Medico m=(Medico) params[1];
                    intent.putExtra("direccion", md);
                    intent.putExtra("medico", m);
                    activity.startActivity(intent);
                }
            });
            for (Telefono telefono : direccion.getTelefonoCollection()) {
                LinearLayout telefonoLayout = new LinearLayout(activity);
                telefonoLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView telefonoDescripcion = new TextView(activity);
                telefonoDescripcion.setText(telefono.getTelefono());
                telefonoDescripcion.setTextSize(20);
                ImageView telefonoiv = new ImageView(activity);
                telefonoiv.setImageResource(R.drawable.celular32);
                telefonoiv.setTag(telefono.getTelefono());
                if (activity.puedeLlamar()) {
                    telefonoiv.setOnClickListener(new View.OnClickListener() {
                        private String tag;
                        @Override
                        public void onClick(View v) {
                            tag = (String) v.getTag();
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse(String.format("tel:%s", tag)));
                            try {
                                activity.startActivity(callIntent);
                            } catch (Exception e) {
                                Log.d("Error", e.getMessage());
                            }
                        }
                    });
                }
                telefonoLayout.addView(telefonoiv);
                telefonoLayout.addView(telefonoDescripcion);
                telefonoslv.addView(telefonoLayout);
            }
            medicodirecionlv.addView(md);
        }
        return convertView;
    }

}

