package com.searchvids.controller;

import com.searchvids.model.payload.ResponseMessage;
import com.searchvids.model.payload.UploadFileResponse;
import com.searchvids.service.ImageStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ImageStorageController {

    private ImageStorageService imageStorageService;

    public ImageStorageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/users/{id}/image")
    @ResponseStatus(HttpStatus.OK)
    public UploadFileResponse uploadImage(@PathVariable Long id, @RequestParam("imageFile") MultipartFile file) {
        ResponseMessage message = imageStorageService.storeImage(id, file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(message.getUser().getImageDetails().getFileName())
                .toUriString();

        return new UploadFileResponse(message.getUser().getImageDetails().getFileName(), fileDownloadUri,
                file.getContentType(), file.getSize(), id);
    }
}
