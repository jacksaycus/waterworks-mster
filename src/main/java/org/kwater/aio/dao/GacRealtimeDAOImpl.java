package org.kwater.aio.dao;

import org.kwater.aio.dto.ProcessRealtimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class GacRealtimeDAOImpl implements IProcessRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(List<ProcessRealtimeDTO> list)
    {
        int[] result = jdbcTemplate.batchUpdate(
                "INSERT IGNORE INTO gac_realtime VALUES(?, ?, ?, ?);",
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
        String strQuery = String.format("SELECT * FROM gac_realtime PARTITION(p_%s) WHERE update_time IN " +
                "(SELECT MAX(update_time) FROM gac_realtime PARTITION(p_%s) GROUP BY name)", partitionName, partitionName);

        try
        {
            return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class));
        }
        catch(DataAccessException e)
        {
            strQuery = String.format("SELECT * FROM gac_realtime PARTITION(p_max) WHERE update_time IN " +
                    "(SELECT MAX(update_time) FROM gac_realtime PARTITION(p_max) GROUP BY name)");

            return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class));
        }
    }

    @Override
    public List<ProcessRealtimeDTO> select(Date start_time)
    {
        String strQuery = "SELECT * FROM gac_realtime WHERE update_time > ? ORDER BY update_time DESC";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time},
                new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class)
        );
    }

    @Override
    public List<ProcessRealtimeDTO> select(String name, Date start_time, Date end_time)
    {
        String strQuery = "SELECT * FROM gac_realtime WHERE name=? AND update_time > ? AND update_time < ?";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{name, start_time, end_time},
                new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class)
        );
    }

    @Override
    public ProcessRealtimeDTO selectLatest(String name)
    {
        String strQuery =  "SELECT * FROM gac_realtime WHERE name=? ORDER BY update_time DESC limit 1";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery,
                    new Object[]{name},
                    new BeanPropertyRowMapper<>(ProcessRealtimeDTO.class)
            );
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public void addPartition(String partitionName, String end_time)
    {
        String strQuery = String.format("ALTER TABLE gac_realtime REORGANIZE PARTITION p_max INTO (" +
                "PARTITION p_%s VALUES LESS THAN('%s') ENGINE = INNODB, " +
                "PARTITION p_max VALUES LESS THAN MAXVALUE ENGINE = INNODB);", partitionName, end_time);
        jdbcTemplate.execute(strQuery);
    }

    @Override
    public void dropPartition(String partitionName)
    {
        String strQuery = String.format("ALTER TABLE gac_realtime DROP PARTITION p_%s", partitionName);
        jdbcTemplate.execute(strQuery);
    }
}
