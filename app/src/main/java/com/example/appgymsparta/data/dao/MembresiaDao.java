package com.example.appgymsparta.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.appgymsparta.data.entity.Membresia;

import java.util.List;

@Dao
public interface MembresiaDao {

    @Insert
    long insertar(Membresia membresia);

    @Query("SELECT * FROM membresias ORDER BY nombre ASC")
    List<Membresia> obtenerTodas();

    @Query("SELECT * FROM membresias WHERE estado = 1 ORDER BY nombre ASC")
    List<Membresia> obtenerActivas();

    @Query("SELECT * FROM membresias WHERE idMembresia = :id LIMIT 1")
    Membresia buscarPorId(int id);

    @Update
    int actualizar(Membresia membresia);
}
