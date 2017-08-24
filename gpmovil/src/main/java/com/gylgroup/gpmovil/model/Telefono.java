package com.gylgroup.gpmovil.model;

import java.io.Serializable;

/**
 * Created by Administrator on 5/9/2017.
 */

public class Telefono implements Serializable{
    private int id;
    private String telefono;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
