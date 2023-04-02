package org.kwater.aio.dao;

import org.kwater.aio.dto.DiatomDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DiatomDAOImpl implements IDiatomDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(DiatomDTO dto)
    {
        String strQuery = "insert into diatom values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try
        {
            return jdbcTemplate.update(
                    strQuery,
                    dto.getUpdate_time(), dto.getMeasure_time(),
                    dto.getPd1_diatom(), dto.getPd2_diatom(), dto.getPd3_diatom(),
                    dto.getPd1_diatom_big(), dto.getPd2_diatom_big(), dto.getPd3_diatom_big(),
                    dto.getPd_diatom_avg()
            );
        }
        catch(DuplicateKeyException e)
        {
            return 0;
        }
    }

    @Override
    public List<DiatomDTO> select()
    {
        String strQuery = "select * from diatom order by update_time desc limit 1000";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(DiatomDTO.class));
    }

    @Override
    public DiatomDTO selectLatest()
    {
        String strQuery = "select * from diatom order by update_time desc limit 1";

        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(DiatomDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int update(DiatomDTO dto)
    {
        String strQuery = "update diatom set measure_time=?, pd1_diatom=?, pd2_diatom=?, pd3_diatom=?, " +
                "pd1_diatom_big=?, pd2_diatom_big=?, pd3_diatom_big=?, pd_diatom_avg=? " +
                "where diatom_index=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getMeasure_time(), dto.getPd1_diatom(), dto.getPd2_diatom(), dto.getPd3_diatom(),
                dto.getPd1_diatom_big(), dto.getPd2_diatom_big(), dto.getPd3_diatom_big(), dto.getPd_diatom_avg(),
                dto.getDiatom_index()
        );
    }

    @Override
    public int delete(int diatom_index)
    {
        String strQuery = "delete from diatom where diatom_index=?";
        return jdbcTemplate.update(strQuery, diatom_index);
    }
}
