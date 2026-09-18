package com.example.appgymsparta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgymsparta.adapter.DetalleRutinaAdapter;
import com.example.appgymsparta.data.entity.DetalleRutina;
import com.example.appgymsparta.data.entity.Rutina;
import com.example.appgymsparta.data.repository.DetalleRutinaRepository;
import com.example.appgymsparta.data.repository.RutinaRepository;
import com.example.appgymsparta.viewmodel.ClienteViewModel;
import com.example.appgymsparta.viewmodel.DetalleRutinaViewModel;
import com.example.appgymsparta.viewmodel.RutinaViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RutinaDetalleActivity extends AppCompatActivity {

    private RutinaViewModel rutinaViewModel;
    private ClienteViewModel clienteViewModel;
    private DetalleRutinaViewModel detalleRutinaViewModel;

    private DetalleRutinaAdapter detalleRutinaAdapter;
    private Rutina rutinaActual;
    private String idRutina = null;

    private ImageView btnBack;
    private TextView tvNombreRutinaVal;
    private TextView tvNombreClienteVal;
    private TextView tvStatusBadge;
    private TextView tvObjetivoVal;
    private TextView tvNivelFechaVal;
    private MaterialButton btnEditarRutina;
    private MaterialButton btnCambiarEstado;

    private MaterialButton btnAgregarEjercicio;
    private MaterialButton btnAddFirstEjercicio;
    private RecyclerView rvEjercicios;
    private LinearLayout layoutEmptyEjercicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rutina_detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("idRutina")) {
            idRutina = getIntent().getStringExtra("idRutina");
        }

        if (idRutina == null || idRutina.trim().isEmpty()) {
            Toast.makeText(this, "No se especificó una rutina válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupViewModels();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idRutina != null && rutinaViewModel != null) {
            rutinaViewModel.buscarPorId(idRutina);
            if (detalleRutinaViewModel != null) {
                detalleRutinaViewModel.obtenerPorRutina(idRutina);
            }
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvNombreRutinaVal = findViewById(R.id.tvNombreRutinaVal);
        tvNombreClienteVal = findViewById(R.id.tvNombreClienteVal);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvObjetivoVal = findViewById(R.id.tvObjetivoVal);
        tvNivelFechaVal = findViewById(R.id.tvNivelFechaVal);
        btnEditarRutina = findViewById(R.id.btnEditarRutina);
        btnCambiarEstado = findViewById(R.id.btnCambiarEstado);

        btnAgregarEjercicio = findViewById(R.id.btnAgregarEjercicio);
        btnAddFirstEjercicio = findViewById(R.id.btnAddFirstEjercicio);
        rvEjercicios = findViewById(R.id.rvEjercicios);
        layoutEmptyEjercicios = findViewById(R.id.layoutEmptyEjercicios);
    }

    private void setupRecyclerView() {
        detalleRutinaAdapter = new DetalleRutinaAdapter();
        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));
        rvEjercicios.setAdapter(detalleRutinaAdapter);

        detalleRutinaAdapter.setOnEjercicioActionListener(new DetalleRutinaAdapter.OnEjercicioActionListener() {
            @Override
            public void onEditarEjercicio(DetalleRutina detalle) {
                mostrarDialogoEjercicio(detalle);
            }

            @Override
            public void onEliminarEjercicio(DetalleRutina detalle) {
                confirmarEliminarEjercicio(detalle);
            }
        });
    }

    private void setupViewModels() {
        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        detalleRutinaViewModel = new ViewModelProvider(this).get(DetalleRutinaViewModel.class);

        rutinaViewModel.buscarPorId(idRutina).observe(this, rutina -> {
            if (rutina != null) {
                this.rutinaActual = rutina;
                mostrarDatosRutina(rutina);
            } else {
                Toast.makeText(RutinaDetalleActivity.this, "Rutina no encontrada en Cloud Firestore", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        detalleRutinaViewModel.obtenerPorRutina(idRutina).observe(this, this::actualizarListaEjercicios);
    }

    private void mostrarDatosRutina(Rutina rutina) {
        tvNombreRutinaVal.setText(rutina.getNombre() != null ? rutina.getNombre() : "Rutina de Entrenamiento");

        if (rutina.getNombreCliente() != null && !rutina.getNombreCliente().isEmpty()) {
            tvNombreClienteVal.setText("Cliente: " + rutina.getNombreCliente());
        } else {
            clienteViewModel.buscarPorId(rutina.getIdCliente()).observe(this, cliente -> {
                if (cliente != null) {
                    String full = ((cliente.getNombres() != null ? cliente.getNombres() : "") + " " + (cliente.getApellidos() != null ? cliente.getApellidos() : "")).trim();
                    tvNombreClienteVal.setText("Cliente: " + full);
                } else {
                    tvNombreClienteVal.setText("Cliente");
                }
            });
        }

        tvObjetivoVal.setText(rutina.getObjetivo() != null && !rutina.getObjetivo().trim().isEmpty()
                ? rutina.getObjetivo() : "Sin objetivo especificado");

        String nivel = rutina.getNivel() != null ? rutina.getNivel() : "General";
        String fecha = rutina.getFechaInicio() != null ? rutina.getFechaInicio() : "-";
        tvNivelFechaVal.setText("Nivel: " + nivel + " • Inicio: " + fecha);

        if (rutina.isEstado()) {
            tvStatusBadge.setText("● ACTIVA");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_active);
            tvStatusBadge.setTextColor(getColor(R.color.badge_green));
            btnCambiarEstado.setText("DESACTIVAR");
        } else {
            tvStatusBadge.setText("● INACTIVA");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_inactive);
            tvStatusBadge.setTextColor(getColor(R.color.red_accent));
            btnCambiarEstado.setText("ACTIVAR");
        }
    }

    private void actualizarListaEjercicios(List<DetalleRutina> ejercicios) {
        int total = ejercicios != null ? ejercicios.size() : 0;

        if (total == 0) {
            rvEjercicios.setVisibility(View.GONE);
            layoutEmptyEjercicios.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyEjercicios.setVisibility(View.GONE);
            rvEjercicios.setVisibility(View.VISIBLE);
            detalleRutinaAdapter.setDetalles(ejercicios);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEditarRutina.setOnClickListener(v -> {
            if (rutinaActual != null) {
                Intent intent = new Intent(RutinaDetalleActivity.this, RegistrarRutinaActivity.class);
                intent.putExtra("idRutina", rutinaActual.getId());
                startActivity(intent);
            }
        });

        btnCambiarEstado.setOnClickListener(v -> confirmarCambioEstado());

        btnAgregarEjercicio.setOnClickListener(v -> mostrarDialogoEjercicio(null));
        btnAddFirstEjercicio.setOnClickListener(v -> mostrarDialogoEjercicio(null));
    }

    private void confirmarCambioEstado() {
        if (rutinaActual == null) return;

        boolean nuevoEstado = !rutinaActual.isEstado();
        String accion = nuevoEstado ? "activar" : "desactivar";

        new AlertDialog.Builder(this)
                .setTitle("Confirmar Acción")
                .setMessage("¿Estás seguro de que deseas " + accion + " esta rutina?")
                .setPositiveButton(accion.toUpperCase(), (dialog, which) -> cambiarEstado(nuevoEstado))
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void cambiarEstado(boolean nuevoEstado) {
        if (rutinaActual == null) return;
        rutinaActual.setEstado(nuevoEstado);

        rutinaViewModel.actualizar(rutinaActual, new RutinaRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                String msg = nuevoEstado ? "Rutina activada correctamente" : "Rutina desactivada correctamente";
                Toast.makeText(RutinaDetalleActivity.this, msg, Toast.LENGTH_SHORT).show();
                mostrarDatosRutina(rutinaActual);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(RutinaDetalleActivity.this, "Error al actualizar la rutina en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoEjercicio(DetalleRutina ejercicioEditar) {
        boolean esEdicion = ejercicioEditar != null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ejercicio_form, null);
        builder.setView(dialogView);

        Spinner spDia = dialogView.findViewById(R.id.spDia);
        EditText etEjercicio = dialogView.findViewById(R.id.etEjercicio);
        EditText etGrupoMuscular = dialogView.findViewById(R.id.etGrupoMuscular);
        EditText etSeries = dialogView.findViewById(R.id.etSeries);
        EditText etRepeticiones = dialogView.findViewById(R.id.etRepeticiones);
        EditText etPeso = dialogView.findViewById(R.id.etPeso);
        EditText etDescanso = dialogView.findViewById(R.id.etDescanso);

        String[] dias = new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dias);
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDia.setAdapter(adapterDias);

        if (esEdicion) {
            etEjercicio.setText(ejercicioEditar.getEjercicio());
            etGrupoMuscular.setText(ejercicioEditar.getGrupoMuscular());
            etSeries.setText(String.valueOf(ejercicioEditar.getSeries()));
            etRepeticiones.setText(String.valueOf(ejercicioEditar.getRepeticiones()));
            etPeso.setText(String.valueOf(ejercicioEditar.getPeso()));
            etDescanso.setText(String.valueOf(ejercicioEditar.getDescanso()));

            if (ejercicioEditar.getDia() != null) {
                for (int d = 0; d < dias.length; d++) {
                    if (dias[d].equalsIgnoreCase(ejercicioEditar.getDia())) {
                        spDia.setSelection(d);
                        break;
                    }
                }
            }
        }

        builder.setTitle(esEdicion ? "EDITAR EJERCICIO" : "AGREGAR EJERCICIO");
        builder.setPositiveButton(esEdicion ? "ACTUALIZAR" : "GUARDAR", null);
        builder.setNegativeButton("CANCELAR", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String diaSel = spDia.getSelectedItem().toString();
            String nombreE = etEjercicio.getText().toString().trim();
            String grupoM = etGrupoMuscular.getText().toString().trim();
            String seriesStr = etSeries.getText().toString().trim();
            String repsStr = etRepeticiones.getText().toString().trim();
            String pesoStr = etPeso.getText().toString().trim();
            String descansoStr = etDescanso.getText().toString().trim();

            if (TextUtils.isEmpty(nombreE)) {
                etEjercicio.setError("Ingresa el nombre del ejercicio");
                etEjercicio.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(grupoM)) {
                etGrupoMuscular.setError("Ingresa el grupo muscular");
                etGrupoMuscular.requestFocus();
                return;
            }

            int series;
            try {
                series = Integer.parseInt(seriesStr);
                if (series <= 0) {
                    etSeries.setError("Las series deben ser mayor a 0");
                    etSeries.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etSeries.setError("Ingresa un número válido de series");
                etSeries.requestFocus();
                return;
            }

            int reps;
            try {
                reps = Integer.parseInt(repsStr);
                if (reps <= 0) {
                    etRepeticiones.setError("Las repeticiones deben ser mayor a 0");
                    etRepeticiones.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etRepeticiones.setError("Ingresa un número válido de repeticiones");
                etRepeticiones.requestFocus();
                return;
            }

            double peso = 0.0;
            if (!TextUtils.isEmpty(pesoStr)) {
                try {
                    peso = Double.parseDouble(pesoStr);
                    if (peso < 0) {
                        etPeso.setError("El peso no puede ser negativo");
                        etPeso.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    etPeso.setError("Ingresa un peso válido");
                    etPeso.requestFocus();
                    return;
                }
            }

            int descanso = 0;
            if (!TextUtils.isEmpty(descansoStr)) {
                try {
                    descanso = Integer.parseInt(descansoStr);
                    if (descanso < 0) {
                        etDescanso.setError("El descanso no puede ser negativo");
                        etDescanso.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    etDescanso.setError("Ingresa segundos válidos");
                    etDescanso.requestFocus();
                    return;
                }
            }

            if (esEdicion) {
                ejercicioEditar.setDia(diaSel);
                ejercicioEditar.setEjercicio(nombreE);
                ejercicioEditar.setGrupoMuscular(grupoM);
                ejercicioEditar.setSeries(series);
                ejercicioEditar.setRepeticiones(reps);
                ejercicioEditar.setPeso(peso);
                ejercicioEditar.setDescanso(descanso);

                detalleRutinaViewModel.actualizar(ejercicioEditar, new DetalleRutinaRepository.OnResultListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        Toast.makeText(RutinaDetalleActivity.this, "¡Ejercicio actualizado en Firestore!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        detalleRutinaViewModel.obtenerPorRutina(idRutina);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(RutinaDetalleActivity.this, "Error al actualizar ejercicio en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                DetalleRutina nuevoDetalle = new DetalleRutina(
                        idRutina,
                        nombreE,
                        grupoM,
                        diaSel,
                        series,
                        reps,
                        peso,
                        descanso
                );

                detalleRutinaViewModel.insertar(nuevoDetalle, new DetalleRutinaRepository.OnResultListener<String>() {
                    @Override
                    public void onSuccess(String idGenerado) {
                        Toast.makeText(RutinaDetalleActivity.this, "¡Ejercicio agregado en Firestore!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        detalleRutinaViewModel.obtenerPorRutina(idRutina);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(RutinaDetalleActivity.this, "Error al agregar ejercicio en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void confirmarEliminarEjercicio(DetalleRutina detalle) {
        if (detalle == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Eliminar Ejercicio")
                .setMessage("¿Estás seguro de que deseas eliminar este ejercicio de la rutina?")
                .setPositiveButton("ELIMINAR", (dialog, which) -> {
                    detalleRutinaViewModel.eliminar(detalle, new DetalleRutinaRepository.OnResultListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Toast.makeText(RutinaDetalleActivity.this, "Ejercicio eliminado de Firestore", Toast.LENGTH_SHORT).show();
                            detalleRutinaViewModel.obtenerPorRutina(idRutina);
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(RutinaDetalleActivity.this, "Error al eliminar ejercicio de Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }
}
