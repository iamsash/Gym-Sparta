package com.example.appgymsparta.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.appgymsparta.data.entity.Rutina;

import java.util.List;

@Dao
public interface RutinaDao {

    @Insert
    long insertar(Rutina rutina);

    @Query("SELECT * FROM rutinas ORDER BY fechaInicio DESC")
    List<Rutina> obtenerTodas();

    @Query("SELECT * FROM rutinas WHERE idCliente = :idCliente ORDER BY fechaInicio DESC")
    List<Rutina> obtenerPorCliente(int idCliente);

    @Query("SELECT * FROM rutinas WHERE idCliente = :idCliente AND estado = 1 ORDER BY fechaInicio DESC")
    List<Rutina> obtenerActivasPorCliente(int idCliente);

    @Query("SELECT * FROM rutinas WHERE idRutina = :id LIMIT 1")
    Rutina buscarPorId(int id);

    @Update
    int actualizar(Rutina rutina);
}
