package cn.bugstack.domain.utils.jwt;

import cn.bugstack.domain.auth.model.vo.UserVO;
import com.auth0.jwt.interfaces.DecodedJWT;

public interface IJwtService {

    String generateToken(UserVO userVO);

    UserVO parseToken(String token);

    DecodedJWT verifyToken(String token);

}
