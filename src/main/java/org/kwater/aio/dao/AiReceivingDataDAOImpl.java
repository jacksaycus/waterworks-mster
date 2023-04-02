package org.kwater.aio.dao;

import org.kwater.aio.dto.ProcessRealtimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class AiReceivingDataDAOImpl
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    public int insert(List<ProcessRealtimeDTO> list)
    {
        int[] result = jdbcTemplate.batchUpdate(
                "INSERT IGNORE INTO ai_receiving_data VALUES(?, ?, ?, ?);",
                new BatchPreparedStatementSetter()
                {
                    public void setValues(PreparedStatement ps, int i) throws SQLException
                    {
                        ps.setTimestamp(1, new java.sql.Timestamp(list.get(i).getUpdate_time().getTime()));
                        ps.setString(2, list.get(i).getName());
                        ps.setString(3, list.get(i).getValue());
                        ps.setInt(4, list.get(i).getQuality().intValue());
                    }

                    public int getBatchSize() { return list.size(); }
                }
        );
        return result.length;
    }

    public int delete(Date update_time)
    {
        String strQuery = "DELETE FROM ai_receiving_data WHERE update_time < ?";
        return jdbcTemplate.update(strQuery, update_time);
    }
}
