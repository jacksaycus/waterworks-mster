package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiCoagulantSimulationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiCoagulantSimulationDAOImpl implements IAiCoagulantSimulationDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(AiCoagulantSimulationDTO dto)
    {
        String strQuery = "INSERT INTO ai_coagulant_simulation (reg_time, state, b_tb, b_ph, b_te, b_cu, b_in_fr, e1_tb, e2_tb) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getReg_time(), dto.getState(), dto.getB_tb(), dto.getB_ph(),
                dto.getB_te(), dto.getB_cu(), dto.getB_in_fr(), dto.getE1_tb(), dto.getE2_tb()
        );
    }

    @Override
    public List<AiCoagulantSimulationDTO> select(Date start_time, Date end_time)
    {
        String strQuery = "SELECT * FROM ai_coagulant_simulation " +
                "WHERE reg_time > ? AND reg_time <= ? ORDER BY simulation_index DESC";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiCoagulantSimulationDTO.class)
        );
    }
}
