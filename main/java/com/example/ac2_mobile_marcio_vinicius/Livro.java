package com.example.ac2_mobile_marcio_vinicius;

import com.google.firebase.firestore.Exclude;

public class Livro {
    private String id;
    private String titulo;
    private String anoDePublicacao;
    private String genero;

    public String getStatusDaLeitura() {
        return statusDaLeitura;
    }

    public void setStatusDaLeitura(String statusDaLeitura) {
        this.statusDaLeitura = statusDaLeitura;
    }

    private String statusDaLeitura;
    private boolean favorito;
    private String autor;

    public Livro() {
    }

    public Livro(String id, String titulo, String anoDePublicacao, String genero,
                 String statusDaLeitura, boolean favorito, String autor) {
        this.id = id;
        this.titulo = titulo;
        this.anoDePublicacao = anoDePublicacao;
        this.genero = genero;
        this.statusDaLeitura = statusDaLeitura;
        this.favorito = favorito;
        this.autor = autor;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAnoDePublicacao() {
        return anoDePublicacao;
    }

    public void setAnoDePublicacao(String anoDePublicacao) {
        this.anoDePublicacao = anoDePublicacao;
    }

    public String getGenero() {
        return genero;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

}

