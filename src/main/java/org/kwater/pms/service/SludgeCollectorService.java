package org.kwater.pms.service;

import org.kwater.pms.repository.SludgeCollectorMapper;
import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SludgeCollectorService {

    private final SludgeCollectorMapper sludgeCollectorMapper;

    public List<Map<String, Object>> alarm() {
        return sludgeCollectorMapper.alarm();
    }

    public List<Map<String, Object>> sludgeInfo() {
        return sludgeCollectorMapper.sludgeInfo();
    }

    public List<Map<String, Object>> count() {
        return sludgeCollectorMapper.count();
    }

    public List<Map<String, Object>> currentInfo(DateForm dateForm) {
        return sludgeCollectorMapper.currentInfo(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return sludgeCollectorMapper.detailInfo(dateForm);
    }

    public List<Map<String, Object>> torqueInfo() {
        return sludgeCollectorMapper.torqueInfo();
    }

}
