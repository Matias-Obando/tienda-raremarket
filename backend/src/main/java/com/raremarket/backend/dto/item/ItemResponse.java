package com.raremarket.backend.dto.item;

import com.raremarket.backend.model.Item;

import java.time.OffsetDateTime;
import java.util.List;

public class ItemResponse {
    private String id;
    private String sellerId;
    private String titulo;
    private String descripcion;
    private double precioEur;
    private String categoria;
    private String subcategoria;
    private String genero;
    private String marca;
    private String talla;
    private String estado;
    private String imagen;
    private List<String> images;
    private String creadoHace;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static ItemResponse from(Item item) {
        ItemResponse response = new ItemResponse();
        response.setId(item.getId());
        response.setSellerId(item.getSellerId());
        response.setTitulo(item.getTitulo());
        response.setDescripcion(item.getDescripcion());
        response.setPrecioEur(item.getPrecioEur());
        response.setCategoria(item.getCategoria());
        response.setSubcategoria(item.getSubcategoria());
        response.setGenero(item.getGenero());
        response.setMarca(item.getMarca());
        response.setTalla(item.getTalla());
        response.setEstado(item.getEstado());
        response.setImagen(item.getImagen());
        response.setImages(item.getImages());
        response.setCreadoHace(item.getCreadoHace());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getCreadoHace() {
        return creadoHace;
    }

    public void setCreadoHace(String creadoHace) {
        this.creadoHace = creadoHace;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
