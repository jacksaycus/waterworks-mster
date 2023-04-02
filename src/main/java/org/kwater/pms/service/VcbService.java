package org.kwater.pms.service;

import org.kwater.pms.repository.VcbMapper;
import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VcbService {
    private final VcbMapper vcbMapper;

    public List<Map<String, Object>> alarm() {
        return vcbMapper.alarm();
    }

    public List<Map<String, Object>> info() {
        return vcbMapper.info();
    }

    public List<Map<String, Object>> count() {
        return vcbMapper.count();
    }

    public List<Map<String, Object>> partDischarge(DateForm dateForm) {
        return vcbMapper.partDischarge(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return vcbMapper.detailInfo(dateForm);
    }


}
