package com.example.dramasv2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dramasv2.R;
import com.example.dramasv2.modelo.Video;
import java.util.List;

public class CapAdapter extends RecyclerView.Adapter<CapAdapter.VideoViewHolder> {

    private Context context;
    private List<Video> videoList;
    private OnVideoClickListener onVideoClickListener;

    public CapAdapter(Context context, List<Video> videoList, OnVideoClickListener onVideoClickListener) {
        this.context = context;
        this.videoList = videoList;
        this.onVideoClickListener = onVideoClickListener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.v_detalles, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        // Obtener el video en la posición actual
        Video video = videoList.get(position);
        // Llamar al método bind para establecer los datos en la vista
        holder.bind(video, onVideoClickListener);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    // Clase ViewHolder para los elementos de la lista
    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEpisodeTitle, tvEpisodeNumber;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEpisodeTitle = itemView.findViewById(R.id.tvTitulo_cap);
            tvEpisodeNumber = itemView.findViewById(R.id.tvCap_);
        }

        // Método para establecer los datos en la vista
        public void bind(Video video, OnVideoClickListener onVideoClickListener) {
            // Establecer el título del episodio
            tvEpisodeTitle.setText(video.getTitle());
            // Establecer el número del episodio
            tvEpisodeNumber.setText("Episodio " + video.getChapterNumber());
            // Configurar el clic en el elemento
            itemView.setOnClickListener(v -> onVideoClickListener.onVideoClick(video));
        }
    }

    // Interfaz para manejar los clics en los elementos de la lista
    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }
}
