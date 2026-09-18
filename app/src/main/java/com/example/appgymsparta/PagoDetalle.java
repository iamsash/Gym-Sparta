package com.example.appgymsparta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.appgymsparta.data.entity.Pago;
import com.example.appgymsparta.data.repository.ClienteRepository;
import com.example.appgymsparta.data.repository.MembresiaRepository;
import com.example.appgymsparta.viewmodel.InscripcionViewModel;
import com.example.appgymsparta.viewmodel.PagoViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class PagoDetalle extends AppCompatActivity {

    private PagoViewModel pagoViewModel;
    private InscripcionViewModel inscripcionViewModel;

    private Pago pagoActual;
    private String idPago = null;

    private ImageView btnBack;
    private TextView tvNombreClienteVal;
    private TextView tvNombreMembresiaVal;
    private TextView tvStatusBadge;
    private TextView tvMontoVal;
    private TextView tvFechaHoraVal;
    private TextView tvNumeroOperacionVal;
    private TextView tvObservacionesVal;
    private MaterialButton btnEditarPago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pago_detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("idPago")) {
            idPago = getIntent().getStringExtra("idPago");
        }

        if (idPago == null || idPago.trim().isEmpty()) {
            Toast.makeText(this, "No se especificó un pago válido", Toast.LENGTH_SHORT).show();
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
        if (idPago != null && pagoViewModel != null) {
            pagoViewModel.buscarPorId(idPago);
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvNombreClienteVal = findViewById(R.id.tvNombreClienteVal);
        tvNombreMembresiaVal = findViewById(R.id.tvNombreMembresiaVal);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvMontoVal = findViewById(R.id.tvMontoVal);
        tvFechaHoraVal = findViewById(R.id.tvFechaHoraVal);
        tvNumeroOperacionVal = findViewById(R.id.tvNumeroOperacionVal);
        tvObservacionesVal = findViewById(R.id.tvObservacionesVal);
        btnEditarPago = findViewById(R.id.btnEditarPago);
    }

    private void setupViewModels() {
        pagoViewModel = new ViewModelProvider(this).get(PagoViewModel.class);
        inscripcionViewModel = new ViewModelProvider(this).get(InscripcionViewModel.class);

        pagoViewModel.buscarPorId(idPago).observe(this, pago -> {
            if (pago != null) {
                this.pagoActual = pago;
                mostrarDatosPago(pago);
            } else {
                Toast.makeText(PagoDetalle.this, "Pago no encontrado en Cloud Firestore", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void mostrarDatosPago(Pago pago) {
        tvMontoVal.setText(String.format(Locale.getDefault(), "S/ %,.2f", pago.getMonto()));
        tvStatusBadge.setText(pago.getMetodoPago() != null ? pago.getMetodoPago() : "Efectivo");

        tvFechaHoraVal.setText(pago.getFechaPago() != null ? pago.getFechaPago() : "-");

        String numOp = pago.getNumeroOperacion() != null && !pago.getNumeroOperacion().trim().isEmpty()
                ? pago.getNumeroOperacion() : "Sin número de operación";
        tvNumeroOperacionVal.setText(numOp);

        String obs = pago.getObservacion() != null && !pago.getObservacion().trim().isEmpty()
                ? pago.getObservacion() : "Sin observaciones";
        tvObservacionesVal.setText(obs);

        boolean esAccesoDiario = pago.getIdInscripcion() == null || "Acceso diario".equalsIgnoreCase(pago.getTipoPago());

        if (esAccesoDiario) {
            tvNombreClienteVal.setText("ACCESO DIARIO");
            tvNombreMembresiaVal.setText("Pase diario individual");
        } else {
            String nomCliente = pago.getNombreCliente() != null && !pago.getNombreCliente().isEmpty()
                    ? pago.getNombreCliente() : "Cliente";
            String nomMembresia = pago.getNombreMembresia() != null && !pago.getNombreMembresia().isEmpty()
                    ? pago.getNombreMembresia() : "Membresía";

            tvNombreClienteVal.setText(nomCliente);
            tvNombreMembresiaVal.setText(nomMembresia);

            if (pago.getIdInscripcion() != null) {
                inscripcionViewModel.buscarPorId(pago.getIdInscripcion()).observe(this, inscripcion -> {
                    if (inscripcion != null) {
                        if (inscripcion.getNombreCliente() != null) tvNombreClienteVal.setText(inscripcion.getNombreCliente());
                        if (inscripcion.getNombreMembresia() != null) tvNombreMembresiaVal.setText(inscripcion.getNombreMembresia());
                    }
                });
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEditarPago.setOnClickListener(v -> {
            if (pagoActual != null) {
                Intent intent = new Intent(PagoDetalle.this, RegistrarPago.class);
                intent.putExtra("idPago", pagoActual.getId());
                startActivity(intent);
            }
        });
    }
}
