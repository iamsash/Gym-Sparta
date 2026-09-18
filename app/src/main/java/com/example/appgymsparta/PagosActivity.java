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

import com.example.appgymsparta.adapter.PagoAdapter;
import com.example.appgymsparta.data.entity.Pago;
import com.example.appgymsparta.viewmodel.PagoViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;
import java.util.Locale;

public class PagosActivity extends AppCompatActivity {

    private PagoViewModel pagoViewModel;
    private PagoAdapter pagoAdapter;

    private ImageView btnBack;
    private TextView tvTotalIngresos;
    private TextView tvTotalPagosCount;
    private RecyclerView rvPagos;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnEmptyRegister;
    private ExtendedFloatingActionButton fabNewPago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pagos);
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
        if (pagoViewModel != null) {
            pagoViewModel.obtenerTodos();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos);
        tvTotalPagosCount = findViewById(R.id.tvTotalPagosCount);
        rvPagos = findViewById(R.id.rvPagos);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnEmptyRegister = findViewById(R.id.btnEmptyRegister);
        fabNewPago = findViewById(R.id.fabNewPago);
    }

    private void setupRecyclerView() {
        pagoAdapter = new PagoAdapter();
        rvPagos.setLayoutManager(new LinearLayoutManager(this));
        rvPagos.setAdapter(pagoAdapter);

        pagoAdapter.setOnPagoClickListener(pago -> {
            if (pago != null && pago.getId() != null) {
                Intent intent = new Intent(PagosActivity.this, PagoDetalle.class);
                intent.putExtra("idPago", pago.getId());
                startActivity(intent);
            }
        });
    }

    private void setupViewModel() {
        pagoViewModel = new ViewModelProvider(this).get(PagoViewModel.class);
        pagoViewModel.obtenerTodos().observe(this, this::actualizarListaPagos);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        fabNewPago.setOnClickListener(v -> abrirRegistrarPago());
        btnEmptyRegister.setOnClickListener(v -> abrirRegistrarPago());
    }

    private void abrirRegistrarPago() {
        Intent intent = new Intent(PagosActivity.this, RegistrarPago.class);
        startActivity(intent);
    }

    private void actualizarListaPagos(List<Pago> pagos) {
        int total = pagos != null ? pagos.size() : 0;
        double sumaTotal = 0.0;

        if (pagos != null) {
            for (Pago p : pagos) {
                sumaTotal += p.getMonto();
            }
        }

        // Actualizar resumen
        tvTotalIngresos.setText(String.format(Locale.getDefault(), "S/ %,.2f", sumaTotal));
        tvTotalPagosCount.setText(String.valueOf(total));

        if (total == 0) {
            rvPagos.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvPagos.setVisibility(View.VISIBLE);
            pagoAdapter.setPagos(pagos);
        }
    }
}
