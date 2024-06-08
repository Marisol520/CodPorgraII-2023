package com.example.dramasv2.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dramasv2.Capitulos_S;
import com.example.dramasv2.CargarImagen;
import com.example.dramasv2.Editar_S;
import com.example.dramasv2.MainActivity;
import com.example.dramasv2.R;
import com.example.dramasv2.modelo.Serie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SerieAdapter extends RecyclerView.Adapter<SerieAdapter.SerieViewHolder> {
    private Context context;
    private List<Serie> seriesList;
    private DatabaseReference db;

    public SerieAdapter(Context context, List<Serie> seriesList) {
        this.context = context;
        this.seriesList = seriesList;
        this.db = FirebaseDatabase.getInstance().getReference("series");
    }

    @NonNull
    @Override
    public SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detalles, parent, false);
        return new SerieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SerieViewHolder holder, int position) {
        Serie serie = seriesList.get(position);
        holder.bind(serie, position);
    }

    @Override
    public int getItemCount() {
        return seriesList.size();
    }

    public void setOnItemClickListener(MainActivity mainActivity) {
        // Implementar según sea necesario para manejar clics.
    }

    public int getPosition() {
        return 0;
    }

    public void filterList(List<Serie> filteredList) {
            seriesList = filteredList;
            notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class SerieViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvSynopsis, tvCategories, tvEpisodes;
        private ImageView imageView;

        public SerieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSynopsis = itemView.findViewById(R.id.tvSynopsis);
            tvCategories = itemView.findViewById(R.id.tvCategories);
            tvEpisodes = itemView.findViewById(R.id.tvEpisodes);
            imageView = itemView.findViewById(R.id.imageView);

            // Manejar clics largos en el item para mostrar un menú
            itemView.setOnLongClickListener(v -> {
                showPopupMenu(getAdapterPosition());
                return true;
            });
        }

        public void bind(Serie serie, int position) {
            tvName.setText(serie.getName());
            tvSynopsis.setText(serie.getSynopsis());
            tvCategories.setText(serie.getCategories());
            tvEpisodes.setText(serie.getEpisodes());

            // Cargar la imagen utilizando Bitmap desde la Uri
            if (serie.getImageUrl() != null) {
                new CargarImagen(imageView).execute(serie.getImageUrl());
            }

            // Manejar clics en el item
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, Capitulos_S.class);
                intent.putExtra("serie", serie); // Asegúrate de que Serie implemente Serializable o Parcelable
                context.startActivity(intent);
            });
        }

        private void showPopupMenu(int position) {
            PopupMenu popupMenu = new PopupMenu(context, itemView);
            popupMenu.inflate(R.menu.menu_1);
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_editar) { // Lógica para editar la serie
                    editSerie(position);
                    return true;
                } else if (itemId == R.id.menu_eliminar) { // Lógica para eliminar la serie
                    deleteSerie(position);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        }

        private void editSerie(int position) {
            Serie serie = seriesList.get(position);
            Intent intent = new Intent(context, Editar_S.class);
            intent.putExtra("serie", serie); // Pasar la serie a la actividad de edición
            context.startActivity(intent);
        }


        private void deleteSerie(int position) {
            Serie serie = seriesList.get(position);
            String serieName = serie.getName(); // Usando el nombre de la serie para eliminar

            // Crear una consulta para encontrar la serie por nombre
            Query query = db.orderByChild("name").equalTo(serieName);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    AtomicBoolean serieDeleted = new AtomicBoolean(false); // Variable para rastrear si se eliminó la serie

                    for (DataSnapshot serieSnapshot : snapshot.getChildren()) {
                        // Eliminar la serie en Firebase
                        serieSnapshot.getRef().removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                serieDeleted.set(true); // Marcar como eliminada si la operación fue exitosa
                            } else {
                                Toast.makeText(context, "Error al eliminar la serie", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    // Si al menos una serie se eliminó correctamente, actualizar la interfaz de usuario
                    if (serieDeleted.get()) {
                        // Eliminar la serie de la lista local
                        seriesList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, seriesList.size());

                        // Mostrar un mensaje indicando que la serie fue eliminada
                        Toast.makeText(context, "Serie eliminada", Toast.LENGTH_SHORT).show();

                        // Verificar si la lista está vacía después de eliminar
                        if (seriesList.isEmpty()) {
                            Toast.makeText(context, "Lista de series vacía", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error en la consulta de eliminación", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    }



