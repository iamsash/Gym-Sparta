package com.example.appgymsparta.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "inscripciones")
public class Inscripcion {

    @PrimaryKey(autoGenerate = true)
    private int idInscripcion; // Mantenido para Room y compatibilidad temporal

    @DocumentId
    private String id;

    private String idCliente;
    private String idMembresia;
    private String nombreCliente;
    private String dniCliente;
    private String nombreMembresia;

    private String fechaInicio;
    private String fechaVencimiento;
    private double precioPagado;
    private String estado;

    // Constructor vacío (requerido por Firestore y Room)
    public Inscripcion() {
    }

    // Constructor completo con IDs String (para Firestore)
    @Ignore
    public Inscripcion(String id, String idCliente, String idMembresia, String nombreCliente, String dniCliente, String nombreMembresia, String fechaInicio, String fechaVencimiento, double precioPagado, String estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.idMembresia = idMembresia;
        this.nombreCliente = nombreCliente;
        this.dniCliente = dniCliente;
        this.nombreMembresia = nombreMembresia;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.precioPagado = precioPagado;
        this.estado = estado;
    }

    // Constructor sin ID de documento (para nuevos registros en Firestore)
    @Ignore
    public Inscripcion(String idCliente, String idMembresia, String nombreCliente, String dniCliente, String nombreMembresia, LocalDate fechaInicio, LocalDate fechaVencimiento, double precioPagado, String estado) {
        this.idCliente = idCliente;
        this.idMembresia = idMembresia;
        this.nombreCliente = nombreCliente;
        this.dniCliente = dniCliente;
        this.nombreMembresia = nombreMembresia;
        if (fechaInicio != null) {
            this.fechaInicio = fechaInicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (fechaVencimiento != null) {
            this.fechaVencimiento = fechaVencimiento.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        this.precioPagado = precioPagado;
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
    public String getIdInscripcionString() {
        return id != null ? id : String.valueOf(idInscripcion);
    }

    public int getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(String idMembresia) {
        this.idMembresia = idMembresia;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public String getNombreMembresia() {
        return nombreMembresia;
    }

    public void setNombreMembresia(String nombreMembresia) {
        this.nombreMembresia = nombreMembresia;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
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

    @Exclude
    public LocalDate getFechaVencimientoLocalDate() {
        if (fechaVencimiento != null && !fechaVencimiento.isEmpty()) {
            try {
                if (fechaVencimiento.contains("/")) {
                    return LocalDate.parse(fechaVencimiento, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                return LocalDate.parse(fechaVencimiento, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                return LocalDate.now();
            }
        }
        return LocalDate.now();
    }

    public double getPrecioPagado() {
        return precioPagado;
    }

    public void setPrecioPagado(double precioPagado) {
        this.precioPagado = precioPagado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
