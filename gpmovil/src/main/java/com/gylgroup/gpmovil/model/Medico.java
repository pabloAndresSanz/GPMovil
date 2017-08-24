package com.gylgroup.gpmovil.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 5/8/2017.
 */

public class Medico implements SimpleItem, Serializable {
    private int id;
    private String nombre;
    private int turnoenmin;
    private Especialidad[] especialidadCollection;
    private Cobertura[] coberturaCollection;
    private List<Direccion> direccionCollection;

    public int getTurnoenmin() {
        return turnoenmin;
    }

    public void setTurnoenmin(int turnoenmin) {
        this.turnoenmin = turnoenmin;
    }

    public List<Direccion> getDireccionCollection() {
        return direccionCollection;
    }

    public void setDireccionCollection(List<Direccion> direccionCollection) {
        this.direccionCollection = direccionCollection;
    }

    public Especialidad[] getEspecialidadCollection() {
        return especialidadCollection;
    }

    public void setEspecialidadCollection(Especialidad[] especialidadCollection) {
        this.especialidadCollection = especialidadCollection;
    }

    public Cobertura[] getCoberturaCollection() {
        return coberturaCollection;
    }

    public void setCoberturaCollection(Cobertura[] coberturaCollection) {
        this.coberturaCollection = coberturaCollection;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return getNombre();
    }
}
