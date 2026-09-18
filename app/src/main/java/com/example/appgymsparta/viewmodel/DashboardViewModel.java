package com.example.appgymsparta.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.appgymsparta.data.repository.AsistenciaRepository;
import com.example.appgymsparta.data.repository.ClienteRepository;
import com.example.appgymsparta.data.repository.InscripcionRepository;
import com.example.appgymsparta.data.repository.PagoRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardViewModel extends AndroidViewModel {

    private final LiveData<Integer> miembrosActivos;
    private final LiveData<Integer> asistenciasHoy;
    private final LiveData<Double> ingresosMes;
    private final LiveData<Integer> vencimientosProximos;

    public DashboardViewModel(@NonNull Application application) {
        super(application);

        ClienteRepository clienteRepository = new ClienteRepository(application);
        AsistenciaRepository asistenciaRepository = new AsistenciaRepository(application);
        PagoRepository pagoRepository = new PagoRepository(application);
        InscripcionRepository inscripcionRepository = new InscripcionRepository(application);

        // 1. Contar Miembros Activos
        this.miembrosActivos = clienteRepository.contarActivos();

        // 2. Contar Asistencias de Hoy (Fecha YYYY-MM-DD)
        String hoyStr = LocalDate.now().toString();
        this.asistenciasHoy = asistenciaRepository.contarPorFecha(hoyStr);

        // 3. Sumar Ingresos del Mes (Año-Mes YYYY-MM)
        String anioMesStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        this.ingresosMes = pagoRepository.sumarIngresosMes(anioMesStr);

        // 4. Contar Vencimientos Próximos (Desde Hoy hasta Hoy + 7 días)
        String hoyMas7Str = LocalDate.now().plusDays(7).toString();
        this.vencimientosProximos = inscripcionRepository.contarVencimientosProximos(hoyStr, hoyMas7Str);
    }

    public LiveData<Integer> getMiembrosActivos() {
        return miembrosActivos;
    }

    public LiveData<Integer> getAsistenciasHoy() {
        return asistenciasHoy;
    }

    public LiveData<Double> getIngresosMes() {
        return ingresosMes;
    }

    public LiveData<Integer> getVencimientosProximos() {
        return vencimientosProximos;
    }
}
