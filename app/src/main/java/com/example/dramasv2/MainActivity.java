package com.example.dramasv2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dramasv2.adapter.SerieAdapter;
import com.example.dramasv2.modelo.Serie;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SerieAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SerieAdapter serieAdapter;
    private List<Serie> seriesList;
    private DatabaseReference db;
    private EditText searchEditText;
    private Button searchButton; // Nuevo botón de búsqueda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        seriesList = new ArrayList<>();
        serieAdapter = new SerieAdapter(this, seriesList);
        recyclerView.setAdapter(serieAdapter);
        serieAdapter.setOnItemClickListener(this);

        db = FirebaseDatabase.getInstance().getReference("series");
        loadSeriesFromDatabase();

        FloatingActionButton fabAgregar = findViewById(R.id.fab);
        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Agrega_S.class);
            startActivityForResult(intent, 1);
        });

        ImageButton btnLogout = findViewById(R.id.action_logout);
        btnLogout.setOnClickListener(v -> logout());

        registerForContextMenu(recyclerView);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.button); // Referencia al nuevo botón de búsqueda

        // Asignar un OnClickListener al botón de búsqueda
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getText().toString().trim();
                search(searchText);
            }
        });

        // Configurar el botón de limpiar búsqueda
        ImageButton clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limpiar el texto de búsqueda y actualizar el RecyclerView
                searchEditText.setText("");
                search("");
            }
        });

        // Observar cambios en el texto de búsqueda para mostrar/ocultar el botón de limpiar
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Mostrar el botón de limpiar si hay texto en el EditText, ocultarlo de lo contrario
                clearButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Configurar el botón flotante de la izquierda
        FloatingActionButton fabLeft = findViewById(R.id.fab_left);
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Chat.class);
                startActivity(intent);
            }
        });

        // Verificar el rol del usuario para mostrar el botón de agregar
        checkUserRole();
    }

    private void loadSeriesFromDatabase() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                seriesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Serie serie = dataSnapshot.getValue(Serie.class);
                    seriesList.add(serie);
                }
                serieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void search(String searchText) {
        // Realizar la búsqueda en la lista de series
        List<Serie> filteredList = new ArrayList<>();
        for (Serie serie : seriesList) {
            if (serie.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(serie);
            }
        }
        // Actualizar el adaptador con los resultados de la búsqueda
        serieAdapter.filterList(filteredList);
    }

    private void checkUserRole() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FloatingActionButton fab = findViewById(R.id.fab);

        if (currentUser != null && currentUser.getEmail().equals("admin@gmail.com")) {
            // Mostrar el botón de agregar para el usuario administrador
            fab.setVisibility(View.VISIBLE);
        } else {
            // Ocultar el botón de agregar para otros usuarios
            fab.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Manejar la respuesta de la actividad de agregar serie si es necesario
        }
    }

    @Override
    public void onItemClick(int position) {
        Serie clickedSerie = seriesList.get(position);
        Intent intent = new Intent(MainActivity.this, Editar_S.class);
        intent.putExtra("serie", clickedSerie);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_1, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = serieAdapter.getPosition();
        Serie clickedSerie = seriesList.get(position);

        int itemId = item.getItemId();
        if (itemId == R.id.menu_editar) {
            Intent intent = new Intent(MainActivity.this, Editar_S.class);
            intent.putExtra("serie", clickedSerie);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.menu_eliminar) {
            String serieName = clickedSerie.getName();
            db.orderByChild("name").equalTo(serieName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot serieSnapshot : snapshot.getChildren()) {
                        serieSnapshot.getRef().removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                seriesList.remove(position);
                                serieAdapter.notifyItemRemoved(position);
                                Toast.makeText(MainActivity.this, "Serie eliminada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Error al eliminar la serie", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(MainActivity.this, "Error en la consulta de eliminación", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
