package com.example.appgymsparta.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.appgymsparta.data.dao.AdministradorDao;
import com.example.appgymsparta.data.database.AppDatabase;
import com.example.appgymsparta.data.entity.Administrador;

import java.util.List;

public class AdministradorRepository {

    private final AdministradorDao administradorDao;

    public AdministradorRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.administradorDao = db.administradorDao();
    }

    public long insertar(Administrador administrador) {
        return administradorDao.insertar(administrador);
    }

    public int actualizar(Administrador administrador) {
        return administradorDao.actualizar(administrador);
    }

    public int eliminar(Administrador administrador) {
        return administradorDao.eliminar(administrador);
    }

    public List<Administrador> obtenerTodos() {
        return administradorDao.obtenerTodos();
    }

    public List<Administrador> obtenerActivos() {
        return administradorDao.obtenerActivos();
    }

    public Administrador buscarPorId(int id) {
        return administradorDao.buscarPorId(id);
    }

    public Administrador buscarPorUsuario(String usuario) {
        return administradorDao.buscarPorUsuario(usuario);
    }

    public Administrador login(String usuario, String password) {
        return administradorDao.login(usuario, password);
    }

    public LiveData<Integer> contarActivos() {
        return administradorDao.contarActivos();
    }
}
