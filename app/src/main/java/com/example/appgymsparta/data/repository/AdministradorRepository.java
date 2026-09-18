package com.example.appgymsparta.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appgymsparta.data.entity.Administrador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdministradorRepository {

    private static final String TAG = "AdminRepository";
    private static final String COLLECTION_ADMINISTRADORES = "administradores";
    private final CollectionReference adminsRef;
    private final FirebaseAuth mAuth;

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public AdministradorRepository(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.adminsRef = db.collection(COLLECTION_ADMINISTRADORES);
        this.mAuth = FirebaseAuth.getInstance();
    }

    // Contar administradores activos para el Dashboard
    public LiveData<Integer> contarActivos() {
        return new LiveData<Integer>() {
            private ListenerRegistration registration;

            @Override
            protected void onActive() {
                super.onActive();
                registration = adminsRef.whereEqualTo("estado", true)
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

    // Obtener todos los administradores desde Firestore
    public void obtenerTodos(OnResultListener<List<Administrador>> listener) {
        adminsRef.orderBy("apellidos", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Administrador> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Administrador a = doc.toObject(Administrador.class);
                        if (a != null) {
                            a.setId(doc.getId());
                            lista.add(a);
                        }
                    }
                    if (listener != null) listener.onSuccess(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener todos los administradores", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Buscar administrador por UID o ID String
    public void buscarPorId(String id, OnResultListener<Administrador> listener) {
        if (id == null || id.trim().isEmpty()) {
            FirebaseUser current = mAuth.getCurrentUser();
            if (current != null) {
                id = current.getUid();
            } else {
                if (listener != null) listener.onSuccess(null);
                return;
            }
        }

        final String idLimpio = id.trim();
        adminsRef.document(idLimpio).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Administrador a = doc.toObject(Administrador.class);
                        if (a != null) {
                            a.setId(doc.getId());
                        }
                        if (listener != null) listener.onSuccess(a);
                    } else {
                        if (listener != null) listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar administrador por ID: " + idLimpio, e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Sobrecarga de compatibilidad para buscar por ID entero
    public void buscarPorId(int id, OnResultListener<Administrador> listener) {
        FirebaseUser current = mAuth.getCurrentUser();
        String uid = current != null ? current.getUid() : String.valueOf(id);
        buscarPorId(uid, listener);
    }

    // Actualizar perfil de administrador en Firestore (Sin tocar contraseñas)
    public void actualizar(Administrador admin, OnResultListener<Boolean> listener) {
        if (admin.getId() == null || admin.getId().isEmpty()) {
            FirebaseUser current = mAuth.getCurrentUser();
            if (current != null) {
                admin.setId(current.getUid());
            } else {
                if (listener != null) listener.onError(new IllegalArgumentException("ID de administrador no válido"));
                return;
            }
        }

        adminsRef.document(admin.getId()).set(admin)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar administrador en Firestore", e);
                    if (listener != null) listener.onError(e);
                });
    }

    // Cambiar contraseña de forma segura usando exclusivamente Firebase Authentication
    public void cambiarPassword(String nuevaPassword, OnResultListener<Boolean> listener) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            if (listener != null) listener.onError(new IllegalStateException("No hay un usuario autenticado en Firebase Auth"));
            return;
        }

        user.updatePassword(nuevaPassword)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar contraseña en FirebaseAuth", e);
                    if (listener != null) listener.onError(e);
                });
    }
}
