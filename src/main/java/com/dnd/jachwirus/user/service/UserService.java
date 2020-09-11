package com.dnd.jachwirus.user.service;

import com.dnd.jachwirus.user.domain.User;
import com.dnd.jachwirus.user.domain.data.LikeData;
import com.dnd.jachwirus.user.exception.RestException;
import com.dnd.jachwirus.user.repository.UserRepository;
import com.dnd.jachwirus.user.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    S3Service s3Service;

    public Optional<User> findByUserId(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    public User changeAvatarUser(
            MultipartFile file,
            String color,
            Long userId
    ) throws RestException {
        Optional<User> foundUser = userRepository.findById(userId);

        if(foundUser.isPresent()){
            foundUser.get().setAvatarColor(color);
            String filePath = getAwsS3UrlByFile(file);
            foundUser.get().setAvatarImageUrl(filePath);
            return userRepository.save(foundUser.get());
        }else {
            throw new RestException(HttpStatus.NOT_FOUND, "User is not exist");
        }
    }

    public User changeLikesUser (
        LikeData likes,
        Long userId
    ) throws RestException {
        Optional<User> foundUser = userRepository.findById(userId);

        if(foundUser.isPresent()){
            foundUser.get().setLikes(likes.getLikes());
            return userRepository.save(foundUser.get());
        }else {
            throw new RestException(HttpStatus.NOT_FOUND, "User is not exist");
        }
    }

    public User changeMoveDataUser(
            Long userId,
            String moveAt,
            String houseName
    ) throws RestException {
        Optional<User> foundUser = userRepository.findById(userId);

        if(foundUser.isPresent()){
            foundUser.get().setMoveAt(moveAt);
            foundUser.get().setHouseName(houseName);
            return userRepository.save(foundUser.get());
        }else {
            throw new RestException(HttpStatus.NOT_FOUND, "User is not exist");
        }
    }

    String getAwsS3UrlByFile(MultipartFile file) {
        String PathUrl = null;
        try {
            PathUrl = s3Service.uploadFile(new UuidUtil().getUuid(), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return PathUrl;
    }


}
