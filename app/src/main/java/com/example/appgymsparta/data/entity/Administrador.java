package com.example.appgymsparta.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "administradores")
public class Administrador {

    @PrimaryKey(autoGenerate = true)
    private int idAdministrador; // Mantenido para Room y compatibilidad temporal

    @DocumentId
    private String id; // UID de Firebase Auth

    private String nombres;
    private String apellidos;
    private String usuario;
    private String email;
    private String password;
    private String telefono;
    private boolean estado;
    private String fechaRegistro;

    // Constructor vacío (requerido por Firestore y Room)
    public Administrador() {
    }

    // Constructor completo para Firestore (con ID String)
    @Ignore
    public Administrador(String id, String nombres, String apellidos, String usuario, String email, String telefono, boolean estado, String fechaRegistro) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.email = email;
        this.telefono = telefono;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    // Constructor completo clásico para Room
    @Ignore
    public Administrador(int idAdministrador, String nombres, String apellidos, String usuario, String password, String telefono, boolean estado, LocalDate fechaRegistro) {
        this.idAdministrador = idAdministrador;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.password = password;
        this.telefono = telefono;
        this.estado = estado;
        if (fechaRegistro != null) {
            this.fechaRegistro = fechaRegistro.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    // Constructor sin ID (para inserciones)
    @Ignore
    public Administrador(String nombres, String apellidos, String usuario, String password, String telefono, boolean estado, LocalDate fechaRegistro) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.password = password;
        this.telefono = telefono;
        this.estado = estado;
        if (fechaRegistro != null) {
            this.fechaRegistro = fechaRegistro.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else {
            this.fechaRegistro = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public String getIdAdministradorString() {
        return id != null ? id : String.valueOf(idAdministrador);
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
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
}
