package com.example.appgymsparta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgymsparta.R;
import com.example.appgymsparta.data.entity.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> listaClientes = new ArrayList<>();
    private OnClienteClickListener listener;

    public interface OnClienteClickListener {
        void onClienteClick(Cliente cliente);
    }

    public void setOnClienteClickListener(OnClienteClickListener listener) {
        this.listener = listener;
    }

    public void setClientes(List<Cliente> clientes) {
        this.listaClientes = clientes != null ? clientes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);
        holder.bind(cliente, listener);
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    static class ClienteViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAvatar;
        private final TextView tvNombreCompleto;
        private final TextView tvDni;
        private final TextView tvTelefono;
        private final TextView tvStatusBadge;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvNombreCompleto = itemView.findViewById(R.id.tvNombreCompleto);
            tvDni = itemView.findViewById(R.id.tvDni);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
        }

        public void bind(Cliente cliente, OnClienteClickListener listener) {
            // Generar iniciales (ej. Juan Pérez -> JP)
            String nombre = cliente.getNombres() != null ? cliente.getNombres().trim() : "";
            String apellido = cliente.getApellidos() != null ? cliente.getApellidos().trim() : "";
            String iniciales = "";
            if (!nombre.isEmpty()) iniciales += nombre.substring(0, 1).toUpperCase();
            if (!apellido.isEmpty()) iniciales += apellido.substring(0, 1).toUpperCase();
            if (iniciales.isEmpty()) iniciales = "C";
            tvAvatar.setText(iniciales);

            // Nombre completo
            String nombreCompleto = nombre + " " + apellido;
            tvNombreCompleto.setText(nombreCompleto.trim());

            // DNI
            tvDni.setText("DNI: " + (cliente.getDni() != null ? cliente.getDni() : "-"));

            // Teléfono
            String tel = cliente.getTelefono() != null && !cliente.getTelefono().isEmpty() ? cliente.getTelefono() : "Sin teléfono";
            tvTelefono.setText("📱 " + tel);

            // Estado (boolean: true -> ACTIVO, false -> INACTIVO)
            if (cliente.isEstado()) {
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
                    listener.onClienteClick(cliente);
                }
            });
        }
    }
}
