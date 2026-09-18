package com.example.appgymsparta.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.appgymsparta.data.entity.Inscripcion;

import java.util.List;

@Dao
public interface InscripcionDao {

    @Insert
    long insertar(Inscripcion inscripcion);

    @Query("SELECT * FROM inscripciones ORDER BY idInscripcion DESC")
    List<Inscripcion> obtenerTodas();

    @Query("SELECT * FROM inscripciones WHERE idCliente = :idCliente ORDER BY fechaInicio DESC")
    List<Inscripcion> obtenerPorCliente(int idCliente);

    @Query("SELECT * FROM inscripciones WHERE idCliente = :idCliente AND (estado = 'ACTIVO' OR estado = 'Activo') ORDER BY fechaInicio DESC")
    List<Inscripcion> obtenerActivasPorCliente(int idCliente);

    @Query("SELECT * FROM inscripciones WHERE estado = 'VENCIDA' OR estado = 'Vencida' ORDER BY fechaVencimiento DESC")
    List<Inscripcion> obtenerVencidas();

    @Query("SELECT * FROM inscripciones WHERE idInscripcion = :id LIMIT 1")
    Inscripcion buscarPorId(int id);

    @Update
    int actualizar(Inscripcion inscripcion);

    @Query("SELECT COUNT(*) FROM inscripciones WHERE fechaVencimiento BETWEEN :hoy AND :hoyMas7")
    LiveData<Integer> contarVencimientosProximos(String hoy, String hoyMas7);
}
