package com.example.appgymsparta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgymsparta.R;
import com.example.appgymsparta.data.entity.Pago;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PagoAdapter extends RecyclerView.Adapter<PagoAdapter.PagoViewHolder> {

    private List<Pago> listaPagos = new ArrayList<>();
    private OnPagoClickListener listener;

    public interface OnPagoClickListener {
        void onPagoClick(Pago pago);
    }

    public void setOnPagoClickListener(OnPagoClickListener listener) {
        this.listener = listener;
    }

    public void setPagos(List<Pago> pagos) {
        this.listaPagos = pagos != null ? pagos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PagoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pago, parent, false);
        return new PagoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagoViewHolder holder, int position) {
        Pago pago = listaPagos.get(position);
        holder.bind(pago, listener);
    }

    @Override
    public int getItemCount() {
        return listaPagos.size();
    }

    static class PagoViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNombreCliente;
        private final TextView tvMontoPago;
        private final TextView tvNombreMembresia;
        private final TextView tvFechaHora;
        private final TextView tvMetodoPagoBadge;

        public PagoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvMontoPago = itemView.findViewById(R.id.tvMontoPago);
            tvNombreMembresia = itemView.findViewById(R.id.tvNombreMembresia);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvMetodoPagoBadge = itemView.findViewById(R.id.tvMetodoPagoBadge);
        }

        public void bind(Pago pago, OnPagoClickListener listener) {
            String nombreCliente;
            String nombreMembresia;

            boolean esAccesoDiario = pago.getIdInscripcion() == null || "Acceso diario".equalsIgnoreCase(pago.getTipoPago());

            if (esAccesoDiario) {
                nombreCliente = "ACCESO DIARIO";
                nombreMembresia = (pago.getObservacion() != null && !pago.getObservacion().trim().isEmpty())
                        ? pago.getObservacion().trim() : "Entrenamiento de 1 día";
            } else {
                nombreCliente = pago.getNombreCliente() != null && !pago.getNombreCliente().isEmpty()
                        ? pago.getNombreCliente() : "Pago de Membresía";
                nombreMembresia = pago.getNombreMembresia() != null && !pago.getNombreMembresia().isEmpty()
                        ? pago.getNombreMembresia() : "Suscripción Activa";
            }

            tvNombreCliente.setText(nombreCliente);
            tvNombreMembresia.setText(nombreMembresia);
            tvMontoPago.setText(String.format(Locale.getDefault(), "+ S/ %,.2f", pago.getMonto()));

            String fecha = pago.getFechaPago() != null ? pago.getFechaPago() : "-";
            tvFechaHora.setText("📅 " + fecha);

            String metodo = pago.getMetodoPago() != null ? pago.getMetodoPago() : "Efectivo";
            tvMetodoPagoBadge.setText(metodo);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPagoClick(pago);
                }
            });
        }
    }
}
