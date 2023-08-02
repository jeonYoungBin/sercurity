package com.security.security.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/szs")
public class FileUploadController {

    @PostMapping("/upload")
    String fileUpload(@RequestParam("file")MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        int pos = originalFilename.lastIndexOf(".");
        String fileName = originalFilename.substring(pos + 1);
        if(!file.isEmpty()) {
            String storeFileName = UUID.randomUUID() + "." + fileName;
            file.transferTo(new File(storeFileName));
        }

        return null;
    }
}
