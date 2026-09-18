package com.example.appgymsparta;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.appgymsparta.adapter.AsistenciaAdapter;
import com.example.appgymsparta.data.entity.Asistencia;
import com.example.appgymsparta.viewmodel.AsistenciaViewModel;
import com.example.appgymsparta.viewmodel.ClienteViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.time.LocalDate;
import java.util.List;

public class AsistenciasActivity extends AppCompatActivity {

    private AsistenciaViewModel asistenciaViewModel;
    private ClienteViewModel clienteViewModel;
    private AsistenciaAdapter asistenciaAdapter;

    private ImageView btnBack;
    private TextView tvAsistenciasHoyCount;
    private RecyclerView rvAsistencias;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnEmptyRegister;
    private ExtendedFloatingActionButton fabNewAsistencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asistencias);
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
        if (asistenciaViewModel != null) {
            String hoyStr = LocalDate.now().toString();
            asistenciaViewModel.obtenerPorFecha(hoyStr);
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvAsistenciasHoyCount = findViewById(R.id.tvAsistenciasHoyCount);
        rvAsistencias = findViewById(R.id.rvAsistencias);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnEmptyRegister = findViewById(R.id.btnEmptyRegister);
        fabNewAsistencia = findViewById(R.id.fabNewAsistencia);
    }

    private void setupRecyclerView() {
        asistenciaAdapter = new AsistenciaAdapter();
        rvAsistencias.setLayoutManager(new LinearLayoutManager(this));
        rvAsistencias.setAdapter(asistenciaAdapter);
    }

    private void setupViewModels() {
        asistenciaViewModel = new ViewModelProvider(this).get(AsistenciaViewModel.class);
        clienteViewModel = new ViewModelProvider(this).get(ClienteViewModel.class);

        // Cargar mapa de Clientes para resolver nombres
        clienteViewModel.obtenerTodos().observe(this, clientes -> {
            asistenciaAdapter.setClientesMap(clientes);
        });

        // Cargar asistencias del día actual desde Firestore
        String hoyStr = LocalDate.now().toString();
        asistenciaViewModel.obtenerPorFecha(hoyStr).observe(this, this::actualizarListaAsistencias);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        fabNewAsistencia.setOnClickListener(v -> abrirRegistrarAsistencia());
        btnEmptyRegister.setOnClickListener(v -> abrirRegistrarAsistencia());
    }

    private void abrirRegistrarAsistencia() {
        Intent intent = new Intent(AsistenciasActivity.this, RegistrarAsistencias.class);
        startActivity(intent);
    }

    private void actualizarListaAsistencias(List<Asistencia> asistencias) {
        int total = asistencias != null ? asistencias.size() : 0;
        tvAsistenciasHoyCount.setText(String.valueOf(total));

        if (total == 0) {
            rvAsistencias.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvAsistencias.setVisibility(View.VISIBLE);
            asistenciaAdapter.setAsistencias(asistencias);
        }
    }
}
