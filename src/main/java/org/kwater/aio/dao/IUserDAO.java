package org.kwater.aio.dao;

import org.kwater.aio.dto.UserDTO;

import java.util.List;

public interface IUserDAO
{
    int insert(UserDTO dto);
    UserDTO selectUser(String userid, String password);
    UserDTO selectUserFromUserid(String userid);
    List<UserDTO> selectAll();
    int update(int authority, UserDTO dto);
    int updatePw(String userid, String password);
    int delete(String userid);
}
