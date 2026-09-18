package com.example.appgymsparta.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

@Entity(tableName = "membresias")
public class Membresia {

    @PrimaryKey(autoGenerate = true)
    private int idMembresia;

    @DocumentId
    private String id;

    private String nombre;
    private String descripcion;
    private int duracionDias;
    private double precio;
    private boolean estado;

    // Constructor vacío (requerido por Firestore y Room)
    public Membresia() {
    }

    // Constructor completo con ID String (para Firestore)
    @Ignore
    public Membresia(String id, String nombre, String descripcion, int duracionDias, double precio, boolean estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionDias = duracionDias;
        this.precio = precio;
        this.estado = estado;
    }

    // Constructor sin ID (para inserciones con ID autogenerado)
    @Ignore
    public Membresia(String nombre, String descripcion, int duracionDias, double precio, boolean estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionDias = duracionDias;
        this.precio = precio;
        this.estado = estado;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getIdMembresiaString() {
        return id != null ? id : String.valueOf(idMembresia);
    }

    public int getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(int idMembresia) {
        this.idMembresia = idMembresia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(int duracionDias) {
        this.duracionDias = duracionDias;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
