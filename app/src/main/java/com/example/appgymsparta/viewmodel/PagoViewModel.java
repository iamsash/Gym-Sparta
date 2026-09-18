package com.example.appgymsparta.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Pago;
import com.example.appgymsparta.data.repository.PagoRepository;

import java.util.List;

public class PagoViewModel extends AndroidViewModel {

    private final PagoRepository pagoRepository;

    private final MutableLiveData<List<Pago>> listaPagosLiveData = new MutableLiveData<>();
    private final MutableLiveData<Pago> pagoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public PagoViewModel(@NonNull Application application) {
        super(application);
        this.pagoRepository = new PagoRepository(application);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void insertar(Pago pago, PagoRepository.OnResultListener<String> listener) {
        pagoRepository.insertar(pago, new PagoRepository.OnResultListener<String>() {
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

    public void actualizar(Pago pago, PagoRepository.OnResultListener<Boolean> listener) {
        pagoRepository.actualizar(pago, new PagoRepository.OnResultListener<Boolean>() {
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

    public LiveData<List<Pago>> obtenerTodos() {
        pagoRepository.obtenerTodos(new PagoRepository.OnResultListener<List<Pago>>() {
            @Override
            public void onSuccess(List<Pago> list) {
                listaPagosLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaPagosLiveData;
    }

    public LiveData<List<Pago>> obtenerPorInscripcion(String idInscripcion) {
        pagoRepository.obtenerPorInscripcion(idInscripcion, new PagoRepository.OnResultListener<List<Pago>>() {
            @Override
            public void onSuccess(List<Pago> list) {
                listaPagosLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaPagosLiveData;
    }

    public LiveData<Pago> buscarPorId(String id) {
        pagoRepository.buscarPorId(id, new PagoRepository.OnResultListener<Pago>() {
            @Override
            public void onSuccess(Pago pago) {
                pagoLiveData.setValue(pago);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return pagoLiveData;
    }

    public LiveData<Pago> buscarPorId(int id) {
        return buscarPorId(String.valueOf(id));
    }

    public LiveData<Double> sumarIngresosMes(String anioMes) {
        return pagoRepository.sumarIngresosMes(anioMes);
    }
}
