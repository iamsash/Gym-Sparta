package com.example.appgymsparta.data.entity;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

public class DetalleRutina {

    private int idDetalle;

    @DocumentId
    private String id;

    private String idRutina;
    private String ejercicio;
    private String grupoMuscular;
    private String dia;
    private int series;
    private int repeticiones;
    private double peso;
    private int descanso;

    // Constructor vacío (requerido por Firestore)
    public DetalleRutina() {
    }

    // Constructor completo con IDs String (para Firestore)
    public DetalleRutina(String id, String idRutina, String ejercicio, String grupoMuscular, String dia, int series, int repeticiones, double peso, int descanso) {
        this.id = id;
        this.idRutina = idRutina;
        this.ejercicio = ejercicio;
        this.grupoMuscular = grupoMuscular;
        this.dia = dia;
        this.series = series;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.descanso = descanso;
    }

    // Constructor para nuevos ejercicios en Firestore
    public DetalleRutina(String idRutina, String ejercicio, String grupoMuscular, String dia, int series, int repeticiones, double peso, int descanso) {
        this.idRutina = idRutina;
        this.ejercicio = ejercicio;
        this.grupoMuscular = grupoMuscular;
        this.dia = dia;
        this.series = series;
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.descanso = descanso;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getIdDetalleString() {
        return id != null ? id : String.valueOf(idDetalle);
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getIdRutina() {
        return idRutina;
    }

    public void setIdRutina(String idRutina) {
        this.idRutina = idRutina;
    }

    public String getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(String ejercicio) {
        this.ejercicio = ejercicio;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getDescanso() {
        return descanso;
    }

    public void setDescanso(int descanso) {
        this.descanso = descanso;
    }
}
