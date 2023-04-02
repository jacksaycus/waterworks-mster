package org.kwater.aio.dao;

import org.kwater.aio.dto.ClassInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClassInfoDAOImpl implements IClassInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(ClassInfoDTO dto)
    {
        String strQuery = "insert into class_info values (?, ?, ?, ?, ?, ?)";
        try
        {
            return jdbcTemplate.update(
                    strQuery,
                    dto.getClass_index(), dto.getDescription(), dto.getTb(), dto.getPh(), dto.getTe(), dto.getCu()
            );
        }
        catch(DuplicateKeyException e)
        {
            return 0;
        }
    }

    @Override
    public List<ClassInfoDTO> select()
    {
        String strQuery = "select * from class_info";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(ClassInfoDTO.class));
    }

    @Override
    public int update(ClassInfoDTO dto)
    {
        String strQuery = "update class_info set description=?, tb=?, ph=?, te=?, cu=? where class_index=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getDescription(), dto.getTb(), dto.getPh(), dto.getTe(), dto.getCu(), dto.getClass_index()
        );
    }

    @Override
    public int delete(int class_index)
    {
        String strQuery = "delete from class_info where class_index=?";
        return jdbcTemplate.update(strQuery, class_index);
    }
}
