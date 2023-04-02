package org.kwater.aio.dao;

import org.kwater.aio.dto.AlarmInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlarmInfoDAOImpl implements IAlarmInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(AlarmInfoDTO dto)
    {
        String strQuery = "insert into alarm_info values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getAlarm_id(), dto.getCode_name(), dto.getDisplay_name(), dto.getUrl(), /*dto.getSeverity(),*/    // 알람 중요도 삭제
                dto.getType(), dto.getCompare(), dto.getValue(), dto.isScada_send(), dto.getTag()
        );
    }

    @Override
    public List<AlarmInfoDTO> select()
    {
// 알람 중요도 삭제
//        String strQuery = "select alarm_info_index, alarm_id, code_name, display_name, severity, " +
//                "type, compare, value, IF(scada_send, 'true', 'false') as scada_send " +
//                "from alarm_info order by alarm_id";
        String strQuery = "select alarm_info_index, alarm_id, code_name, display_name, url," +
                "type, compare, value, IF(scada_send, 'true', 'false') as scada_send, tag " +
                "from alarm_info order by alarm_id";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AlarmInfoDTO.class));
    }

    @Override
    public int update(AlarmInfoDTO dto)
    {
// 알람 중요도 삭제
        //String strQuery = "update alarm_info set display_name=?, severity=?, value=?, scada_send=? where alarm_info_index=?";
        String strQuery = "update alarm_info set display_name=?, url=?, value=?, scada_send=?, tag=? where alarm_info_index=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getDisplay_name(), dto.getUrl(), dto.getValue(), dto.isScada_send(), dto.getTag(), dto.getAlarm_info_index());
    }

    @Override
    public int delete(int alarm_id)
    {
        String strQuery = "delete from alarm_info where alarm_id=?";
        return jdbcTemplate.update(strQuery, alarm_id);
    }
}
