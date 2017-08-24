package com.gylgroup.gpmovil;

import com.gylgroup.gpmovil.model.Cobertura;
import com.gylgroup.gpmovil.model.Especialidad;
import com.gylgroup.gpmovil.model.Medico;
import com.gylgroup.gpmovil.model.Provincia;
import com.gylgroup.gpmovil.model.Turno;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Administrator on 5/8/2017.
 */

public interface GPAdimWS {
    @Headers({"Accept: application/json"})
    @GET("webresources/com.gylgroup.gp.provincia")
    Call<List<Provincia>> listProvincia();
    @Headers({"Accept: application/json"})
    @GET("webresources/com.gylgroup.gp.especialidad")
    Call<List<Especialidad>> listEspecialidad();
    @Headers({"Accept: application/json"})
    @GET("webresources/com.gylgroup.gp.cobertura")
    Call<List<Cobertura>> listCobertura();
    @Headers({"Accept: application/json"})
    @GET("webresources/com.gylgroup.gp.medico/byCoberturaProvinciaEspecialidad/{cobertura}/{provincia}/{especialidad}")
    Call<List<Medico>> listMedico(@Path("cobertura") String cobertura, @Path("provincia") Long provincia, @Path("especialidad") Long especialidad);
    @POST("webresources/com.gylgroup.gp.turno")
    Call<Turno> createTurno(@Body Turno turno);
}
