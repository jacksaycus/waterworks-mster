package org.kwater.pms.service;

import org.kwater.pms.repository.MotorMapper;
import org.kwater.pms.web.common.DateForm;
import org.kwater.pms.web.common.RequestForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MotorService {

    private final MotorMapper motorMapper;

    public List<Map<String, Object>> alarm() {
        return motorMapper.alarm();
    }

    public List<Map<String, Object>> runningInfo() {
        return motorMapper.runningInfo();
    }

    public List<Map<String, Object>> distribution(RequestForm requestForm) {
        return motorMapper.distribution(requestForm);
    }

    public List<Map<String, Object>> count() {
        return motorMapper.count();
    }

    public List<Map<String, Object>> vibrationGraph(DateForm dateForm) {
        return motorMapper.vibrationGraph(dateForm);
    }

    public List<Map<String, Object>> vibrationValues() {
        return motorMapper.vibrationValues();
    }

    public List<Map<String, Object>> vibrationFindById(RequestForm requestForm) {
        return motorMapper.vibrationFindById(requestForm);
    }

    public List<Map<String, Object>> alarmDetails() {
        return motorMapper.alarmDetails();
    }

    public List<Map<String, Object>> scadaInfo() {
        return motorMapper.scadaInfo();
    }

    public List<Map<String, Object>> flowPressure(String id) {
        return motorMapper.flowPressure(id);
    }

    public List<Map<String, Object>> motorDetails(RequestForm requestForm) {
        return motorMapper.motorDetails(requestForm);
    }

    public List<Map<String, Object>> bearingTempInfo(RequestForm requestForm) {
        return motorMapper.bearingTempInfo(requestForm);
    }

    public List<Map<String, Object>> windingTempInfo(RequestForm requestForm) {
        return motorMapper.windingTempInfo(requestForm);
    }

}
