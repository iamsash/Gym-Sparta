package com.example.appgymsparta.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Administrador;
import com.example.appgymsparta.data.repository.AdministradorRepository;

import java.util.List;
import java.util.concurrent.Executors;

public class AdministradorViewModel extends AndroidViewModel {

    private final AdministradorRepository administradorRepository;

    private final MutableLiveData<List<Administrador>> listaAdministradoresLiveData = new MutableLiveData<>();
    private final MutableLiveData<Administrador> administradorLiveData = new MutableLiveData<>();

    public AdministradorViewModel(@NonNull Application application) {
        super(application);
        this.administradorRepository = new AdministradorRepository(application);
    }

    // Operaciones de escritura
    public long insertar(Administrador administrador) {
        return administradorRepository.insertar(administrador);
    }

    public int actualizar(Administrador administrador) {
        return administradorRepository.actualizar(administrador);
    }

    public int eliminar(Administrador administrador) {
        return administradorRepository.eliminar(administrador);
    }

    // Operaciones de lectura asíncronas
    public LiveData<List<Administrador>> obtenerTodos() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Administrador> list = administradorRepository.obtenerTodos();
            listaAdministradoresLiveData.postValue(list);
        });
        return listaAdministradoresLiveData;
    }

    public LiveData<List<Administrador>> obtenerActivos() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Administrador> list = administradorRepository.obtenerActivos();
            listaAdministradoresLiveData.postValue(list);
        });
        return listaAdministradoresLiveData;
    }

    public LiveData<Administrador> buscarPorId(int id) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Administrador admin = administradorRepository.buscarPorId(id);
            administradorLiveData.postValue(admin);
        });
        return administradorLiveData;
    }

    public LiveData<Administrador> buscarPorUsuario(String usuario) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Administrador admin = administradorRepository.buscarPorUsuario(usuario);
            administradorLiveData.postValue(admin);
        });
        return administradorLiveData;
    }

    public LiveData<Administrador> login(String usuario, String password) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Administrador admin = administradorRepository.login(usuario, password);
            administradorLiveData.postValue(admin);
        });
        return administradorLiveData;
    }

    public LiveData<Integer> contarActivos() {
        return administradorRepository.contarActivos();
    }
}
