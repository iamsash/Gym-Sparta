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
import com.example.appgymsparta.data.repository.AdministradorRepository;
import com.example.appgymsparta.viewmodel.AdministradorViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditarAdministradorActivity extends AppCompatActivity {

    private AdministradorViewModel administradorViewModel;
    private Administrador adminActual;
    private String idAdministrador = null;

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
            idAdministrador = getIntent().getStringExtra("idAdministrador");
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

        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        String uid = (idAdministrador != null && !idAdministrador.isEmpty()) ? idAdministrador : (current != null ? current.getUid() : null);

        if (uid != null) {
            administradorViewModel.buscarPorId(uid).observe(this, admin -> {
                if (admin != null) {
                    this.adminActual = admin;
                    cargarDatos(admin);
                }
            });
        }
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

        if (adminActual != null) {
            adminActual.setNombres(nombres);
            adminActual.setApellidos(apellidos);
            adminActual.setUsuario(usuario);
            adminActual.setTelefono(telefono);
        } else {
            Toast.makeText(this, "No se encontró el perfil de administrador a modificar", Toast.LENGTH_SHORT).show();
            return;
        }

        isGuardando = true;
        btnGuardarCambios.setEnabled(false);

        administradorViewModel.actualizar(adminActual, new AdministradorRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isGuardando = false;
                btnGuardarCambios.setEnabled(true);
                Toast.makeText(EditarAdministradorActivity.this, "Perfil actualizado exitosamente en Firestore", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                isGuardando = false;
                btnGuardarCambios.setEnabled(true);
                Toast.makeText(EditarAdministradorActivity.this, "Error al actualizar perfil en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
