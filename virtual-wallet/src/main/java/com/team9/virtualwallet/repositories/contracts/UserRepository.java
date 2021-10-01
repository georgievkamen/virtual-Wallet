package com.team9.virtualwallet.repositories.contracts;

import com.team9.virtualwallet.models.Pages;
import com.team9.virtualwallet.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    Pages<User> getAll(User user, Pageable pageable);

    Pages<User> getAllUnverified(Pageable pageable);

    void updateProfilePhoto(User user, MultipartFile multipartFile);

    Pages<User> filter(Optional<String> userName, Optional<String> phoneNumber, Optional<String> email, Pageable pageable);

    User getByFieldNotDeleted(String fieldName, String searchTerm, int userId);

    void updateIdAndSelfiePhoto(User user, MultipartFile multipartFile, MultipartFile selfiePhoto);

}
