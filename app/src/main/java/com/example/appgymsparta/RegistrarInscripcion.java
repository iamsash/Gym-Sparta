package com.example.appgymsparta;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.appgymsparta.data.entity.Inscripcion;
import com.example.appgymsparta.data.entity.Membresia;
import com.example.appgymsparta.data.repository.InscripcionRepository;
import com.example.appgymsparta.viewmodel.ClienteViewModel;
import com.example.appgymsparta.viewmodel.InscripcionViewModel;
import com.example.appgymsparta.viewmodel.MembresiaViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegistrarInscripcion extends AppCompatActivity {

    private InscripcionViewModel inscripcionViewModel;
    private ClienteViewModel clienteViewModel;
    private MembresiaViewModel membresiaViewModel;

    private Inscripcion inscripcionEdicion;
    private String idInscripcionEditar = null;

    private List<Cliente> listaClientes = new ArrayList<>();
    private List<Membresia> listaMembresias = new ArrayList<>();

    private ImageView btnBack;
    private TextView tvHeaderTitle;
    private TextView tvHeaderSubtitle;
    private Spinner spCliente;
    private Spinner spMembresia;
    private TextInputLayout tilFechaInicio;
    private TextInputLayout tilFechaVencimiento;
    private TextInputLayout tilPrecioPagado;
    private TextInputEditText etFechaInicio;
    private TextInputEditText etFechaVencimiento;
    private TextInputEditText etPrecioPagado;
    private MaterialButton btnGuardarInscripcion;
    private MaterialButton btnCancelar;

    private LocalDate fechaInicioSeleccionada = LocalDate.now();
    private LocalDate fechaVencimientoCalculada;
    private Membresia membresiaSeleccionada;
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private boolean isGuardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_inscripciones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("idInscripcion")) {
            idInscripcionEditar = getIntent().getStringExtra("idInscripcion");
        }

        initViews();
        setupViewModels();
        setupListeners();

        if (isModoEdicion()) {
            tvHeaderTitle.setText("EDITAR INSCRIPCIÓN");
            tvHeaderSubtitle.setText("Actualiza las condiciones de la suscripción");
            btnGuardarInscripcion.setText("ACTUALIZAR INSCRIPCIÓN");
        } else {
            actualizarFechasYPrecio();
        }
    }

    private boolean isModoEdicion() {
        return idInscripcionEditar != null && !idInscripcionEditar.trim().isEmpty();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);
        spCliente = findViewById(R.id.spCliente);
        spMembresia = findViewById(R.id.spMembresia);
        tilFechaInicio = findViewById(R.id.tilFechaInicio);
        tilFechaVencimiento = findViewById(R.id.tilFechaVencimiento);
        tilPrecioPagado = findViewById(R.id.tilPrecioPagado);
        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaVencimiento = findViewById(R.id.etFechaVencimiento);
        etPrecioPagado = findViewById(R.id.etPrecioPagado);
        btnGuardarInscripcion = findViewById(R.id.btnGuardarInscripcion);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void setupViewModels() {
        inscripcionViewModel = new ViewModelProvider(this).get(InscripcionViewModel.class);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        membresiaViewModel = new ViewModelProvider(this).get(MembresiaViewModel.class);

        // Cargar Clientes Activos reales desde Firestore
        clienteViewModel.obtenerActivos().observe(this, clientes -> {
            this.listaClientes = clientes != null ? clientes : new ArrayList<>();
            poblarSpinnerClientes();
        });

        // Cargar Membresías Activas reales desde Firestore
        membresiaViewModel.obtenerActivas().observe(this, membresias -> {
            this.listaMembresias = membresias != null ? membresias : new ArrayList<>();
            poblarSpinnerMembresias();
        });

        // Si estamos en modo edición, cargar la inscripción desde Firestore
        if (isModoEdicion()) {
            inscripcionViewModel.buscarPorId(idInscripcionEditar).observe(this, inscripcion -> {
                if (inscripcion != null && inscripcionEdicion == null) {
                    this.inscripcionEdicion = inscripcion;
                    cargarDatosEdicion(inscripcion);
                }
            });
        }
    }

    private void poblarSpinnerClientes() {
        List<String> nombresClientes = new ArrayList<>();
        nombresClientes.add("Seleccionar Cliente");

        for (Cliente c : listaClientes) {
            String nombre = ((c.getNombres() != null ? c.getNombres() : "") + " " + (c.getApellidos() != null ? c.getApellidos() : "")).trim();
            nombresClientes.add(nombre + " (DNI: " + c.getDni() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresClientes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCliente.setAdapter(adapter);

        if (isModoEdicion() && inscripcionEdicion != null) {
            seleccionarClienteEnSpinner(inscripcionEdicion.getIdCliente());
        }
    }

    private void poblarSpinnerMembresias() {
        List<String> nombresMembresias = new ArrayList<>();
        nombresMembresias.add("Seleccionar Membresía");

        for (Membresia m : listaMembresias) {
            nombresMembresias.add(m.getNombre() + " (" + m.getDuracionDias() + " días - S/ " + String.format(Locale.US, "%.2f", m.getPrecio()) + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresMembresias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMembresia.setAdapter(adapter);

        if (isModoEdicion() && inscripcionEdicion != null) {
            seleccionarMembresiaEnSpinner(inscripcionEdicion.getIdMembresia());
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnGuardarInscripcion.setOnClickListener(v -> procesarGuardadoOEdicion());

        // DatePicker para fechaInicio
        etFechaInicio.setOnClickListener(v -> mostrarDatePicker());

        // Cambio de selección en Spinner Membresía -> recalcular vencimiento y precio
        spMembresia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && (position - 1) < listaMembresias.size()) {
                    membresiaSeleccionada = listaMembresias.get(position - 1);
                    actualizarFechasYPrecio();
                } else {
                    membresiaSeleccionada = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void mostrarDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (fechaInicioSeleccionada != null) {
            cal.set(fechaInicioSeleccionada.getYear(), fechaInicioSeleccionada.getMonthValue() - 1, fechaInicioSeleccionada.getDayOfMonth());
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            fechaInicioSeleccionada = LocalDate.of(year, month + 1, dayOfMonth);
            actualizarFechasYPrecio();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void actualizarFechasYPrecio() {
        etFechaInicio.setText(fechaInicioSeleccionada.format(fmt));

        if (membresiaSeleccionada != null) {
            fechaVencimientoCalculada = fechaInicioSeleccionada.plusDays(membresiaSeleccionada.getDuracionDias());
            etFechaVencimiento.setText(fechaVencimientoCalculada.format(fmt));

            if (!isModoEdicion()) {
                etPrecioPagado.setText(String.format(Locale.US, "%.2f", membresiaSeleccionada.getPrecio()));
            }
        } else {
            etFechaVencimiento.setText("-");
        }
    }

    private void cargarDatosEdicion(Inscripcion inscripcion) {
        if (inscripcion.getFechaInicioLocalDate() != null) {
            fechaInicioSeleccionada = inscripcion.getFechaInicioLocalDate();
        }
        if (inscripcion.getFechaVencimientoLocalDate() != null) {
            fechaVencimientoCalculada = inscripcion.getFechaVencimientoLocalDate();
        }

        etFechaInicio.setText(fechaInicioSeleccionada.format(fmt));
        etFechaVencimiento.setText(fechaVencimientoCalculada != null ? fechaVencimientoCalculada.format(fmt) : "-");
        etPrecioPagado.setText(String.format(Locale.US, "%.2f", inscripcion.getPrecioPagado()));

        seleccionarClienteEnSpinner(inscripcion.getIdCliente());
        seleccionarMembresiaEnSpinner(inscripcion.getIdMembresia());
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

    private void seleccionarMembresiaEnSpinner(String idMembresia) {
        if (idMembresia == null) return;
        for (int i = 0; i < listaMembresias.size(); i++) {
            Membresia m = listaMembresias.get(i);
            if (idMembresia.equals(m.getId()) || idMembresia.equals(String.valueOf(m.getIdMembresia()))) {
                spMembresia.setSelection(i + 1);
                membresiaSeleccionada = m;
                break;
            }
        }
    }

    private void procesarGuardadoOEdicion() {
        if (isGuardando) return;

        tilFechaInicio.setError(null);
        tilPrecioPagado.setError(null);

        int posCliente = spCliente.getSelectedItemPosition();
        int posMembresia = spMembresia.getSelectedItemPosition();

        if (posCliente == 0 || (posCliente - 1) >= listaClientes.size()) {
            Toast.makeText(this, "Por favor, selecciona un cliente para la inscripción", Toast.LENGTH_SHORT).show();
            return;
        }

        if (posMembresia == 0 || (posMembresia - 1) >= listaMembresias.size()) {
            Toast.makeText(this, "Por favor, selecciona una membresía", Toast.LENGTH_SHORT).show();
            return;
        }

        Cliente clienteSel = listaClientes.get(posCliente - 1);
        Membresia membresiaSel = listaMembresias.get(posMembresia - 1);

        String precioStr = etPrecioPagado.getText() != null ? etPrecioPagado.getText().toString().trim() : "";
        if (TextUtils.isEmpty(precioStr)) {
            tilPrecioPagado.setError("El precio pagado es obligatorio");
            etPrecioPagado.requestFocus();
            return;
        }

        double precioPagado;
        try {
            precioPagado = Double.parseDouble(precioStr);
            if (precioPagado < 0) {
                tilPrecioPagado.setError("El precio pagado no puede ser negativo");
                etPrecioPagado.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            tilPrecioPagado.setError("Ingresa un monto válido");
            etPrecioPagado.requestFocus();
            return;
        }

        // Recalcular vencimiento si hiciera falta
        if (fechaVencimientoCalculada == null) {
            fechaVencimientoCalculada = fechaInicioSeleccionada.plusDays(membresiaSel.getDuracionDias());
        }

        String estadoInscripcion = "ACTIVA";
        if (fechaVencimientoCalculada.isBefore(LocalDate.now())) {
            estadoInscripcion = "VENCIDA";
        }

        isGuardando = true;
        btnGuardarInscripcion.setEnabled(false);

        String nombreCliente = ((clienteSel.getNombres() != null ? clienteSel.getNombres() : "") + " " + (clienteSel.getApellidos() != null ? clienteSel.getApellidos() : "")).trim();
        String idCliente = clienteSel.getId() != null ? clienteSel.getId() : String.valueOf(clienteSel.getIdCliente());
        String idMembresia = membresiaSel.getId() != null ? membresiaSel.getId() : String.valueOf(membresiaSel.getIdMembresia());

        if (isModoEdicion() && inscripcionEdicion != null) {
            // Actualización en Firestore
            inscripcionEdicion.setIdCliente(idCliente);
            inscripcionEdicion.setIdMembresia(idMembresia);
            inscripcionEdicion.setNombreCliente(nombreCliente);
            inscripcionEdicion.setDniCliente(clienteSel.getDni());
            inscripcionEdicion.setNombreMembresia(membresiaSel.getNombre());
            inscripcionEdicion.setFechaInicio(fechaInicioSeleccionada.toString());
            inscripcionEdicion.setFechaVencimiento(fechaVencimientoCalculada.toString());
            inscripcionEdicion.setPrecioPagado(precioPagado);
            inscripcionEdicion.setEstado(estadoInscripcion);

            inscripcionViewModel.actualizar(inscripcionEdicion, new InscripcionRepository.OnResultListener<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    isGuardando = false;
                    btnGuardarInscripcion.setEnabled(true);
                    Toast.makeText(RegistrarInscripcion.this, "¡Inscripción actualizada exitosamente en Firestore!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    isGuardando = false;
                    btnGuardarInscripcion.setEnabled(true);
                    Toast.makeText(RegistrarInscripcion.this, "Error al actualizar en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Nuevo Registro en Firestore
            Inscripcion nuevaInscripcion = new Inscripcion(
                    idCliente,
                    idMembresia,
                    nombreCliente,
                    clienteSel.getDni(),
                    membresiaSel.getNombre(),
                    fechaInicioSeleccionada,
                    fechaVencimientoCalculada,
                    precioPagado,
                    estadoInscripcion
            );

            inscripcionViewModel.insertar(nuevaInscripcion, new InscripcionRepository.OnResultListener<String>() {
                @Override
                public void onSuccess(String idGenerado) {
                    isGuardando = false;
                    btnGuardarInscripcion.setEnabled(true);
                    Toast.makeText(RegistrarInscripcion.this, "¡Inscripción registrada exitosamente en Firestore!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    isGuardando = false;
                    btnGuardarInscripcion.setEnabled(true);
                    Toast.makeText(RegistrarInscripcion.this, "Error al registrar en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
