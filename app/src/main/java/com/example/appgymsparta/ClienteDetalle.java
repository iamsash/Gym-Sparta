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

import com.example.appgymsparta.data.entity.Cliente;
import com.example.appgymsparta.data.repository.ClienteRepository;
import com.example.appgymsparta.viewmodel.ClienteViewModel;
import com.google.android.material.button.MaterialButton;

public class ClienteDetalle extends AppCompatActivity {

    private ClienteViewModel clienteViewModel;
    private Cliente clienteActual;
    private String idCliente = null;

    private ImageView btnBack;
    private TextView tvAvatar;
    private TextView tvNombreCompleto;
    private TextView tvStatusBadge;
    private TextView tvDniVal;
    private TextView tvTelefonoVal;
    private TextView tvSexoVal;
    private TextView tvFechaRegistroVal;
    private MaterialButton btnEditarCliente;
    private MaterialButton btnEliminarCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener idCliente (String) enviado desde ClientesActivity o RegistrarCliente
        if (getIntent() != null && getIntent().hasExtra("idCliente")) {
            idCliente = getIntent().getStringExtra("idCliente");
        }

        if (idCliente == null || idCliente.trim().isEmpty()) {
            Toast.makeText(this, "No se especificó un cliente válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupViewModel();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos actualizados del cliente desde Firestore
        if (idCliente != null && clienteViewModel != null) {
            clienteViewModel.buscarPorId(idCliente);
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvAvatar = findViewById(R.id.tvAvatar);
        tvNombreCompleto = findViewById(R.id.tvNombreCompleto);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvDniVal = findViewById(R.id.tvDniVal);
        tvTelefonoVal = findViewById(R.id.tvTelefonoVal);
        tvSexoVal = findViewById(R.id.tvSexoVal);
        tvFechaRegistroVal = findViewById(R.id.tvFechaRegistroVal);
        btnEditarCliente = findViewById(R.id.btnEditarCliente);
        btnEliminarCliente = findViewById(R.id.btnEliminarCliente);
    }

    private void setupViewModel() {
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        // Buscar cliente por idCliente String en Firestore mediante ClienteViewModel
        clienteViewModel.buscarPorId(idCliente).observe(this, cliente -> {
            if (cliente != null) {
                this.clienteActual = cliente;
                mostrarDatosCliente(cliente);
            } else {
                Toast.makeText(ClienteDetalle.this, "Cliente no encontrado en Cloud Firestore", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void mostrarDatosCliente(Cliente cliente) {
        String nombre = cliente.getNombres() != null ? cliente.getNombres().trim() : "";
        String apellido = cliente.getApellidos() != null ? cliente.getApellidos().trim() : "";

        // Iniciales Avatar
        String iniciales = "";
        if (!nombre.isEmpty()) iniciales += nombre.substring(0, 1).toUpperCase();
        if (!apellido.isEmpty()) iniciales += apellido.substring(0, 1).toUpperCase();
        if (iniciales.isEmpty()) iniciales = "C";
        tvAvatar.setText(iniciales);

        // Nombre Completo
        tvNombreCompleto.setText((nombre + " " + apellido).trim());

        // Estado
        if (cliente.isEstado()) {
            tvStatusBadge.setText("● ACTIVO");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_active);
            tvStatusBadge.setTextColor(getColor(R.color.badge_green));
        } else {
            tvStatusBadge.setText("● INACTIVO");
            tvStatusBadge.setBackgroundResource(R.drawable.bg_status_inactive);
            tvStatusBadge.setTextColor(getColor(R.color.red_accent));
        }

        // DNI
        tvDniVal.setText(cliente.getDni() != null ? cliente.getDni() : "-");

        // Teléfono
        String tel = cliente.getTelefono() != null && !cliente.getTelefono().isEmpty() ? cliente.getTelefono() : "Sin teléfono registrado";
        tvTelefonoVal.setText(tel);

        // Sexo
        tvSexoVal.setText(cliente.getSexo() != null ? cliente.getSexo() : "-");

        // Fecha de Registro
        String fecha = cliente.getFechaRegistro() != null ? cliente.getFechaRegistro() : "-";
        tvFechaRegistroVal.setText(fecha);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Reutilizar RegistrarCliente en modo edición enviando String ID
        btnEditarCliente.setOnClickListener(v -> {
            if (clienteActual != null) {
                Intent intent = new Intent(ClienteDetalle.this, RegistrarCliente.class);
                intent.putExtra("idCliente", clienteActual.getId());
                startActivity(intent);
            }
        });

        // Diálogo de confirmación para desactivar o eliminar cliente
        btnEliminarCliente.setOnClickListener(v -> confirmarDesactivarOEliminar());
    }

    private void confirmarDesactivarOEliminar() {
        if (clienteActual == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Confirmar Acción")
                .setMessage("¿Deseas desactivar o eliminar a este cliente en Cloud Firestore?")
                .setPositiveButton("DESACTIVAR", (dialog, which) -> desactivarCliente())
                .setNegativeButton("ELIMINAR", (dialog, which) -> eliminarClienteFisicamente())
                .setNeutralButton("CANCELAR", null)
                .show();
    }

    private void desactivarCliente() {
        if (clienteActual == null) return;
        clienteActual.setEstado(false);

        clienteViewModel.actualizar(clienteActual, new ClienteRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(ClienteDetalle.this, "Cliente desactivado correctamente en Firestore", Toast.LENGTH_SHORT).show();
                mostrarDatosCliente(clienteActual);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ClienteDetalle.this, "Error al desactivar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarClienteFisicamente() {
        if (clienteActual == null) return;

        clienteViewModel.eliminar(clienteActual, new ClienteRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(ClienteDetalle.this, "Cliente eliminado de Cloud Firestore", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ClienteDetalle.this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
