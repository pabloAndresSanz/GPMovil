package com.gylgroup.gpmovil.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gylgroup.gpmovil.R;
import com.gylgroup.gpmovil.model.DireccionLocal;
import com.gylgroup.gpmovil.model.SimpleItem;

import java.util.List;

/**
 * Created by Administrator on 5/8/2017.
 */

public class DireccionLocalAdapter extends BaseAdapter {
    protected Activity activity;
    protected static LayoutInflater layoutInflater = null;
    protected List<DireccionLocal> lst;

    public DireccionLocalAdapter(Activity activity, List<DireccionLocal> lst){
        super();
        this.activity = activity;
        this.lst = lst;
        layoutInflater=LayoutInflater.from(activity);
        //layoutInflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return lst.size();
    }

    public Object getItem(int position) {
        return lst.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView id;
        public TextView descripcion;
    }

    public  View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.direccion_local, null);
            viewHolder = new ViewHolder();
            viewHolder.id = (TextView) convertView.findViewById(R.id.id);
            viewHolder.descripcion = (TextView) convertView.findViewById(R.id.descripcion);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.id.setText(lst.get(position).getId());
        viewHolder.descripcion.setText(lst.get(position).toString());
        return convertView;
    }

}

