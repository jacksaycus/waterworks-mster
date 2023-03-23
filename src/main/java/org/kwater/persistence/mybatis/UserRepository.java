package org.kwater.persistence.mybatis;

import org.kwater.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    @Autowired
    private UserMapper userMapper;
    public User loadUserByUsername(String userId) {
        System.err.println("user repository starts");
        return userMapper.getUser(userId);
    }
}
