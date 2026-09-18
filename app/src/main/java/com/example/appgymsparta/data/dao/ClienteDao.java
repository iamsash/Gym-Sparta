package com.example.appgymsparta.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.appgymsparta.data.entity.Cliente;

import java.util.List;

@Dao
public interface ClienteDao {

    @Insert
    long insertar(Cliente cliente);

    @Query("SELECT * FROM clientes ORDER BY apellidos ASC, nombres ASC")
    List<Cliente> obtenerTodos();

    @Query("SELECT * FROM clientes WHERE dni = :dni LIMIT 1")
    Cliente buscarPorDni(String dni);

    @Query("SELECT * FROM clientes WHERE idCliente = :id LIMIT 1")
    Cliente buscarPorId(int id);

    @Update
    int actualizar(Cliente cliente);

    @Delete
    int eliminar(Cliente cliente);

    @Query("SELECT * FROM clientes WHERE estado = 1 ORDER BY apellidos ASC, nombres ASC")
    List<Cliente> obtenerActivos();

    @Query("SELECT * FROM clientes WHERE nombres LIKE '%' || :texto || '%' OR apellidos LIKE '%' || :texto || '%' OR (nombres || ' ' || apellidos) LIKE '%' || :texto || '%' ORDER BY apellidos ASC, nombres ASC")
    List<Cliente> buscarPorNombreOApellido(String texto);

    @Query("SELECT COUNT(*) FROM clientes WHERE estado = 1")
    LiveData<Integer> contarActivos();
}
