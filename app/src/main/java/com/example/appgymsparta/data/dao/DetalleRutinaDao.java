package com.example.appgymsparta.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.appgymsparta.data.entity.DetalleRutina;

import java.util.List;

@Dao
public interface DetalleRutinaDao {

    @Insert
    long insertar(DetalleRutina detalle);

    @Query("SELECT * FROM detalle_rutinas WHERE idRutina = :idRutina")
    List<DetalleRutina> obtenerPorRutina(int idRutina);

    @Query("SELECT * FROM detalle_rutinas WHERE idDetalle = :id LIMIT 1")
    DetalleRutina buscarPorId(int id);

    @Update
    int actualizar(DetalleRutina detalle);

    @Delete
    int eliminar(DetalleRutina detalle);
}
