package com.raremarket.backend.dto.item;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ItemUpsertRequest {
    private String sellerId;

    @NotBlank
    @Size(max = 140)
    private String titulo;

    @NotBlank
    @Size(max = 2000)
    private String descripcion;

    @DecimalMin(value = "0.0", inclusive = false)
    private double precioEur;

    @NotBlank
    @Size(max = 80)
    private String categoria;

    @Size(max = 80)
    private String subcategoria;

    @NotBlank
    @Size(max = 20)
    private String genero;

    @NotBlank
    @Size(max = 80)
    private String marca;

    @NotBlank
    @Size(max = 40)
    private String talla;

    @NotBlank
    @Size(max = 40)
    private String estado;

    @NotBlank
    @Size(max = 2048)
    private String imagen;

    @NotNull
    @NotEmpty
    @Size(max = 6)
    private List<String> images;

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
}
