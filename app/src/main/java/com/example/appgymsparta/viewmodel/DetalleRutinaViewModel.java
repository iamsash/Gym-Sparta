package com.example.appgymsparta.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.DetalleRutina;
import com.example.appgymsparta.data.repository.DetalleRutinaRepository;

import java.util.List;

public class DetalleRutinaViewModel extends AndroidViewModel {

    private final DetalleRutinaRepository detalleRutinaRepository;

    private final MutableLiveData<List<DetalleRutina>> listaDetalleRutinaLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public DetalleRutinaViewModel(@NonNull Application application) {
        super(application);
        this.detalleRutinaRepository = new DetalleRutinaRepository(application);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void insertar(DetalleRutina detalle, DetalleRutinaRepository.OnResultListener<String> listener) {
        detalleRutinaRepository.insertar(detalle, new DetalleRutinaRepository.OnResultListener<String>() {
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

    public void actualizar(DetalleRutina detalle, DetalleRutinaRepository.OnResultListener<Boolean> listener) {
        detalleRutinaRepository.actualizar(detalle, new DetalleRutinaRepository.OnResultListener<Boolean>() {
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

    public void eliminar(DetalleRutina detalle, DetalleRutinaRepository.OnResultListener<Boolean> listener) {
        detalleRutinaRepository.eliminar(detalle, new DetalleRutinaRepository.OnResultListener<Boolean>() {
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

    public LiveData<List<DetalleRutina>> obtenerPorRutina(String idRutina) {
        detalleRutinaRepository.obtenerPorRutina(idRutina, new DetalleRutinaRepository.OnResultListener<List<DetalleRutina>>() {
            @Override
            public void onSuccess(List<DetalleRutina> list) {
                listaDetalleRutinaLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaDetalleRutinaLiveData;
    }

    public LiveData<List<DetalleRutina>> obtenerPorRutina(int idRutina) {
        return obtenerPorRutina(String.valueOf(idRutina));
    }
}
