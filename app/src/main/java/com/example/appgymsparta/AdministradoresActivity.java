package com.example.appgymsparta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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

import com.example.appgymsparta.data.entity.Administrador;
import com.example.appgymsparta.viewmodel.AdministradorViewModel;
import com.google.android.material.button.MaterialButton;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;

public class AdministradoresActivity extends AppCompatActivity {

    private AdministradorViewModel administradorViewModel;
    private Administrador adminActual;

    private ImageView btnBack;
    private TextView tvNombreCompletoVal;
    private TextView tvUsuarioVal;
    private TextView tvStatusBadge;
    private TextView tvNombresVal;
    private TextView tvApellidosVal;
    private TextView tvTelefonoVal;
    private TextView tvFechaRegistroVal;

    private MaterialButton btnEditarDatos;
    private MaterialButton btnCambiarPassword;
    private MaterialButton btnVolver;

    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administradores);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupViewModel();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (administradorViewModel != null) {
            administradorViewModel.obtenerTodos();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvNombreCompletoVal = findViewById(R.id.tvNombreCompletoVal);
        tvUsuarioVal = findViewById(R.id.tvUsuarioVal);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvNombresVal = findViewById(R.id.tvNombresVal);
        tvApellidosVal = findViewById(R.id.tvApellidosVal);
        tvTelefonoVal = findViewById(R.id.tvTelefonoVal);
        tvFechaRegistroVal = findViewById(R.id.tvFechaRegistroVal);

        btnEditarDatos = findViewById(R.id.btnEditarDatos);
        btnCambiarPassword = findViewById(R.id.btnCambiarPassword);
        btnVolver = findViewById(R.id.btnVolver);
    }

    private void setupViewModel() {
        administradorViewModel = new ViewModelProvider(this).get(AdministradorViewModel.class);

        // Cargar administrador real almacenado en Room
        administradorViewModel.obtenerTodos().observe(this, listaAdmins -> {
            if (listaAdmins != null && !listaAdmins.isEmpty()) {
                this.adminActual = listaAdmins.get(0);
                mostrarDatosAdmin(adminActual);
                btnEditarDatos.setEnabled(true);
                btnCambiarPassword.setEnabled(true);
            } else {
                this.adminActual = null;
                mostrarEstadoSinAdmin();
            }
        });
    }

    private void mostrarEstadoSinAdmin() {
        tvNombreCompletoVal.setText("Sin Administrador Registrado");
        tvUsuarioVal.setText("@desconocido");
        tvNombresVal.setText("No registrado");
        tvApellidosVal.setText("No registrado");
        tvTelefonoVal.setText("-");
        tvFechaRegistroVal.setText("-");

        tvStatusBadge.setText("● SIN REGISTRO");
        tvStatusBadge.setBackgroundResource(R.drawable.bg_status_inactive);
        tvStatusBadge.setTextColor(getColor(R.color.red_accent));

        btnEditarDatos.setEnabled(true);
        btnCambiarPassword.setEnabled(false);
    }

    private void mostrarDatosAdmin(Administrador admin) {
        String nombres = admin.getNombres() != null ? admin.getNombres() : "";
        String apellidos = admin.getApellidos() != null ? admin.getApellidos() : "";
        String full = (nombres + " " + apellidos).trim();

        tvNombreCompletoVal.setText(full.isEmpty() ? "Administrador SPARTA" : full);
        tvUsuarioVal.setText(admin.getUsuario() != null ? "@" + admin.getUsuario() : "@admin");

        tvNombresVal.setText(nombres.isEmpty() ? "-" : nombres);
        tvApellidosVal.setText(apellidos.isEmpty() ? "-" : apellidos);

        String tel = admin.getTelefono() != null && !admin.getTelefono().trim().isEmpty()
                ? admin.getTelefono().trim() : "No registrado";
        tvTelefonoVal.setText(tel);

        String fecha = admin.getFechaRegistro() != null ? admin.getFechaRegistro().format(fmt) : "-";
        tvFechaRegistroVal.setText(fecha);

        if (admin.isEstado()) {
            tvStatusBadge.setText("● ACTIVO");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_active);
            tvStatusBadge.setTextColor(getColor(R.color.badge_green));
        } else {
            tvStatusBadge.setText("● INACTIVO");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_inactive);
            tvStatusBadge.setTextColor(getColor(R.color.red_accent));
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnVolver.setOnClickListener(v -> finish());

        btnEditarDatos.setOnClickListener(v -> abrirEditarAdministrador());
        btnCambiarPassword.setOnClickListener(v -> abrirCambiarContrasena());
    }

    private void abrirEditarAdministrador() {
        Intent intent = new Intent(AdministradoresActivity.this, EditarAdministradorActivity.class);
        if (adminActual != null) {
            intent.putExtra("idAdministrador", adminActual.getIdAdministrador());
        }
        startActivity(intent);
    }

    private void abrirCambiarContrasena() {
        Intent intent = new Intent(AdministradoresActivity.this, CambiarContrasenaActivity.class);
        if (adminActual != null) {
            intent.putExtra("idAdministrador", adminActual.getIdAdministrador());
        }
        startActivity(intent);
    }
}
