package com.example.appgymsparta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.appgymsparta.viewmodel.DashboardViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private DashboardViewModel dashboardViewModel;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ScrollView scrollView;

    private ImageView btnMenu;
    private TextView tvStatActiveVal;
    private TextView tvStatAttendanceVal;
    private TextView tvStatIncomeVal;
    private TextView tvStatExpirationsVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            findViewById(R.id.main).setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            findViewById(R.id.bottomNavigation).setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        initViews();
        setupViewModel();
        setupListeners();
        setupNavigationDrawer();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        scrollView = findViewById(R.id.scrollView);

        btnMenu = findViewById(R.id.btnMenu);
        tvStatActiveVal = findViewById(R.id.tvStatActiveVal);
        tvStatAttendanceVal = findViewById(R.id.tvStatAttendanceVal);
        tvStatIncomeVal = findViewById(R.id.tvStatIncomeVal);
        tvStatExpirationsVal = findViewById(R.id.tvStatExpirationsVal);
    }

    private void setupViewModel() {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // 1. Observar Miembros Activos
        dashboardViewModel.getMiembrosActivos().observe(this, cantidad -> {
            int val = cantidad != null ? cantidad : 0;
            tvStatActiveVal.setText(String.valueOf(val));
        });

        // 2. Observar Asistencias de Hoy
        dashboardViewModel.getAsistenciasHoy().observe(this, cantidad -> {
            int val = cantidad != null ? cantidad : 0;
            tvStatAttendanceVal.setText(String.valueOf(val));
        });

        // 3. Observar Ingresos del Mes (Formato S/ X,XXX.XX)
        dashboardViewModel.getIngresosMes().observe(this, total -> {
            double val = total != null ? total : 0.0;
            tvStatIncomeVal.setText(String.format(Locale.getDefault(), "S/ %,.2f", val));
        });

        // 4. Observar Vencimientos Próximos
        dashboardViewModel.getVencimientosProximos().observe(this, cantidad -> {
            int val = cantidad != null ? cantidad : 0;
            tvStatExpirationsVal.setText(String.valueOf(val));
        });
    }

    private void setupListeners() {
        // Abrir Menú Lateral (Drawer)
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        // Acceso directo hacia la pantalla de Clientes
        findViewById(R.id.btnQuickClients).setOnClickListener(v -> abrirClientes());
        findViewById(R.id.cardStatActive).setOnClickListener(v -> abrirClientes());

        // Acceso directo hacia la pantalla de Membresías
        findViewById(R.id.btnQuickMemberships).setOnClickListener(v -> abrirMembresias());

        // Acceso directo hacia la pantalla de Inscripciones
        findViewById(R.id.btnQuickInscriptions).setOnClickListener(v -> abrirInscripciones());
        findViewById(R.id.cardStatExpirations).setOnClickListener(v -> abrirInscripciones());

        // Acceso directo hacia la pantalla de Pagos
        findViewById(R.id.btnQuickPayments).setOnClickListener(v -> abrirPagos());
        findViewById(R.id.cardStatIncome).setOnClickListener(v -> abrirPagos());

        // Acceso directo hacia la pantalla de Asistencias
        findViewById(R.id.btnQuickAttendance).setOnClickListener(v -> abrirAsistencias());
        findViewById(R.id.cardStatAttendance).setOnClickListener(v -> abrirAsistencias());

        // Acceso directo hacia la pantalla de Rutinas
        findViewById(R.id.btnQuickRoutines).setOnClickListener(v -> abrirRutinas());

        // BARRA DE NAVEGACIÓN INFERIOR (Inicio | Clientes | Rutinas | Reportes | Pagos)
        findViewById(R.id.navHome).setOnClickListener(v -> {
            if (scrollView != null) {
                scrollView.smoothScrollTo(0, 0);
            }
        });
        findViewById(R.id.navClients).setOnClickListener(v -> abrirClientes());
        findViewById(R.id.navRoutines).setOnClickListener(v -> abrirRutinas());
        findViewById(R.id.navReports).setOnClickListener(v -> {
            Toast.makeText(DashboardActivity.this, "Módulo de Reportes", Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.navPayments).setOnClickListener(v -> abrirPagos());
    }

    private void setupNavigationDrawer() {
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.drawer_notifications) {
                    Toast.makeText(DashboardActivity.this, "Notificaciones del sistema", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.drawer_admins) {
                    abrirAdministradores();
                } else if (id == R.id.drawer_settings) {
                    abrirConfiguracion();
                } else if (id == R.id.drawer_about) {
                    abrirAcercaSistema();
                } else if (id == R.id.drawer_logout) {
                    Toast.makeText(DashboardActivity.this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                }

                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            });
        }
    }

    private void abrirClientes() {
        Intent intent = new Intent(this, ClientesActivity.class);
        startActivity(intent);
    }

    private void abrirMembresias() {
        Intent intent = new Intent(this, MembresiasActivity.class);
        startActivity(intent);
    }

    private void abrirInscripciones() {
        Intent intent = new Intent(this, InscripcionesActivity.class);
        startActivity(intent);
    }

    private void abrirPagos() {
        Intent intent = new Intent(this, PagosActivity.class);
        startActivity(intent);
    }

    private void abrirAsistencias() {
        Intent intent = new Intent(this, AsistenciasActivity.class);
        startActivity(intent);
    }

    private void abrirRutinas() {
        Intent intent = new Intent(this, RutinasActivity.class);
        startActivity(intent);
    }

    private void abrirAdministradores() {
        Intent intent = new Intent(this, AdministradoresActivity.class);
        startActivity(intent);
    }

    private void abrirConfiguracion() {
        Intent intent = new Intent(this, ConfiguracionActivity.class);
        startActivity(intent);
    }

    private void abrirAcercaSistema() {
        Intent intent = new Intent(this, AcercaSistemaActivity.class);
        startActivity(intent);
    }
}
