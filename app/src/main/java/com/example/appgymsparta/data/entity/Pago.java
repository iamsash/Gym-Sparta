package com.example.appgymsparta.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "pagos")
public class Pago {

    @PrimaryKey(autoGenerate = true)
    private int idPago; // Mantenido para Room y compatibilidad temporal

    @DocumentId
    private String id;

    private String idInscripcion; // Nullable (null para Acceso diario)
    private String idCliente;
    private String nombreCliente;
    private String nombreMembresia;

    private String tipoPago; // "Membresía" o "Acceso diario"
    private double monto;
    private String fechaPago;
    private String metodoPago;
    private String numeroOperacion;
    private String observacion;

    // Constructor vacío (requerido por Firestore y Room)
    public Pago() {
    }

    // Constructor completo con IDs String (para Firestore)
    @Ignore
    public Pago(String id, String idInscripcion, String idCliente, String nombreCliente, String nombreMembresia, String tipoPago, double monto, String fechaPago, String metodoPago, String numeroOperacion, String observacion) {
        this.id = id;
        this.idInscripcion = idInscripcion;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.nombreMembresia = nombreMembresia;
        this.tipoPago = tipoPago;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.numeroOperacion = numeroOperacion;
        this.observacion = observacion;
    }

    // Constructor sin ID de documento (para nuevos pagos en Firestore)
    @Ignore
    public Pago(String idInscripcion, String idCliente, String nombreCliente, String nombreMembresia, String tipoPago, double monto, LocalDateTime fechaPago, String metodoPago, String numeroOperacion, String observacion) {
        this.idInscripcion = idInscripcion;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.nombreMembresia = nombreMembresia;
        this.tipoPago = tipoPago;
        this.monto = monto;
        if (fechaPago != null) {
            this.fechaPago = fechaPago.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } else {
            this.fechaPago = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        }
        this.metodoPago = metodoPago;
        this.numeroOperacion = numeroOperacion;
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
    public String getIdPagoString() {
        return id != null ? id : String.valueOf(idPago);
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public String getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(String idInscripcion) {
        this.idInscripcion = idInscripcion;
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

    public String getNombreMembresia() {
        return nombreMembresia;
    }

    public void setNombreMembresia(String nombreMembresia) {
        this.nombreMembresia = nombreMembresia;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    @Exclude
    public LocalDateTime getFechaPagoLocalDateTime() {
        if (fechaPago != null && !fechaPago.isEmpty()) {
            try {
                return LocalDateTime.parse(fechaPago, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            } catch (Exception e) {
                return LocalDateTime.now();
            }
        }
        return LocalDateTime.now();
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNumeroOperacion() {
        return numeroOperacion;
    }

    public void setNumeroOperacion(String numeroOperacion) {
        this.numeroOperacion = numeroOperacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
