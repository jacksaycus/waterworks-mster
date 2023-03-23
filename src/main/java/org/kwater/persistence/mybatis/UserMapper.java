package org.kwater.persistence.mybatis;

import org.kwater.domain.User;

public interface UserMapper {
    User getUser(String userId);
}
