package com.moveup.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.io.IOException;

@Service
public class ImageService {

    private static final String JPEG_PREFIX = "data:image/jpeg;base64,";
    private static final String PNG_PREFIX = "data:image/png;base64,";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * Converte un MultipartFile in stringa Base64
     */
    public String convertToBase64(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Controllo dimensione file
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File troppo grande. Dimensione massima: 5MB");
        }

        // Controllo tipo file
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Tipo file non supportato. Solo JPEG e PNG sono accettati");
        }

        // Converti in Base64
        byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);

        // Aggiungi prefix per data URL
        String prefix = contentType.equals("image/jpeg") ? JPEG_PREFIX : PNG_PREFIX;
        return prefix + base64;
    }

    /**
     * Estrae solo la parte Base64 da una stringa data URL
     */
    public String extractBase64Data(String base64DataUrl) {
        if (base64DataUrl == null || base64DataUrl.isEmpty()) {
            return null;
        }

        // Rimuovi il prefix se presente
        if (base64DataUrl.startsWith(JPEG_PREFIX)) {
            return base64DataUrl.substring(JPEG_PREFIX.length());
        } else if (base64DataUrl.startsWith(PNG_PREFIX)) {
            return base64DataUrl.substring(PNG_PREFIX.length());
        }

        // Se non ha prefix, restituisci come è
        return base64DataUrl;
    }

    /**
     * Valida se una stringa è un'immagine Base64 valida
     */
    public boolean isValidBase64Image(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return false;
        }

        try {
            // Prova a decodificare
            if (base64String.contains(",")) {
                // È un data URL, estrai solo la parte Base64
                String base64Data = base64String.split(",")[1];
                Base64.getDecoder().decode(base64Data);
            } else {
                // È solo Base64
                Base64.getDecoder().decode(base64String);
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Ottiene il tipo MIME da una stringa Base64
     */
    public String getMimeType(String base64DataUrl) {
        if (base64DataUrl == null) {
            return null;
        }

        if (base64DataUrl.startsWith(JPEG_PREFIX)) {
            return "image/jpeg";
        } else if (base64DataUrl.startsWith(PNG_PREFIX)) {
            return "image/png";
        }

        return "image/jpeg"; // default
    }
}