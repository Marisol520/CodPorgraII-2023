package com.example.dramasv2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dramasv2.adapter.CapAdapter;
import com.example.dramasv2.adapter.CommentAdapter;
import com.example.dramasv2.modelo.Comment;
import com.example.dramasv2.modelo.Serie;
import com.example.dramasv2.modelo.Video;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Capitulos_S extends AppCompatActivity {

    private TextView tvName;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private CapAdapter videoAdapter;
    private List<Video> videoList;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText editTextComment;
    private Button buttonAddComment;
    private DatabaseReference commentsReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capitulos_s);

        findViewById(R.id.fabAdd_Caps).setOnClickListener(v -> {
            Intent intent = new Intent(Capitulos_S.this, Agregar_V.class);
            startActivity(intent);
        });

        tvName = findViewById(R.id.tvName_s);
        imageView = findViewById(R.id.imageView_s);
        recyclerView = findViewById(R.id.recyclerView_s);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoList = new ArrayList<>();
        videoAdapter = new CapAdapter(this, videoList, this::onVideoClick);
        recyclerView.setAdapter(videoAdapter);

        Serie serie = (Serie) getIntent().getSerializableExtra("serie");

        if (serie != null) {
            tvName.setText(serie.getName());

            if (serie.getImageUrl() != null) {
                new Thread(() -> {
                    try {
                        URL url = new URL(serie.getImageUrl());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);

                        runOnUiThread(() -> imageView.setImageBitmap(myBitmap));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("videos");
        loadVideosFromFirebase();

        // Configurar el botón de regreso
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Configurar el RecyclerView de comentarios
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerViewComments.setAdapter(commentAdapter);

        editTextComment = findViewById(R.id.editTextComment);
        buttonAddComment = findViewById(R.id.buttonAddComment);

        commentsReference = FirebaseDatabase.getInstance().getReference("comments");

        buttonAddComment.setOnClickListener(v -> {
            String commentText = editTextComment.getText().toString();
            if (!commentText.isEmpty()) {
                Comment comment = new Comment("User", commentText, "12:00 PM");
                commentsReference.push().setValue(comment);
                editTextComment.setText("");
            }
        });

        loadCommentsFromFirebase();
    }

    private void loadVideosFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                videoList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Video video = postSnapshot.getValue(Video.class);
                    if (video != null) {
                        Log.d("Capitulos_S", "Video loaded: " + video.getTitle() + ", Chapter: " + video.getChapterNumber());
                        videoList.add(video);
                    }
                }
                videoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar error
            }
        });
    }


    private void loadCommentsFromFirebase() {
        commentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = postSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar error
            }
        });
    }

    private void onVideoClick(Video video) {
        Intent intent = new Intent(Capitulos_S.this, Reproductor_V.class);
        intent.putExtra("videoUrl", video.getVideoUrl());
        startActivity(intent);
    }
}
