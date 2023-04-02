package org.kwater.aio.dao;

import org.kwater.aio.dto.ProcessRealtimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class EmsRealtimeDAOImpl implements IProcessRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(List<ProcessRealtimeDTO> list)
    {
        int[] result = jdbcTemplate.batchUpdate(
                "INSERT INTO ems_realtime VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                        "update_time = VALUES(update_time), value = VALUES(value);",
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

    @Override
    public List<ProcessRealtimeDTO> select(String partitionName)
    {
        String strQuery = "SELECT * FROM ems_realtime ORDER BY name";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class));
    }

    @Override
    public List<ProcessRealtimeDTO> select(Date start_time)
    {
        // Do anything
        return null;
    }

    @Override
    public List<ProcessRealtimeDTO> select(String name, Date start_time, Date end_time)
    {
        // Do anything
        return null;
    }

    @Override
    public ProcessRealtimeDTO selectLatest(String name)
    {
        // Do anything
        return null;
    }

    @Override
    public void addPartition(String partitionName, String end_time)
    {
        // Do anything
    }

    @Override
    public void dropPartition(String partitionName)
    {
        // Do anything
    }
}
