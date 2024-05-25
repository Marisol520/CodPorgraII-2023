package com.example.tienda_;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class adaptadorProductos extends BaseAdapter {
    Context context;
    ArrayList<Productos> datosProductosArrayList;
    Productos misProductos;
    LayoutInflater layoutInflater;
    public adaptadorProductos(Context context, ArrayList<Productos> datosProductosArrayList) {
        this.context = context;
        this.datosProductosArrayList = datosProductosArrayList;
    }
    @Override
    public int getCount() {
        return datosProductosArrayList.size();
    }
    @Override
    public Object getItem(int i) {
        return datosProductosArrayList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return Long.parseLong(datosProductosArrayList.get(i).getIdProducto());
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.list_viewimagenes, viewGroup, false);
        try{
            misProductos = datosProductosArrayList.get(i);

            TextView tempVal = itemView.findViewById(R.id.lblCodigo);
            tempVal.setText(misProductos.getCodigo());

            tempVal = itemView.findViewById(R.id.lblDescripcion);
            tempVal.setText(misProductos.getDescripcion());

            tempVal = itemView.findViewById(R.id.lblMarca);
            tempVal.setText(misProductos.getMarca());


            tempVal = itemView.findViewById(R.id.lblPresentacion);
            tempVal.setText(misProductos.getPresentacion());

            tempVal = itemView.findViewById(R.id.lblPrecio);
            tempVal.setText(misProductos.getPrecio());

            ImageView imgView = itemView.findViewById(R.id.imgFoto);
            Bitmap imagenBitmap = BitmapFactory.decodeFile(misProductos.getFoto());
            imgView.setImageBitmap(imagenBitmap);
        }catch (Exception e){
            Toast.makeText(context, "Error en Adaptador Productos: "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return itemView;
    }
}
