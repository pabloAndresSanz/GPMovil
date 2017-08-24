package com.gylgroup.gpmovil.model;

import java.io.Serializable;

/**
 * Created by Administrator on 5/8/2017.
 */

public class Provincia implements SimpleItem, Serializable {
    private int id;
    private String descripcion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
