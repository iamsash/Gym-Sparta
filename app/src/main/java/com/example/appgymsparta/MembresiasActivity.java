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

import com.example.appgymsparta.adapter.MembresiaAdapter;
import com.example.appgymsparta.data.entity.Membresia;
import com.example.appgymsparta.viewmodel.MembresiaViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class MembresiasActivity extends AppCompatActivity {

    private MembresiaViewModel membresiaViewModel;
    private MembresiaAdapter membresiaAdapter;

    private ImageView btnBack;
    private RecyclerView rvMembresias;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnEmptyRegister;
    private ExtendedFloatingActionButton fabNewMembership;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_membresias);
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
        if (membresiaViewModel != null) {
            membresiaViewModel.obtenerTodas();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvMembresias = findViewById(R.id.rvMembresias);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnEmptyRegister = findViewById(R.id.btnEmptyRegister);
        fabNewMembership = findViewById(R.id.fabNewMembership);
    }

    private void setupRecyclerView() {
        membresiaAdapter = new MembresiaAdapter();
        rvMembresias.setLayoutManager(new LinearLayoutManager(this));
        rvMembresias.setAdapter(membresiaAdapter);

        membresiaAdapter.setOnMembresiaClickListener(membresia -> {
            if (membresia != null && membresia.getId() != null) {
                Intent intent = new Intent(MembresiasActivity.this, MembresiaDetalle.class);
                intent.putExtra("idMembresia", membresia.getId());
                startActivity(intent);
            }
        });
    }

    private void setupViewModel() {
        membresiaViewModel = new ViewModelProvider(this).get(MembresiaViewModel.class);
        membresiaViewModel.obtenerTodas().observe(this, this::actualizarListaMembresias);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        fabNewMembership.setOnClickListener(v -> abrirRegistrarMembresia());
        btnEmptyRegister.setOnClickListener(v -> abrirRegistrarMembresia());
    }

    private void abrirRegistrarMembresia() {
        Intent intent = new Intent(MembresiasActivity.this, RegistrarMembresia.class);
        startActivity(intent);
    }

    private void actualizarListaMembresias(List<Membresia> membresias) {
        int total = membresias != null ? membresias.size() : 0;

        if (total == 0) {
            rvMembresias.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvMembresias.setVisibility(View.VISIBLE);
            membresiaAdapter.setMembresias(membresias);
        }
    }
}
