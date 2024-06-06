package com.example.parcial_i;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView tempVal;
    Button btn;
    FloatingActionButton btnRegresar;
    String id="", rev="", idProd="", accion="nuevo";
    ImageView img;
    String urlCompletaFoto;
    Intent tomarFotoIntent;
    utilidades utls;
    DB db;
    detectarInternet di;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            utls = new utilidades();
        }
        db = new DB(getApplicationContext(), "", null, 1);
        di = new detectarInternet(getApplicationContext());

        btnRegresar = findViewById(R.id.fabListaProd);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regresarLista = new Intent(getApplicationContext(), ProductosActivity.class);
                startActivity(regresarLista);
            }
        });
        btn = findViewById(R.id.btnGuardarProd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    tempVal = findViewById(R.id.txtCod);
                    String codigo = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtDes);
                    String descripcion = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtMar);
                    String marca = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtPres);
                    String presentacion = tempVal.getText().toString();

                    tempVal = findViewById(R.id.txtPrec);
                    String precioStr = tempVal.getText().toString(); // Obteniendo el precio como String
                    double precio = Double.parseDouble(precioStr); // Convertir el precio a double

                    tempVal = findViewById(R.id.txtCos);
                    String costoStr = tempVal.getText().toString(); // Obteniendo el costo como String
                    double costo = Double.parseDouble(costoStr); // Convertir el costo a double

                    tempVal = findViewById(R.id.txtSto);
                    String stock = tempVal.getText().toString();

                    // Calcular ganancia
                    double ganancia = calcularGanancia(precio, costo);

                    String respuesta = "";
                    if (di.hayConexionInternet()) {
                        // Obtener datos a enviar al servidor
                        JSONObject datosProd = new JSONObject();
                        if (accion.equals("modificar")) {
                            datosProd.put("_id", id);
                            datosProd.put("_rev", rev);
                        }
                        datosProd.put("idProd", idProd);
                        datosProd.put("codigo", codigo);
                        datosProd.put("descripcion", descripcion);
                        datosProd.put("marca", marca);
                        datosProd.put("presentacion", presentacion);
                        datosProd.put("precio", precio);
                        datosProd.put("costo", costo);
                        datosProd.put("ganancia", ganancia); // Agregar ganancia al objeto JSON
                        datosProd.put("stock", stock);
                        datosProd.put("urlCompletaFoto", urlCompletaFoto);

                        // Enviamos los datos al servidor
                        enviarDatosServidor objGuardarDatosServidor = new enviarDatosServidor(getApplicationContext());
                        respuesta = objGuardarDatosServidor.execute(datosProd.toString()).get();

                        // Comprobación de la respuesta
                        JSONObject respuestaJSONObject = new JSONObject(respuesta);
                        if (respuestaJSONObject.getBoolean("ok")) {
                            id = respuestaJSONObject.getString("id");
                            rev = respuestaJSONObject.getString("rev");
                        } else {
                            respuesta = "Error al guardar en servidor: " + respuesta;
                        }
                    }
                    String[] datos = new String[]{id, rev, idProd, codigo, descripcion, marca, presentacion, precioStr, costoStr, stock, urlCompletaFoto}; // Usar precioStr y costoStr
                    respuesta = db.administrar_prod(accion, datos);
                    if (respuesta.equals("ok")) {
                        mostrarMsg("Producto registrado con éxito.");
                        listarProd();
                    } else {
                        mostrarMsg("Error al intentar registrar el producto: " + respuesta);
                    }
                } catch (Exception e) {
                    mostrarMsg("Error al guardar datos en el servidor o en SQLite: " + e.getMessage());
                }
            }
        });

        img = findViewById(R.id.btnImgProd);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFotoProd();
            }
        });
        mostrarDatosProd();
    }
    private double calcularGanancia(double precioVenta, double costo) {
        return precioVenta - costo;
    }

    private void tomarFotoProd(){
        tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fotoProd = null;
        try{
            fotoProd = crearImagenamigo();
            if( fotoProd!=null ){
                Uri urifotoAmigo = FileProvider.getUriForFile(MainActivity.this,
                        "com.Parcial_I.fileprovider", fotoProd);
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, urifotoAmigo);
                startActivityForResult(tomarFotoIntent, 1);
            }else{
                mostrarMsg("No se pudo tomar la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al abrir la camara"+ e.getMessage());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if( requestCode==1 && resultCode==RESULT_OK ){
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                img.setImageBitmap(imagenBitmap);
            }else{
                mostrarMsg("Se cancelo la toma de la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al seleccionar la foto"+ e.getMessage());
        }
    }
    private File crearImagenamigo() throws Exception{
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),
                fileName = "imagen_"+fechaHoraMs+"_";
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if( dirAlmacenamiento.exists()==false ){
            dirAlmacenamiento.mkdirs();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        urlCompletaFoto = image.getAbsolutePath();
        return image;
    }
    private void mostrarDatosProd() {
        try {
            Bundle parametros = getIntent().getExtras();
            if (parametros != null) {
                accion = parametros.getString("accion");

                if ("modificar".equals(accion)) {
                    String productosString = parametros.getString("productos");
                    if (productosString != null) {
                        JSONObject jsonObject = new JSONObject(productosString).getJSONObject("value");
                        id = jsonObject.getString("_id");
                        rev = jsonObject.getString("_rev");
                        idProd = jsonObject.getString("idProd");

                        tempVal = findViewById(R.id.txtCod);
                        tempVal.setText(jsonObject.getString("codigo"));

                        tempVal = findViewById(R.id.txtDes);
                        tempVal.setText(jsonObject.getString("descripcion"));

                        tempVal = findViewById(R.id.txtMar);
                        tempVal.setText(jsonObject.getString("marca"));

                        tempVal = findViewById(R.id.txtPres);
                        tempVal.setText(jsonObject.getString("presentacion"));

                        tempVal = findViewById(R.id.txtPrec);
                        tempVal.setText(jsonObject.getString("precio"));

                        tempVal = findViewById(R.id.txtCos);
                        tempVal.setText(jsonObject.getString("costo"));

                        tempVal = findViewById(R.id.txtSto);
                        tempVal.setText(jsonObject.getString("stock"));

                        urlCompletaFoto = jsonObject.getString("urlCompletaFoto");
                        Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                        img.setImageBitmap(imagenBitmap);
                    } else {
                        mostrarMsg("El valor asociado con la clave es nulo.");
                    }
                } else { // Nuevos registros
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        idProd = utls.generarIdUnico();
                    }
                }
            } else {
                mostrarMsg("No se encontraron parámetros.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mostrarMsg("Error al mostrar los datos: " + e.getMessage());
        }
    }



    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    private void listarProd(){
        Intent intent = new Intent(getApplicationContext(), ProductosActivity.class);
        startActivity(intent);
    }
}