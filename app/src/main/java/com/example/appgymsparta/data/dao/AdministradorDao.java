package com.example.appgymsparta.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.appgymsparta.data.entity.Administrador;

import java.util.List;

@Dao
public interface AdministradorDao {

    @Insert
    long insertar(Administrador administrador);

    @Update
    int actualizar(Administrador administrador);

    @Delete
    int eliminar(Administrador administrador);

    @Query("SELECT * FROM administradores ORDER BY apellidos ASC, nombres ASC")
    List<Administrador> obtenerTodos();

    @Query("SELECT * FROM administradores WHERE estado = 1 ORDER BY apellidos ASC, nombres ASC")
    List<Administrador> obtenerActivos();

    @Query("SELECT * FROM administradores WHERE idAdministrador = :id LIMIT 1")
    Administrador buscarPorId(int id);

    @Query("SELECT * FROM administradores WHERE usuario = :usuario LIMIT 1")
    Administrador buscarPorUsuario(String usuario);

    @Query("SELECT * FROM administradores WHERE usuario = :usuario AND password = :password AND estado = 1 LIMIT 1")
    Administrador login(String usuario, String password);

    @Query("SELECT COUNT(*) FROM administradores WHERE estado = 1")
    LiveData<Integer> contarActivos();
}
