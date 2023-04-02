package org.kwater.pms.service;

import org.kwater.pms.repository.MainMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {
    private final MainMapper mainMapper;

    public List<Map<String, Object>> waterPacAll() {
        return mainMapper.waterPacAll();
    }

    public List<Map<String, Object>> rapidAggloSludgeAll() {
        return mainMapper.rapidAggloSludgeAll();
    }

    public List<Map<String, Object>> filterGacAll() {
        return mainMapper.filterGacAll();
    }

    public List<Map<String, Object>> motifCoolAll() {
        return mainMapper.motifCoolAll();
    }

    public List<Map<String, Object>> pumpTransformerVcbAll() {
        return mainMapper.pumpTransformerVcbAll();
    }

    public List<Map<String, Object>> waterAlarmAll() {
        return mainMapper.waterAlarmAll();
    }

    public List<Map<String, Object>> rapidAggloSludgeAlarmAll() {
        return mainMapper.rapidAggloSludgeAlarmAll();
    }

    public List<Map<String, Object>> filterAlarmAll() {
        return mainMapper.filterAlarmAll();
    }

    public List<Map<String, Object>> ozoneAlarmAll() {
        return mainMapper.ozoneAlarmAll();
    }

    public List<Map<String, Object>> pumpAlarmAll() {
        return mainMapper.pumpAlarmAll();
    }

    public List<Map<String, Object>> motorDataAll() {
        return mainMapper.motorDataAll();
    }

    public List<Map<String, Object>> pumpBearingTempAll() {
        return mainMapper.pumpBearingTempAll();
    }

    public List<Map<String, Object>> motorAlarm() {
        return mainMapper.motorAlarm();
    }

    public List<Map<String, Object>> alarmDataAll() {
        return mainMapper.alarmDataAll();
    }

    public List<Map<String, Object>> operationAll() {
        return mainMapper.operationAll();
    }

}
