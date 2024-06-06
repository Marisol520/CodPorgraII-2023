package com.example.parcial_i;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Base64;

@RequiresApi(api = Build.VERSION_CODES.O)
public class utilidades {
    static String url_consulta = "http://192.168.1.9:5984/fatima/_design/giselle/_view/giselle";
    static String url_mto = "http://192.168.1.9:5984/fatima";
    static String user="Admin";
    static String passwd="12345";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user+":"+passwd).getBytes());
    public String generarIdUnico(){
        return java.util.UUID.randomUUID().toString();
    }
}