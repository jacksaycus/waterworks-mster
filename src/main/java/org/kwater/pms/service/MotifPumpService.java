package org.kwater.pms.service;

import org.kwater.pms.repository.MotifPumpMapper;
import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MotifPumpService {

    private final MotifPumpMapper motifPumpMapper;

    public List<Map<String, Object>> alarm() {
        return motifPumpMapper.alarm();
    }

    public List<Map<String, Object>> motifPumpInfo() {
        return motifPumpMapper.motifPumpInfo();
    }

    public List<Map<String, Object>> count() {
        return motifPumpMapper.count();
    }

    public List<Map<String, Object>> currentInfo(DateForm dateForm) {
        return motifPumpMapper.currentInfo(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return motifPumpMapper.detailInfo(dateForm);
    }


}
