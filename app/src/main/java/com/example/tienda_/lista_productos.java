package com.example.tienda_;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class lista_productos extends AppCompatActivity {
    Bundle parametros = new Bundle();
    FloatingActionButton btnAgregarProductos;
    ListView lts;
    Cursor cProductos;
    Productos misProductos;
    DB db;
    final ArrayList<Productos> alProductos=new ArrayList<Productos>();
    final ArrayList<Productos> alProductosCopy=new ArrayList<Productos>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_products);

        btnAgregarProductos = findViewById(R.id.fabAgregarProductos);
        btnAgregarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parametros.putString("accion","nuevo");
                abrirActividad(parametros);
            }
        });
        obtenerDatosProductos();
        buscarProductos();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        cProductos.moveToPosition(info.position);
        menu.setHeaderTitle(cProductos.getString(1)); //1 es el codigo
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try{
            int itemId = item.getItemId();
            if (itemId == R.id.mnxAgregar) {
                parametros.putString("accion", "nuevo");
                abrirActividad(parametros);
            } else if (itemId == R.id.mnxModificar) {
                String[] productos = {
                        cProductos.getString(0), //idProducto
                        cProductos.getString(1), //codigo
                        cProductos.getString(2), //descripcion
                        cProductos.getString(3), //marca
                        cProductos.getString(4), //presentacion
                        cProductos.getString(5), //precio
                        cProductos.getString(6), //foto
                };
                parametros.putString("accion", "modificar");
                parametros.putStringArray("productos", productos);
                abrirActividad(parametros);
            } else if (itemId == R.id.mnxEliminar) {
                eliminarProductos();
            }
            return true;
        }catch (Exception e){
            mostrarMsg("Error al seleccionar una opcion del menu: "+ e.getMessage());
            return super.onContextItemSelected(item);
        }
    }
    private void eliminarProductos(){
        try{
            AlertDialog.Builder confirmar = new AlertDialog.Builder(lista_productos.this);
            confirmar.setTitle("Estas seguro de eliminar a: ");
            confirmar.setMessage(cProductos.getString(1)); //1 es el codigo
            confirmar.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String respuesta = db.administrar_productos("eliminar", new String[]{cProductos.getString(0)});//0 es el idProducto
                    if(respuesta.equals("ok")){
                        mostrarMsg("Producto eliminado con exito");
                        obtenerDatosProductos();
                    }else{
                        mostrarMsg("Error al eliminar el producto: "+ respuesta);
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
        }catch (Exception e){
            mostrarMsg("Error al eliminar producto: "+ e.getMessage());
        }
    }
    private void abrirActividad(Bundle parametros){
        Intent abrirActividad = new Intent(getApplicationContext(), MainActivity.class);
        abrirActividad.putExtras(parametros);
        startActivity(abrirActividad);
    }
    private void obtenerDatosProductos(){
        try {
            alProductos.clear();
            alProductosCopy.clear();

            db = new DB(lista_productos.this, "", null, 1);
            cProductos = db.consultar_productos();

            if( cProductos.moveToFirst() ){
                lts = findViewById(R.id.ltsProductos);
                do{
                    misProductos = new Productos(
                            cProductos.getString(0),//idProducto
                            cProductos.getString(1),//codigo
                            cProductos.getString(2),//descripcion
                            cProductos.getString(3),//marca
                            cProductos.getString(4),//presentacion
                            cProductos.getString(5),//precio
                            cProductos.getString(6) //foto
                    );
                    alProductos.add(misProductos);
                }while(cProductos.moveToNext());
                alProductosCopy.addAll(alProductos);

                adaptadorProductos adImagenes = new adaptadorProductos(lista_productos.this, alProductos);
                lts.setAdapter(adImagenes);

                registerForContextMenu(lts);
            }else{
                mostrarMsg("No hay Datos de productos que mostrar.");
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar datos: "+ e.getMessage());
        }
    }
    private void buscarProductos(){
        TextView tempVal;
        tempVal = findViewById(R.id.txtBuscarP);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    alProductos.clear();
                    String valor = tempVal.getText().toString().trim().toLowerCase();
                    if( valor.length()<=0 ){
                        alProductos.addAll(alProductosCopy);
                    }else{
                        for (Productos producto : alProductosCopy){
                            String codigo = producto.getCodigo();
                            String descripcion = producto.getDescripcion();
                            String marca = producto.getMarca();
                            String presentacion = producto.getPresentacion();
                            String precio = producto.getPrecio();
                            if(codigo.toLowerCase().trim().contains(valor) ||
                                    descripcion.toLowerCase().trim().contains(valor) ||
                                    marca.trim().contains(valor) ||
                                    presentacion.trim().toLowerCase().contains(valor) ||
                                    precio.trim().contains(valor)){
                                alProductos.add(producto);
                            }
                        }
                        adaptadorProductos adImagenes = new adaptadorProductos(getApplicationContext(), alProductos);
                        lts.setAdapter(adImagenes);
                    }
                }catch (Exception e){
                    mostrarMsg("Error al buscar: "+ e.getMessage());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
