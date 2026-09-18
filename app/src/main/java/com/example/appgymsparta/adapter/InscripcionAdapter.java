package com.example.appgymsparta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgymsparta.R;
import com.example.appgymsparta.data.entity.Cliente;
import com.example.appgymsparta.data.entity.Inscripcion;
import com.example.appgymsparta.data.entity.Membresia;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InscripcionAdapter extends RecyclerView.Adapter<InscripcionAdapter.InscripcionViewHolder> {

    private List<Inscripcion> listaInscripciones = new ArrayList<>();
    private Map<String, String> mapClientes = new HashMap<>();
    private Map<String, String> mapMembresias = new HashMap<>();
    private OnInscripcionClickListener listener;

    public interface OnInscripcionClickListener {
        void onInscripcionClick(Inscripcion inscripcion);
    }

    public void setOnInscripcionClickListener(OnInscripcionClickListener listener) {
        this.listener = listener;
    }

    public void setInscripciones(List<Inscripcion> inscripciones) {
        this.listaInscripciones = inscripciones != null ? inscripciones : new ArrayList<>();
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

    public void setMembresiasMap(List<Membresia> membresias) {
        mapMembresias.clear();
        if (membresias != null) {
            for (Membresia m : membresias) {
                if (m.getId() != null) mapMembresias.put(m.getId(), m.getNombre() != null ? m.getNombre() : "Membresía");
                mapMembresias.put(String.valueOf(m.getIdMembresia()), m.getNombre() != null ? m.getNombre() : "Membresía");
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InscripcionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inscripcion, parent, false);
        return new InscripcionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InscripcionViewHolder holder, int position) {
        Inscripcion inscripcion = listaInscripciones.get(position);
        holder.bind(inscripcion, mapClientes, mapMembresias, listener);
    }

    @Override
    public int getItemCount() {
        return listaInscripciones.size();
    }

    static class InscripcionViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNombreCliente;
        private final TextView tvPrecioPagado;
        private final TextView tvNombreMembresia;
        private final TextView tvFechas;
        private final TextView tvStatusBadge;

        public InscripcionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvPrecioPagado = itemView.findViewById(R.id.tvPrecioPagado);
            tvNombreMembresia = itemView.findViewById(R.id.tvNombreMembresia);
            tvFechas = itemView.findViewById(R.id.tvFechas);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
        }

        public void bind(Inscripcion inscripcion, Map<String, String> mapClientes, Map<String, String> mapMembresias, OnInscripcionClickListener listener) {
            String nombreCliente = mapClientes.get(inscripcion.getIdCliente());
            if (nombreCliente == null || nombreCliente.isEmpty()) {
                nombreCliente = inscripcion.getNombreCliente() != null ? inscripcion.getNombreCliente() : "Cliente";
            }
            tvNombreCliente.setText(nombreCliente);

            String nombreMembresia = mapMembresias.get(inscripcion.getIdMembresia());
            if (nombreMembresia == null || nombreMembresia.isEmpty()) {
                nombreMembresia = inscripcion.getNombreMembresia() != null ? inscripcion.getNombreMembresia() : "Membresía";
            }
            tvNombreMembresia.setText(nombreMembresia);

            tvPrecioPagado.setText(String.format(Locale.getDefault(), "S/ %,.2f", inscripcion.getPrecioPagado()));

            String fInicio = inscripcion.getFechaInicio() != null ? inscripcion.getFechaInicio() : "-";
            String fVenc = inscripcion.getFechaVencimiento() != null ? inscripcion.getFechaVencimiento() : "-";
            tvFechas.setText("📅 " + fInicio + " ➔ " + fVenc);

            // Estado y verificación de vencimiento automático
            String est = inscripcion.getEstado() != null ? inscripcion.getEstado().toUpperCase() : "ACTIVA";
            if (inscripcion.getFechaVencimiento() != null && inscripcion.getFechaVencimientoLocalDate().isBefore(LocalDate.now()) && "ACTIVA".equals(est)) {
                est = "VENCIDA";
            }

            if ("ACTIVA".equals(est) || "ACTIVO".equals(est)) {
                tvStatusBadge.setText("● ACTIVA");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_active);
                tvStatusBadge.setTextColor(itemView.getContext().getColor(R.color.badge_green));
            } else if ("VENCIDA".equals(est)) {
                tvStatusBadge.setText("● VENCIDA");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_orange);
                tvStatusBadge.setTextColor(itemView.getContext().getColor(R.color.badge_orange));
            } else {
                tvStatusBadge.setText("● " + est);
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_inactive);
                tvStatusBadge.setTextColor(itemView.getContext().getColor(R.color.red_accent));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInscripcionClick(inscripcion);
                }
            });
        }
    }
}
