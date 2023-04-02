package org.kwater.aio.dao;

import org.kwater.aio.dto.PmsAiDTO;
import org.kwater.aio.dto.PmsScadaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PmsRealtimeDAOImpl implements IPmsRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<PmsAiDTO> selectAi()
    {
        String strQuery = "SELECT " +
                "m.moter_id, m.acq_date, m.DE_rms_amp as motor_de_amp, m.NDE_rms_amp as motor_nde_amp, p.DE_rms_amp as pump_de_amp, p.NDE_rms_amp as pump_nde_amp " +
                "FROM " +
                "tb_ai_diag_moter as m " +
                "LEFT JOIN " +
                "tb_ai_diag_pump as p on m.moter_id = p.pump_id and m.acq_date = p.acq_date " +
                "WHERE " +
                "m.acq_date = (SELECT acq_date FROM tb_ai_diag_moter WHERE acq_date >= DATE_SUB(NOW(), INTERVAL 15 DAY) " +
                "GROUP BY " +
                "acq_date ORDER BY acq_date DESC LIMIT 0, 1) " +
                "ORDER BY m.acq_date ASC;";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(PmsAiDTO.class));
    }

    @Override
    public List<PmsScadaDTO> selectScada()
    {
        String strQuery = "SELECT pump_scada_id, brg_motor_de_temp, brg_motor_nde_temp, brg_pump_de_temp, brg_pump_nde_temp, acq_date " +
                "FROM tb_pump_scada " +
                "WHERE acq_date = (SELECT acq_date FROM tb_pump_scada WHERE acq_date >= DATE_SUB(NOW(), INTERVAL 3 MINUTE) " +
                "GROUP BY acq_date ORDER BY acq_date DESC LIMIT 0, 1);";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(PmsScadaDTO.class));
    }
}
