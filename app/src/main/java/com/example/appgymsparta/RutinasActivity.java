package com.example.appgymsparta;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgymsparta.adapter.RutinaAdapter;
import com.example.appgymsparta.data.entity.Cliente;
import com.example.appgymsparta.data.entity.Rutina;
import com.example.appgymsparta.viewmodel.ClienteViewModel;
import com.example.appgymsparta.viewmodel.RutinaViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RutinasActivity extends AppCompatActivity {

    private RutinaViewModel rutinaViewModel;
    private ClienteViewModel clienteViewModel;
    private RutinaAdapter rutinaAdapter;

    private ImageView btnBack;
    private TextInputEditText etSearch;
    private RecyclerView rvRutinas;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnEmptyRegister;
    private ExtendedFloatingActionButton fabNewRutina;

    private List<Rutina> listaCompletaRutinas = new ArrayList<>();
    private List<Cliente> listaClientes = new ArrayList<>();
    private Map<String, String> mapClientes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rutinas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupViewModels();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rutinaViewModel != null) {
            rutinaViewModel.obtenerTodas();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);
        rvRutinas = findViewById(R.id.rvRutinas);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnEmptyRegister = findViewById(R.id.btnEmptyRegister);
        fabNewRutina = findViewById(R.id.fabNewRutina);
    }

    private void setupRecyclerView() {
        rutinaAdapter = new RutinaAdapter();
        rvRutinas.setLayoutManager(new LinearLayoutManager(this));
        rvRutinas.setAdapter(rutinaAdapter);

        rutinaAdapter.setOnRutinaClickListener(rutina -> {
            if (rutina != null && rutina.getId() != null) {
                Intent intent = new Intent(RutinasActivity.this, RutinaDetalleActivity.class);
                intent.putExtra("idRutina", rutina.getId());
                startActivity(intent);
            }
        });
    }

    private void setupViewModels() {
        rutinaViewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        clienteViewModel.obtenerTodos().observe(this, clientes -> {
            this.listaClientes = clientes != null ? clientes : new ArrayList<>();
            mapClientes.clear();
            for (Cliente c : listaClientes) {
                String full = ((c.getNombres() != null ? c.getNombres() : "") + " " + (c.getApellidos() != null ? c.getApellidos() : "")).trim();
                if (c.getId() != null) mapClientes.put(c.getId(), full);
                mapClientes.put(String.valueOf(c.getIdCliente()), full);
            }
            rutinaAdapter.setClientesMap(listaClientes);
            filtrarRutinas();
        });

        rutinaViewModel.obtenerTodas().observe(this, rutinas -> {
            this.listaCompletaRutinas = rutinas != null ? rutinas : new ArrayList<>();
            filtrarRutinas();
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        fabNewRutina.setOnClickListener(v -> abrirRegistrarRutina());
        btnEmptyRegister.setOnClickListener(v -> abrirRegistrarRutina());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarRutinas();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void abrirRegistrarRutina() {
        Intent intent = new Intent(RutinasActivity.this, RegistrarRutinaActivity.class);
        startActivity(intent);
    }

    private void filtrarRutinas() {
        String query = etSearch.getText() != null ? etSearch.getText().toString().trim().toLowerCase() : "";

        List<Rutina> filtradas = new ArrayList<>();
        if (query.isEmpty()) {
            filtradas.addAll(listaCompletaRutinas);
        } else {
            for (Rutina r : listaCompletaRutinas) {
                String nombreRutina = r.getNombre() != null ? r.getNombre().toLowerCase() : "";
                String nombreCliente = mapClientes.get(r.getIdCliente());
                if (nombreCliente == null) nombreCliente = "";
                nombreCliente = nombreCliente.toLowerCase();

                if (nombreRutina.contains(query) || nombreCliente.contains(query)) {
                    filtradas.add(r);
                }
            }
        }

        int total = filtradas.size();
        if (total == 0) {
            rvRutinas.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvRutinas.setVisibility(View.VISIBLE);
            rutinaAdapter.setRutinas(filtradas);
        }
    }
}
