package com.example.appgymsparta;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.example.appgymsparta.data.entity.Rutina;
import com.example.appgymsparta.data.repository.RutinaRepository;
import com.example.appgymsparta.viewmodel.ClienteViewModel;
import com.example.appgymsparta.viewmodel.RutinaViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistrarRutinaActivity extends AppCompatActivity {

    private RutinaViewModel rutinaViewModel;
    private ClienteViewModel clienteViewModel;

    private Rutina rutinaEdicion;
    private String idRutinaEditar = null;

    private List<Cliente> listaClientes = new ArrayList<>();

    private ImageView btnBack;
    private TextView tvHeaderTitle;
    private TextView tvHeaderSubtitle;
    private Spinner spCliente;
    private Spinner spNivel;
    private TextInputLayout tilNombre;
    private TextInputLayout tilObjetivo;
    private TextInputLayout tilFechaInicio;
    private TextInputEditText etNombre;
    private TextInputEditText etObjetivo;
    private TextInputEditText etFechaInicio;
    private MaterialButton btnGuardarRutina;
    private MaterialButton btnCancelar;

    private LocalDate fechaInicioSeleccionada = LocalDate.now();
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private boolean isGuardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_rutina);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("idRutina")) {
            idRutinaEditar = getIntent().getStringExtra("idRutina");
        }

        initViews();
        setupSpinnerNivel();
        setupViewModels();
        setupListeners();

        if (isModoEdicion()) {
            tvHeaderTitle.setText("EDITAR RUTINA");
            tvHeaderSubtitle.setText("Actualiza las condiciones del plan de entrenamiento");
            btnGuardarRutina.setText("ACTUALIZAR RUTINA");
        } else {
            etFechaInicio.setText(fechaInicioSeleccionada.format(fmt));
        }
    }

    private boolean isModoEdicion() {
        return idRutinaEditar != null && !idRutinaEditar.trim().isEmpty();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);
        spCliente = findViewById(R.id.spCliente);
        spNivel = findViewById(R.id.spNivel);
        tilNombre = findViewById(R.id.tilNombre);
        tilObjetivo = findViewById(R.id.tilObjetivo);
        tilFechaInicio = findViewById(R.id.tilFechaInicio);
        etNombre = findViewById(R.id.etNombre);
        etObjetivo = findViewById(R.id.etObjetivo);
        etFechaInicio = findViewById(R.id.etFechaInicio);
        btnGuardarRutina = findViewById(R.id.btnGuardarRutina);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void setupSpinnerNivel() {
        String[] opcionesNivel = new String[]{"Seleccionar Nivel", "Principiante", "Intermedio", "Avanzado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesNivel);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNivel.setAdapter(adapter);
    }

    private void setupViewModels() {
        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        clienteViewModel.obtenerActivos().observe(this, clientes -> {
            this.listaClientes = clientes != null ? clientes : new ArrayList<>();
            poblarSpinnerClientes();
        });

        if (isModoEdicion()) {
            rutinaViewModel.buscarPorId(idRutinaEditar).observe(this, rutina -> {
                if (rutina != null && rutinaEdicion == null) {
                    this.rutinaEdicion = rutina;
                    cargarDatosEdicion(rutina);
                }
            });
        }
    }

    private void poblarSpinnerClientes() {
        List<String> items = new ArrayList<>();
        items.add("Seleccionar Cliente");

        for (Cliente c : listaClientes) {
            String full = ((c.getNombres() != null ? c.getNombres() : "") + " " + (c.getApellidos() != null ? c.getApellidos() : "")).trim();
            items.add(full + " (DNI: " + c.getDni() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCliente.setAdapter(adapter);

        if (isModoEdicion() && rutinaEdicion != null) {
            seleccionarClienteEnSpinner(rutinaEdicion.getIdCliente());
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        etFechaInicio.setOnClickListener(v -> mostrarDatePicker());
        btnGuardarRutina.setOnClickListener(v -> procesarGuardadoOEdicion());
    }

    private void mostrarDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (fechaInicioSeleccionada != null) {
            cal.set(fechaInicioSeleccionada.getYear(), fechaInicioSeleccionada.getMonthValue() - 1, fechaInicioSeleccionada.getDayOfMonth());
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            fechaInicioSeleccionada = LocalDate.of(year, month + 1, dayOfMonth);
            etFechaInicio.setText(fechaInicioSeleccionada.format(fmt));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void cargarDatosEdicion(Rutina rutina) {
        etNombre.setText(rutina.getNombre() != null ? rutina.getNombre() : "");
        etObjetivo.setText(rutina.getObjetivo() != null ? rutina.getObjetivo() : "");

        if (rutina.getFechaInicio() != null) {
            try {
                fechaInicioSeleccionada = rutina.getFechaInicioLocalDate();
            } catch (Exception e) {
                fechaInicioSeleccionada = LocalDate.now();
            }
            etFechaInicio.setText(fechaInicioSeleccionada.format(fmt));
        }

        if (rutina.getNivel() != null) {
            String n = rutina.getNivel();
            String[] niveles = new String[]{"Seleccionar Nivel", "Principiante", "Intermedio", "Avanzado"};
            for (int k = 0; k < niveles.length; k++) {
                if (niveles[k].equalsIgnoreCase(n)) {
                    spNivel.setSelection(k);
                    break;
                }
            }
        }

        seleccionarClienteEnSpinner(rutina.getIdCliente());
    }

    private void seleccionarClienteEnSpinner(String idCliente) {
        if (idCliente == null) return;
        for (int i = 0; i < listaClientes.size(); i++) {
            Cliente c = listaClientes.get(i);
            if (idCliente.equals(c.getId()) || idCliente.equals(String.valueOf(c.getIdCliente()))) {
                spCliente.setSelection(i + 1);
                break;
            }
        }
    }

    private void procesarGuardadoOEdicion() {
        if (isGuardando) return;

        tilNombre.setError(null);
        tilObjetivo.setError(null);
        tilFechaInicio.setError(null);

        int posCliente = spCliente.getSelectedItemPosition();
        if (posCliente == 0 || (posCliente - 1) >= listaClientes.size()) {
            Toast.makeText(this, "Por favor, selecciona un cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        Cliente clienteSel = listaClientes.get(posCliente - 1);

        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        if (TextUtils.isEmpty(nombre)) {
            tilNombre.setError("El nombre de la rutina es obligatorio");
            etNombre.requestFocus();
            return;
        }

        String objetivo = etObjetivo.getText() != null ? etObjetivo.getText().toString().trim() : "";
        if (TextUtils.isEmpty(objetivo)) {
            tilObjetivo.setError("El objetivo de la rutina es obligatorio");
            etObjetivo.requestFocus();
            return;
        }

        int posNivel = spNivel.getSelectedItemPosition();
        if (posNivel == 0) {
            Toast.makeText(this, "Por favor, selecciona el nivel de la rutina", Toast.LENGTH_SHORT).show();
            return;
        }
        String nivelSel = spNivel.getSelectedItem().toString();

        isGuardando = true;
        btnGuardarRutina.setEnabled(false);

        String idCliente = clienteSel.getId() != null ? clienteSel.getId() : String.valueOf(clienteSel.getIdCliente());
        String nombreCliente = ((clienteSel.getNombres() != null ? clienteSel.getNombres() : "") + " " + (clienteSel.getApellidos() != null ? clienteSel.getApellidos() : "")).trim();

        if (isModoEdicion() && rutinaEdicion != null) {
            rutinaEdicion.setIdCliente(idCliente);
            rutinaEdicion.setNombreCliente(nombreCliente);
            rutinaEdicion.setNombre(nombre);
            rutinaEdicion.setObjetivo(objetivo);
            rutinaEdicion.setNivel(nivelSel);
            rutinaEdicion.setFechaInicio(fechaInicioSeleccionada.toString());

            rutinaViewModel.actualizar(rutinaEdicion, new RutinaRepository.OnResultListener<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    isGuardando = false;
                    btnGuardarRutina.setEnabled(true);
                    Toast.makeText(RegistrarRutinaActivity.this, "¡Rutina actualizada exitosamente en Firestore!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrarRutinaActivity.this, RutinaDetalleActivity.class);
                    intent.putExtra("idRutina", rutinaEdicion.getId());
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    isGuardando = false;
                    btnGuardarRutina.setEnabled(true);
                    Toast.makeText(RegistrarRutinaActivity.this, "Error al actualizar en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Rutina nuevaRutina = new Rutina(
                    idCliente,
                    nombreCliente,
                    nombre,
                    objetivo,
                    nivelSel,
                    fechaInicioSeleccionada,
                    true
            );

            rutinaViewModel.insertar(nuevaRutina, new RutinaRepository.OnResultListener<String>() {
                @Override
                public void onSuccess(String idGenerado) {
                    isGuardando = false;
                    btnGuardarRutina.setEnabled(true);
                    Toast.makeText(RegistrarRutinaActivity.this, "¡Rutina creada exitosamente en Firestore!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrarRutinaActivity.this, RutinaDetalleActivity.class);
                    intent.putExtra("idRutina", idGenerado);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    isGuardando = false;
                    btnGuardarRutina.setEnabled(true);
                    Toast.makeText(RegistrarRutinaActivity.this, "Error al crear en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
