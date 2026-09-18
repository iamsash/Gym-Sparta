package com.example.appgymsparta.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "clientes")
public class Cliente {

    @PrimaryKey(autoGenerate = true)
    private int idCliente; // Mantenido para Room y compatibilidad temporal con otras entidades

    @DocumentId
    private String id;

    private String dni;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String sexo;
    private String fechaRegistro;
    private boolean estado;

    // Constructor vacío (requerido por Firestore y Room)
    public Cliente() {
    }

    // Constructor completo con ID String (para Firestore)
    @Ignore
    public Cliente(String id, String dni, String nombres, String apellidos, String telefono, String sexo, String fechaRegistro, boolean estado) {
        this.id = id;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.sexo = sexo;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
    }

    // Constructor sin ID (para inserciones con ID autogenerado en Firestore)
    @Ignore
    public Cliente(String dni, String nombres, String apellidos, String telefono, String sexo, String fechaRegistro, boolean estado) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.sexo = sexo;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
    }

    // Constructor compatible con LocalDate (para registro)
    @Ignore
    public Cliente(String dni, String nombres, String apellidos, String telefono, String sexo, LocalDate fechaRegistro, boolean estado) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.sexo = sexo;
        if (fechaRegistro != null) {
            this.fechaRegistro = fechaRegistro.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else {
            this.fechaRegistro = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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
    public String getIdClienteString() {
        return id != null ? id : String.valueOf(idCliente);
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Exclude
    public LocalDate getFechaRegistroLocalDate() {
        if (fechaRegistro != null && !fechaRegistro.isEmpty()) {
            try {
                if (fechaRegistro.contains("/")) {
                    return LocalDate.parse(fechaRegistro, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                return LocalDate.parse(fechaRegistro, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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
