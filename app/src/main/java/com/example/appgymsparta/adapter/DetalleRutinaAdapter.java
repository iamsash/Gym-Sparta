package com.example.appgymsparta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgymsparta.R;
import com.example.appgymsparta.data.entity.DetalleRutina;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetalleRutinaAdapter extends RecyclerView.Adapter<DetalleRutinaAdapter.DetalleRutinaViewHolder> {

    private List<DetalleRutina> listaDetalles = new ArrayList<>();
    private OnEjercicioActionListener listener;

    public interface OnEjercicioActionListener {
        void onEditarEjercicio(DetalleRutina detalle);
        void onEliminarEjercicio(DetalleRutina detalle);
    }

    public void setOnEjercicioActionListener(OnEjercicioActionListener listener) {
        this.listener = listener;
    }

    public void setDetalles(List<DetalleRutina> detalles) {
        this.listaDetalles = detalles != null ? detalles : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetalleRutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detalle_rutina, parent, false);
        return new DetalleRutinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetalleRutinaViewHolder holder, int position) {
        DetalleRutina detalle = listaDetalles.get(position);
        holder.bind(detalle, listener);
    }

    @Override
    public int getItemCount() {
        return listaDetalles.size();
    }

    static class DetalleRutinaViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDiaBadge;
        private final TextView tvEjercicio;
        private final TextView tvGrupoMuscular;
        private final TextView tvDetallesTecnicos;
        private final ImageView btnEditarEjercicio;
        private final ImageView btnEliminarEjercicio;

        public DetalleRutinaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiaBadge = itemView.findViewById(R.id.tvDiaBadge);
            tvEjercicio = itemView.findViewById(R.id.tvEjercicio);
            tvGrupoMuscular = itemView.findViewById(R.id.tvGrupoMuscular);
            tvDetallesTecnicos = itemView.findViewById(R.id.tvDetallesTecnicos);
            btnEditarEjercicio = itemView.findViewById(R.id.btnEditarEjercicio);
            btnEliminarEjercicio = itemView.findViewById(R.id.btnEliminarEjercicio);
        }

        public void bind(DetalleRutina detalle, OnEjercicioActionListener listener) {
            String dia = detalle.getDia() != null && !detalle.getDia().isEmpty() ? detalle.getDia().toUpperCase() : "DÍA";
            tvDiaBadge.setText(dia);

            tvEjercicio.setText(detalle.getEjercicio() != null ? detalle.getEjercicio() : "Ejercicio");
            tvGrupoMuscular.setText(detalle.getGrupoMuscular() != null ? "Grupo: " + detalle.getGrupoMuscular() : "General");

            String tech = String.format(Locale.getDefault(), "💪 %d series × %d reps | %.1f kg | %ds descanso",
                    detalle.getSeries(), detalle.getRepeticiones(), detalle.getPeso(), detalle.getDescanso());
            tvDetallesTecnicos.setText(tech);

            btnEditarEjercicio.setOnClickListener(v -> {
                if (listener != null) listener.onEditarEjercicio(detalle);
            });

            btnEliminarEjercicio.setOnClickListener(v -> {
                if (listener != null) listener.onEliminarEjercicio(detalle);
            });
        }
    }
}
