package com.gylgroup.gpmovil.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 5/8/2017.
 */

public class Direccion implements Serializable{
    private int id;
    private String descripcion;
    private String direccion;
    private Provincia idprovincia;
    private List<Telefono> telefonoCollection;
    private List<Agenda> agendaCollection;

    public List<Agenda> getAgendaCollection() {
        return agendaCollection;
    }

    public void setAgendaCollection(List<Agenda> agendaCollection) {
        this.agendaCollection = agendaCollection;
    }

    public List<Telefono> getTelefonoCollection() {
        return telefonoCollection;
    }

    public void setTelefonoCollection(List<Telefono> telefonoCollection) {
        this.telefonoCollection = telefonoCollection;
    }

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Provincia getIdprovincia() {
        return idprovincia;
    }

    public void setIdprovincia(Provincia idprovincia) {
        this.idprovincia = idprovincia;
    }
}
