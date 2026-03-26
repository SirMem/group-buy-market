package cn.bugstack.domain.auth.repository;

import cn.bugstack.domain.auth.model.vo.UserVO;

public interface IAuthRepository {

    UserVO queryByUsername(String username);

    UserVO queryById(Long id);

    UserVO insertUser(String username, String password);

    UserVO updateUser(Long id, String username, String password);
}
