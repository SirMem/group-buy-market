package cn.bugstack.trigger.http;


import cn.bugstack.api.IAuthApi;
import cn.bugstack.domain.auth.service.IAuthService;
import cn.bugstack.types.dto.request.AuthRequest;
import cn.bugstack.types.dto.request.PasswordRequest;
import cn.bugstack.types.dto.response.AuthResponse;
import cn.bugstack.types.dto.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController implements IAuthApi {

    @Resource
    private IAuthService authService;

    @PostMapping("/login")
    @Override
    public Result<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return Result.success(response);
    }

    @PostMapping("/register")
    @Override
    public Result<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/profile")
    @Override
    public Result<AuthResponse> profile() {
        return Result.success(authService.profile());
    }

    @PostMapping("/password")
    @Override
    public Result<AuthResponse> password(@Valid @RequestBody PasswordRequest request) {
        return Result.success(authService.password(request));
    }

}
