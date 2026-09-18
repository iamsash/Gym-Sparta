package com.example.appgymsparta;

import android.os.Bundle;
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

import com.example.appgymsparta.data.entity.Asistencia;
import com.example.appgymsparta.data.entity.Cliente;
import com.example.appgymsparta.data.entity.Inscripcion;
import com.example.appgymsparta.data.entity.Membresia;
import com.example.appgymsparta.data.repository.AsistenciaRepository;
import com.example.appgymsparta.data.repository.ClienteRepository;
import com.example.appgymsparta.data.repository.InscripcionRepository;
import com.example.appgymsparta.data.repository.MembresiaRepository;
import com.example.appgymsparta.viewmodel.AsistenciaViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrarAsistencias extends AppCompatActivity {

    private AsistenciaViewModel asistenciaViewModel;

    private List<Cliente> clientesElegibles = new ArrayList<>();
    private Map<String, Inscripcion> mapInscripcionesVigentes = new HashMap<>();
    private Map<String, Membresia> mapMembresias = new HashMap<>();

    private ImageView btnBack;
    private Spinner spCliente;
    private LinearLayout cardInfoCliente;
    private TextView tvNombreClienteVal;
    private TextView tvMembresiaVal;
    private TextView tvVencimientoVal;
    private TextView tvFechaHoraVal;
    private TextInputEditText etObservacion;
    private MaterialButton btnRegistrarIngreso;
    private MaterialButton btnCancelar;

    private Cliente clienteSeleccionado;
    private boolean isGuardando = false;

    private DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_asistencias);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupViewModels();
        setupListeners();
        mostrarFechaHoraActual();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        spCliente = findViewById(R.id.spCliente);
        cardInfoCliente = findViewById(R.id.cardInfoCliente);
        tvNombreClienteVal = findViewById(R.id.tvNombreClienteVal);
        tvMembresiaVal = findViewById(R.id.tvMembresiaVal);
        tvVencimientoVal = findViewById(R.id.tvVencimientoVal);
        tvFechaHoraVal = findViewById(R.id.tvFechaHoraVal);
        etObservacion = findViewById(R.id.etObservacion);
        btnRegistrarIngreso = findViewById(R.id.btnRegistrarIngreso);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void mostrarFechaHoraActual() {
        LocalDate hoy = LocalDate.now();
        LocalTime hora = LocalTime.now();

        String turno = "MAÑANA";
        if (!hora.isBefore(LocalTime.of(12, 0)) && hora.isBefore(LocalTime.of(18, 0))) {
            turno = "TARDE";
        } else if (!hora.isBefore(LocalTime.of(18, 0))) {
            turno = "NOCHE";
        }

        String texto = "📅 " + hoy.format(fmtFecha) + " • " + hora.format(fmtHora) + " hs (" + turno + ")";
        tvFechaHoraVal.setText(texto);
    }

    private void setupViewModels() {
        asistenciaViewModel = new ViewModelProvider(this).get(AsistenciaViewModel.class);

        // Cargar clientes con inscripción ACTIVA Y VIGENTE desde Firestore
        ClienteRepository clienteRepo = new ClienteRepository(getApplicationContext());
        InscripcionRepository inscripcionRepo = new InscripcionRepository(getApplicationContext());
        MembresiaRepository membresiaRepo = new MembresiaRepository(getApplicationContext());

        clienteRepo.obtenerActivos(new ClienteRepository.OnResultListener<List<Cliente>>() {
            @Override
            public void onSuccess(List<Cliente> todosClientes) {
                membresiaRepo.obtenerTodas(new MembresiaRepository.OnResultListener<List<Membresia>>() {
                    @Override
                    public void onSuccess(List<Membresia> todasMembresias) {
                        inscripcionRepo.obtenerTodas(new InscripcionRepository.OnResultListener<List<Inscripcion>>() {
                            @Override
                            public void onSuccess(List<Inscripcion> todasInscripciones) {
                                Map<String, Membresia> mAcc = new HashMap<>();
                                if (todasMembresias != null) {
                                    for (Membresia m : todasMembresias) {
                                        if (m.getId() != null) mAcc.put(m.getId(), m);
                                        mAcc.put(String.valueOf(m.getIdMembresia()), m);
                                    }
                                }

                                LocalDate hoy = LocalDate.now();
                                Map<String, Inscripcion> mapInscValidas = new HashMap<>();

                                if (todasInscripciones != null) {
                                    for (Inscripcion i : todasInscripciones) {
                                        String est = i.getEstado() != null ? i.getEstado().toUpperCase() : "";
                                        boolean esActiva = "ACTIVA".equals(est) || "ACTIVO".equals(est);
                                        boolean esVigente = i.getFechaVencimientoLocalDate() != null && !i.getFechaVencimientoLocalDate().isBefore(hoy);

                                        if (esActiva && esVigente) {
                                            if (i.getIdCliente() != null && !mapInscValidas.containsKey(i.getIdCliente())) {
                                                mapInscValidas.put(i.getIdCliente(), i);
                                            }
                                        }
                                    }
                                }

                                List<Cliente> elegibles = new ArrayList<>();
                                if (todosClientes != null) {
                                    for (Cliente c : todosClientes) {
                                        String cId = c.getId() != null ? c.getId() : String.valueOf(c.getIdCliente());
                                        if (mapInscValidas.containsKey(cId)) {
                                            elegibles.add(c);
                                        }
                                    }
                                }

                                runOnUiThread(() -> {
                                    RegistrarAsistencias.this.clientesElegibles = elegibles;
                                    RegistrarAsistencias.this.mapInscripcionesVigentes = mapInscValidas;
                                    RegistrarAsistencias.this.mapMembresias = mAcc;

                                    poblarSpinnerClientes();
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                poblarSpinnerClientes();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        poblarSpinnerClientes();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                poblarSpinnerClientes();
            }
        });
    }

    private void poblarSpinnerClientes() {
        List<String> items = new ArrayList<>();

        if (clientesElegibles.isEmpty()) {
            items.add("No hay clientes con membresía vigente");
            btnRegistrarIngreso.setEnabled(false);
            Toast.makeText(this, "No hay clientes con membresía vigente para registrar asistencia.\nRegistra o renueva una inscripción para habilitar el ingreso.", Toast.LENGTH_LONG).show();
        } else {
            items.add("Seleccionar Cliente con Membresía Vigente");
            btnRegistrarIngreso.setEnabled(true);

            for (Cliente c : clientesElegibles) {
                String full = ((c.getNombres() != null ? c.getNombres() : "") + " " + (c.getApellidos() != null ? c.getApellidos() : "")).trim();
                items.add(full + " (DNI: " + c.getDni() + ")");
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCliente.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());

        spCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && (position - 1) < clientesElegibles.size()) {
                    clienteSeleccionado = clientesElegibles.get(position - 1);
                    mostrarInfoClienteSeleccionado(clienteSeleccionado);
                } else {
                    clienteSeleccionado = null;
                    cardInfoCliente.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRegistrarIngreso.setOnClickListener(v -> procesarRegistroIngreso());
    }

    private void mostrarInfoClienteSeleccionado(Cliente cliente) {
        cardInfoCliente.setVisibility(View.VISIBLE);

        String full = ((cliente.getNombres() != null ? cliente.getNombres() : "") + " " + (cliente.getApellidos() != null ? cliente.getApellidos() : "")).trim();
        tvNombreClienteVal.setText(full);

        String cId = cliente.getId() != null ? cliente.getId() : String.valueOf(cliente.getIdCliente());
        Inscripcion insc = mapInscripcionesVigentes.get(cId);
        if (insc != null) {
            Membresia mem = mapMembresias.get(insc.getIdMembresia());
            String nombreM = mem != null && mem.getNombre() != null ? mem.getNombre() : (insc.getNombreMembresia() != null ? insc.getNombreMembresia() : "Membresía");
            tvMembresiaVal.setText("Plan: " + nombreM + " (" + (mem != null ? mem.getDuracionDias() : 30) + " días)");

            String fVenc = insc.getFechaVencimiento() != null ? insc.getFechaVencimiento() : "-";
            tvVencimientoVal.setText("Vence el: " + fVenc + " (VIGENTE ✓)");
        }
    }

    private void procesarRegistroIngreso() {
        if (isGuardando) return;

        if (clienteSeleccionado == null) {
            Toast.makeText(this, "Por favor, selecciona un cliente para registrar ingreso", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDate hoy = LocalDate.now();
        LocalTime horaIngreso = LocalTime.now();
        String hoyStr = hoy.toString();
        String cId = clienteSeleccionado.getId() != null ? clienteSeleccionado.getId() : String.valueOf(clienteSeleccionado.getIdCliente());
        String nombreCompleto = ((clienteSeleccionado.getNombres() != null ? clienteSeleccionado.getNombres() : "") + " " + (clienteSeleccionado.getApellidos() != null ? clienteSeleccionado.getApellidos() : "")).trim();

        isGuardando = true;
        btnRegistrarIngreso.setEnabled(false);

        // Verificar duplicados para hoy en Firestore
        asistenciaViewModel.contarPorClienteYFecha(cId, hoyStr, new AsistenciaRepository.OnResultListener<Integer>() {
            @Override
            public void onSuccess(Integer yaExisteCount) {
                if (yaExisteCount != null && yaExisteCount > 0) {
                    isGuardando = false;
                    btnRegistrarIngreso.setEnabled(true);
                    Toast.makeText(RegistrarAsistencias.this, "Este cliente ya tiene registrada su asistencia de hoy.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Guardar Asistencia en Firestore
                String obs = etObservacion.getText() != null ? etObservacion.getText().toString().trim() : "";
                Asistencia nuevaAsistencia = new Asistencia(
                        cId,
                        nombreCompleto,
                        hoy,
                        horaIngreso,
                        null,
                        obs
                );

                asistenciaViewModel.registrar(nuevaAsistencia, new AsistenciaRepository.OnResultListener<String>() {
                    @Override
                    public void onSuccess(String idGenerado) {
                        isGuardando = false;
                        btnRegistrarIngreso.setEnabled(true);
                        Toast.makeText(RegistrarAsistencias.this, "¡Asistencia registrada correctamente en Firestore!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        isGuardando = false;
                        btnRegistrarIngreso.setEnabled(true);
                        Toast.makeText(RegistrarAsistencias.this, "Error al registrar asistencia en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                isGuardando = false;
                btnRegistrarIngreso.setEnabled(true);
                Toast.makeText(RegistrarAsistencias.this, "Error de consulta en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
