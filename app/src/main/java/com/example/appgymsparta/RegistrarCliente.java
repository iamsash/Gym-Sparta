package com.example.appgymsparta;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.appgymsparta.data.entity.Cliente;
import com.example.appgymsparta.data.repository.ClienteRepository;
import com.example.appgymsparta.viewmodel.ClienteViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;

public class RegistrarCliente extends AppCompatActivity {

    private ClienteViewModel clienteViewModel;
    private Cliente clienteExistenteEdicion;
    private String idClienteEditar = null;

    private ImageView btnBack;
    private TextView tvHeaderTitle;
    private TextView tvHeaderSubtitle;
    private TextInputLayout tilDni;
    private TextInputLayout tilNombres;
    private TextInputLayout tilApellidos;
    private TextInputLayout tilTelefono;
    private TextInputEditText etDni;
    private TextInputEditText etNombres;
    private TextInputEditText etApellidos;
    private TextInputEditText etTelefono;
    private Spinner spSexo;
    private MaterialButton btnGuardarCliente;
    private MaterialButton btnCancelar;

    private boolean isGuardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("idCliente")) {
            idClienteEditar = getIntent().getStringExtra("idCliente");
        }

        initViews();
        setupSpinner();
        setupViewModel();
        setupListeners();

        if (isModoEdicion()) {
            configurarModoEdicion();
        }
    }

    private boolean isModoEdicion() {
        return idClienteEditar != null && !idClienteEditar.trim().isEmpty();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);
        tilDni = findViewById(R.id.tilDni);
        tilNombres = findViewById(R.id.tilNombres);
        tilApellidos = findViewById(R.id.tilApellidos);
        tilTelefono = findViewById(R.id.tilTelefono);
        etDni = findViewById(R.id.etDni);
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etTelefono = findViewById(R.id.etTelefono);
        spSexo = findViewById(R.id.spSexo);
        btnGuardarCliente = findViewById(R.id.btnGuardarCliente);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void setupSpinner() {
        String[] opcionesSexo = new String[]{"Seleccionar", "Masculino", "Femenino"};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesSexo);
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSexo.setAdapter(adapterSexo);
    }

    private void setupViewModel() {
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
    }

    private void configurarModoEdicion() {
        tvHeaderTitle.setText("EDITAR CLIENTE");
        tvHeaderSubtitle.setText("Actualiza los datos del cliente");
        btnGuardarCliente.setText("ACTUALIZAR CLIENTE");

        clienteViewModel.buscarPorId(idClienteEditar).observe(this, cliente -> {
            if (cliente != null && clienteExistenteEdicion == null) {
                this.clienteExistenteEdicion = cliente;
                etDni.setText(cliente.getDni());
                etNombres.setText(cliente.getNombres());
                etApellidos.setText(cliente.getApellidos());
                etTelefono.setText(cliente.getTelefono());

                if (cliente.getSexo() != null) {
                    if (cliente.getSexo().equalsIgnoreCase("Masculino")) {
                        spSexo.setSelection(1);
                    } else if (cliente.getSexo().equalsIgnoreCase("Femenino")) {
                        spSexo.setSelection(2);
                    }
                }
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnGuardarCliente.setOnClickListener(v -> procesarRegistroOEdicion());
    }

    private void procesarRegistroOEdicion() {
        if (isGuardando) return;

        // Limpiar errores previos
        tilDni.setError(null);
        tilNombres.setError(null);
        tilApellidos.setError(null);
        tilTelefono.setError(null);

        // Obtener valores limpios de espacios en blanco
        String dni = etDni.getText() != null ? etDni.getText().toString().trim() : "";
        String nombres = etNombres.getText() != null ? etNombres.getText().toString().trim() : "";
        String apellidos = etApellidos.getText() != null ? etApellidos.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
        String sexo = spSexo.getSelectedItem() != null ? spSexo.getSelectedItem().toString() : "Seleccionar";

        // 1. Validación DNI obligatorio
        if (TextUtils.isEmpty(dni)) {
            tilDni.setError("El DNI es obligatorio");
            etDni.requestFocus();
            return;
        }

        // 2. Validación DNI exactamente 8 dígitos numéricos
        if (dni.length() != 8 || !dni.matches("\\d+")) {
            tilDni.setError("El DNI debe tener exactamente 8 dígitos numéricos");
            etDni.requestFocus();
            return;
        }

        // 3. Validación Nombres obligatorios
        if (TextUtils.isEmpty(nombres)) {
            tilNombres.setError("Los nombres son obligatorios");
            etNombres.requestFocus();
            return;
        }

        // 4. Validación Apellidos obligatorios
        if (TextUtils.isEmpty(apellidos)) {
            tilApellidos.setError("Los apellidos son obligatorios");
            etApellidos.requestFocus();
            return;
        }

        // 5. Validación Teléfono (opcional, pero si se ingresa debe contener solo números)
        if (!TextUtils.isEmpty(telefono) && !telefono.matches("\\d+")) {
            tilTelefono.setError("El teléfono debe contener únicamente números");
            etTelefono.requestFocus();
            return;
        }

        // 6. Validación Sexo obligatorio
        if (spSexo.getSelectedItemPosition() == 0 || sexo.equalsIgnoreCase("Seleccionar")) {
            Toast.makeText(this, "Por favor, selecciona el sexo del cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        isGuardando = true;
        btnGuardarCliente.setEnabled(false);

        // Validar DNI en Cloud Firestore mediante ClienteViewModel
        clienteViewModel.buscarPorDni(dni).observe(this, clienteDuplicado -> {
            if (!isGuardando) return;

            // Si se está creando un nuevo cliente y el DNI ya existe, O si en modo edición pertenece a OTRO cliente
            if (clienteDuplicado != null && (!isModoEdicion() || !clienteDuplicado.getId().equals(idClienteEditar))) {
                isGuardando = false;
                btnGuardarCliente.setEnabled(true);
                tilDni.setError("El DNI " + dni + " ya se encuentra registrado");
                etDni.requestFocus();
            } else {
                if (isModoEdicion() && clienteExistenteEdicion != null) {
                    // MODO EDICIÓN / ACTUALIZACIÓN EN FIRESTORE
                    clienteExistenteEdicion.setDni(dni);
                    clienteExistenteEdicion.setNombres(nombres);
                    clienteExistenteEdicion.setApellidos(apellidos);
                    clienteExistenteEdicion.setTelefono(telefono);
                    clienteExistenteEdicion.setSexo(sexo);

                    clienteViewModel.actualizar(clienteExistenteEdicion, new ClienteRepository.OnResultListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            isGuardando = false;
                            btnGuardarCliente.setEnabled(true);
                            Toast.makeText(RegistrarCliente.this, "¡Cliente actualizado exitosamente en Firestore!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(Exception e) {
                            isGuardando = false;
                            btnGuardarCliente.setEnabled(true);
                            Toast.makeText(RegistrarCliente.this, "Error al actualizar en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // MODO NUEVO REGISTRO EN FIRESTORE
                    Cliente nuevoCliente = new Cliente(
                            dni,
                            nombres,
                            apellidos,
                            telefono,
                            sexo,
                            LocalDate.now(),
                            true
                    );

                    clienteViewModel.insertar(nuevoCliente, new ClienteRepository.OnResultListener<String>() {
                        @Override
                        public void onSuccess(String idGenerado) {
                            isGuardando = false;
                            btnGuardarCliente.setEnabled(true);
                            Toast.makeText(RegistrarCliente.this, "¡Cliente guardado exitosamente en Firestore!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(Exception e) {
                            isGuardando = false;
                            btnGuardarCliente.setEnabled(true);
                            Toast.makeText(RegistrarCliente.this, "Error al registrar en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
