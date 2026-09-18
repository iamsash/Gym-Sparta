package com.example.appgymsparta;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.appgymsparta.data.entity.Membresia;
import com.example.appgymsparta.data.repository.MembresiaRepository;
import com.example.appgymsparta.viewmodel.MembresiaViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class RegistrarMembresia extends AppCompatActivity {

    private MembresiaViewModel membresiaViewModel;
    private Membresia membresiaExistenteEdicion;
    private String idMembresiaEditar = null;

    private ImageView btnBack;
    private TextView tvHeaderTitle;
    private TextView tvHeaderSubtitle;
    private TextInputLayout tilNombre;
    private TextInputLayout tilDescripcion;
    private TextInputLayout tilDuracion;
    private TextInputLayout tilPrecio;
    private TextInputEditText etNombre;
    private TextInputEditText etDescripcion;
    private TextInputEditText etDuracion;
    private TextInputEditText etPrecio;
    private MaterialButton btnGuardarMembresia;
    private MaterialButton btnCancelar;

    private boolean isGuardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_membresia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("idMembresia")) {
            idMembresiaEditar = getIntent().getStringExtra("idMembresia");
        }

        initViews();
        setupViewModel();
        setupListeners();

        if (isModoEdicion()) {
            configurarModoEdicion();
        }
    }

    private boolean isModoEdicion() {
        return idMembresiaEditar != null && !idMembresiaEditar.trim().isEmpty();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);
        tilNombre = findViewById(R.id.tilNombre);
        tilDescripcion = findViewById(R.id.tilDescripcion);
        tilDuracion = findViewById(R.id.tilDuracion);
        tilPrecio = findViewById(R.id.tilPrecio);
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etDuracion = findViewById(R.id.etDuracion);
        etPrecio = findViewById(R.id.etPrecio);
        btnGuardarMembresia = findViewById(R.id.btnGuardarMembresia);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void setupViewModel() {
        membresiaViewModel = new ViewModelProvider(this).get(MembresiaViewModel.class);
    }

    private void configurarModoEdicion() {
        tvHeaderTitle.setText("EDITAR MEMBRESÍA");
        tvHeaderSubtitle.setText("Actualiza las condiciones del plan o tarifa");
        btnGuardarMembresia.setText("ACTUALIZAR MEMBRESÍA");

        membresiaViewModel.buscarPorId(idMembresiaEditar).observe(this, membresia -> {
            if (membresia != null && membresiaExistenteEdicion == null) {
                this.membresiaExistenteEdicion = membresia;
                etNombre.setText(membresia.getNombre());
                etDescripcion.setText(membresia.getDescripcion());
                etDuracion.setText(String.valueOf(membresia.getDuracionDias()));
                etPrecio.setText(String.format(Locale.US, "%.2f", membresia.getPrecio()));
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnGuardarMembresia.setOnClickListener(v -> procesarGuardadoOEdicion());
    }

    private void procesarGuardadoOEdicion() {
        if (isGuardando) return;

        tilNombre.setError(null);
        tilDuracion.setError(null);
        tilPrecio.setError(null);

        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String descripcion = etDescripcion.getText() != null ? etDescripcion.getText().toString().trim() : "";
        String duracionStr = etDuracion.getText() != null ? etDuracion.getText().toString().trim() : "";
        String precioStr = etPrecio.getText() != null ? etPrecio.getText().toString().trim() : "";

        // 1. Nombre obligatorio
        if (TextUtils.isEmpty(nombre)) {
            tilNombre.setError("El nombre de la membresía es obligatorio");
            etNombre.requestFocus();
            return;
        }

        // 2. Duración obligatoria y mayor que 0
        if (TextUtils.isEmpty(duracionStr)) {
            tilDuracion.setError("La duración en días es obligatoria");
            etDuracion.requestFocus();
            return;
        }

        int duracionDias;
        try {
            duracionDias = Integer.parseInt(duracionStr);
            if (duracionDias <= 0) {
                tilDuracion.setError("La duración debe ser mayor a 0 días");
                etDuracion.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            tilDuracion.setError("Ingresa un número de días válido");
            etDuracion.requestFocus();
            return;
        }

        // 3. Precio obligatorio y no negativo
        if (TextUtils.isEmpty(precioStr)) {
            tilPrecio.setError("El precio es obligatorio");
            etPrecio.requestFocus();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
            if (precio < 0) {
                tilPrecio.setError("El precio no puede ser negativo");
                etPrecio.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            tilPrecio.setError("Ingresa un precio válido");
            etPrecio.requestFocus();
            return;
        }

        isGuardando = true;
        btnGuardarMembresia.setEnabled(false);

        if (isModoEdicion() && membresiaExistenteEdicion != null) {
            // Actualización en Firestore
            membresiaExistenteEdicion.setNombre(nombre);
            membresiaExistenteEdicion.setDescripcion(descripcion);
            membresiaExistenteEdicion.setDuracionDias(duracionDias);
            membresiaExistenteEdicion.setPrecio(precio);

            membresiaViewModel.actualizar(membresiaExistenteEdicion, new MembresiaRepository.OnResultListener<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    isGuardando = false;
                    btnGuardarMembresia.setEnabled(true);
                    Toast.makeText(RegistrarMembresia.this, "¡Membresía actualizada exitosamente en Firestore!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    isGuardando = false;
                    btnGuardarMembresia.setEnabled(true);
                    Toast.makeText(RegistrarMembresia.this, "Error al actualizar en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Nuevo Registro en Firestore
            Membresia nuevaMembresia = new Membresia(
                    nombre,
                    descripcion,
                    duracionDias,
                    precio,
                    true // estado = activo por defecto
            );

            membresiaViewModel.insertar(nuevaMembresia, new MembresiaRepository.OnResultListener<String>() {
                @Override
                public void onSuccess(String idGenerado) {
                    isGuardando = false;
                    btnGuardarMembresia.setEnabled(true);
                    Toast.makeText(RegistrarMembresia.this, "¡Membresía creada exitosamente en Firestore!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    isGuardando = false;
                    btnGuardarMembresia.setEnabled(true);
                    Toast.makeText(RegistrarMembresia.this, "Error al crear en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
