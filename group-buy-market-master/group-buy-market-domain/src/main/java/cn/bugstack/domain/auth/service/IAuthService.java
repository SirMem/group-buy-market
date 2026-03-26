package cn.bugstack.domain.auth.service;

import cn.bugstack.types.dto.request.AuthRequest;
import cn.bugstack.types.dto.request.PasswordRequest;
import cn.bugstack.types.dto.response.AuthResponse;

/**
 * @create 2025-11-12 11:06
 */
public interface IAuthService {

    AuthResponse login(AuthRequest request);

    AuthResponse register(AuthRequest request);

    AuthResponse profile();

    AuthResponse password(PasswordRequest request);

}
