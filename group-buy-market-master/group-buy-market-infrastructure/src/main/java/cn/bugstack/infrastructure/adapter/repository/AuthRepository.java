package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.auth.model.vo.UserVO;
import cn.bugstack.domain.auth.repository.IAuthRepository;
import cn.bugstack.infrastructure.persistent.dao.IUserDao;
import cn.bugstack.infrastructure.persistent.po.User;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

import static cn.bugstack.domain.auth.model.enumeration.UserRoleType.ACCOUNT;

@Repository
public class AuthRepository implements IAuthRepository {

    @Resource
    private IUserDao userDao;

    @Override
    public UserVO queryByUsername(String username) {
        User user = userDao.queryByUsername(username);
        return toUserVO(user);
    }

    @Override
    public UserVO queryById(Long id) {
        User user = userDao.queryById(id);
        return toUserVO(user);
    }

    @Override
    public UserVO insertUser(String username, String password) {
        User user = User.builder()
                .username(username)
                .password(password)
                .role(ACCOUNT.getType())
                .userStatus(1)
                .build();
        userDao.insert(user);
        return toUserVO(user);
    }

    @Override
    public UserVO updateUser(Long id, String username, String password) {
        User user = User.builder()
                .id(id)
                .username(username)
                .password(password)
                .build();
        userDao.update(user);
        return toUserVO(user);
    }

    private UserVO toUserVO(User user) {
        if (user == null) {
            return null;
        }
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .userStatus(user.getUserStatus())
                .build();
    }
}
