package com.example.dramasv2.modelo;

import android.net.Uri;

import java.io.Serializable;

public class Serie implements Serializable {
    private String name;
    private String synopsis;
    private String categories;
    private String episodes;
    private String imageUrl;

    public Serie() {
        // Constructor vacío necesario para Firestore
    }

    public Serie(String name, String synopsis, String categories, String episodes, String imageUrl) {
        this.name = name;
        this.synopsis = synopsis;
        this.categories = categories;
        this.episodes = episodes;
        this.imageUrl = imageUrl;
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getEpisodes() {
        return episodes;
    }

    public void setEpisodes(String episodes) {
        this.episodes = episodes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Uri getImageUri() {
        // Convertir la URL de la imagen en una Uri
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return Uri.parse(imageUrl);
        } else {
            return null;
        }
    }

    // Método para obtener un identificador único basado en el nombre de la serie
    public String getId() {
        // Puedes utilizar el nombre de la serie como identificador único
        return name;
    }
}


