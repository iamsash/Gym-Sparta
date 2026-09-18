package com.example.appgymsparta;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class RegistrarAdministradorActivity extends AppCompatActivity {

    private static final String TAG = "RegistrarAdmin";
    private static final String CODIGO_MAESTRO_GIMNASIO = "SPARTA2026";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView btnBack;
    private TextInputLayout tilNombres;
    private TextInputLayout tilApellidos;
    private TextInputLayout tilUsuario;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputLayout tilTelefono;
    private TextInputLayout tilCodigoAutorizacion;

    private TextInputEditText etNombres;
    private TextInputEditText etApellidos;
    private TextInputEditText etUsuario;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextInputEditText etTelefono;
    private TextInputEditText etCodigoAutorizacion;

    private MaterialButton btnRegistrarAdmin;
    private MaterialButton btnVolverLogin;

    private boolean isGuardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_administrador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tilNombres = findViewById(R.id.tilNombres);
        tilApellidos = findViewById(R.id.tilApellidos);
        tilUsuario = findViewById(R.id.tilUsuario);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tilTelefono = findViewById(R.id.tilTelefono);
        tilCodigoAutorizacion = findViewById(R.id.tilCodigoAutorizacion);

        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etUsuario = findViewById(R.id.etUsuario);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etTelefono = findViewById(R.id.etTelefono);
        etCodigoAutorizacion = findViewById(R.id.etCodigoAutorizacion);

        btnRegistrarAdmin = findViewById(R.id.btnRegistrarAdmin);
        btnVolverLogin = findViewById(R.id.btnVolverLogin);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnVolverLogin.setOnClickListener(v -> finish());
        btnRegistrarAdmin.setOnClickListener(v -> procesarRegistro());
    }

    private void procesarRegistro() {
        if (isGuardando) return;

        limpiarErrores();

        String nombres = etNombres.getText() != null ? etNombres.getText().toString().trim() : "";
        String apellidos = etApellidos.getText() != null ? etApellidos.getText().toString().trim() : "";
        String usuario = etUsuario.getText() != null ? etUsuario.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
        String codigoIngresado = etCodigoAutorizacion.getText() != null ? etCodigoAutorizacion.getText().toString().trim() : "";

        // Validaciones
        if (TextUtils.isEmpty(nombres)) {
            tilNombres.setError("Ingresa los nombres");
            etNombres.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(apellidos)) {
            tilApellidos.setError("Ingresa los apellidos");
            etApellidos.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(usuario)) {
            tilUsuario.setError("Ingresa un nombre de usuario");
            etUsuario.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Ingresa un correo electrónico válido");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            tilPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(codigoIngresado)) {
            tilCodigoAutorizacion.setError("Ingresa el código de autorización del gimnasio");
            etCodigoAutorizacion.requestFocus();
            return;
        }

        if (!CODIGO_MAESTRO_GIMNASIO.equalsIgnoreCase(codigoIngresado)) {
            tilCodigoAutorizacion.setError("Código de autorización no válido");
            etCodigoAutorizacion.requestFocus();
            return;
        }

        isGuardando = true;
        btnRegistrarAdmin.setEnabled(false);

        // 1. Crear cuenta en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        // 2. Guardar perfil del administrador directamente en Cloud Firestore
                        guardarPerfilEnFirestore(user, nombres, apellidos, usuario, email, telefono);
                    } else {
                        habilitarFormulario();
                        Exception e = task.getException();
                        Log.e(TAG, "Error al crear cuenta en FirebaseAuth", e);
                        String msg = e != null ? e.getMessage() : "Error al crear cuenta";
                        Toast.makeText(RegistrarAdministradorActivity.this, "Error de registro: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarPerfilEnFirestore(FirebaseUser user, String nombres, String apellidos, String usuario, String email, String telefono) {
        Map<String, Object> adminMap = new HashMap<>();
        adminMap.put("nombres", nombres);
        adminMap.put("apellidos", apellidos);
        adminMap.put("usuario", usuario);
        adminMap.put("email", email);
        adminMap.put("telefono", telefono);
        adminMap.put("estado", true);
        adminMap.put("fechaRegistro", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Guardar en la colección 'administradores' usando el UID de Firebase Auth
        db.collection("administradores").document(user.getUid()).set(adminMap)
                .addOnSuccessListener(aVoid -> {
                    habilitarFormulario();
                    Toast.makeText(RegistrarAdministradorActivity.this, "¡Administrador registrado exitosamente en Firebase!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al guardar perfil en Firestore", e);
                    // Si falla Firestore, eliminar la cuenta creada en Auth para no dejar cuentas huérfanas
                    user.delete().addOnCompleteListener(task -> {
                        mAuth.signOut();
                        habilitarFormulario();
                        Toast.makeText(RegistrarAdministradorActivity.this, "Error al guardar perfil en Firestore. Operación cancelada.", Toast.LENGTH_LONG).show();
                    });
                });
    }

    private void habilitarFormulario() {
        isGuardando = false;
        btnRegistrarAdmin.setEnabled(true);
    }

    private void limpiarErrores() {
        tilNombres.setError(null);
        tilApellidos.setError(null);
        tilUsuario.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tilTelefono.setError(null);
        tilCodigoAutorizacion.setError(null);
    }
}
