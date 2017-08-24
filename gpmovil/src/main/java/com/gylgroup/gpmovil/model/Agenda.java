package com.gylgroup.gpmovil.model;

import java.io.Serializable;

/**
 * Created by Administrator on 5/8/2017.
 */

public class Agenda implements Serializable {
    private int id;

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    private String agenda;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



}
