package com.example.appgymsparta;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgymsparta.adapter.ClienteAdapter;
import com.example.appgymsparta.data.entity.Cliente;
import com.example.appgymsparta.viewmodel.ClienteViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ClientesActivity extends AppCompatActivity {

    private ClienteViewModel clienteViewModel;
    private ClienteAdapter clienteAdapter;

    private ImageView btnBack;
    private TextView tvTotalCount;
    private TextView tvActiveCount;
    private TextInputEditText etSearch;
    private RecyclerView rvClientes;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnEmptyRegister;
    private ExtendedFloatingActionButton fabNewClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clientes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar la lista desde Firestore al volver a esta pantalla
        if (clienteViewModel != null) {
            clienteViewModel.obtenerTodos();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvActiveCount = findViewById(R.id.tvActiveCount);
        etSearch = findViewById(R.id.etSearch);
        rvClientes = findViewById(R.id.rvClientes);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnEmptyRegister = findViewById(R.id.btnEmptyRegister);
        fabNewClient = findViewById(R.id.fabNewClient);
    }

    private void setupRecyclerView() {
        clienteAdapter = new ClienteAdapter();
        rvClientes.setLayoutManager(new LinearLayoutManager(this));
        rvClientes.setAdapter(clienteAdapter);

        // Abrir ClienteDetalle al hacer clic en una tarjeta de cliente
        clienteAdapter.setOnClienteClickListener(cliente -> {
            if (cliente != null && cliente.getId() != null) {
                Intent intent = new Intent(ClientesActivity.this, ClienteDetalle.class);
                intent.putExtra("idCliente", cliente.getId());
                startActivity(intent);
            }
        });
    }

    private void setupViewModel() {
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        // Observar lista de clientes desde Cloud Firestore vía ClienteViewModel
        clienteViewModel.obtenerTodos().observe(this, this::actualizarListaClientes);
    }

    private void setupListeners() {
        // Regresar al Dashboard
        btnBack.setOnClickListener(v -> finish());

        // Navegación hacia RegistrarCliente
        fabNewClient.setOnClickListener(v -> abrirRegistrarCliente());
        btnEmptyRegister.setOnClickListener(v -> abrirRegistrarCliente());

        // Búsqueda en tiempo real por nombre, apellido o DNI en Firestore
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    clienteViewModel.obtenerTodos();
                } else {
                    clienteViewModel.buscarPorNombre(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void abrirRegistrarCliente() {
        Intent intent = new Intent(ClientesActivity.this, RegistrarCliente.class);
        startActivity(intent);
    }

    private void actualizarListaClientes(List<Cliente> clientes) {
        int total = clientes != null ? clientes.size() : 0;
        int activos = 0;

        if (clientes != null) {
            for (Cliente c : clientes) {
                if (c.isEstado()) {
                    activos++;
                }
            }
        }

        // Actualizar tarjetas de resumen
        tvTotalCount.setText(String.valueOf(total));
        tvActiveCount.setText(String.valueOf(activos));

        // Mostrar lista o estado vacío
        if (total == 0) {
            rvClientes.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvClientes.setVisibility(View.VISIBLE);
            clienteAdapter.setClientes(clientes);
        }
    }
}
