package com.gylgroup.gpmovil;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

/**
 * Created by gyl on 27/6/2017.
 */

public class DireccionesDialog extends DialogFragment {
    private ArrayList<Address> addresses;
    public DireccionesDialog() {

    }
    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] cadenas=new CharSequence[addresses.size()];
        for(int index=0;index<addresses.size();index++) {
            cadenas[index]=Utils.AddressToString(addresses.get(index));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccionar dirección")
                .setItems(cadenas, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Listener) getActivity()).onFinishDireccionesDialog(addresses.get(which));
                    }
                });
        return builder.create();
    }
    public interface Listener {
        void onFinishDireccionesDialog(Address address);
    }
}



