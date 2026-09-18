package com.example.appgymsparta.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "rutinas")
public class Rutina {

    @PrimaryKey(autoGenerate = true)
    private int idRutina; // Mantenido para Room y compatibilidad temporal

    @DocumentId
    private String id;

    private String idCliente;
    private String nombreCliente;
    private String nombre;
    private String objetivo;
    private String nivel;
    private String fechaInicio;
    private boolean estado;

    // Constructor vacío (requerido por Firestore y Room)
    public Rutina() {
    }

    // Constructor completo con IDs String (para Firestore)
    @Ignore
    public Rutina(String id, String idCliente, String nombreCliente, String nombre, String objetivo, String nivel, String fechaInicio, boolean estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.nombre = nombre;
        this.objetivo = objetivo;
        this.nivel = nivel;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
    }

    // Constructor para nuevos registros en Firestore
    @Ignore
    public Rutina(String idCliente, String nombreCliente, String nombre, String objetivo, String nivel, LocalDate fechaInicio, boolean estado) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.nombre = nombre;
        this.objetivo = objetivo;
        this.nivel = nivel;
        if (fechaInicio != null) {
            this.fechaInicio = fechaInicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else {
            this.fechaInicio = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
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
    public String getIdRutinaString() {
        return id != null ? id : String.valueOf(idRutina);
    }

    public int getIdRutina() {
        return idRutina;
    }

    public void setIdRutina(int idRutina) {
        this.idRutina = idRutina;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    @Exclude
    public LocalDate getFechaInicioLocalDate() {
        if (fechaInicio != null && !fechaInicio.isEmpty()) {
            try {
                if (fechaInicio.contains("/")) {
                    return LocalDate.parse(fechaInicio, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                return LocalDate.parse(fechaInicio, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                return LocalDate.now();
            }
        }
        return LocalDate.now();
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
