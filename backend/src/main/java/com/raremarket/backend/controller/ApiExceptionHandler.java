package com.raremarket.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            fieldErrors.putIfAbsent(field, translateFieldValidation(field, error.getCode()));
        });

        String message = fieldErrors.values().stream().findFirst().orElse("Revisa los datos enviados.");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", message);
        body.put("errors", fieldErrors);
        body.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleStatus(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String translatedMessage = translateReason(exception.getReason());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", translatedMessage);
        body.put("status", status.value());

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Ha ocurrido un error inesperado. Intentalo de nuevo.");
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String translateFieldValidation(String field, String code) {
        if (field == null || code == null) {
            return "Revisa los datos enviados.";
        }

        return switch (field) {
            case "titulo" -> switch (code) {
                case "NotBlank" -> "El titulo es obligatorio.";
                case "Size" -> "El titulo no puede superar 140 caracteres.";
                default -> "El titulo no es valido.";
            };
            case "descripcion" -> switch (code) {
                case "NotBlank" -> "La descripcion es obligatoria.";
                case "Size" -> "La descripcion no puede superar 2000 caracteres.";
                default -> "La descripcion no es valida.";
            };
            case "precioEur" -> "El precio debe ser mayor que 0.";
            case "categoria" -> switch (code) {
                case "NotBlank" -> "La categoria es obligatoria.";
                case "Size" -> "La categoria no puede superar 80 caracteres.";
                default -> "La categoria no es valida.";
            };
            case "marca" -> switch (code) {
                case "NotBlank" -> "La marca es obligatoria.";
                case "Size" -> "La marca no puede superar 80 caracteres.";
                default -> "La marca no es valida.";
            };
            case "talla" -> switch (code) {
                case "NotBlank" -> "La talla es obligatoria.";
                case "Size" -> "La talla no puede superar 40 caracteres.";
                default -> "La talla no es valida.";
            };
            case "estado" -> switch (code) {
                case "NotBlank" -> "El estado es obligatorio.";
                case "Size" -> "El estado no puede superar 40 caracteres.";
                default -> "El estado no es valido.";
            };
            case "imagen" -> switch (code) {
                case "NotBlank" -> "Debes indicar una imagen principal.";
                case "Size" -> "La URL de la imagen principal es demasiado larga.";
                default -> "La imagen principal no es valida.";
            };
            case "images" -> switch (code) {
                case "NotNull", "NotEmpty" -> "Debes subir al menos una imagen.";
                case "Size" -> "Puedes subir un maximo de 6 imagenes por articulo.";
                default -> "Las imagenes no son validas.";
            };
            default -> "Revisa los datos enviados.";
        };
    }

    private String translateReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "No se pudo completar la solicitud.";
        }

        String normalized = reason.trim();

        return switch (normalized) {
            case "Authentication required" -> "Necesitas iniciar sesion para realizar esta accion.";
            case "Invalid authentication subject" -> "Tu sesion no es valida. Vuelve a iniciar sesion.";
            case "Item not found" -> "Articulo no encontrado.";
            case "User not found" -> "Usuario no encontrado.";
            case "You can only modify your own items" -> "Solo puedes modificar o eliminar tus propios articulos.";
            case "minPrice cannot be negative" -> "El precio minimo no puede ser negativo.";
            case "maxPrice cannot be negative" -> "El precio maximo no puede ser negativo.";
            case "minPrice cannot be greater than maxPrice" -> "El precio minimo no puede ser mayor que el maximo.";
            case "precioEur cannot be negative" -> "El precio no puede ser negativo.";
            case "imagen is required" -> "Debes indicar una imagen principal.";
            default -> {
                if (normalized.toLowerCase(Locale.ROOT).endsWith(" is required")) {
                    String field = normalized.substring(0, normalized.length() - " is required".length());
                    yield "El campo " + field + " es obligatorio.";
                }
                yield normalized;
            }
        };
    }
}
