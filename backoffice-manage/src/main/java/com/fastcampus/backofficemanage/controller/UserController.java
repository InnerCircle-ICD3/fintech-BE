package com.fastcampus.backofficemanage.controller;

import com.fastcampus.backofficemanage.dto.UserSignupRequest;
import com.fastcampus.backofficemanage.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestBody UserSignupRequest request) {
        userService.signup(request);
        return "회원가입 성공!";
    }
}