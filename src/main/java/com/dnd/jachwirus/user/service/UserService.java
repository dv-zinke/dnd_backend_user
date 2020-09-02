package com.dnd.jachwirus.user.service;

import com.dnd.jachwirus.user.domain.User;
import com.dnd.jachwirus.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public Optional<User> findByUserId(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUser() {
        return userRepository.findAll();
    }


}
