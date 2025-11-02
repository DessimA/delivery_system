package com.delivery.controller;

import com.delivery.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    public UploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/imagem")
    public ResponseEntity<String> uploadImage(@RequestParam("imagem") MultipartFile file) {
        String url = cloudinaryService.uploadFile(file);
        return ResponseEntity.ok(url);
    }
}
