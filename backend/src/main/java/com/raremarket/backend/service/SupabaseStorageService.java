package com.raremarket.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class SupabaseStorageService {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public record UploadResult(String publicUrl, String storagePath) {}

    public UploadResult uploadAvatar(MultipartFile file, String userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Avatar file is empty");
        }

        String supabaseUrl = requiredConfig("SUPABASE_URL");
        String serviceRoleKey = requiredConfig("SUPABASE_SERVICE_ROLE_KEY");
        String bucket = readConfig("SUPABASE_AVATAR_BUCKET", "avatars");

        String storagePath = buildStoragePath(userId, file.getOriginalFilename());
        String uploadUrl = normalizeBaseUrl(supabaseUrl) + "/storage/v1/object/" + encodePathSegment(bucket) + "/" + encodePath(storagePath);

        HttpRequest request;
        try {
            request = HttpRequest.newBuilder(URI.create(uploadUrl))
                    .header("apikey", serviceRoleKey)
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("x-upsert", "true")
                    .header("Content-Type", file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer el archivo del avatar.", ex);
        }

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La subida del avatar fue interrumpida.", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo conectar con Supabase Storage.", ex);
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Supabase Storage devolvió " + response.statusCode() + ": " + response.body());
        }

        String publicUrl = normalizeBaseUrl(supabaseUrl) + "/storage/v1/object/public/" + encodePathSegment(bucket) + "/" + encodePath(storagePath);
        return new UploadResult(publicUrl, storagePath);
    }

    public void deleteAvatar(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return;
        }

        String supabaseUrl = requiredConfig("SUPABASE_URL");
        String serviceRoleKey = requiredConfig("SUPABASE_SERVICE_ROLE_KEY");
        String bucket = readConfig("SUPABASE_AVATAR_BUCKET", "avatars");
        String deleteUrl = normalizeBaseUrl(supabaseUrl) + "/storage/v1/object/" + encodePathSegment(bucket) + "/" + encodePath(storagePath);

        HttpRequest request = HttpRequest.newBuilder(URI.create(deleteUrl))
                .header("apikey", serviceRoleKey)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Supabase Storage no pudo eliminar el archivo: " + response.body());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La limpieza del avatar fue interrumpida.", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo conectar con Supabase Storage para borrar el avatar.", ex);
        }
    }

    public String extractStoragePathFromPublicUrl(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            return null;
        }

        String supabaseUrl = requiredConfig("SUPABASE_URL");
        String bucket = readConfig("SUPABASE_AVATAR_BUCKET", "avatars");
        String prefix = normalizeBaseUrl(supabaseUrl) + "/storage/v1/object/public/" + encodePathSegment(bucket) + "/";

        if (!publicUrl.startsWith(prefix)) {
            return null;
        }

        String encodedPath = publicUrl.substring(prefix.length());
        if (encodedPath.isBlank()) {
            return null;
        }

        String[] segments = encodedPath.split("/");
        StringBuilder decodedPath = new StringBuilder();
        for (int index = 0; index < segments.length; index++) {
            if (index > 0) {
                decodedPath.append('/');
            }
            decodedPath.append(URLDecoder.decode(segments[index], StandardCharsets.UTF_8));
        }

        return decodedPath.toString();
    }

    private String buildStoragePath(String userId, String originalFilename) {
        String safeName = originalFilename == null || originalFilename.isBlank()
                ? "avatar.png"
                : originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return "profiles/" + userId + "/" + UUID.randomUUID() + "-" + safeName;
    }

    private String normalizeBaseUrl(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String encodePath(String path) {
        StringBuilder encoded = new StringBuilder();
        String[] segments = path.split("/");
        for (int index = 0; index < segments.length; index++) {
            if (index > 0) {
                encoded.append('/');
            }
            encoded.append(encodePathSegment(segments[index]));
        }
        return encoded.toString();
    }

    private String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String requiredConfig(String key) {
        String value = readConfig(key, null);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta configurar " + key + " en el backend.");
        }
        return value.trim();
    }

    private String readConfig(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }

        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }

        return defaultValue;
    }
}