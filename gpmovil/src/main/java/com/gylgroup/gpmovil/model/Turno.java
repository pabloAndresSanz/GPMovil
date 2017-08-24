package com.gylgroup.gpmovil.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 5/8/2017.
 */

public class Turno implements Serializable {
    private int id;
    private Medico idmedico;
    private String paciente;
    private Date fechahora;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Medico getIdmedico() {
        return idmedico;
    }

    public void setIdmedico(Medico idmedico) {
        this.idmedico = idmedico;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public Date getFechahora() {
        return fechahora;
    }

    public void setFechahora(Date fechahora) {
        this.fechahora = fechahora;
    }
}
