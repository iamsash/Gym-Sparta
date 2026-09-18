package com.example.appgymsparta.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.appgymsparta.data.entity.DetalleRutina;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DetalleRutinaRepository {

    private static final String TAG = "DetalleRutinaRepo";
    private final FirebaseFirestore db;

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public DetalleRutinaRepository(Context context) {
        this.db = FirebaseFirestore.getInstance();
    }

    private CollectionReference getEjerciciosRef(String idRutina) {
        return db.collection("rutinas").document(idRutina).collection("ejercicios");
    }

    // Insertar ejercicio en subcolección 'ejercicios' de la rutina
    public void insertar(DetalleRutina detalle, OnResultListener<String> listener) {
        if (detalle.getIdRutina() == null || detalle.getIdRutina().isEmpty()) {
            if (listener != null) listener.onError(new IllegalArgumentException("ID de rutina no válido para insertar ejercicio"));
            return;
        }

        getEjerciciosRef(detalle.getIdRutina()).add(detalle)
                .addOnSuccessListener(documentReference -> {
                    String idGenerado = documentReference.getId();
                    detalle.setId(idGenerado);
                    if (listener != null) listener.onSuccess(idGenerado);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al insertar ejercicio en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Actualizar ejercicio
    public void actualizar(DetalleRutina detalle, OnResultListener<Boolean> listener) {
        if (detalle.getIdRutina() == null || detalle.getId() == null) {
            if (listener != null) listener.onError(new IllegalArgumentException("IDs inválidos para actualizar ejercicio"));
            return;
        }

        getEjerciciosRef(detalle.getIdRutina()).document(detalle.getId()).set(detalle)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar ejercicio en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener ejercicios por ID de rutina String
    public void obtenerPorRutina(String idRutina, OnResultListener<List<DetalleRutina>> listener) {
        if (idRutina == null || idRutina.isEmpty()) {
            if (listener != null) listener.onSuccess(new ArrayList<>());
            return;
        }

        getEjerciciosRef(idRutina).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DetalleRutina> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        DetalleRutina d = doc.toObject(DetalleRutina.class);
                        if (d != null) {
                            d.setId(doc.getId());
                            d.setIdRutina(idRutina);
                            lista.add(d);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener ejercicios por rutina", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Sobrecarga de compatibilidad para int idRutina
    public void obtenerPorRutina(int idRutina, OnResultListener<List<DetalleRutina>> listener) {
        obtenerPorRutina(String.valueOf(idRutina), listener);
    }

    // Eliminar ejercicio
    public void eliminar(DetalleRutina detalle, OnResultListener<Boolean> listener) {
        if (detalle.getIdRutina() == null || detalle.getId() == null) {
            if (listener != null) listener.onError(new IllegalArgumentException("IDs inválidos para eliminar ejercicio"));
            return;
        }

        getEjerciciosRef(detalle.getIdRutina()).document(detalle.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al eliminar ejercicio de Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }
}
