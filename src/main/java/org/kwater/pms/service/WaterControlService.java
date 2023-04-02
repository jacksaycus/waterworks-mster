package org.kwater.pms.service;

import org.kwater.pms.repository.WaterControlValveMapper;
import org.kwater.pms.web.common.RequestForm;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WaterControlService {

    private final WaterControlValveMapper waterControlValveMapper;

    public List<HashMap<String, Object>> alarm() {
        return waterControlValveMapper.alarm();
    }

    public List<HashMap<String, Object>> eqOpen() {
        return waterControlValveMapper.eqOpen();
    }

    public List<HashMap<String, Object>> currentGraph(RequestForm requestForm) {
        return waterControlValveMapper.currentGraph(requestForm);
    }

    public List<HashMap<String, Object>> statusInfo() {
        return waterControlValveMapper.statusInfo();
    }

    public List<HashMap<String, Object>> overCurrentGraph(RequestForm requestForm) {
        return waterControlValveMapper.overCurrentGraph(
            requestForm);
    }

    public List<HashMap<String, Object>> voltageFluctuationGraph(RequestForm requestForm) {
        return waterControlValveMapper.voltageFluctuationGraph(
            requestForm);
    }

    public List<HashMap<String ,Object>> currentAndOpen() {
        return waterControlValveMapper.currentAndOpen();
    }

    public List<HashMap<String, Object>> flow() {
        return waterControlValveMapper.flow();
    }

}
