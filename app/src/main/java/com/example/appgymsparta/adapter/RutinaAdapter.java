package com.example.appgymsparta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgymsparta.R;
import com.example.appgymsparta.data.entity.Cliente;
import com.example.appgymsparta.data.entity.Rutina;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RutinaAdapter extends RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder> {

    private List<Rutina> listaRutinas = new ArrayList<>();
    private Map<String, String> mapClientes = new HashMap<>();
    private OnRutinaClickListener listener;

    public interface OnRutinaClickListener {
        void onRutinaClick(Rutina rutina);
    }

    public void setOnRutinaClickListener(OnRutinaClickListener listener) {
        this.listener = listener;
    }

    public void setRutinas(List<Rutina> rutinas) {
        this.listaRutinas = rutinas != null ? rutinas : new ArrayList<>();
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
    public RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rutina, parent, false);
        return new RutinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaViewHolder holder, int position) {
        Rutina rutina = listaRutinas.get(position);
        holder.bind(rutina, mapClientes, listener);
    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }

    static class RutinaViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNombreRutina;
        private final TextView tvNombreCliente;
        private final TextView tvObjetivo;
        private final TextView tvNivelFecha;
        private final TextView tvStatusBadge;

        public RutinaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreRutina = itemView.findViewById(R.id.tvNombreRutina);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvObjetivo = itemView.findViewById(R.id.tvObjetivo);
            tvNivelFecha = itemView.findViewById(R.id.tvNivelFecha);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
        }

        public void bind(Rutina rutina, Map<String, String> mapClientes, OnRutinaClickListener listener) {
            tvNombreRutina.setText(rutina.getNombre() != null ? rutina.getNombre() : "Rutina de Entrenamiento");

            String nombreCliente = mapClientes.get(rutina.getIdCliente());
            if (nombreCliente == null || nombreCliente.isEmpty()) {
                nombreCliente = rutina.getNombreCliente() != null ? rutina.getNombreCliente() : "Cliente";
            }
            tvNombreCliente.setText(nombreCliente);

            String obj = rutina.getObjetivo() != null && !rutina.getObjetivo().trim().isEmpty()
                    ? "Objetivo: " + rutina.getObjetivo() : "Sin objetivo especificado";
            tvObjetivo.setText(obj);

            String nivel = rutina.getNivel() != null ? rutina.getNivel() : "General";
            String fecha = rutina.getFechaInicio() != null ? rutina.getFechaInicio() : "-";
            tvNivelFecha.setText("Nivel: " + nivel + " • Inicio: " + fecha);

            if (rutina.isEstado()) {
                tvStatusBadge.setText("● ACTIVA");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_active);
                tvStatusBadge.setTextColor(itemView.getContext().getColor(R.color.badge_green));
            } else {
                tvStatusBadge.setText("● INACTIVA");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_inactive);
                tvStatusBadge.setTextColor(itemView.getContext().getColor(R.color.red_accent));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRutinaClick(rutina);
                }
            });
        }
    }
}
