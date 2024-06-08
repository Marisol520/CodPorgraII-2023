package com.example.dramasv2;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Reproductor_V extends AppCompatActivity implements SensorEventListener {

    private boolean isFullScreen = true;
    private RelativeLayout overlayLayout;
    private TextView tvTitle;
    private TextView tvChapterNumber;
    private VideoView videoView;
    private SensorManager sensorManager;
    private Sensor proximitySensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar la actividad para que esté en modo de pantalla completa y orientación horizontal
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.reproductor);

        videoView = findViewById(R.id.videoView_1);
        overlayLayout = findViewById(R.id.overlayLayout);
        tvTitle = findViewById(R.id.tvTitle);
        tvChapterNumber = findViewById(R.id.tvChapterNumber);

        String videoUrl = getIntent().getStringExtra("videoUrl");
        String title = getIntent().getStringExtra("titulo");
        String chapterNumber = getIntent().getStringExtra("numeroCapitulo");

        if (videoUrl != null) {
            Uri uri = Uri.parse(videoUrl);
            videoView.setVideoURI(uri);

            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);

            // Reproducir el video automáticamente
            videoView.start();
        }

        // Mostrar el título y número de capítulo
        if (title != null) {
            tvTitle.setText(title);
        }
        if (chapterNumber != null) {
            tvChapterNumber.setText("Capítulo " + chapterNumber);
        }

        // Inicializar el SensorManager y el sensor de proximidad
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Agregar un listener al botón de atrás
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cerrar la actividad cuando se presiona el botón de atrás
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el listener del sensor de proximidad
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar el listener del sensor de proximidad para evitar fugas de memoria
        sensorManager.unregisterListener(this);
    }

    // Método para alternar la visibilidad de la superposición
    public void toggleOverlay(View view) {
        if (overlayLayout.getVisibility() == View.VISIBLE) {
            overlayLayout.setVisibility(View.INVISIBLE);
        } else {
            overlayLayout.setVisibility(View.VISIBLE);
        }
    }

    // Método del SensorEventListener para manejar cambios en el sensor de proximidad
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            if (distance < proximitySensor.getMaximumRange()) {
                // Si el objeto está cerca, pausar la reproducción del video
                videoView.pause();
            } else {
                // Si el objeto está lejos, reanudar la reproducción del video
                videoView.start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesitas implementar nada aquí para el sensor de proximidad
    }
}







