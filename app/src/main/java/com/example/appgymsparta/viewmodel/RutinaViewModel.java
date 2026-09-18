package com.example.appgymsparta.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Rutina;
import com.example.appgymsparta.data.repository.RutinaRepository;

import java.util.List;

public class RutinaViewModel extends AndroidViewModel {

    private final RutinaRepository rutinaRepository;

    private final MutableLiveData<List<Rutina>> listaRutinasLiveData = new MutableLiveData<>();
    private final MutableLiveData<Rutina> rutinaLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public RutinaViewModel(@NonNull Application application) {
        super(application);
        this.rutinaRepository = new RutinaRepository(application);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void insertar(Rutina rutina, RutinaRepository.OnResultListener<String> listener) {
        rutinaRepository.insertar(rutina, new RutinaRepository.OnResultListener<String>() {
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

    public void actualizar(Rutina rutina, RutinaRepository.OnResultListener<Boolean> listener) {
        rutinaRepository.actualizar(rutina, new RutinaRepository.OnResultListener<Boolean>() {
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

    public LiveData<List<Rutina>> obtenerTodas() {
        rutinaRepository.obtenerTodas(new RutinaRepository.OnResultListener<List<Rutina>>() {
            @Override
            public void onSuccess(List<Rutina> list) {
                listaRutinasLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaRutinasLiveData;
    }

    public LiveData<List<Rutina>> obtenerPorCliente(String idCliente) {
        rutinaRepository.obtenerPorCliente(idCliente, new RutinaRepository.OnResultListener<List<Rutina>>() {
            @Override
            public void onSuccess(List<Rutina> list) {
                listaRutinasLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaRutinasLiveData;
    }

    public LiveData<List<Rutina>> obtenerPorCliente(int idCliente) {
        return obtenerPorCliente(String.valueOf(idCliente));
    }

    public LiveData<Rutina> buscarPorId(String id) {
        rutinaRepository.buscarPorId(id, new RutinaRepository.OnResultListener<Rutina>() {
            @Override
            public void onSuccess(Rutina rutina) {
                rutinaLiveData.setValue(rutina);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return rutinaLiveData;
    }

    public LiveData<Rutina> buscarPorId(int id) {
        return buscarPorId(String.valueOf(id));
    }
}
