package org.kwater.pms.service;

import org.kwater.pms.repository.CoolingPumpMapper;
import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoolingPumpService {

    private final CoolingPumpMapper coolingPumpMapper;

    public List<Map<String, Object>> alarm() {
        return coolingPumpMapper.alarm();
    }

    public List<Map<String, Object>> coolingPumpInfo() {
        return coolingPumpMapper.coolingPumpInfo();
    }

    public List<Map<String, Object>> coolingCount() {
        return coolingPumpMapper.coolingCount();
    }

    public List<Map<String, Object>> currentInfo(DateForm dateForm) {
        return coolingPumpMapper.currentInfo(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return coolingPumpMapper.detailInfo(dateForm);
    }

}
