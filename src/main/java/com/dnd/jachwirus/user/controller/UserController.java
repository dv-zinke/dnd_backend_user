package com.dnd.jachwirus.user.controller;

import com.dnd.jachwirus.user.config.JwtTokenProvider;
import com.dnd.jachwirus.user.domain.User;
import com.dnd.jachwirus.user.domain.data.LikeData;
import com.dnd.jachwirus.user.exception.RestException;
import com.dnd.jachwirus.user.repository.UserRepository;
import com.dnd.jachwirus.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Autowired
    UserService userService;

    // 회원가입
    @PostMapping("/join")
    public User join(@RequestBody Map<String, String> user) throws RestException {

        Optional<User> emailExistUser = userRepository.findByEmail(user.get("email"));
        Optional<User> nicknameExistUser = userRepository.findByNickname(user.get("nickname"));



        if(emailExistUser.isPresent()) {
            throw new RestException(HttpStatus.BAD_REQUEST, "존재 하는 이메일입니다.");
        }

        if(nicknameExistUser.isPresent()) {
            throw new RestException(HttpStatus.BAD_REQUEST, "존재 하는 닉네임입니다.");
        }
        // create random object - reuse this as often as possible
        Random random = new Random();

        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = random.nextInt(0xffffff + 1);

        // format it as hexadecimal string (with hashtag and leading zeros)
        String colorCode = String.format("#%06x", nextInt);

        return userRepository.save(User.builder()
                .email(user.get("email"))
                .password(passwordEncoder.encode(user.get("password")))
                .nickname(user.get("nickname"))
                .level((long) 1)
                .avatarColor(colorCode)
                .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                .build());
    }

    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) throws JsonProcessingException {
        User member = userRepository.findByEmail(user.get("email"))
                .orElseThrow(() ->  new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        ObjectNode childNode1 = mapper.createObjectNode();
        childNode1.put("email", member.getEmail());
        childNode1.put("nickname", member.getNickname());
        childNode1.put("avatar_color", member.getAvatarColor());
        childNode1.put("avatar_image_url", member.getAvatarImageUrl());
        childNode1.put("level", member.getLevel());
        childNode1.put("role", member.getRoles().toString());
        childNode1.put("id", member.getId());
        childNode1.put("likes", member.getLikes());
        childNode1.put("move_at", member.getMoveAt());
        childNode1.put("house_name", member.getHouseName());
        rootNode.set("info", childNode1);
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        return jwtTokenProvider.createToken(member.getUsername(),jsonString);

    }

    @GetMapping("/find")
    public Optional<User> findByUserId(@RequestParam  Long userId){
        return userService.findByUserId(userId);
    }

    @GetMapping("/all-user")
    public List<User> findAllUser() { return userService.findAllUser();}

    @PostMapping("/avatar/{userId}")
    public User changeAvatarUser(
            @RequestParam("file") MultipartFile file,
            @RequestParam("color") String color,
            @PathVariable Long userId
    ) throws RestException {
        return userService.changeAvatarUser(file,color,userId);
    }

    @PostMapping("move/{userId}")
    public User changeMoveUser(
            @PathVariable Long userId,
            @RequestParam("move_at") String moveAt,
            @RequestParam("house_name") String houseName
    ) throws RestException {
        return userService.changeMoveDataUser(userId, moveAt, houseName);
    }

    @PostMapping("/likes/{userId}")
    public User changeLikesUser(
           @PathVariable Long userId,
           @RequestBody LikeData likes
    ) throws RestException {
        return userService.changeLikesUser(likes, userId);
    }
}
