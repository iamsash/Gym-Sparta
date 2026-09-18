package com.example.appgymsparta.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Inscripcion;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class InscripcionRepository {

    private static final String TAG = "InscripcionRepository";
    private static final String COLLECTION_INSCRIPCIONES = "inscripciones";
    private final CollectionReference inscripcionesRef;

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public InscripcionRepository(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.inscripcionesRef = db.collection(COLLECTION_INSCRIPCIONES);
    }

    // Contar vencimientos próximos para el Dashboard (Próximos 7 días) mediante LiveData reactivo
    public LiveData<Integer> contarVencimientosProximos(String hoy, String hoyMas7) {
        return new LiveData<Integer>() {
            private ListenerRegistration registration;

            @Override
            protected void onActive() {
                super.onActive();
                registration = inscripcionesRef
                        .whereGreaterThanOrEqualTo("fechaVencimiento", hoy)
                        .whereLessThanOrEqualTo("fechaVencimiento", hoyMas7)
                        .addSnapshotListener((snapshots, e) -> {
                            if (e != null) {
                                Log.e(TAG, "Error en SnapshotListener de contarVencimientosProximos", e);
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

    // Insertar nueva inscripción
    public void insertar(Inscripcion inscripcion, OnResultListener<String> listener) {
        inscripcionesRef.add(inscripcion)
                .addOnSuccessListener(documentReference -> {
                    String idGenerado = documentReference.getId();
                    inscripcion.setId(idGenerado);
                    if (listener != null) listener.onSuccess(idGenerado);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al insertar inscripción en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Actualizar inscripción
    public void actualizar(Inscripcion inscripcion, OnResultListener<Boolean> listener) {
        if (inscripcion.getId() == null || inscripcion.getId().isEmpty()) {
            Exception err = new IllegalArgumentException("ID de inscripción no válido para actualización");
            Log.e(TAG, err.getMessage());
            if (listener != null) listener.onError(err);
            return;
        }

        inscripcionesRef.document(inscripcion.getId()).set(inscripcion)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar inscripción en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener todas las inscripciones
    public void obtenerTodas(OnResultListener<List<Inscripcion>> listener) {
        inscripcionesRef.orderBy("fechaInicio", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Inscripcion> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Inscripcion i = doc.toObject(Inscripcion.class);
                        if (i != null) {
                            i.setId(doc.getId());
                            lista.add(i);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener todas las inscripciones", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener inscripciones por ID de cliente
    public void obtenerPorCliente(String idCliente, OnResultListener<List<Inscripcion>> listener) {
        if (idCliente == null || idCliente.isEmpty()) {
            if (listener != null) listener.onSuccess(new ArrayList<>());
            return;
        }

        inscripcionesRef.whereEqualTo("idCliente", idCliente)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Inscripcion> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Inscripcion i = doc.toObject(Inscripcion.class);
                        if (i != null) {
                            i.setId(doc.getId());
                            lista.add(i);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener inscripciones por cliente", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener inscripciones por cliente (Sobrecarga de compatibilidad)
    public void obtenerPorCliente(int idCliente, OnResultListener<List<Inscripcion>> listener) {
        obtenerPorCliente(String.valueOf(idCliente), listener);
    }

    // Buscar inscripción por ID String
    public void buscarPorId(String id, OnResultListener<Inscripcion> listener) {
        if (id == null || id.trim().isEmpty()) {
            if (listener != null) listener.onSuccess(null);
            return;
        }

        final String idLimpio = id.trim();
        inscripcionesRef.document(idLimpio).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Inscripcion i = doc.toObject(Inscripcion.class);
                        if (i != null) {
                            i.setId(doc.getId());
                        }
                        if (listener != null) listener.onSuccess(i);
                    } else {
                        if (listener != null) listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar inscripción por ID: " + idLimpio, e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Buscar inscripción por ID int (Sobrecarga de compatibilidad)
    public void buscarPorId(int id, OnResultListener<Inscripcion> listener) {
        buscarPorId(String.valueOf(id), listener);
    }
}
