package com.gylgroup.gpmovil.model;

import android.location.Address;

import com.gylgroup.gpmovil.Utils;

/**
 * Created by gyl on 27/6/2017.
 */

public class DireccionLocal  {

    private String id;
    private Address address;
    private String pisoDepto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPisoDepto() {
        return pisoDepto;
    }

    public void setPisoDepto(String pisoDepto) {
        this.pisoDepto = pisoDepto;
    }

    @Override
    public String toString() {
        return Utils.AddressToString(address) + System.getProperty("line.separator") + pisoDepto;
    }

}
