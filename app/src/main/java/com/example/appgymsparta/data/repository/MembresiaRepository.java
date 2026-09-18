package com.example.appgymsparta.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.appgymsparta.data.entity.Membresia;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MembresiaRepository {

    private static final String TAG = "MembresiaRepository";
    private static final String COLLECTION_MEMBRESIAS = "membresias";
    private final CollectionReference membresiasRef;

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public MembresiaRepository(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.membresiasRef = db.collection(COLLECTION_MEMBRESIAS);
    }

    // Insertar nueva membresía en Firestore
    public void insertar(Membresia membresia, OnResultListener<String> listener) {
        membresiasRef.add(membresia)
                .addOnSuccessListener(documentReference -> {
                    String idGenerado = documentReference.getId();
                    membresia.setId(idGenerado);
                    if (listener != null) listener.onSuccess(idGenerado);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al insertar membresía en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Actualizar membresía en Firestore
    public void actualizar(Membresia membresia, OnResultListener<Boolean> listener) {
        if (membresia.getId() == null || membresia.getId().isEmpty()) {
            Exception err = new IllegalArgumentException("ID de membresía no válido para actualización");
            Log.e(TAG, err.getMessage());
            if (listener != null) listener.onError(err);
            return;
        }

        membresiasRef.document(membresia.getId()).set(membresia)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar membresía en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener todas las membresías ordenadas por precio
    public void obtenerTodas(OnResultListener<List<Membresia>> listener) {
        membresiasRef.orderBy("precio", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Membresia> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Membresia m = doc.toObject(Membresia.class);
                        if (m != null) {
                            m.setId(doc.getId());
                            lista.add(m);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener todas las membresías", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener membresías activas
    public void obtenerActivas(OnResultListener<List<Membresia>> listener) {
        membresiasRef.whereEqualTo("estado", true).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Membresia> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Membresia m = doc.toObject(Membresia.class);
                        if (m != null) {
                            m.setId(doc.getId());
                            lista.add(m);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener membresías activas", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Buscar membresía por ID String
    public void buscarPorId(String id, OnResultListener<Membresia> listener) {
        if (id == null || id.trim().isEmpty()) {
            if (listener != null) listener.onSuccess(null);
            return;
        }

        final String idLimpio = id.trim();
        membresiasRef.document(idLimpio).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Membresia m = doc.toObject(Membresia.class);
                        if (m != null) {
                            m.setId(doc.getId());
                        }
                        if (listener != null) listener.onSuccess(m);
                    } else {
                        // Fallback por ID entero
                        try {
                            int idNum = Integer.parseInt(idLimpio);
                            membresiasRef.whereEqualTo("idMembresia", idNum).limit(1).get()
                                    .addOnSuccessListener(snapshots -> {
                                        if (!snapshots.isEmpty()) {
                                            DocumentSnapshot d = snapshots.getDocuments().get(0);
                                            Membresia m = d.toObject(Membresia.class);
                                            if (m != null) m.setId(d.getId());
                                            if (listener != null) listener.onSuccess(m);
                                        } else {
                                            if (listener != null) listener.onSuccess(null);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (listener != null) listener.onError(e);
                                    });
                        } catch (NumberFormatException nfe) {
                            if (listener != null) listener.onSuccess(null);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar membresía por ID: " + idLimpio, e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Sobrecarga de compatibilidad para buscar por ID entero
    public void buscarPorId(int id, OnResultListener<Membresia> listener) {
        buscarPorId(String.valueOf(id), listener);
    }
}
