package com.gylgroup.gpmovil.model;

import android.support.annotation.NonNull;

import com.google.api.services.calendar.model.TimePeriod;

/**
 * Created by gyl on 15/7/2017.
 */

public class PeriodoTiempo implements Comparable<PeriodoTiempo>{
    private TimePeriod timePeriod;
    private String agenda;

    @Override
    public int compareTo(@NonNull PeriodoTiempo periodoTiempo) {
        long dif=this.getTimePeriod().getStart().getValue()-periodoTiempo.getTimePeriod().getStart().getValue();
        if (dif>0) return 1;
        if (dif<0) return -1;
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        PeriodoTiempo periodoTiempo=(PeriodoTiempo) obj;
        return this.compareTo(periodoTiempo)==0;
    }

    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }
}
