package com.example.appgymsparta.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.appgymsparta.data.entity.Cliente;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {

    private static final String TAG = "ClienteRepository";
    private static final String COLLECTION_CLIENTES = "clientes";
    private final CollectionReference clientesRef;

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public ClienteRepository(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.clientesRef = db.collection(COLLECTION_CLIENTES);
    }

    // Contar activos para el Dashboard mediante LiveData con gestión limpia de SnapshotListener (Evita memory leaks)
    public LiveData<Integer> contarActivos() {
        return new LiveData<Integer>() {
            private ListenerRegistration registration;

            @Override
            protected void onActive() {
                super.onActive();
                registration = clientesRef.whereEqualTo("estado", true)
                        .addSnapshotListener((snapshots, e) -> {
                            if (e != null) {
                                Log.e(TAG, "Error en SnapshotListener de contarActivos", e);
                                postValue(0);
                                return;
                            }
                            if (snapshots != null) {
                                postValue(snapshots.size());
                            } else {
                                postValue(0);
                            }
                        });
            }

            @Override
            protected void onInactive() {
                super.onInactive();
                if (registration != null) {
                    registration.remove();
                    registration = null;
                }
            }
        };
    }

    // Insertar un nuevo cliente de forma asíncrona
    public void insertar(Cliente cliente, OnResultListener<String> listener) {
        clientesRef.add(cliente)
                .addOnSuccessListener(documentReference -> {
                    String idGenerado = documentReference.getId();
                    cliente.setId(idGenerado);
                    if (listener != null) listener.onSuccess(idGenerado);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al insertar cliente en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Actualizar cliente existente en Firestore
    public void actualizar(Cliente cliente, OnResultListener<Boolean> listener) {
        if (cliente.getId() == null || cliente.getId().isEmpty()) {
            Exception err = new IllegalArgumentException("ID de cliente no asignado para actualización");
            Log.e(TAG, err.getMessage());
            if (listener != null) listener.onError(err);
            return;
        }

        clientesRef.document(cliente.getId()).set(cliente)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar cliente en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Eliminar cliente por ID en Firestore
    public void eliminar(Cliente cliente, OnResultListener<Boolean> listener) {
        if (cliente.getId() == null || cliente.getId().isEmpty()) {
            Exception err = new IllegalArgumentException("ID de cliente no asignado para eliminación");
            Log.e(TAG, err.getMessage());
            if (listener != null) listener.onError(err);
            return;
        }

        clientesRef.document(cliente.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al eliminar cliente en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener todos los clientes (Asíncrono con Callback)
    public void obtenerTodos(OnResultListener<List<Cliente>> listener) {
        clientesRef.orderBy("apellidos", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Cliente> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Cliente c = doc.toObject(Cliente.class);
                        if (c != null) {
                            c.setId(doc.getId());
                            lista.add(c);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener todos los clientes", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener solo clientes activos (Asíncrono con Callback)
    public void obtenerActivos(OnResultListener<List<Cliente>> listener) {
        clientesRef.whereEqualTo("estado", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Cliente> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Cliente c = doc.toObject(Cliente.class);
                        if (c != null) {
                            c.setId(doc.getId());
                            lista.add(c);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener clientes activos", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Buscar cliente por ID String (Con soporte para IDs de Firestore e IDs enteros legados)
    public void buscarPorId(String id, OnResultListener<Cliente> listener) {
        if (id == null || id.trim().isEmpty()) {
            if (listener != null) listener.onSuccess(null);
            return;
        }

        final String idLimpio = id.trim();

        // 1. Intentar buscar por ID de documento de Firestore
        clientesRef.document(idLimpio).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Cliente c = doc.toObject(Cliente.class);
                        if (c != null) {
                            c.setId(doc.getId());
                        }
                        if (listener != null) listener.onSuccess(c);
                    } else {
                        // 2. Fallback si el ID es numérico (módulos pendientes en Room)
                        try {
                            int idNum = Integer.parseInt(idLimpio);
                            clientesRef.whereEqualTo("idCliente", idNum).limit(1).get()
                                    .addOnSuccessListener(querySnapshots -> {
                                        if (!querySnapshots.isEmpty()) {
                                            DocumentSnapshot d = querySnapshots.getDocuments().get(0);
                                            Cliente c = d.toObject(Cliente.class);
                                            if (c != null) {
                                                c.setId(d.getId());
                                            }
                                            if (listener != null) listener.onSuccess(c);
                                        } else {
                                            if (listener != null) listener.onSuccess(null);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error en fallback por idCliente entero", e);
                                        if (listener != null) listener.onError(e);
                                    });
                        } catch (NumberFormatException nfe) {
                            if (listener != null) listener.onSuccess(null);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar cliente por ID: " + idLimpio, e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Sobrecarga de compatibilidad para buscar por ID entero
    public void buscarPorId(int id, OnResultListener<Cliente> listener) {
        buscarPorId(String.valueOf(id), listener);
    }

    // Buscar cliente por DNI
    public void buscarPorDni(String dni, OnResultListener<Cliente> listener) {
        if (dni == null || dni.trim().isEmpty()) {
            if (listener != null) listener.onSuccess(null);
            return;
        }

        clientesRef.whereEqualTo("dni", dni.trim()).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        Cliente c = doc.toObject(Cliente.class);
                        if (c != null) {
                            c.setId(doc.getId());
                        }
                        if (listener != null) listener.onSuccess(c);
                    } else {
                        if (listener != null) listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar por DNI", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Buscar clientes por coincidencia de nombre, apellido o DNI
    public void buscarPorNombre(String texto, OnResultListener<List<Cliente>> listener) {
        if (texto == null || texto.trim().isEmpty()) {
            obtenerTodos(listener);
            return;
        }

        String query = texto.trim().toLowerCase();
        obtenerTodos(new OnResultListener<List<Cliente>>() {
            @Override
            public void onSuccess(List<Cliente> result) {
                List<Cliente> filtrada = new ArrayList<>();
                for (Cliente c : result) {
                    String nom = c.getNombres() != null ? c.getNombres().toLowerCase() : "";
                    String ape = c.getApellidos() != null ? c.getApellidos().toLowerCase() : "";
                    String dni = c.getDni() != null ? c.getDni() : "";
                    String full = (nom + " " + ape).trim();

                    if (nom.contains(query) || ape.contains(query) || full.contains(query) || dni.contains(query)) {
                        filtrada.add(c);
                    }
                }
                if (listener != null) listener.onSuccess(filtrada);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error al filtrar por nombre", e);
                if (listener != null) listener.onError(e);
            }
        });
    }
}
