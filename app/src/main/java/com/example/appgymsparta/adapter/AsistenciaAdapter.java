package com.example.appgymsparta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgymsparta.R;
import com.example.appgymsparta.data.entity.Asistencia;
import com.example.appgymsparta.data.entity.Cliente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsistenciaAdapter extends RecyclerView.Adapter<AsistenciaAdapter.AsistenciaViewHolder> {

    private List<Asistencia> listaAsistencias = new ArrayList<>();
    private Map<String, String> mapClientes = new HashMap<>();

    public void setAsistencias(List<Asistencia> asistencias) {
        this.listaAsistencias = asistencias != null ? asistencias : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setClientesMap(List<Cliente> clientes) {
        mapClientes.clear();
        if (clientes != null) {
            for (Cliente c : clientes) {
                String nombreCompleto = ((c.getNombres() != null ? c.getNombres() : "") + " " + (c.getApellidos() != null ? c.getApellidos() : "")).trim();
                if (c.getId() != null) mapClientes.put(c.getId(), nombreCompleto);
                mapClientes.put(String.valueOf(c.getIdCliente()), nombreCompleto);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AsistenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asistencia, parent, false);
        return new AsistenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AsistenciaViewHolder holder, int position) {
        Asistencia asistencia = listaAsistencias.get(position);
        holder.bind(asistencia, mapClientes);
    }

    @Override
    public int getItemCount() {
        return listaAsistencias.size();
    }

    static class AsistenciaViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNombreCliente;
        private final TextView tvFechaHora;
        private final TextView tvObservacion;
        private final TextView tvTurnoBadge;

        public AsistenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvObservacion = itemView.findViewById(R.id.tvObservacion);
            tvTurnoBadge = itemView.findViewById(R.id.tvTurnoBadge);
        }

        public void bind(Asistencia asistencia, Map<String, String> mapClientes) {
            String nombreCliente = mapClientes.get(asistencia.getIdCliente());
            if (nombreCliente == null || nombreCliente.isEmpty()) {
                nombreCliente = asistencia.getNombreCliente() != null ? asistencia.getNombreCliente() : "Cliente";
            }
            tvNombreCliente.setText(nombreCliente);

            String fechaStr = asistencia.getFecha() != null ? asistencia.getFecha() : "-";
            String horaStr = asistencia.getHoraIngreso() != null ? asistencia.getHoraIngreso() : "-";
            tvFechaHora.setText(fechaStr + " • " + horaStr + " hs");

            if (asistencia.getObservacion() != null && !asistencia.getObservacion().trim().isEmpty()) {
                tvObservacion.setVisibility(View.VISIBLE);
                tvObservacion.setText(asistencia.getObservacion().trim());
            } else {
                tvObservacion.setVisibility(View.GONE);
            }

            tvTurnoBadge.setText("INGRESÓ ✓");
        }
    }
}
