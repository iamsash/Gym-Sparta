package com.example.appgymsparta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;

    private boolean isValidando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> validarIngreso());

        findViewById(R.id.tvForgotPassword).setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Función próximamente disponible", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.tvRegister).setOnClickListener(v -> abrirRegistrarAdministrador());

        findViewById(R.id.btnGoogle).setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Acceso con Google próximamente disponible", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.btnFacebook).setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Acceso con Facebook próximamente disponible", Toast.LENGTH_SHORT).show()
        );
    }

    private void abrirRegistrarAdministrador() {
        Intent intent = new Intent(MainActivity.this, RegistrarAdministradorActivity.class);
        startActivity(intent);
    }

    private void validarIngreso() {
        if (isValidando) return;

        tilUsername.setError(null);
        tilPassword.setError(null);

        String userInput = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // 1. Validar campos no vacíos
        if (TextUtils.isEmpty(userInput)) {
            tilUsername.setError("Ingresa tu usuario o correo");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Ingresa tu contraseña");
            etPassword.requestFocus();
            return;
        }

        isValidando = true;
        btnLogin.setEnabled(false);

        // 2. Si el usuario ingresó correo directamente o un nombre de usuario
        if (Patterns.EMAIL_ADDRESS.matcher(userInput).matches()) {
            autenticarConFirebase(userInput, password);
        } else {
            // Buscar correo por nombre de usuario en la colección 'administradores'
            db.collection("administradores")
                    .whereEqualTo("usuario", userInput)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            String emailResuelto = doc.getString("email");
                            if (emailResuelto != null && !emailResuelto.isEmpty()) {
                                autenticarConFirebase(emailResuelto, password);
                            } else {
                                mostrarErrorCredenciales();
                            }
                        } else {
                            mostrarErrorCredenciales();
                        }
                    });
        }
    }

    private void autenticarConFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        verificarPerfilAdministrador(user);
                    } else {
                        mostrarErrorCredenciales();
                    }
                });
    }

    private void verificarPerfilAdministrador(FirebaseUser user) {
        db.collection("administradores").document(user.getUid()).get()
                .addOnCompleteListener(task -> {
                    isValidando = false;
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        DocumentSnapshot doc = task.getResult();
                        Boolean estado = doc.getBoolean("estado");

                        if (estado != null && estado) {
                            String nombres = doc.getString("nombres");
                            Toast.makeText(MainActivity.this, "¡Bienvenido a GYM SPARTA, " + (nombres != null ? nombres : "") + "!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            mAuth.signOut();
                            Toast.makeText(MainActivity.this, "La cuenta de usuario se encuentra inactiva. Contacte al administrador.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Si no existe perfil en Firestore, permitir ingreso directo de seguridad
                        Toast.makeText(MainActivity.this, "¡Bienvenido a GYM SPARTA!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void mostrarErrorCredenciales() {
        isValidando = false;
        btnLogin.setEnabled(true);
        tilUsername.setError("Usuario o contraseña incorrectos");
        tilPassword.setError("Usuario o contraseña incorrectos");
        etUsername.requestFocus();
    }
}
