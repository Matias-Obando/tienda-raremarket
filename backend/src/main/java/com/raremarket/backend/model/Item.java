package com.raremarket.backend.model;

public class Item {
    private String id;
    private String titulo;
    private String descripcion;
    private double precioEur;
    private String categoria;
    private String marca;
    private String talla;
    private String estado;
    private String imagen;
    private String[] images;
    private String creadoHace;

    public Item() {}

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecioEur() { return precioEur; }
    public void setPrecioEur(double precioEur) { this.precioEur = precioEur; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String[] getImages() { return images; }
    public void setImages(String[] images) { this.images = images; }

    public String getCreadoHace() { return creadoHace; }
    public void setCreadoHace(String creadoHace) { this.creadoHace = creadoHace; }
}
