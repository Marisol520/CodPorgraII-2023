package com.example.dramasv2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Agrega_S extends AppCompatActivity {

    private EditText edtName, edtSynopsis, edtCategories, edtEpisodes;
    private Button btnAdd, selectFoto;
    private ImageView imageView;
    private Uri selectedImageUri;

    private static final int PICK_IMAGE_REQUEST = 100;
    private DatabaseReference db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_s);

        edtName = findViewById(R.id.edtName);
        edtSynopsis = findViewById(R.id.edtSynopsis);
        edtCategories = findViewById(R.id.edtCategories);
        edtEpisodes = findViewById(R.id.edtEpisodes);
        btnAdd = findViewById(R.id.btnAdd);
        selectFoto = findViewById(R.id.selectfoto);
        imageView = findViewById(R.id.imageView);

        db = FirebaseDatabase.getInstance().getReference("series");
        storage = FirebaseStorage.getInstance();

        selectFoto.setOnClickListener(v -> openImagePicker());

        btnAdd.setOnClickListener(v -> addSerie());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(bitmap);
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addSerie() {
        String name = edtName.getText().toString().trim();
        String synopsis = edtSynopsis.getText().toString().trim();
        String categories = edtCategories.getText().toString().trim();
        String episodes = edtEpisodes.getText().toString().trim();

        if (name.isEmpty() || synopsis.isEmpty() || categories.isEmpty() || episodes.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageAndAddSerie(name, synopsis, categories, episodes);
    }

    private void uploadImageAndAddSerie(String name, String synopsis, String categories, String episodes) {
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("Images/" + UUID.randomUUID().toString());

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imagesRef.putBytes(data);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    imagesRef.getDownloadUrl().addOnCompleteListener(downloadTask -> {
                        if (downloadTask.isSuccessful()) {
                            String imageUrl = downloadTask.getResult().toString();

                            // Crear una nueva serie con la URL de la imagen
                            String serieId = db.push().getKey();
                            Map<String, Object> serie = new HashMap<>();
                            serie.put("name", name);
                            serie.put("synopsis", synopsis);
                            serie.put("categories", categories);
                            serie.put("episodes", episodes);
                            serie.put("imageUrl", imageUrl);

                            if (serieId != null) {
                                db.child(serieId).setValue(serie)
                                        .addOnCompleteListener(addTask -> {
                                            if (addTask.isSuccessful()) {
                                                Toast.makeText(Agrega_S.this, "Serie added", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent();
                                                intent.putExtra("imageUrl", imageUrl);
                                                setResult(RESULT_OK, intent);
                                                finish(); // Volver a la actividad anterior después de agregar
                                            } else {
                                                Toast.makeText(Agrega_S.this, "Error adding serie", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(Agrega_S.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Agrega_S.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImageFromUrl(String imageUrl) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                inputStream = connection.getInputStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                runOnUiThread(() -> imageView.setImageBitmap(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Agrega_S.this, "Error loading image", Toast.LENGTH_SHORT).show());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}


