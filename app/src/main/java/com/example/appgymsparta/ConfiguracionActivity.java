package com.example.appgymsparta;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ConfiguracionActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "GymSpartaConfig";
    private static final String KEY_NOMBRE = "gym_nombre";
    private static final String KEY_TELEFONO = "gym_telefono";
    private static final String KEY_DIRECCION = "gym_direccion";

    private SharedPreferences preferences;

    private ImageView btnBack;
    private TextInputLayout tilNombreGimnasio;
    private TextInputLayout tilTelefono;
    private TextInputLayout tilDireccion;
    private TextInputEditText etNombreGimnasio;
    private TextInputEditText etTelefono;
    private TextInputEditText etDireccion;
    private MaterialButton btnGuardarConfiguracion;
    private MaterialButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        initViews();
        cargarConfiguraciones();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tilNombreGimnasio = findViewById(R.id.tilNombreGimnasio);
        tilTelefono = findViewById(R.id.tilTelefono);
        tilDireccion = findViewById(R.id.tilDireccion);
        etNombreGimnasio = findViewById(R.id.etNombreGimnasio);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        btnGuardarConfiguracion = findViewById(R.id.btnGuardarConfiguracion);
        btnVolver = findViewById(R.id.btnVolver);
    }

    private void cargarConfiguraciones() {
        String nombre = preferences.getString(KEY_NOMBRE, "");
        String telefono = preferences.getString(KEY_TELEFONO, "");
        String direccion = preferences.getString(KEY_DIRECCION, "");

        etNombreGimnasio.setText(nombre);
        etTelefono.setText(telefono);
        etDireccion.setText(direccion);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnVolver.setOnClickListener(v -> finish());
        btnGuardarConfiguracion.setOnClickListener(v -> guardarConfiguracion());
    }

    private void guardarConfiguracion() {
        tilNombreGimnasio.setError(null);

        String nombre = etNombreGimnasio.getText() != null ? etNombreGimnasio.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
        String direccion = etDireccion.getText() != null ? etDireccion.getText().toString().trim() : "";

        if (TextUtils.isEmpty(nombre)) {
            tilNombreGimnasio.setError("El nombre del gimnasio es obligatorio");
            etNombreGimnasio.requestFocus();
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_NOMBRE, nombre);
        editor.putString(KEY_TELEFONO, telefono);
        editor.putString(KEY_DIRECCION, direccion);
        editor.apply();

        Toast.makeText(this, "Configuración guardada correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }
}
