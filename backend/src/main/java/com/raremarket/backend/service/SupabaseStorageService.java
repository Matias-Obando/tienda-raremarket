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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class SupabaseStorageService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    public record UploadResult(String publicUrl, String storagePath) {}

    public UploadResult uploadAvatar(MultipartFile file, String userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Avatar file is empty");
        }

        String supabaseUrl = requiredConfig("SUPABASE_URL");
        String serviceRoleKey = requiredConfig("SUPABASE_SERVICE_ROLE_KEY");
        String bucket = readConfig("SUPABASE_AVATAR_BUCKET", "avatars");

        String storagePath = buildStoragePath(userId, file.getOriginalFilename());
        return uploadBinaryToBucket(file, supabaseUrl, serviceRoleKey, bucket, storagePath);
    }

    public List<UploadResult> uploadItemImages(List<MultipartFile> files, String userId) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Debes enviar al menos una imagen.");
        }

        List<MultipartFile> nonEmptyFiles = files.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();

        if (nonEmptyFiles.isEmpty()) {
            throw new IllegalArgumentException("Debes enviar al menos una imagen.");
        }
        if (nonEmptyFiles.size() > 6) {
            throw new IllegalArgumentException("Puedes subir un maximo de 6 imagenes por producto.");
        }

        String supabaseUrl = requiredConfig("SUPABASE_URL");
        String serviceRoleKey = requiredConfig("SUPABASE_SERVICE_ROLE_KEY");
        String bucket = readConfig("SUPABASE_ITEM_BUCKET", "item-images");

        List<UploadResult> uploaded = new ArrayList<>();
        for (MultipartFile file : nonEmptyFiles) {
            validateItemImage(file);
            String storagePath = buildItemStoragePath(userId, file.getOriginalFilename());
            uploaded.add(uploadBinaryToBucket(file, supabaseUrl, serviceRoleKey, bucket, storagePath));
        }

        return uploaded;
    }

    public int deleteItemImagesForOwner(String userId, List<String> publicUrls) {
        if (userId == null || userId.isBlank() || publicUrls == null || publicUrls.isEmpty()) {
            return 0;
        }

        String bucket = readConfig("SUPABASE_ITEM_BUCKET", "item-images");
        String requiredPrefix = "items/" + userId.trim() + "/";

        Set<String> uniquePaths = new LinkedHashSet<>();
        for (String publicUrl : publicUrls) {
            String storagePath = extractStoragePathFromPublicUrl(publicUrl, bucket);
            if (storagePath == null || storagePath.isBlank()) {
                continue;
            }
            if (!storagePath.startsWith(requiredPrefix)) {
                continue;
            }
            uniquePaths.add(storagePath);
        }

        int deleted = 0;
        for (String storagePath : uniquePaths) {
            if (deleteStorageObject(bucket, storagePath, false)) {
                deleted++;
            }
        }

        return deleted;
    }

    private UploadResult uploadBinaryToBucket(
            MultipartFile file,
            String supabaseUrl,
            String serviceRoleKey,
            String bucket,
            String storagePath
    ) {
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

        String bucket = readConfig("SUPABASE_AVATAR_BUCKET", "avatars");
        deleteStorageObject(bucket, storagePath, true);
    }

    public String extractStoragePathFromPublicUrl(String publicUrl) {
        String bucket = readConfig("SUPABASE_AVATAR_BUCKET", "avatars");
        return extractStoragePathFromPublicUrl(publicUrl, bucket);
    }

    private String extractStoragePathFromPublicUrl(String publicUrl, String bucket) {
        if (publicUrl == null || publicUrl.isBlank()) {
            return null;
        }

        String supabaseUrl = requiredConfig("SUPABASE_URL");
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

    private boolean deleteStorageObject(String bucket, String storagePath, boolean failOnError) {
        String supabaseUrl = requiredConfig("SUPABASE_URL");
        String serviceRoleKey = requiredConfig("SUPABASE_SERVICE_ROLE_KEY");
        String deleteUrl = normalizeBaseUrl(supabaseUrl) + "/storage/v1/object/" + encodePathSegment(bucket) + "/" + encodePath(storagePath);

        HttpRequest request = HttpRequest.newBuilder(URI.create(deleteUrl))
                .header("apikey", serviceRoleKey)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            boolean success = response.statusCode() >= 200 && response.statusCode() < 300;
            if (!success && failOnError) {
                throw new IllegalStateException("Supabase Storage no pudo eliminar el archivo: " + response.body());
            }
            return success;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            if (failOnError) {
                throw new IllegalStateException("La limpieza del archivo fue interrumpida.", ex);
            }
            return false;
        } catch (IOException ex) {
            if (failOnError) {
                throw new IllegalStateException("No se pudo conectar con Supabase Storage para borrar el archivo.", ex);
            }
            return false;
        }
    }

    private String buildStoragePath(String userId, String originalFilename) {
        String safeName = originalFilename == null || originalFilename.isBlank()
                ? "avatar.png"
                : originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return "profiles/" + userId + "/" + UUID.randomUUID() + "-" + safeName;
    }

    private String buildItemStoragePath(String userId, String originalFilename) {
        String safeName = originalFilename == null || originalFilename.isBlank()
                ? "item.jpg"
                : originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return "items/" + userId + "/" + UUID.randomUUID() + "-" + safeName;
    }

    private void validateItemImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || ALLOWED_IMAGE_TYPES.stream().noneMatch(type -> type.equalsIgnoreCase(contentType))) {
            throw new IllegalArgumentException("Formato no permitido. Usa JPG, PNG o WEBP.");
        }

        long maxBytes = 5L * 1024L * 1024L;
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("Cada imagen debe pesar menos de 5MB.");
        }
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