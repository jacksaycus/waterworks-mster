package org.kwater.pms.service;

import org.kwater.pms.repository.TransformersMapper;
import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TransformersService {

    private final TransformersMapper transformersMapper;

    public List<Map<String, Object>> alarm() {
        return transformersMapper.alarm();
    }

    public List<Map<String, Object>> info() {
        return transformersMapper.info();
    }

    public List<Map<String, Object>> count() {
        return transformersMapper.count();
    }

    public List<Map<String, Object>> disCharge(DateForm dateForm) {
        return transformersMapper.disCharge(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return transformersMapper.detailInfo(dateForm);
    }

}
