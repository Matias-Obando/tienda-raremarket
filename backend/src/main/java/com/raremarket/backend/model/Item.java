package com.raremarket.backend.model;

public class Item {

    private String id;
    private String titulo;
    private String descripcion;
    private double precioEur;
    private Categoria categoria;
    private String marca;
    private Talla talla;
    private Estado estado;
    private String imagen;
    private String creadoHace;

    public Item() {
    }

    public Item(String id, String titulo, String descripcion, double precioEur, Categoria categoria,
                String marca, Talla talla, Estado estado, String imagen, String creadoHace) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precioEur = precioEur;
        this.categoria = categoria;
        this.marca = marca;
        this.talla = talla;
        this.estado = estado;
        this.imagen = imagen;
        this.creadoHace = creadoHace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioEur() {
        return precioEur;
    }

    public void setPrecioEur(double precioEur) {
        this.precioEur = precioEur;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Talla getTalla() {
        return talla;
    }

    public void setTalla(Talla talla) {
        this.talla = talla;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCreadoHace() {
        return creadoHace;
    }

    public void setCreadoHace(String creadoHace) {
        this.creadoHace = creadoHace;
    }
}
