package com.example.appgymsparta.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Inscripcion;
import com.example.appgymsparta.data.repository.InscripcionRepository;

import java.util.List;

public class InscripcionViewModel extends AndroidViewModel {

    private final InscripcionRepository inscripcionRepository;

    private final MutableLiveData<List<Inscripcion>> listaInscripcionesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Inscripcion> inscripcionLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public InscripcionViewModel(@NonNull Application application) {
        super(application);
        this.inscripcionRepository = new InscripcionRepository(application);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void insertar(Inscripcion inscripcion, InscripcionRepository.OnResultListener<String> listener) {
        inscripcionRepository.insertar(inscripcion, new InscripcionRepository.OnResultListener<String>() {
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

    public void actualizar(Inscripcion inscripcion, InscripcionRepository.OnResultListener<Boolean> listener) {
        inscripcionRepository.actualizar(inscripcion, new InscripcionRepository.OnResultListener<Boolean>() {
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

    public LiveData<List<Inscripcion>> obtenerTodas() {
        inscripcionRepository.obtenerTodas(new InscripcionRepository.OnResultListener<List<Inscripcion>>() {
            @Override
            public void onSuccess(List<Inscripcion> list) {
                listaInscripcionesLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaInscripcionesLiveData;
    }

    public LiveData<List<Inscripcion>> obtenerPorCliente(String idCliente) {
        inscripcionRepository.obtenerPorCliente(idCliente, new InscripcionRepository.OnResultListener<List<Inscripcion>>() {
            @Override
            public void onSuccess(List<Inscripcion> list) {
                listaInscripcionesLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaInscripcionesLiveData;
    }

    public LiveData<List<Inscripcion>> obtenerPorCliente(int idCliente) {
        return obtenerPorCliente(String.valueOf(idCliente));
    }

    public LiveData<Inscripcion> buscarPorId(String id) {
        inscripcionRepository.buscarPorId(id, new InscripcionRepository.OnResultListener<Inscripcion>() {
            @Override
            public void onSuccess(Inscripcion inscripcion) {
                inscripcionLiveData.setValue(inscripcion);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return inscripcionLiveData;
    }

    public LiveData<Inscripcion> buscarPorId(int id) {
        return buscarPorId(String.valueOf(id));
    }

    public LiveData<Integer> contarVencimientosProximos(String hoy, String hoyMas7) {
        return inscripcionRepository.contarVencimientosProximos(hoy, hoyMas7);
    }
}
