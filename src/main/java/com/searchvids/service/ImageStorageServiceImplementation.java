package com.searchvids.service;

import com.searchvids.exception.FileStorageException;
import com.searchvids.exception.ResourceNotFoundException;
import com.searchvids.model.Image;
import com.searchvids.model.User;
import com.searchvids.model.payload.ResponseMessage;
import com.searchvids.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageServiceImplementation implements ImageStorageService {

    private UserRepository userRepository;

    public ImageStorageServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseMessage storeImage(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException();
            }

            Image image = new Image(fileName, file.getContentType(), file.getBytes());

            user.setImageDetails(image);
            userRepository.save(user);

            return new ResponseMessage("Image uploaded successfully", HttpStatus.OK.getReasonPhrase(), user);
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file " + fileName + " please try again", ex);
        }
    }
}
