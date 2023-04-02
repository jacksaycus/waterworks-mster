package org.kwater.pms.repository;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MainMapper {

    List<Map<String, Object>> waterPacAll();

    List<Map<String, Object>> rapidAggloSludgeAll();

    List<Map<String, Object>> filterGacAll();

    List<Map<String, Object>> motifCoolAll();

    List<Map<String, Object>> pumpTransformerVcbAll();

    List<Map<String, Object>> waterAlarmAll();

    List<Map<String, Object>> rapidAggloSludgeAlarmAll();

    List<Map<String, Object>> filterAlarmAll();

    List<Map<String, Object>> ozoneAlarmAll();

    List<Map<String, Object>> pumpAlarmAll();

    List<Map<String, Object>> motorDataAll();

    List<Map<String, Object>> pumpBearingTempAll();

    List<Map<String, Object>> motorAlarm();

    List<Map<String, Object>> alarmDataAll();

    List<Map<String, Object>> operationAll();

}
