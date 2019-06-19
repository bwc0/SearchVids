package com.searchvids.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    void uploadImageFile(Long userId, MultipartFile file);

}
