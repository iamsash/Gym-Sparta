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

import com.example.appgymsparta.data.repository.AdministradorRepository;
import com.example.appgymsparta.viewmodel.AdministradorViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CambiarContrasenaActivity extends AppCompatActivity {

    private AdministradorViewModel administradorViewModel;

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
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnCambiarPassword.setOnClickListener(v -> procesarCambioContrasena());
    }

    private void procesarCambioContrasena() {
        if (isGuardando) return;

        tilPassActual.setError(null);
        tilPassNueva.setError(null);
        tilPassConfirmar.setError(null);

        String passActual = etPassActual.getText() != null ? etPassActual.getText().toString().trim() : "";
        String passNueva = etPassNueva.getText() != null ? etPassNueva.getText().toString().trim() : "";
        String passConfirmar = etPassConfirmar.getText() != null ? etPassConfirmar.getText().toString().trim() : "";

        // 1. Validar Contraseña Actual
        if (TextUtils.isEmpty(passActual)) {
            tilPassActual.setError("Ingresa la contraseña actual");
            etPassActual.requestFocus();
            return;
        }

        // 2. Validar Nueva Contraseña
        if (TextUtils.isEmpty(passNueva)) {
            tilPassNueva.setError("Ingresa la nueva contraseña");
            etPassNueva.requestFocus();
            return;
        }

        if (passNueva.length() < 6) {
            tilPassNueva.setError("La contraseña debe tener al menos 6 caracteres");
            etPassNueva.requestFocus();
            return;
        }

        // 3. Validar Confirmación
        if (!passNueva.equals(passConfirmar)) {
            tilPassConfirmar.setError("Las contraseñas no coinciden");
            etPassConfirmar.requestFocus();
            return;
        }

        isGuardando = true;
        btnCambiarPassword.setEnabled(false);

        // Actualizar contraseña de forma segura usando Firebase Authentication
        administradorViewModel.cambiarPassword(passNueva, new AdministradorRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isGuardando = false;
                btnCambiarPassword.setEnabled(true);
                Toast.makeText(CambiarContrasenaActivity.this, "¡Contraseña actualizada exitosamente en Firebase Auth!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                isGuardando = false;
                btnCambiarPassword.setEnabled(true);
                Toast.makeText(CambiarContrasenaActivity.this, "Error al cambiar contraseña: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
