package com.example.appgymsparta.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Asistencia;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AsistenciaRepository {

    private static final String TAG = "AsistenciaRepository";
    private static final String COLLECTION_ASISTENCIAS = "asistencias";
    private final CollectionReference asistenciasRef;

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public AsistenciaRepository(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.asistenciasRef = db.collection(COLLECTION_ASISTENCIAS);
    }

    // Contar asistencias de hoy para el Dashboard mediante SnapshotListener en tiempo real
    public LiveData<Integer> contarPorFecha(String fecha) {
        return new LiveData<Integer>() {
            private ListenerRegistration registration;

            @Override
            protected void onActive() {
                super.onActive();
                registration = asistenciasRef.whereEqualTo("fecha", fecha)
                        .addSnapshotListener((snapshots, e) -> {
                            if (e != null) {
                                Log.e(TAG, "Error en SnapshotListener de contarPorFecha", e);
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

    // Registrar nueva asistencia
    public void registrar(Asistencia asistencia, OnResultListener<String> listener) {
        asistenciasRef.add(asistencia)
                .addOnSuccessListener(documentReference -> {
                    String idGenerado = documentReference.getId();
                    asistencia.setId(idGenerado);
                    if (listener != null) listener.onSuccess(idGenerado);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al registrar asistencia en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener asistencias por fecha
    public void obtenerPorFecha(String fecha, OnResultListener<List<Asistencia>> listener) {
        if (fecha == null || fecha.isEmpty()) {
            if (listener != null) listener.onSuccess(new ArrayList<>());
            return;
        }

        asistenciasRef.whereEqualTo("fecha", fecha)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Asistencia> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Asistencia a = doc.toObject(Asistencia.class);
                        if (a != null) {
                            a.setId(doc.getId());
                            lista.add(a);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener asistencias por fecha", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Contar asistencias por cliente y fecha (para verificar duplicados)
    public void contarPorClienteYFecha(String idCliente, String fecha, OnResultListener<Integer> listener) {
        if (idCliente == null || idCliente.isEmpty()) {
            if (listener != null) listener.onSuccess(0);
            return;
        }

        asistenciasRef.whereEqualTo("idCliente", idCliente)
                .whereEqualTo("fecha", fecha)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots != null ? queryDocumentSnapshots.size() : 0;
                    if (listener != null) listener.onSuccess(count);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al contar asistencias por cliente y fecha", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Eliminar asistencia
    public void eliminar(Asistencia asistencia, OnResultListener<Boolean> listener) {
        if (asistencia.getId() == null || asistencia.getId().isEmpty()) {
            if (listener != null) listener.onError(new IllegalArgumentException("ID de asistencia no válido"));
            return;
        }

        asistenciasRef.document(asistencia.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al eliminar asistencia", e);
                    if (listener != null) listener.onError(e);
                });
    }
}
