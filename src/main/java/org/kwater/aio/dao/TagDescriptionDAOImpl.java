package org.kwater.aio.dao;

import org.kwater.aio.dto.TagDescriptionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagDescriptionDAOImpl implements ITagDescriptionDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(TagDescriptionDTO dto)
    {
        String strQuery = "insert into tag_description values (null, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getName(), dto.getDescription(), dto.getCreated()
        );
    }

    @Override
    public List<TagDescriptionDTO> select()
    {
        String strQuery = "select * from tag_description";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(TagDescriptionDTO.class));
    }

    @Override
    public int update(TagDescriptionDTO dto)
    {
        String strQuery = "update tag_description set description=?, created=? where tag_index=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getDescription(), dto.getCreated(), dto.getTag_index()
        );
    }

    @Override
    public int delete(int tagIndex)
    {
        String strQuery = "delete from tag_description where tag_index=?";
        return jdbcTemplate.update(strQuery, tagIndex);
    }
}
