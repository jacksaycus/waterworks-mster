package org.kwater.pms.repository;

import org.kwater.pms.web.common.DateForm;
import org.kwater.pms.web.common.RequestForm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MotorMapper {

    List<Map<String, Object>> alarm();
    List<Map<String, Object>> runningInfo();
    List<Map<String, Object>> distribution(RequestForm requestForm);
    List<Map<String, Object>> vibrationFindById(RequestForm requestForm);
    List<Map<String, Object>> vibrationGraph(DateForm dateForm);
    List<Map<String, Object>> count();
    List<Map<String, Object>> vibrationValues();
    List<Map<String, Object>> alarmDetails();
    List<Map<String, Object>> scadaInfo();
    List<Map<String, Object>> flowPressure(String id);
    List<Map<String, Object>> motorDetails(RequestForm requestForm);
    List<Map<String, Object>> bearingTempInfo(RequestForm requestForm);
    List<Map<String, Object>> windingTempInfo(RequestForm requestForm);

}
