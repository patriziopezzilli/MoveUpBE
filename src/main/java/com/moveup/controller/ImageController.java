package com.moveup.controller;

import com.moveup.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    /**
     * Upload immagine e conversione in Base64
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String base64Image = imageService.convertToBase64(file);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "base64Image", base64Image,
                "fileName", file.getOriginalFilename(),
                "fileSize", file.getSize(),
                "mimeType", file.getContentType()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Valida se una stringa Base64 è un'immagine valida
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateBase64(@RequestBody Map<String, String> request) {
        String base64String = request.get("base64Image");

        boolean isValid = imageService.isValidBase64Image(base64String);
        String mimeType = isValid ? imageService.getMimeType(base64String) : null;

        return ResponseEntity.ok(Map.of(
            "isValid", isValid,
            "mimeType", mimeType
        ));
    }
}