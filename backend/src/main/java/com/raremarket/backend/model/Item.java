package com.raremarket.backend.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "seller_id", nullable = false)
    private String sellerId;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "text")
    private String descripcion;

    @Column(name = "precio_eur", nullable = false)
    private double precioEur;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String talla;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false, columnDefinition = "text")
    private String imagen;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_images", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    private List<String> images = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    @Transient
    public String getCreadoHace() {
        if (createdAt == null) {
            return null;
        }

        Duration duration = Duration.between(createdAt, OffsetDateTime.now());
        long minutes = Math.max(duration.toMinutes(), 0);
        if (minutes < 1) {
            return "hace menos de 1 minuto";
        }
        if (minutes < 60) {
            return "hace " + minutes + (minutes == 1 ? " minuto" : " minutos");
        }

        long hours = duration.toHours();
        if (hours < 24) {
            return "hace " + hours + (hours == 1 ? " hora" : " horas");
        }

        long days = duration.toDays();
        if (days < 30) {
            return "hace " + days + (days == 1 ? " dia" : " dias");
        }

        long months = days / 30;
        if (months < 12) {
            return "hace " + months + (months == 1 ? " mes" : " meses");
        }

        long years = days / 365;
        return "hace " + years + (years == 1 ? " ano" : " anos");
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
        this.images = images == null ? new ArrayList<>() : new ArrayList<>(images);
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
