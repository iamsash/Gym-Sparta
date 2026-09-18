package com.example.appgymsparta.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Membresia;
import com.example.appgymsparta.data.repository.MembresiaRepository;

import java.util.List;

public class MembresiaViewModel extends AndroidViewModel {

    private final MembresiaRepository membresiaRepository;

    private final MutableLiveData<List<Membresia>> listaMembresiasLiveData = new MutableLiveData<>();
    private final MutableLiveData<Membresia> membresiaLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public MembresiaViewModel(@NonNull Application application) {
        super(application);
        this.membresiaRepository = new MembresiaRepository(application);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void insertar(Membresia membresia, MembresiaRepository.OnResultListener<String> listener) {
        membresiaRepository.insertar(membresia, new MembresiaRepository.OnResultListener<String>() {
            @Override
            public void onSuccess(String idGenerado) {
                if (listener != null) listener.onSuccess(idGenerado);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
                if (listener != null) listener.onError(e);
            }
        });
    }

    public void actualizar(Membresia membresia, MembresiaRepository.OnResultListener<Boolean> listener) {
        membresiaRepository.actualizar(membresia, new MembresiaRepository.OnResultListener<Boolean>() {
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

    public LiveData<List<Membresia>> obtenerTodas() {
        membresiaRepository.obtenerTodas(new MembresiaRepository.OnResultListener<List<Membresia>>() {
            @Override
            public void onSuccess(List<Membresia> list) {
                listaMembresiasLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaMembresiasLiveData;
    }

    public LiveData<List<Membresia>> obtenerActivas() {
        membresiaRepository.obtenerActivas(new MembresiaRepository.OnResultListener<List<Membresia>>() {
            @Override
            public void onSuccess(List<Membresia> list) {
                listaMembresiasLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaMembresiasLiveData;
    }

    public LiveData<Membresia> buscarPorId(String id) {
        membresiaRepository.buscarPorId(id, new MembresiaRepository.OnResultListener<Membresia>() {
            @Override
            public void onSuccess(Membresia membresia) {
                membresiaLiveData.setValue(membresia);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return membresiaLiveData;
    }

    public LiveData<Membresia> buscarPorId(int id) {
        return buscarPorId(String.valueOf(id));
    }
}
