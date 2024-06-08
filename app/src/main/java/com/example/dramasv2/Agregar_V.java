package com.example.dramasv2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dramasv2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Agregar_V extends AppCompatActivity {

    private EditText edtTitulo, edtNumeroCapitulo;
    private Button btnSeleccionarVideo, btnAgregar;
    private VideoView videoView;
    private Uri videoUri;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_v);

        // Inicializar Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("videos");
        storageReference = FirebaseStorage.getInstance().getReference("videos");

        // Referenciar vistas
        edtTitulo = findViewById(R.id.edtTitulo);
        edtNumeroCapitulo = findViewById(R.id.edtNumeroCapitulo);
        btnSeleccionarVideo = findViewById(R.id.btnSeleccionarVideo);
        btnAgregar = findViewById(R.id.btnAgregar);
        videoView = findViewById(R.id.videoPreview);

        // Escuchar clic en el botón de seleccionar video
        btnSeleccionarVideo.setOnClickListener(v -> seleccionarVideo());

        // Escuchar clic en el botón de agregar
        btnAgregar.setOnClickListener(v -> agregarVideo());

        // Pausar el video por defecto
        videoView.pause();
    }

    // Método para seleccionar un video desde el almacenamiento
    private void seleccionarVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, 1);
    }

    // Método para manejar el resultado de la selección del video
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            // Mostrar el video seleccionado en el VideoView
            videoView.setVideoURI(videoUri);
            // Pausar la reproducción del video
            videoView.pause();
            Toast.makeText(this, "Video seleccionado", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para agregar y subir un video a Firebase Storage y Realtime Database
    private void agregarVideo() {
        String titulo = edtTitulo.getText().toString().trim();
        String numeroCapituloStr = edtNumeroCapitulo.getText().toString().trim();

        if (titulo.isEmpty() || numeroCapituloStr.isEmpty() || videoUri == null) {
            Toast.makeText(this, "Por favor ingresa un título, número de capítulo y selecciona un video", Toast.LENGTH_SHORT).show();
            return;
        }

        int numeroCapitulo;
        try {
            numeroCapitulo = Integer.parseInt(numeroCapituloStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Número de capítulo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar un nombre único para el video
        String videoId = UUID.randomUUID().toString();

        // Referencia al almacenamiento de Firebase para guardar el video
        StorageReference videoRef = storageReference.child(videoId);

        // Subir el video a Firebase Storage
        videoRef.putFile(videoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener la URL del video subido
                    videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String videoUrl = uri.toString();

                        // Crear un nuevo registro en Realtime Database con los detalles del video
                        Map<String, Object> videoData = new HashMap<>();
                        videoData.put("title", titulo);
                        videoData.put("chapterNumber", numeroCapitulo);
                        videoData.put("videoUrl", videoUrl);

                        databaseReference.child(videoId).setValue(videoData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(Agregar_V.this, "Video agregado con éxito", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(Agregar_V.this, "Error al agregar el video", Toast.LENGTH_SHORT).show());
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(Agregar_V.this, "Error al subir el video", Toast.LENGTH_SHORT).show());
    }
}