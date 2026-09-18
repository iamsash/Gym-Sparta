package com.example.appgymsparta.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.appgymsparta.data.entity.Asistencia;

import java.util.List;

@Dao
public interface AsistenciaDao {

    @Insert
    long registrar(Asistencia asistencia);

    @Query("SELECT * FROM asistencias ORDER BY fecha DESC, horaIngreso DESC")
    List<Asistencia> obtenerTodas();

    @Query("SELECT * FROM asistencias WHERE idCliente = :idCliente ORDER BY fecha DESC, horaIngreso DESC")
    List<Asistencia> obtenerPorCliente(int idCliente);

    @Query("SELECT * FROM asistencias WHERE fecha = :fecha ORDER BY horaIngreso DESC")
    List<Asistencia> obtenerPorFecha(String fecha);

    @Query("SELECT * FROM asistencias WHERE fecha = :fecha ORDER BY horaIngreso DESC")
    List<Asistencia> obtenerAsistenciasDelDia(String fecha);

    @Query("SELECT * FROM asistencias WHERE idAsistencia = :id LIMIT 1")
    Asistencia buscarPorId(int id);

    @Query("SELECT COUNT(*) FROM asistencias WHERE idCliente = :idCliente AND fecha = :fecha")
    int contarPorClienteYFecha(int idCliente, String fecha);

    @Delete
    int eliminar(Asistencia asistencia);

    @Query("SELECT COUNT(*) FROM asistencias WHERE fecha = :fecha")
    LiveData<Integer> contarPorFecha(String fecha);
}
