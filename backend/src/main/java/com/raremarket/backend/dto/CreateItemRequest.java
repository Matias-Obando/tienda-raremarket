package com.raremarket.backend.dto;

import com.raremarket.backend.model.Categoria;
import com.raremarket.backend.model.Estado;
import com.raremarket.backend.model.Talla;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateItemRequest {

    @NotBlank
    private String titulo;

    @NotBlank
    private String descripcion;

    @DecimalMin("0.01")
    private double precioEur;

    @NotNull
    private Categoria categoria;

    @NotBlank
    private String marca;

    @NotNull
    private Talla talla;

    @NotNull
    private Estado estado;

    @NotBlank
    private String imagen;

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
}
