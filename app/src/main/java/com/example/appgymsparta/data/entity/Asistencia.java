package com.example.appgymsparta.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "asistencias")
public class Asistencia {

    @PrimaryKey(autoGenerate = true)
    private int idAsistencia; // Mantenido para Room y compatibilidad temporal

    @DocumentId
    private String id;

    private String idCliente;
    private String nombreCliente;
    private String fecha;
    private String horaIngreso;
    private String horaSalida;
    private String observacion;

    // Constructor vacío (requerido por Firestore y Room)
    public Asistencia() {
    }

    // Constructor completo con IDs String (para Firestore)
    @Ignore
    public Asistencia(String id, String idCliente, String nombreCliente, String fecha, String horaIngreso, String horaSalida, String observacion) {
        this.id = id;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.fecha = fecha;
        this.horaIngreso = horaIngreso;
        this.horaSalida = horaSalida;
        this.observacion = observacion;
    }

    // Constructor con objetos Java Time (para registro en Firestore)
    @Ignore
    public Asistencia(String idCliente, String nombreCliente, LocalDate fecha, LocalTime horaIngreso, LocalTime horaSalida, String observacion) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        if (fecha != null) {
            this.fecha = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else {
            this.fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (horaIngreso != null) {
            this.horaIngreso = horaIngreso.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        if (horaSalida != null) {
            this.horaSalida = horaSalida.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        this.observacion = observacion;
    }

    // Constructor legacy para compatibilidad con int idCliente
    @Ignore
    public Asistencia(int idCliente, LocalDate fecha, LocalTime horaIngreso, LocalTime horaSalida, String observacion) {
        this.idCliente = String.valueOf(idCliente);
        if (fecha != null) {
            this.fecha = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (horaIngreso != null) {
            this.horaIngreso = horaIngreso.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        if (horaSalida != null) {
            this.horaSalida = horaSalida.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
        this.observacion = observacion;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getIdAsistenciaString() {
        return id != null ? id : String.valueOf(idAsistencia);
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Exclude
    public LocalDate getFechaLocalDate() {
        if (fecha != null && !fecha.isEmpty()) {
            try {
                if (fecha.contains("/")) {
                    return LocalDate.parse(fecha, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                return LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                return LocalDate.now();
            }
        }
        return LocalDate.now();
    }

    public String getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(String horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    @Exclude
    public LocalTime getHoraIngresoLocalTime() {
        if (horaIngreso != null && !horaIngreso.isEmpty()) {
            try {
                return LocalTime.parse(horaIngreso, DateTimeFormatter.ofPattern("HH:mm:ss"));
            } catch (Exception e) {
                return LocalTime.now();
            }
        }
        return LocalTime.now();
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
