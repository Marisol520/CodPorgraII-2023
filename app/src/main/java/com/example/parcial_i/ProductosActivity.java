package com.example.parcial_i;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductosActivity extends AppCompatActivity {

    Bundle parametros = new Bundle();
    FloatingActionButton btnAgregarProd;
    ListView lts;
    Cursor cProd;
    Producto productosAdapter;
    DB db;
    final ArrayList<Producto> alProd = new ArrayList<>();
    final ArrayList<Producto> alProdCopy = new ArrayList<>();
    JSONArray datosJSON;
    JSONObject jsonObject;
    obtenerDatosServidor datosServidor;
    detectarInternet di;
    int posicion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        db = new DB(ProductosActivity.this, "", null, 1);
        btnAgregarProd = findViewById(R.id.fabAgregarProd);
        btnAgregarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parametros.putString("accion", "nuevo");
                abrirActividad(parametros);
            }
        });
        try {
            di = new detectarInternet(getApplicationContext());
            if (di.hayConexionInternet()) {
                obtenerDatosProdServidor();
            } else {
                obtenerDatosProd();
            }
        } catch (Exception e) {
            mostrarMsg("Error al cargar lista: " + e.getMessage());
        }
        buscarProd();
    }

    private void obtenerDatosProdServidor() {
        try {
            datosServidor = new obtenerDatosServidor();
            String data = datosServidor.execute().get();
            jsonObject = new JSONObject(data);
            datosJSON = jsonObject.getJSONArray("rows");
            mostrarDatosProd();
        } catch (Exception e) {
            mostrarMsg("Error al obtener datos del server: " + e.getMessage());
        }
    }

    private void mostrarDatosProd() {
        try {
            if (datosJSON.length() > 0) {
                lts = findViewById(R.id.ltsProd);
                alProd.clear();
                alProdCopy.clear();

                JSONObject misDatosJSONObject;
                for (int i = 0; i < datosJSON.length(); i++) {
                    misDatosJSONObject = datosJSON.getJSONObject(i).getJSONObject("value");
                    productosAdapter = new Producto(
                            misDatosJSONObject.getString("_id"),
                            misDatosJSONObject.getString("_rev"),
                            misDatosJSONObject.getString("idProd"),
                            misDatosJSONObject.getString("codigo"),
                            misDatosJSONObject.getString("descripcion"),
                            misDatosJSONObject.getString("marca"),
                            misDatosJSONObject.getString("presentacion"),
                            misDatosJSONObject.getString("precio"),
                            misDatosJSONObject.getString("costo"),
                            misDatosJSONObject.getString("stock"),
                            misDatosJSONObject.getString("urlCompletaFoto")
                    );
                    alProd.add(productosAdapter);
                }
                alProdCopy.addAll(alProd);
                AdaptadorImagenes adImagenes = new AdaptadorImagenes(ProductosActivity.this, alProd);
                lts.setAdapter(adImagenes);
                registerForContextMenu(lts);
            } else {
                mostrarMsg("No hay datos que mostrar.");
            }
        } catch (Exception e) {
            mostrarMsg("Error al mostrar los datos: " + e.getMessage());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // Verifica si hay datos JSON y si se puede mover a la posición específica
        if (datosJSON != null && info.position >= 0 && info.position < datosJSON.length()) {
            try {
                JSONObject producto = datosJSON.getJSONObject(info.position).getJSONObject("value");
                menu.setHeaderTitle(producto.getString("nombre")); // Suponiendo que "nombre" es el campo que deseas utilizar como título
            } catch (JSONException e) {
                e.printStackTrace();
                // Maneja cualquier excepción que pueda ocurrir al obtener el título del producto
            }
        }
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            int itemId = item.getItemId();

            if (itemId == R.id.mnxAgregar) {
                parametros.putString("accion", "nuevo");
                abrirActividad(parametros);
            } else if (itemId == R.id.mnxModificar) {
                parametros.putString("accion", "modificar");
                parametros.putString("amigos", datosJSON.getJSONObject(posicion).toString());
                abrirActividad(parametros);
            } else if (itemId == R.id.mnxEliminar) {
                eliminarProd();
            }
        } catch (Exception e) {
            mostrarMsg("Error al seleccionar una opcion del mennu: " + e.getMessage());
            return super.onContextItemSelected(item);
        }
        return true;
    }

    private void eliminarProd() {
        try {
            if (cProd != null && !cProd.isClosed() && cProd.moveToFirst()) {
                AlertDialog.Builder confirmar = new AlertDialog.Builder(ProductosActivity.this);
                confirmar.setTitle("Estás seguro de eliminar: ");
                confirmar.setMessage(cProd.getString(1)); //1 es el nombre
                confirmar.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String respuesta = db.administrar_prod("eliminar", new String[]{cProd.getString(0)});//0 es el idAmigo
                            if (respuesta.equals("ok")) {
                                mostrarMsg("Producto eliminado con éxito");
                                obtenerDatosProd();
                            } else {
                                mostrarMsg("Error al eliminar el producto: " + respuesta);
                            }
                        } catch (Exception e) {
                            mostrarMsg("Error al intentar eliminar: " + e.getMessage());
                        }
                    }
                });
                confirmar.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                confirmar.create().show();
            } else {
                mostrarMsg("No se puede eliminar el producto porque no se ha seleccionado ninguno.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMsg("Error al eliminar producto: " + e.getMessage());
        }
    }

    private void abrirActividad(Bundle parametros) {
        Intent abrirActividad = new Intent(getApplicationContext(), MainActivity.class);
        abrirActividad.putExtras(parametros);
        startActivity(abrirActividad);
    }

    private void obtenerDatosProd() {
        try {
            cProd = db.consultar_prod();

            if (cProd.moveToFirst()) {
                datosJSON = new JSONArray();
                do {
                    jsonObject = new JSONObject();
                    JSONObject jsonObjectValue = new JSONObject();
                    jsonObject.put("_id", cProd.getString(0));
                    jsonObject.put("_rev", cProd.getString(1));
                    jsonObject.put("idProd", cProd.getString(2));
                    jsonObject.put("codigo", cProd.getString(3));
                    jsonObject.put("descripcion", cProd.getString(4));
                    jsonObject.put("marca", cProd.getString(5));
                    jsonObject.put("presentacion", cProd.getString(6));
                    jsonObject.put("precio", cProd.getString(7));
                    jsonObject.put("costo", cProd.getString(8));
                    jsonObject.put("stock", cProd.getString(9));
                    jsonObject.put("urlCompletaFoto", cProd.getString(10));

                    jsonObjectValue.put("value", jsonObject);
                    datosJSON.put(jsonObjectValue);

                } while (cProd.moveToNext());
                mostrarDatosProd();
            } else {
                mostrarMsg("No hay Datos de amigos que mostrar.");
            }
        } catch (Exception e) {
            mostrarMsg("Error al mostrar datos: " + e.getMessage());
        }
    }

    private void buscarProd() {
        TextView tempVal;
        tempVal = findViewById(R.id.txtBuscarProd);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    alProd.clear();
                    String valor = tempVal.getText().toString().trim().toLowerCase();
                    if (valor.length() <= 0) {
                        alProd.addAll(alProdCopy);
                    } else {
                        for (Producto producto : alProdCopy) {
                            String codigo = producto.getCodigo();
                            String descripcion = producto.getDescripcion();
                            String marca = producto.getMarca();
                            String presentacion = producto.getPresentacion();
                            String precio = producto.getPrecio();
                            String costo= producto.getCosto();
                            String stock = producto.getStock();
                            if (codigo.toLowerCase().trim().contains(valor) ||
                                    descripcion.toLowerCase().trim().contains(valor) ||
                                    marca.trim().contains(valor) ||
                                    presentacion.trim().toLowerCase().contains(valor) ||
                                    precio.trim().toLowerCase().contains(valor) ||
                                    costo.trim().toLowerCase().contains(valor) ||
                                    stock.trim().toLowerCase().contains(valor)) {
                                alProd.add(producto);
                            }
                        }
                        AdaptadorImagenes adImagenes = new AdaptadorImagenes(getApplicationContext(), alProd);
                        lts.setAdapter(adImagenes);
                    }
                } catch (Exception e) {
                    mostrarMsg("Error al buscar: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void mostrarMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
