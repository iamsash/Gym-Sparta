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

import java.util.concurrent.Executors;

public class CambiarContrasenaActivity extends AppCompatActivity {

    private AdministradorViewModel administradorViewModel;
    private Administrador adminActual;
    private int idAdministrador = -1;

    private ImageView btnBack;
    private TextInputLayout tilPassActual;
    private TextInputLayout tilPassNueva;
    private TextInputLayout tilPassConfirmar;
    private TextInputEditText etPassActual;
    private TextInputEditText etPassNueva;
    private TextInputEditText etPassConfirmar;
    private MaterialButton btnCambiarPassword;
    private MaterialButton btnCancelar;

    private boolean isGuardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cambiar_contrasena);
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
        tilPassActual = findViewById(R.id.tilPassActual);
        tilPassNueva = findViewById(R.id.tilPassNueva);
        tilPassConfirmar = findViewById(R.id.tilPassConfirmar);
        etPassActual = findViewById(R.id.etPassActual);
        etPassNueva = findViewById(R.id.etPassNueva);
        etPassConfirmar = findViewById(R.id.etPassConfirmar);
        btnCambiarPassword = findViewById(R.id.btnCambiarPassword);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void setupViewModel() {
        administradorViewModel = new ViewModelProvider(this).get(AdministradorViewModel.class);

        if (idAdministrador != -1) {
            administradorViewModel.buscarPorId(idAdministrador).observe(this, admin -> {
                if (admin != null && adminActual == null) {
                    this.adminActual = admin;
                }
            });
        } else {
            administradorViewModel.obtenerTodos().observe(this, listaAdmins -> {
                if (listaAdmins != null && !listaAdmins.isEmpty() && adminActual == null) {
                    this.adminActual = listaAdmins.get(0);
                }
            });
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnCambiarPassword.setOnClickListener(v -> procesarCambioContrasena());
    }

    private void procesarCambioContrasena() {
        if (isGuardando) return;

        if (adminActual == null) {
            Toast.makeText(this, "No se encontró el administrador a actualizar.", Toast.LENGTH_SHORT).show();
            return;
        }

        tilPassActual.setError(null);
        tilPassNueva.setError(null);
        tilPassConfirmar.setError(null);

        String passActual = etPassActual.getText() != null ? etPassActual.getText().toString().trim() : "";
        String passNueva = etPassNueva.getText() != null ? etPassNueva.getText().toString().trim() : "";
        String passConfirmar = etPassConfirmar.getText() != null ? etPassConfirmar.getText().toString().trim() : "";

        String passwordExistente = adminActual.getPassword() != null ? adminActual.getPassword() : "admin123";

        // 1. Validar Contraseña Actual
        if (TextUtils.isEmpty(passActual)) {
            tilPassActual.setError("Ingresa la contraseña actual");
            etPassActual.requestFocus();
            return;
        }

        if (!passActual.equals(passwordExistente)) {
            tilPassActual.setError("La contraseña actual es incorrecta");
            etPassActual.requestFocus();
            return;
        }

        // 2. Validar Nueva Contraseña
        if (TextUtils.isEmpty(passNueva)) {
            tilPassNueva.setError("Ingresa la nueva contraseña");
            etPassNueva.requestFocus();
            return;
        }

        if (passNueva.length() < 4) {
            tilPassNueva.setError("La contraseña debe tener al menos 4 caracteres");
            etPassNueva.requestFocus();
            return;
        }

        // 3. Validar Confirmación
        if (!passNueva.equals(passConfirmar)) {
            tilPassConfirmar.setError("Las contraseñas no coinciden");
            etPassConfirmar.requestFocus();
            return;
        }

        // 4. Actualizar contraseña y guardar en Room mediante ViewModel -> Repository -> DAO
        adminActual.setPassword(passNueva);

        isGuardando = true;
        btnCambiarPassword.setEnabled(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            int resultado = administradorViewModel.actualizar(adminActual);
            runOnUiThread(() -> {
                isGuardando = false;
                btnCambiarPassword.setEnabled(true);
                if (resultado > 0) {
                    Toast.makeText(CambiarContrasenaActivity.this, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CambiarContrasenaActivity.this, "Error al actualizar la contraseña en la base de datos", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
