package cn.bugstack.api;


import cn.bugstack.types.dto.request.AuthRequest;
import cn.bugstack.types.dto.request.PasswordRequest;
import cn.bugstack.types.dto.response.AuthResponse;
import cn.bugstack.types.dto.result.Result;

public interface IAuthApi {

    Result<AuthResponse> login(AuthRequest request);

    Result<AuthResponse> register(AuthRequest request);

    Result<AuthResponse> profile();

    Result<AuthResponse> password(PasswordRequest request);
}

