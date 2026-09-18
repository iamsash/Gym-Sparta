package com.example.appgymsparta.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Pago;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PagoRepository {

    private static final String TAG = "PagoRepository";
    private static final String COLLECTION_PAGOS = "pagos";
    private final CollectionReference pagosRef;

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public PagoRepository(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.pagosRef = db.collection(COLLECTION_PAGOS);
    }

    // Sumar ingresos del mes para el Dashboard mediante LiveData con SnapshotListener
    public LiveData<Double> sumarIngresosMes(String anioMes) {
        return new LiveData<Double>() {
            private ListenerRegistration registration;

            @Override
            protected void onActive() {
                super.onActive();
                registration = pagosRef
                        .whereGreaterThanOrEqualTo("fechaPago", anioMes + "-01T00:00:00")
                        .whereLessThanOrEqualTo("fechaPago", anioMes + "-31T23:59:59")
                        .addSnapshotListener((snapshots, e) -> {
                            if (e != null) {
                                Log.e(TAG, "Error en SnapshotListener de sumarIngresosMes", e);
                                postValue(0.0);
                                return;
                            }
                            double suma = 0.0;
                            if (snapshots != null) {
                                for (DocumentSnapshot doc : snapshots) {
                                    Double m = doc.getDouble("monto");
                                    if (m != null) {
                                        suma += m;
                                    }
                                }
                            }
                            postValue(suma);
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

    // Insertar nuevo pago
    public void insertar(Pago pago, OnResultListener<String> listener) {
        pagosRef.add(pago)
                .addOnSuccessListener(documentReference -> {
                    String idGenerado = documentReference.getId();
                    pago.setId(idGenerado);
                    if (listener != null) listener.onSuccess(idGenerado);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al insertar pago en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Actualizar pago
    public void actualizar(Pago pago, OnResultListener<Boolean> listener) {
        if (pago.getId() == null || pago.getId().isEmpty()) {
            Exception err = new IllegalArgumentException("ID de pago no válido para actualización");
            Log.e(TAG, err.getMessage());
            if (listener != null) listener.onError(err);
            return;
        }

        pagosRef.document(pago.getId()).set(pago)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar pago en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener todos los pagos
    public void obtenerTodos(OnResultListener<List<Pago>> listener) {
        pagosRef.orderBy("fechaPago", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Pago> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Pago p = doc.toObject(Pago.class);
                        if (p != null) {
                            p.setId(doc.getId());
                            lista.add(p);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener todos los pagos", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Obtener pagos por ID de inscripción
    public void obtenerPorInscripcion(String idInscripcion, OnResultListener<List<Pago>> listener) {
        if (idInscripcion == null || idInscripcion.isEmpty()) {
            if (listener != null) listener.onSuccess(new ArrayList<>());
            return;
        }

        pagosRef.whereEqualTo("idInscripcion", idInscripcion)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Pago> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Pago p = doc.toObject(Pago.class);
                        if (p != null) {
                            p.setId(doc.getId());
                            lista.add(p);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener pagos por inscripción", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Buscar pago por ID String
    public void buscarPorId(String id, OnResultListener<Pago> listener) {
        if (id == null || id.trim().isEmpty()) {
            if (listener != null) listener.onSuccess(null);
            return;
        }

        final String idLimpio = id.trim();
        pagosRef.document(idLimpio).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Pago p = doc.toObject(Pago.class);
                        if (p != null) {
                            p.setId(doc.getId());
                        }
                        if (listener != null) listener.onSuccess(p);
                    } else {
                        if (listener != null) listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar pago por ID: " + idLimpio, e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Sobrecarga de compatibilidad para buscar por ID int
    public void buscarPorId(int id, OnResultListener<Pago> listener) {
        buscarPorId(String.valueOf(id), listener);
    }
}
