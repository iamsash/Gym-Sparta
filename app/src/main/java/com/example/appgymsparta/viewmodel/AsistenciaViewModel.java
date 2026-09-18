package com.example.appgymsparta.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Asistencia;
import com.example.appgymsparta.data.repository.AsistenciaRepository;

import java.util.List;

public class AsistenciaViewModel extends AndroidViewModel {

    private final AsistenciaRepository asistenciaRepository;

    private final MutableLiveData<List<Asistencia>> listaAsistenciasLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public AsistenciaViewModel(@NonNull Application application) {
        super(application);
        this.asistenciaRepository = new AsistenciaRepository(application);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void registrar(Asistencia asistencia, AsistenciaRepository.OnResultListener<String> listener) {
        asistenciaRepository.registrar(asistencia, new AsistenciaRepository.OnResultListener<String>() {
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

    public void eliminar(Asistencia asistencia, AsistenciaRepository.OnResultListener<Boolean> listener) {
        asistenciaRepository.eliminar(asistencia, new AsistenciaRepository.OnResultListener<Boolean>() {
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

    public LiveData<List<Asistencia>> obtenerPorFecha(String fecha) {
        asistenciaRepository.obtenerPorFecha(fecha, new AsistenciaRepository.OnResultListener<List<Asistencia>>() {
            @Override
            public void onSuccess(List<Asistencia> list) {
                listaAsistenciasLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaAsistenciasLiveData;
    }

    public void contarPorClienteYFecha(String idCliente, String fecha, AsistenciaRepository.OnResultListener<Integer> listener) {
        asistenciaRepository.contarPorClienteYFecha(idCliente, fecha, listener);
    }

    public LiveData<Integer> contarPorFecha(String fecha) {
        return asistenciaRepository.contarPorFecha(fecha);
    }
}
