package com.example.appgymsparta;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.appgymsparta.data.entity.Administrador;
import com.example.appgymsparta.viewmodel.AdministradorViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.concurrent.Executors;

public class EditarAdministradorActivity extends AppCompatActivity {

    private AdministradorViewModel administradorViewModel;
    private Administrador adminActual;
    private int idAdministrador = -1;

    private ImageView btnBack;
    private TextInputLayout tilNombres;
    private TextInputLayout tilApellidos;
    private TextInputLayout tilUsuario;
    private TextInputLayout tilTelefono;
    private TextInputEditText etNombres;
    private TextInputEditText etApellidos;
    private TextInputEditText etUsuario;
    private TextInputEditText etTelefono;
    private MaterialButton btnGuardarCambios;
    private MaterialButton btnCancelar;

    private boolean isGuardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_administrador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("idAdministrador")) {
            idAdministrador = getIntent().getIntExtra("idAdministrador", -1);
        }

        initViews();
        setupViewModel();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tilNombres = findViewById(R.id.tilNombres);
        tilApellidos = findViewById(R.id.tilApellidos);
        tilUsuario = findViewById(R.id.tilUsuario);
        tilTelefono = findViewById(R.id.tilTelefono);
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etUsuario = findViewById(R.id.etUsuario);
        etTelefono = findViewById(R.id.etTelefono);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void setupViewModel() {
        administradorViewModel = new ViewModelProvider(this).get(AdministradorViewModel.class);

        if (idAdministrador != -1) {
            administradorViewModel.buscarPorId(idAdministrador).observe(this, admin -> {
                if (admin != null) {
                    this.adminActual = admin;
                    cargarDatos(admin);
                } else {
                    cargarAdminPorDefecto();
                }
            });
        } else {
            cargarAdminPorDefecto();
        }
    }

    private void cargarAdminPorDefecto() {
        administradorViewModel.obtenerTodos().observe(this, listaAdmins -> {
            if (listaAdmins != null && !listaAdmins.isEmpty()) {
                if (adminActual == null) {
                    this.adminActual = listaAdmins.get(0);
                    cargarDatos(adminActual);
                }
            } else if (adminActual == null) {
                // Si la base de datos está vacía, crear instancia temporal
                this.adminActual = new Administrador("Administrador", "SPARTA", "admin", "admin123", "999888777", true, LocalDate.now());
                cargarDatos(adminActual);
            }
        });
    }

    private void cargarDatos(Administrador admin) {
        if (admin == null) return;
        etNombres.setText(admin.getNombres() != null ? admin.getNombres() : "");
        etApellidos.setText(admin.getApellidos() != null ? admin.getApellidos() : "");
        etUsuario.setText(admin.getUsuario() != null ? admin.getUsuario() : "");
        etTelefono.setText(admin.getTelefono() != null ? admin.getTelefono() : "");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnGuardarCambios.setOnClickListener(v -> procesarGuardado());
    }

    private void procesarGuardado() {
        if (isGuardando) return;

        tilNombres.setError(null);
        tilApellidos.setError(null);
        tilUsuario.setError(null);

        String nombres = etNombres.getText() != null ? etNombres.getText().toString().trim() : "";
        String apellidos = etApellidos.getText() != null ? etApellidos.getText().toString().trim() : "";
        String usuario = etUsuario.getText() != null ? etUsuario.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";

        if (TextUtils.isEmpty(nombres)) {
            tilNombres.setError("El nombre es obligatorio");
            etNombres.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(apellidos)) {
            tilApellidos.setError("El apellido es obligatorio");
            etApellidos.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(usuario)) {
            tilUsuario.setError("El usuario es obligatorio");
            etUsuario.requestFocus();
            return;
        }

        if (adminActual == null) {
            adminActual = new Administrador(nombres, apellidos, usuario, "admin123", telefono, true, LocalDate.now());
        } else {
            adminActual.setNombres(nombres);
            adminActual.setApellidos(apellidos);
            adminActual.setUsuario(usuario);
            adminActual.setTelefono(telefono);
        }

        isGuardando = true;
        btnGuardarCambios.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            long filasAfectadas;
            if (adminActual.getIdAdministrador() > 0) {
                filasAfectadas = administradorViewModel.actualizar(adminActual);
            } else {
                filasAfectadas = administradorViewModel.insertar(adminActual);
                if (filasAfectadas > 0) {
                    adminActual.setIdAdministrador((int) filasAfectadas);
                }
            }

            runOnUiThread(() -> {
                isGuardando = false;
                btnGuardarCambios.setEnabled(true);
                if (filasAfectadas > 0) {
                    Toast.makeText(EditarAdministradorActivity.this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditarAdministradorActivity.this, "Error al guardar en Room", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
