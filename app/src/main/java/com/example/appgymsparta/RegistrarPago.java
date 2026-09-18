package com.example.appgymsparta;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.appgymsparta.data.entity.Inscripcion;
import com.example.appgymsparta.data.entity.Pago;
import com.example.appgymsparta.data.repository.PagoRepository;
import com.example.appgymsparta.viewmodel.InscripcionViewModel;
import com.example.appgymsparta.viewmodel.PagoViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegistrarPago extends AppCompatActivity {

    private PagoViewModel pagoViewModel;
    private InscripcionViewModel inscripcionViewModel;

    private Pago pagoEdicion;
    private String idPagoEditar = null;

    private List<Inscripcion> listaInscripciones = new ArrayList<>();
    private double saldoPendienteCalculado = 0.0;

    private ImageView btnBack;
    private TextView tvHeaderTitle;
    private TextView tvHeaderSubtitle;
    private Spinner spTipoPago;
    private LinearLayout layoutSeccionInscripcion;
    private Spinner spInscripcion;
    private Spinner spMetodoPago;
    private TextInputLayout tilMonto;
    private TextInputLayout tilNumeroOperacion;
    private TextInputLayout tilObservacion;
    private TextInputEditText etMonto;
    private TextInputEditText etNumeroOperacion;
    private TextInputEditText etObservacion;
    private MaterialButton btnGuardarPago;
    private MaterialButton btnCancelar;

    private boolean isGuardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_pago);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("idPago")) {
            idPagoEditar = getIntent().getStringExtra("idPago");
        }

        initViews();
        setupSpinnersEstaticos();
        setupViewModels();
        setupListeners();

        if (isModoEdicion()) {
            tvHeaderTitle.setText("EDITAR PAGO");
            tvHeaderSubtitle.setText("Actualiza los datos del pago registrado");
            btnGuardarPago.setText("ACTUALIZAR PAGO");
        }
    }

    private boolean isModoEdicion() {
        return idPagoEditar != null && !idPagoEditar.trim().isEmpty();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);
        spTipoPago = findViewById(R.id.spTipoPago);
        layoutSeccionInscripcion = findViewById(R.id.layoutSeccionInscripcion);
        spInscripcion = findViewById(R.id.spInscripcion);
        spMetodoPago = findViewById(R.id.spMetodoPago);
        tilMonto = findViewById(R.id.tilMonto);
        tilNumeroOperacion = findViewById(R.id.tilNumeroOperacion);
        tilObservacion = findViewById(R.id.tilObservacion);
        etMonto = findViewById(R.id.etMonto);
        etNumeroOperacion = findViewById(R.id.etNumeroOperacion);
        etObservacion = findViewById(R.id.etObservacion);
        btnGuardarPago = findViewById(R.id.btnGuardarPago);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void setupSpinnersEstaticos() {
        // Spinner Tipo de Pago
        String[] opcionesTipo = new String[]{"Membresía", "Acceso diario"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesTipo);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoPago.setAdapter(adapterTipo);

        // Spinner Método de Pago
        String[] opcionesMetodo = new String[]{"Seleccionar", "Efectivo", "Yape / Plin", "Tarjeta", "Transferencia"};
        ArrayAdapter<String> adapterMetodo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesMetodo);
        adapterMetodo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMetodoPago.setAdapter(adapterMetodo);
    }

    private void setupViewModels() {
        pagoViewModel = new ViewModelProvider(this).get(PagoViewModel.class);
        inscripcionViewModel = new ViewModelProvider(this).get(InscripcionViewModel.class);

        // Cargar inscripciones activas directamente desde Firestore
        inscripcionViewModel.obtenerTodas().observe(this, inscripciones -> {
            this.listaInscripciones = inscripciones != null ? inscripciones : new ArrayList<>();
            poblarSpinnerInscripciones();
        });

        if (isModoEdicion()) {
            pagoViewModel.buscarPorId(idPagoEditar).observe(this, pago -> {
                if (pago != null && pagoEdicion == null) {
                    this.pagoEdicion = pago;
                    cargarDatosEdicion(pago);
                }
            });
        }
    }

    private void poblarSpinnerInscripciones() {
        List<String> items = new ArrayList<>();
        items.add("Seleccionar Suscripción / Cliente");

        for (Inscripcion i : listaInscripciones) {
            String c = i.getNombreCliente() != null ? i.getNombreCliente() : "Cliente";
            String m = i.getNombreMembresia() != null ? i.getNombreMembresia() : "Membresía";

            items.add(c + " - " + m + " (Total: S/ " + String.format(Locale.US, "%.2f", i.getPrecioPagado()) + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spInscripcion.setAdapter(adapter);

        if (isModoEdicion() && pagoEdicion != null && pagoEdicion.getIdInscripcion() != null) {
            seleccionarInscripcionEnSpinner(pagoEdicion.getIdInscripcion());
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());

        // Manejar cambio entre "Membresía" y "Acceso diario"
        spTipoPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Membresía
                    layoutSeccionInscripcion.setVisibility(View.VISIBLE);
                    tilMonto.setHint("Monto A Cuenta / Pago *");
                } else { // Acceso diario
                    layoutSeccionInscripcion.setVisibility(View.GONE);
                    tilMonto.setHint("Monto de Pago *");
                    if (!isModoEdicion()) {
                        etMonto.setText("5.00");
                        if (TextUtils.isEmpty(etObservacion.getText())) {
                            etObservacion.setText("Acceso diario");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Al seleccionar una inscripción, consultar sus pagos previos para calcular el saldo pendiente
        spInscripcion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spTipoPago.getSelectedItemPosition() == 0 && position > 0 && (position - 1) < listaInscripciones.size()) {
                    Inscripcion i = listaInscripciones.get(position - 1);
                    calcularSaldoPendienteYSugerirMonto(i);
                } else {
                    saldoPendienteCalculado = 0.0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnGuardarPago.setOnClickListener(v -> procesarGuardadoOEdicion());
    }

    private void calcularSaldoPendienteYSugerirMonto(Inscripcion inscripcion) {
        if (inscripcion == null || inscripcion.getId() == null) return;

        pagoViewModel.obtenerPorInscripcion(inscripcion.getId()).observe(this, pagosInscripcion -> {
            double totalAbonado = 0.0;
            if (pagosInscripcion != null) {
                for (Pago p : pagosInscripcion) {
                    // Si estamos editando un pago existente, excluir su propio monto del cálculo previo
                    if (isModoEdicion() && pagoEdicion != null && p.getId() != null && p.getId().equals(pagoEdicion.getId())) {
                        continue;
                    }
                    totalAbonado += p.getMonto();
                }
            }

            double precioTotal = inscripcion.getPrecioPagado();
            saldoPendienteCalculado = precioTotal - totalAbonado;
            if (saldoPendienteCalculado < 0) {
                saldoPendienteCalculado = 0.0;
            }

            // Sugerir el saldo pendiente como monto en la casilla si es un nuevo pago
            if (!isModoEdicion()) {
                etMonto.setText(String.format(Locale.US, "%.2f", saldoPendienteCalculado));
            }

            tilMonto.setHint(String.format(Locale.US, "Monto A Cuenta (Saldo Pendiente: S/ %.2f) *", saldoPendienteCalculado));
        });
    }

    private void cargarDatosEdicion(Pago pago) {
        boolean esAccesoDiario = pago.getIdInscripcion() == null || "Acceso diario".equalsIgnoreCase(pago.getTipoPago());

        if (esAccesoDiario) {
            spTipoPago.setSelection(1); // Acceso diario
            layoutSeccionInscripcion.setVisibility(View.GONE);
        } else {
            spTipoPago.setSelection(0); // Membresía
            layoutSeccionInscripcion.setVisibility(View.VISIBLE);
            if (pago.getIdInscripcion() != null) {
                seleccionarInscripcionEnSpinner(pago.getIdInscripcion());
            }
        }

        etMonto.setText(String.format(Locale.US, "%.2f", pago.getMonto()));
        etNumeroOperacion.setText(pago.getNumeroOperacion() != null ? pago.getNumeroOperacion() : "");
        etObservacion.setText(pago.getObservacion() != null ? pago.getObservacion() : "");

        if (pago.getMetodoPago() != null) {
            String metodo = pago.getMetodoPago();
            String[] opciones = new String[]{"Seleccionar", "Efectivo", "Yape / Plin", "Tarjeta", "Transferencia"};
            for (int k = 0; k < opciones.length; k++) {
                if (opciones[k].equalsIgnoreCase(metodo)) {
                    spMetodoPago.setSelection(k);
                    break;
                }
            }
        }
    }

    private void seleccionarInscripcionEnSpinner(String idInscripcion) {
        if (idInscripcion == null) return;
        for (int k = 0; k < listaInscripciones.size(); k++) {
            Inscripcion i = listaInscripciones.get(k);
            if (idInscripcion.equals(i.getId()) || idInscripcion.equals(String.valueOf(i.getIdInscripcion()))) {
                spInscripcion.setSelection(k + 1);
                break;
            }
        }
    }

    private void procesarGuardadoOEdicion() {
        if (isGuardando) return;

        tilMonto.setError(null);

        boolean esAccesoDiario = spTipoPago.getSelectedItemPosition() == 1;
        String idInscripcionSeleccionada = null;
        String nombreCliente = "Acceso diario";
        String nombreMembresia = "Pase diario";
        String idCliente = null;
        String tipoPagoGuardar = esAccesoDiario ? "Acceso diario" : "Membresía";

        if (!esAccesoDiario) {
            int posInscripcion = spInscripcion.getSelectedItemPosition();
            if (posInscripcion == 0 || (posInscripcion - 1) >= listaInscripciones.size()) {
                Toast.makeText(this, "Por favor, selecciona una suscripción para el pago de membresía", Toast.LENGTH_SHORT).show();
                return;
            }
            Inscripcion inscripcionSel = listaInscripciones.get(posInscripcion - 1);
            idInscripcionSeleccionada = inscripcionSel.getId();
            idCliente = inscripcionSel.getIdCliente();
            nombreCliente = inscripcionSel.getNombreCliente() != null ? inscripcionSel.getNombreCliente() : "Cliente";
            nombreMembresia = inscripcionSel.getNombreMembresia() != null ? inscripcionSel.getNombreMembresia() : "Membresía";
        }

        int posMetodo = spMetodoPago.getSelectedItemPosition();
        if (posMetodo == 0) {
            Toast.makeText(this, "Por favor, selecciona el método de pago", Toast.LENGTH_SHORT).show();
            return;
        }

        String metodoSel = spMetodoPago.getSelectedItem().toString();

        String montoStr = etMonto.getText() != null ? etMonto.getText().toString().trim() : "";
        if (TextUtils.isEmpty(montoStr)) {
            tilMonto.setError("El monto es obligatorio");
            etMonto.requestFocus();
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoStr);
            if (monto <= 0) {
                tilMonto.setError("El monto debe ser mayor a 0");
                etMonto.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            tilMonto.setError("Ingresa un monto válido");
            etMonto.requestFocus();
            return;
        }

        // Validación de pago parcial: no superar el saldo pendiente (si es pago de membresía)
        if (!esAccesoDiario && saldoPendienteCalculado > 0 && monto > (saldoPendienteCalculado + 0.01)) {
            tilMonto.setError(String.format(Locale.US, "El monto (S/ %.2f) supera el saldo pendiente (S/ %.2f)", monto, saldoPendienteCalculado));
            etMonto.requestFocus();
            return;
        }

        String numOp = etNumeroOperacion.getText() != null ? etNumeroOperacion.getText().toString().trim() : "";
        String obs = etObservacion.getText() != null ? etObservacion.getText().toString().trim() : "";
        if (esAccesoDiario && TextUtils.isEmpty(obs)) {
            obs = "Acceso diario";
        }

        // Prevenir doble clic / envío duplicado
        isGuardando = true;
        btnGuardarPago.setEnabled(false);

        if (isModoEdicion() && pagoEdicion != null) {
            // Actualización en Firestore
            pagoEdicion.setIdInscripcion(idInscripcionSeleccionada);
            pagoEdicion.setIdCliente(idCliente);
            pagoEdicion.setNombreCliente(nombreCliente);
            pagoEdicion.setNombreMembresia(nombreMembresia);
            pagoEdicion.setTipoPago(tipoPagoGuardar);
            pagoEdicion.setMonto(monto);
            pagoEdicion.setMetodoPago(metodoSel);
            pagoEdicion.setNumeroOperacion(numOp);
            pagoEdicion.setObservacion(obs);

            pagoViewModel.actualizar(pagoEdicion, new PagoRepository.OnResultListener<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    isGuardando = false;
                    btnGuardarPago.setEnabled(true);
                    Toast.makeText(RegistrarPago.this, "¡Pago actualizado exitosamente en Firestore!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    isGuardando = false;
                    btnGuardarPago.setEnabled(true);
                    Toast.makeText(RegistrarPago.this, "Error al actualizar en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Nuevo Pago parcial o completo en Firestore
            Pago nuevoPago = new Pago(
                    idInscripcionSeleccionada,
                    idCliente,
                    nombreCliente,
                    nombreMembresia,
                    tipoPagoGuardar,
                    monto,
                    LocalDateTime.now(),
                    metodoSel,
                    numOp,
                    obs
            );

            pagoViewModel.insertar(nuevoPago, new PagoRepository.OnResultListener<String>() {
                @Override
                public void onSuccess(String idGenerado) {
                    isGuardando = false;
                    btnGuardarPago.setEnabled(true);
                    Toast.makeText(RegistrarPago.this, "¡Pago registrado exitosamente en Firestore!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    isGuardando = false;
                    btnGuardarPago.setEnabled(true);
                    Toast.makeText(RegistrarPago.this, "Error al registrar en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
