package com.example.appgymsparta.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Cliente;
import com.example.appgymsparta.data.repository.ClienteRepository;

import java.util.List;

public class ClienteViewModel extends AndroidViewModel {

    private final ClienteRepository clienteRepository;

    private final MutableLiveData<List<Cliente>> listaClientesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Cliente> clienteLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> operacionResultadoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public ClienteViewModel(@NonNull Application application) {
        super(application);
        this.clienteRepository = new ClienteRepository(application);
    }

    public LiveData<String> getOperacionResultadoLiveData() {
        return operacionResultadoLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Contar miembros activos para el Dashboard
    public LiveData<Integer> contarActivos() {
        return clienteRepository.contarActivos();
    }

    // Insertar cliente
    public void insertar(Cliente cliente, ClienteRepository.OnResultListener<String> listener) {
        clienteRepository.insertar(cliente, new ClienteRepository.OnResultListener<String>() {
            @Override
            public void onSuccess(String idGenerado) {
                operacionResultadoLiveData.setValue(idGenerado);
                if (listener != null) listener.onSuccess(idGenerado);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
                if (listener != null) listener.onError(e);
            }
        });
    }

    // Actualizar cliente
    public void actualizar(Cliente cliente, ClienteRepository.OnResultListener<Boolean> listener) {
        clienteRepository.actualizar(cliente, new ClienteRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                operacionResultadoLiveData.setValue("UPDATED");
                if (listener != null) listener.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
                if (listener != null) listener.onError(e);
            }
        });
    }

    // Eliminar cliente
    public void eliminar(Cliente cliente, ClienteRepository.OnResultListener<Boolean> listener) {
        clienteRepository.eliminar(cliente, new ClienteRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                operacionResultadoLiveData.setValue("DELETED");
                if (listener != null) listener.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
                if (listener != null) listener.onError(e);
            }
        });
    }

    // Obtener todos los clientes
    public LiveData<List<Cliente>> obtenerTodos() {
        clienteRepository.obtenerTodos(new ClienteRepository.OnResultListener<List<Cliente>>() {
            @Override
            public void onSuccess(List<Cliente> list) {
                listaClientesLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaClientesLiveData;
    }

    // Obtener activos
    public LiveData<List<Cliente>> obtenerActivos() {
        clienteRepository.obtenerActivos(new ClienteRepository.OnResultListener<List<Cliente>>() {
            @Override
            public void onSuccess(List<Cliente> list) {
                listaClientesLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaClientesLiveData;
    }

    // Buscar por nombre o filtro
    public LiveData<List<Cliente>> buscarPorNombre(String texto) {
        clienteRepository.buscarPorNombre(texto, new ClienteRepository.OnResultListener<List<Cliente>>() {
            @Override
            public void onSuccess(List<Cliente> list) {
                listaClientesLiveData.setValue(list);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return listaClientesLiveData;
    }

    // Buscar por DNI
    public LiveData<Cliente> buscarPorDni(String dni) {
        clienteRepository.buscarPorDni(dni, new ClienteRepository.OnResultListener<Cliente>() {
            @Override
            public void onSuccess(Cliente cliente) {
                clienteLiveData.setValue(cliente);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return clienteLiveData;
    }

    // Buscar por ID de documento String
    public LiveData<Cliente> buscarPorId(String id) {
        clienteRepository.buscarPorId(id, new ClienteRepository.OnResultListener<Cliente>() {
            @Override
            public void onSuccess(Cliente cliente) {
                clienteLiveData.setValue(cliente);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
        return clienteLiveData;
    }

    // Sobrecarga de compatibilidad para buscar por ID int
    public LiveData<Cliente> buscarPorId(int id) {
        return buscarPorId(String.valueOf(id));
    }
}
