package com.example.appgymsparta;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.appgymsparta.adapter.InscripcionAdapter;
import com.example.appgymsparta.data.entity.Inscripcion;
import com.example.appgymsparta.viewmodel.ClienteViewModel;
import com.example.appgymsparta.viewmodel.InscripcionViewModel;
import com.example.appgymsparta.viewmodel.MembresiaViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class InscripcionesActivity extends AppCompatActivity {

    private InscripcionViewModel inscripcionViewModel;
    private ClienteViewModel clienteViewModel;
    private MembresiaViewModel membresiaViewModel;
    private InscripcionAdapter inscripcionAdapter;

    private ImageView btnBack;
    private RecyclerView rvInscripciones;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnEmptyRegister;
    private ExtendedFloatingActionButton fabNewInscripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inscripciones);
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
        if (inscripcionViewModel != null) {
            inscripcionViewModel.obtenerTodas();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvInscripciones = findViewById(R.id.rvInscripciones);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnEmptyRegister = findViewById(R.id.btnEmptyRegister);
        fabNewInscripcion = findViewById(R.id.fabNewInscripcion);
    }

    private void setupRecyclerView() {
        inscripcionAdapter = new InscripcionAdapter();
        rvInscripciones.setLayoutManager(new LinearLayoutManager(this));
        rvInscripciones.setAdapter(inscripcionAdapter);

        inscripcionAdapter.setOnInscripcionClickListener(inscripcion -> {
            if (inscripcion != null && inscripcion.getId() != null) {
                Intent intent = new Intent(InscripcionesActivity.this, InscripcionDetalle.class);
                intent.putExtra("idInscripcion", inscripcion.getId());
                startActivity(intent);
            }
        });
    }

    private void setupViewModels() {
        inscripcionViewModel = new ViewModelProvider(this).get(InscripcionViewModel.class);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);
        membresiaViewModel = new ViewModelProvider(this).get(MembresiaViewModel.class);

        // Cargar mapas de Clientes y Membresías para resolver nombres reales
        clienteViewModel.obtenerTodos().observe(this, clientes -> {
            inscripcionAdapter.setClientesMap(clientes);
        });

        membresiaViewModel.obtenerTodas().observe(this, membresias -> {
            inscripcionAdapter.setMembresiasMap(membresias);
        });

        // Cargar lista de inscripciones desde Firestore
        inscripcionViewModel.obtenerTodas().observe(this, this::actualizarListaInscripciones);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        fabNewInscripcion.setOnClickListener(v -> abrirRegistrarInscripcion());
        btnEmptyRegister.setOnClickListener(v -> abrirRegistrarInscripcion());
    }

    private void abrirRegistrarInscripcion() {
        Intent intent = new Intent(InscripcionesActivity.this, RegistrarInscripcion.class);
        startActivity(intent);
    }

    private void actualizarListaInscripciones(List<Inscripcion> inscripciones) {
        int total = inscripciones != null ? inscripciones.size() : 0;

        if (total == 0) {
            rvInscripciones.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvInscripciones.setVisibility(View.VISIBLE);
            inscripcionAdapter.setInscripciones(inscripciones);
        }
    }
}
