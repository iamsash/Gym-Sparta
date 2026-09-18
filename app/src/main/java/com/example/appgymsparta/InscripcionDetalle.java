package com.example.appgymsparta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.appgymsparta.data.entity.Inscripcion;
import com.example.appgymsparta.data.entity.Pago;
import com.example.appgymsparta.data.repository.InscripcionRepository;
import com.example.appgymsparta.viewmodel.ClienteViewModel;
import com.example.appgymsparta.viewmodel.InscripcionViewModel;
import com.example.appgymsparta.viewmodel.MembresiaViewModel;
import com.example.appgymsparta.viewmodel.PagoViewModel;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.util.Locale;

public class InscripcionDetalle extends AppCompatActivity {

    private InscripcionViewModel inscripcionViewModel;
    private ClienteViewModel clienteViewModel;
    private MembresiaViewModel membresiaViewModel;
    private PagoViewModel pagoViewModel;

    private Inscripcion inscripcionActual;
    private String idInscripcion = null;

    private ImageView btnBack;
    private TextView tvNombreClienteVal;
    private TextView tvNombreMembresiaVal;
    private TextView tvStatusBadge;
    private TextView tvPrecioPagadoVal;
    private TextView tvFechaInicioVal;
    private TextView tvFechaVencimientoVal;
    private MaterialButton btnEditarInscripcion;
    private MaterialButton btnCancelarInscripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inscripcion_detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("idInscripcion")) {
            idInscripcion = getIntent().getStringExtra("idInscripcion");
        }

        if (idInscripcion == null || idInscripcion.trim().isEmpty()) {
            Toast.makeText(this, "No se especificó una inscripción válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupViewModels();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idInscripcion != null && inscripcionViewModel != null) {
            inscripcionViewModel.buscarPorId(idInscripcion);
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvNombreClienteVal = findViewById(R.id.tvNombreClienteVal);
        tvNombreMembresiaVal = findViewById(R.id.tvNombreMembresiaVal);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvPrecioPagadoVal = findViewById(R.id.tvPrecioPagadoVal);
        tvFechaInicioVal = findViewById(R.id.tvFechaInicioVal);
        tvFechaVencimientoVal = findViewById(R.id.tvFechaVencimientoVal);
        btnEditarInscripcion = findViewById(R.id.btnEditarInscripcion);
        btnCancelarInscripcion = findViewById(R.id.btnCancelarInscripcion);
    }

    private void setupViewModels() {
        inscripcionViewModel = new ViewModelProvider(this).get(InscripcionViewModel.class);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        membresiaViewModel = new ViewModelProvider(this).get(MembresiaViewModel.class);
        pagoViewModel = new ViewModelProvider(this).get(PagoViewModel.class);

        // Buscar inscripción por ID String desde Firestore
        inscripcionViewModel.buscarPorId(idInscripcion).observe(this, inscripcion -> {
            if (inscripcion != null) {
                this.inscripcionActual = inscripcion;
                mostrarDatosInscripcion(inscripcion);
            } else {
                Toast.makeText(InscripcionDetalle.this, "Inscripción no encontrada en Cloud Firestore", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void mostrarDatosInscripcion(Inscripcion inscripcion) {
        // Resolver Nombre del Cliente desde denormalizado o consulta
        if (inscripcion.getNombreCliente() != null && !inscripcion.getNombreCliente().isEmpty()) {
            tvNombreClienteVal.setText(inscripcion.getNombreCliente());
        } else {
            clienteViewModel.buscarPorId(inscripcion.getIdCliente()).observe(this, cliente -> {
                if (cliente != null) {
                    String nombreCompleto = ((cliente.getNombres() != null ? cliente.getNombres() : "") + " " + (cliente.getApellidos() != null ? cliente.getApellidos() : "")).trim();
                    tvNombreClienteVal.setText(nombreCompleto);
                } else {
                    tvNombreClienteVal.setText("Cliente");
                }
            });
        }

        // Resolver Nombre de la Membresía
        if (inscripcion.getNombreMembresia() != null && !inscripcion.getNombreMembresia().isEmpty()) {
            tvNombreMembresiaVal.setText(inscripcion.getNombreMembresia());
        } else {
            membresiaViewModel.buscarPorId(inscripcion.getIdMembresia()).observe(this, membresia -> {
                if (membresia != null) {
                    tvNombreMembresiaVal.setText(membresia.getNombre() != null ? membresia.getNombre() : "Membresía");
                } else {
                    tvNombreMembresiaVal.setText("Membresía");
                }
            });
        }

        // Consultar los pagos parciales registrados para calcular el saldo pendiente
        pagoViewModel.obtenerPorInscripcion(inscripcion.getId()).observe(this, pagosInscripcion -> {
            double totalAbonado = 0.0;
            if (pagosInscripcion != null) {
                for (Pago p : pagosInscripcion) {
                    totalAbonado += p.getMonto();
                }
            }

            double precioTotal = inscripcion.getPrecioPagado();
            double saldoPendiente = precioTotal - totalAbonado;
            if (saldoPendiente < 0) saldoPendiente = 0.0;

            String textoDesglose = String.format(Locale.getDefault(), "Total: S/ %,.2f  |  Abonado: S/ %,.2f  |  Saldo: S/ %,.2f", precioTotal, totalAbonado, saldoPendiente);
            tvPrecioPagadoVal.setText(textoDesglose);
        });

        tvFechaInicioVal.setText(inscripcion.getFechaInicio() != null ? inscripcion.getFechaInicio() : "-");
        tvFechaVencimientoVal.setText(inscripcion.getFechaVencimiento() != null ? inscripcion.getFechaVencimiento() : "-");

        // Estado y verificación de vencimiento automático
        String est = inscripcion.getEstado() != null ? inscripcion.getEstado().toUpperCase() : "ACTIVA";
        if (inscripcion.getFechaVencimientoLocalDate() != null && inscripcion.getFechaVencimientoLocalDate().isBefore(LocalDate.now()) && "ACTIVA".equals(est)) {
            est = "VENCIDA";
        }

        if ("ACTIVA".equals(est) || "ACTIVO".equals(est)) {
            tvStatusBadge.setText("● ACTIVA");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_active);
            tvStatusBadge.setTextColor(getColor(R.color.badge_green));
        } else if ("VENCIDA".equals(est)) {
            tvStatusBadge.setText("● VENCIDA");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_orange);
            tvStatusBadge.setTextColor(getColor(R.color.badge_orange));
        } else {
            tvStatusBadge.setText("● " + est);
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_inactive);
            tvStatusBadge.setTextColor(getColor(R.color.red_accent));
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEditarInscripcion.setOnClickListener(v -> {
            if (inscripcionActual != null) {
                Intent intent = new Intent(InscripcionDetalle.this, RegistrarInscripcion.class);
                intent.putExtra("idInscripcion", inscripcionActual.getId());
                startActivity(intent);
            }
        });

        btnCancelarInscripcion.setOnClickListener(v -> confirmarCancelacion());
    }

    private void confirmarCancelacion() {
        if (inscripcionActual == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Confirmar Cancelación")
                .setMessage("¿Estás seguro de que deseas cancelar esta inscripción?\n\nLa suscripción cambiará su estado a CANCELADA en Cloud Firestore.")
                .setPositiveButton("CANCELAR INSCRIPCIÓN", (dialog, which) -> cancelarInscripcion())
                .setNegativeButton("VOLVER", null)
                .show();
    }

    private void cancelarInscripcion() {
        if (inscripcionActual == null) return;
        inscripcionActual.setEstado("CANCELADA");

        inscripcionViewModel.actualizar(inscripcionActual, new InscripcionRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(InscripcionDetalle.this, "Inscripción cancelada correctamente en Firestore", Toast.LENGTH_SHORT).show();
                mostrarDatosInscripcion(inscripcionActual);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(InscripcionDetalle.this, "Error al cancelar la inscripción en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
