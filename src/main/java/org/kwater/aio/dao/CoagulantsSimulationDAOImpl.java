package org.kwater.aio.dao;

import org.kwater.aio.dto.CoagulantsSimulationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CoagulantsSimulationDAOImpl implements ICoagulantsSimulationDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(CoagulantsSimulationDTO dto)
    {
        String strQuery = "insert into coagulants_simulation " +
                "(reg_time, userid, state, tb, ph, te, cu) " +
                "values (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getReg_time(), dto.getUserid(), dto.getState(),
                dto.getTb(), dto.getPh(), dto.getTe(), dto.getCu()
        );
    }

    @Override
    public List<CoagulantsSimulationDTO> select()
    {
        String strQuery = "select cs.simulation_index, cs.reg_time, cs.complete_time, cs.userid, cs.state, cs.cluster_id, " +
                "ci1.shortname as chemical1, cs.injection1_percent, ci2.shortname as chemical2, cs.injection2_percent, " +
                "cs.tb, cs.ph, cs.te, cs.cu " +
                "from coagulants_simulation as cs " +
                "left outer join chemical_info as ci1 on cs.chemical1_code = ci1.code " +
                "left outer join chemical_info as ci2 on cs.chemical2_code = ci2.code " +
                "order by cs.simulation_index desc limit 1000";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(CoagulantsSimulationDTO.class));
    }

    @Override
    public List<CoagulantsSimulationDTO> select(boolean upper, int state)
    {
        String strQuery;

        if(upper == true)
        {
            strQuery = "select cs.simulation_index, cs.reg_time, cs.complete_time, cs.userid, cs.state, cs.cluster_id, " +
                    "ci1.shortname as chemical1, cs.injection1_percent, ci2.shortname as chemical2, cs.injection2_percent, " +
                    "cs.tb, cs.ph, cs.te, cs.cu " +
                    "from coagulants_simulation as cs " +
                    "left outer join chemical_info as ci1 on cs.chemical1_code = ci1.code " +
                    "left outer join chemical_info as ci2 on cs.chemical2_code = ci2.code " +
                    "where state > ? " +
                    "order by cs.simulation_index desc limit 1000";
        }
        else
        {
            strQuery = "select cs.simulation_index, cs.reg_time, cs.complete_time, cs.userid, cs.state, cs.cluster_id, " +
                    "ci1.shortname as chemical1, cs.injection1_percent, ci2.shortname as chemical2, cs.injection2_percent, " +
                    "cs.tb, cs.ph, cs.te, cs.cu " +
                    "from coagulants_simulation as cs " +
                    "left outer join chemical_info as ci1 on cs.chemical1_code = ci1.code " +
                    "left outer join chemical_info as ci2 on cs.chemical2_code = ci2.code " +
                    "where state < ? " +
                    "order by cs.simulation_index desc limit 1000";
        }

        return jdbcTemplate.query(strQuery, new Object[]{state}, new BeanPropertyRowMapper<>(CoagulantsSimulationDTO.class));
    }
}
