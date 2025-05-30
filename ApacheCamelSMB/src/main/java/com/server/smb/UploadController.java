package com.server.smb;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping
public class UploadController {

    private final SmbService smbService;

    public UploadController(SmbService smbService) {
        this.smbService = smbService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            smbService.uploadFile(file.getOriginalFilename(), is);
            return ResponseEntity.ok("Arquivo enviado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("filename") String filename) {
        System.out.println("DEu bom..................");
        try {

            InputStream inputStream = smbService.getFileAsStream(filename);

            // Detecta tipo de mídia pelo nome do arquivo, ou force application/octet-stream para download
            String contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename.replace("\"", "") + "\"; filename*=UTF-8''" + URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()))                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            return ResponseEntity
                    .status(404)
                    .body(null);
        }
    }
}
