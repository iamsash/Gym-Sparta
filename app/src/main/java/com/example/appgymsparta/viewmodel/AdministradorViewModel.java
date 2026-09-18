package com.example.appgymsparta.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Administrador;
import com.example.appgymsparta.data.repository.AdministradorRepository;

import java.util.List;

public class AdministradorViewModel extends AndroidViewModel {

    private final AdministradorRepository administradorRepository;

    private final MutableLiveData<List<Administrador>> listaAdministradoresLiveData = new MutableLiveData<>();
    private final MutableLiveData<Administrador> administradorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public AdministradorViewModel(@NonNull Application application) {
        super(application);
        this.administradorRepository = new AdministradorRepository(application);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void actualizar(Administrador administrador, AdministradorRepository.OnResultListener<Boolean> listener) {
        administradorRepository.actualizar(administrador, new AdministradorRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (listener != null) listener.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
                if (listener != null) listener.onError(e);
            }
        });
    }

    public void cambiarPassword(String nuevaPassword, AdministradorRepository.OnResultListener<Boolean> listener) {
        administradorRepository.cambiarPassword(nuevaPassword, new AdministradorRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (listener != null) listener.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
                if (listener != null) listener.onError(e);
            }
        });
    }

    public LiveData<List<Administrador>> obtenerTodos() {
        administradorRepository.obtenerTodos(new AdministradorRepository.OnResultListener<List<Administrador>>() {
            @Override
            public void onSuccess(List<Administrador> list) {
                listaAdministradoresLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaAdministradoresLiveData;
    }

    public LiveData<Administrador> buscarPorId(String id) {
        administradorRepository.buscarPorId(id, new AdministradorRepository.OnResultListener<Administrador>() {
            @Override
            public void onSuccess(Administrador admin) {
                administradorLiveData.setValue(admin);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return administradorLiveData;
    }

    public LiveData<Administrador> buscarPorId(int id) {
        return buscarPorId(String.valueOf(id));
    }

    public LiveData<Integer> contarActivos() {
        return administradorRepository.contarActivos();
    }
}
