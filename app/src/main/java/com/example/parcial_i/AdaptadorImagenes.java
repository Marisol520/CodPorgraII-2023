package com.example.parcial_i;

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

public class AdaptadorImagenes extends BaseAdapter {
    Context context;
    ArrayList<Producto> datosProductosArrayList;
    LayoutInflater layoutInflater;

    public AdaptadorImagenes(Context context, ArrayList<Producto> datosProductosArrayList) {
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
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = layoutInflater.inflate(R.layout.listview_imagenes, viewGroup, false);
        }

        try {
            Producto producto = datosProductosArrayList.get(i);

            TextView tempVal = view.findViewById(R.id.lblCod);
            tempVal.setText(producto.getCodigo());

            tempVal = view.findViewById(R.id.lblDes);
            tempVal.setText(producto.getDescripcion());

            tempVal = view.findViewById(R.id.lblMar);
            tempVal.setText(producto.getMarca());

            tempVal = view.findViewById(R.id.lblPrec);
            tempVal.setText("$"+producto.getPrecio());

            ImageView imgView = view.findViewById(R.id.imgFoto);
            Bitmap imagenBitmap = BitmapFactory.decodeFile(producto.getFoto());
            imgView.setImageBitmap(imagenBitmap);
        } catch (Exception e) {
            Toast.makeText(context, "Error en Adaptador Imagenes: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }
}
