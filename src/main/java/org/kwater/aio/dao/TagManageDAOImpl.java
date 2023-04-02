package org.kwater.aio.dao;

import org.kwater.aio.dto.TagManageDTO;
import org.kwater.aio.dto.TagManageRangeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagManageDAOImpl implements ITagManageDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(TagManageDTO dto)
    {
        String strQuery = "insert into tag_manage values (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getAlgorithm_code(), dto.getProcess_code(), dto.getSeries(), dto.getLocation(),
                dto.getItem(), dto.getName(), dto.getDisplay(), dto.getType()
        );
    }

    @Override
    public List<TagManageDTO> select()
    {
        String strQuery = "select * from tag_manage";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(TagManageDTO.class));
    }

    @Override
    public List<TagManageDTO> select(int type)
    {
        String strQuery = "select * from tag_manage where type=?";
        return jdbcTemplate.query(strQuery, new Object[]{type}, new BeanPropertyRowMapper<>(TagManageDTO.class));
    }

    @Override
    public List<TagManageDTO> select(String process)
    {
        String strQuery = "select * from tag_manage where algorithm_code=?";
        return jdbcTemplate.query(strQuery, new Object[]{process}, new BeanPropertyRowMapper<>(TagManageDTO.class));
    }

    @Override
    public TagManageRangeDTO selectRange(String process)
    {
        String strQuery = "SELECT MIN(NULLIF(location, 0)) AS min, MAX(location) AS max FROM tag_manage WHERE algorithm_code=? AND process_code=? AND series != 0";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery,
                    new Object[]{process, process},
                    new BeanPropertyRowMapper<>(TagManageRangeDTO.class)
            );
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int update(TagManageDTO dto)
    {
        String strQuery = "update tag_manage set display=?, type=? " +
                "where algorithm_code=? and process_code=? and series=? and location=? and item=? and name=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getDisplay(), dto.getType(),
                dto.getAlgorithm_code(), dto.getProcess_code(), dto.getSeries(), dto.getLocation(),
                dto.getItem(), dto.getName()
        );
    }

    @Override
    public int delete(TagManageDTO dto)
    {
        String strQuery = "delete from tag_manage " +
                "where algorithm_code=? and process_code=? and series=? and location=? and item=? and name=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getAlgorithm_code(), dto.getProcess_code(), dto.getSeries(), dto.getLocation(),
                dto.getItem(), dto.getName()
        );
    }
}
