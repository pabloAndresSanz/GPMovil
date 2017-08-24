package com.gylgroup.gpmovil.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gylgroup.gpmovil.R;
import com.gylgroup.gpmovil.model.SimpleItem;

import java.util.List;

/**
 * Created by Administrator on 5/8/2017.
 */

public class StringAdapter extends BaseAdapter {
    protected Activity activity;
    protected static LayoutInflater layoutInflater = null;
    protected List<String> lst;

    public StringAdapter(Activity activity, List<String> lst){
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
        return Long.valueOf(position);
    }

    public static class ViewHolder{
        public TextView descripcion;
    }

    public  View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item, null);
            viewHolder = new ViewHolder();
            viewHolder.descripcion = (TextView) convertView.findViewById(R.id.descripcion);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.descripcion.setText(lst.get(position));
        return convertView;
    }
}

