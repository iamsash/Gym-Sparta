package com.example.appgymsparta.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.appgymsparta.data.entity.Rutina;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class RutinaRepository {

    private static final String TAG = "RutinaRepository";
    private static final String COLLECTION_RUTINAS = "rutinas";
    private final CollectionReference rutinasRef;

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public RutinaRepository(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.rutinasRef = db.collection(COLLECTION_RUTINAS);
    }

    // Insertar nueva rutina
    public void insertar(Rutina rutina, OnResultListener<String> listener) {
        rutinasRef.add(rutina)
                .addOnSuccessListener(documentReference -> {
                    String idGenerado = documentReference.getId();
                    rutina.setId(idGenerado);
                    if (listener != null) listener.onSuccess(idGenerado);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al insertar rutina en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Actualizar rutina
    public void actualizar(Rutina rutina, OnResultListener<Boolean> listener) {
        if (rutina.getId() == null || rutina.getId().isEmpty()) {
            Exception err = new IllegalArgumentException("ID de rutina no válido para actualización");
            Log.e(TAG, err.getMessage());
            if (listener != null) listener.onError(err);
            return;
        }

        rutinasRef.document(rutina.getId()).set(rutina)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar rutina en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener todas las rutinas
    public void obtenerTodas(OnResultListener<List<Rutina>> listener) {
        rutinasRef.orderBy("fechaInicio", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Rutina> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Rutina r = doc.toObject(Rutina.class);
                        if (r != null) {
                            r.setId(doc.getId());
                            lista.add(r);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener todas las rutinas", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener rutinas por cliente
    public void obtenerPorCliente(String idCliente, OnResultListener<List<Rutina>> listener) {
        if (idCliente == null || idCliente.isEmpty()) {
            if (listener != null) listener.onSuccess(new ArrayList<>());
            return;
        }

        rutinasRef.whereEqualTo("idCliente", idCliente)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Rutina> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Rutina r = doc.toObject(Rutina.class);
                        if (r != null) {
                            r.setId(doc.getId());
                            lista.add(r);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener rutinas por cliente", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Buscar rutina por ID String
    public void buscarPorId(String id, OnResultListener<Rutina> listener) {
        if (id == null || id.trim().isEmpty()) {
            if (listener != null) listener.onSuccess(null);
            return;
        }

        final String idLimpio = id.trim();
        rutinasRef.document(idLimpio).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Rutina r = doc.toObject(Rutina.class);
                        if (r != null) {
                            r.setId(doc.getId());
                        }
                        if (listener != null) listener.onSuccess(r);
                    } else {
                        if (listener != null) listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar rutina por ID: " + idLimpio, e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Sobrecarga de compatibilidad para buscar por ID int
    public void buscarPorId(int id, OnResultListener<Rutina> listener) {
        buscarPorId(String.valueOf(id), listener);
    }
}
