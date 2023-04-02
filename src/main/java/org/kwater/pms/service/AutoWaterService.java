package org.kwater.pms.service;

import org.kwater.pms.repository.AutoWaterMapper;
import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AutoWaterService {

    private final AutoWaterMapper autoWaterMapper;

    public List<Map<String, Object>> alarm() {
        return autoWaterMapper.alarm();
    }

    public List<Map<String, Object>> waterPumpInfo() {
        return autoWaterMapper.waterPumpInfo();
    }

    public List<Map<String, Object>> currentInfo(DateForm dateForm) {
        return autoWaterMapper.currentInfo(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return autoWaterMapper.detailInfo(dateForm);
    }
}
