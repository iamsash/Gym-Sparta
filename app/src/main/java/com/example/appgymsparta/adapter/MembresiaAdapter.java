package com.example.appgymsparta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgymsparta.R;
import com.example.appgymsparta.data.entity.Membresia;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MembresiaAdapter extends RecyclerView.Adapter<MembresiaAdapter.MembresiaViewHolder> {

    private List<Membresia> listaMembresias = new ArrayList<>();
    private OnMembresiaClickListener listener;

    public interface OnMembresiaClickListener {
        void onMembresiaClick(Membresia membresia);
    }

    public void setOnMembresiaClickListener(OnMembresiaClickListener listener) {
        this.listener = listener;
    }

    public void setMembresias(List<Membresia> membresias) {
        this.listaMembresias = membresias != null ? membresias : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MembresiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_membresia, parent, false);
        return new MembresiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembresiaViewHolder holder, int position) {
        Membresia membresia = listaMembresias.get(position);
        holder.bind(membresia, listener);
    }

    @Override
    public int getItemCount() {
        return listaMembresias.size();
    }

    static class MembresiaViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNombreMembresia;
        private final TextView tvPrecioMembresia;
        private final TextView tvDuracion;
        private final TextView tvDescripcion;
        private final TextView tvStatusBadge;

        public MembresiaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreMembresia = itemView.findViewById(R.id.tvNombreMembresia);
            tvPrecioMembresia = itemView.findViewById(R.id.tvPrecioMembresia);
            tvDuracion = itemView.findViewById(R.id.tvDuracion);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
        }

        public void bind(Membresia membresia, OnMembresiaClickListener listener) {
            tvNombreMembresia.setText(membresia.getNombre() != null ? membresia.getNombre() : "MEMBRESÍA");
            tvPrecioMembresia.setText(String.format(Locale.getDefault(), "S/ %,.2f", membresia.getPrecio()));
            tvDuracion.setText("Duración: " + membresia.getDuracionDias() + " días");

            String desc = membresia.getDescripcion() != null && !membresia.getDescripcion().trim().isEmpty()
                    ? membresia.getDescripcion() : "Sin descripción";
            tvDescripcion.setText(desc);

            if (membresia.isEstado()) {
                tvStatusBadge.setText("● ACTIVO");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_active);
                tvStatusBadge.setTextColor(itemView.getContext().getColor(R.color.badge_green));
            } else {
                tvStatusBadge.setText("● INACTIVO");
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_inactive);
                tvStatusBadge.setTextColor(itemView.getContext().getColor(R.color.red_accent));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMembresiaClick(membresia);
                }
            });
        }
    }
}
