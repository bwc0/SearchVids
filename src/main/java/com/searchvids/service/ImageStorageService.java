package com.searchvids.service;

import com.searchvids.model.payload.ResponseMessage;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    ResponseMessage storeImage(Long id, MultipartFile file);
}
