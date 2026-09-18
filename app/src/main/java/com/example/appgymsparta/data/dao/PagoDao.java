package com.example.appgymsparta.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.appgymsparta.data.entity.Pago;

import java.util.List;

@Dao
public interface PagoDao {

    @Insert
    long insertar(Pago pago);

    @Query("SELECT * FROM pagos ORDER BY fechaPago DESC")
    List<Pago> obtenerTodos();

    @Query("SELECT * FROM pagos WHERE idInscripcion = :idInscripcion ORDER BY fechaPago DESC")
    List<Pago> obtenerPorInscripcion(int idInscripcion);

    @Query("SELECT p.* FROM pagos p INNER JOIN inscripciones i ON p.idInscripcion = i.idInscripcion WHERE i.idCliente = :idCliente ORDER BY p.fechaPago DESC")
    List<Pago> obtenerPorCliente(int idCliente);

    @Query("SELECT * FROM pagos WHERE fechaPago BETWEEN :fechaInicio AND :fechaFin ORDER BY fechaPago DESC")
    List<Pago> obtenerPagosPorFecha(String fechaInicio, String fechaFin);

    @Query("SELECT * FROM pagos WHERE idPago = :id LIMIT 1")
    Pago buscarPorId(int id);

    @Update
    int actualizar(Pago pago);

    @Query("SELECT COALESCE(SUM(monto), 0.0) FROM pagos WHERE substr(fechaPago, 1, 7) = :anioMes")
    LiveData<Double> sumarIngresosMes(String anioMes);
}
