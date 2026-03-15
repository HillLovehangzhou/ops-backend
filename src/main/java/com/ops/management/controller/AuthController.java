package com.ops.management.controller;

import com.ops.management.dto.ApiResponse;
import com.ops.management.dto.LoginRequest;
import com.ops.management.dto.LoginResponse;
import com.ops.management.entity.User;
import com.ops.management.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getUserInfo() {
        User user = authService.getCurrentUser();
        if (user == null) {
            return ApiResponse.error(401, "未登录");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole().name());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());

        return ApiResponse.success(userInfo);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success();
    }
}
