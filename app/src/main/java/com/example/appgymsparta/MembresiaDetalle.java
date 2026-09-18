package com.example.appgymsparta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.appgymsparta.data.entity.Membresia;
import com.example.appgymsparta.data.repository.MembresiaRepository;
import com.example.appgymsparta.viewmodel.MembresiaViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class MembresiaDetalle extends AppCompatActivity {

    private MembresiaViewModel membresiaViewModel;
    private Membresia membresiaActual;
    private String idMembresia = null;

    private ImageView btnBack;
    private TextView tvNombreMembresia;
    private TextView tvPrecioVal;
    private TextView tvStatusBadge;
    private TextView tvDuracionVal;
    private TextView tvDescripcionVal;
    private MaterialButton btnEditarMembresia;
    private MaterialButton btnCambiarEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_membresia_detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("idMembresia")) {
            idMembresia = getIntent().getStringExtra("idMembresia");
        }

        if (idMembresia == null || idMembresia.trim().isEmpty()) {
            Toast.makeText(this, "No se especificó una membresía válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupViewModel();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idMembresia != null && membresiaViewModel != null) {
            membresiaViewModel.buscarPorId(idMembresia);
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvNombreMembresia = findViewById(R.id.tvNombreMembresia);
        tvPrecioVal = findViewById(R.id.tvPrecioVal);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvDuracionVal = findViewById(R.id.tvDuracionVal);
        tvDescripcionVal = findViewById(R.id.tvDescripcionVal);
        btnEditarMembresia = findViewById(R.id.btnEditarMembresia);
        btnCambiarEstado = findViewById(R.id.btnCambiarEstado);
    }

    private void setupViewModel() {
        membresiaViewModel = new ViewModelProvider(this).get(MembresiaViewModel.class);

        membresiaViewModel.buscarPorId(idMembresia).observe(this, membresia -> {
            if (membresia != null) {
                this.membresiaActual = membresia;
                mostrarDatosMembresia(membresia);
            } else {
                Toast.makeText(MembresiaDetalle.this, "Membresía no encontrada en Cloud Firestore", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void mostrarDatosMembresia(Membresia membresia) {
        tvNombreMembresia.setText(membresia.getNombre() != null ? membresia.getNombre() : "MEMBRESÍA");
        tvPrecioVal.setText(String.format(Locale.getDefault(), "S/ %,.2f", membresia.getPrecio()));
        tvDuracionVal.setText(membresia.getDuracionDias() + " días");

        String desc = membresia.getDescripcion() != null && !membresia.getDescripcion().trim().isEmpty()
                ? membresia.getDescripcion() : "Sin descripción adicional";
        tvDescripcionVal.setText(desc);

        if (membresia.isEstado()) {
            tvStatusBadge.setText("● ACTIVO");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_active);
            tvStatusBadge.setTextColor(getColor(R.color.badge_green));
            btnCambiarEstado.setText("DESACTIVAR MEMBRESÍA");
        } else {
            tvStatusBadge.setText("● INACTIVO");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_inactive);
            tvStatusBadge.setTextColor(getColor(R.color.red_accent));
            btnCambiarEstado.setText("ACTIVAR MEMBRESÍA");
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEditarMembresia.setOnClickListener(v -> {
            if (membresiaActual != null) {
                Intent intent = new Intent(MembresiaDetalle.this, RegistrarMembresia.class);
                intent.putExtra("idMembresia", membresiaActual.getId());
                startActivity(intent);
            }
        });

        btnCambiarEstado.setOnClickListener(v -> confirmarCambioEstado());
    }

    private void confirmarCambioEstado() {
        if (membresiaActual == null) return;

        boolean nuevoEstado = !membresiaActual.isEstado();
        String accion = nuevoEstado ? "activar" : "desactivar";

        new AlertDialog.Builder(this)
                .setTitle("Confirmar Acción")
                .setMessage("¿Estás seguro de que deseas " + accion + " esta membresía?")
                .setPositiveButton(accion.toUpperCase(), (dialog, which) -> cambiarEstado(nuevoEstado))
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void cambiarEstado(boolean nuevoEstado) {
        if (membresiaActual == null) return;
        membresiaActual.setEstado(nuevoEstado);

        membresiaViewModel.actualizar(membresiaActual, new MembresiaRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                String msg = nuevoEstado ? "Membresía activada correctamente" : "Membresía desactivada correctamente";
                Toast.makeText(MembresiaDetalle.this, msg, Toast.LENGTH_SHORT).show();
                mostrarDatosMembresia(membresiaActual);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MembresiaDetalle.this, "Error al actualizar la membresía en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
