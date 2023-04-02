package org.kwater.pms.service;

import org.kwater.pms.repository.PahcsTubeMapper;
import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PahcsTubeService {

    private final PahcsTubeMapper pahcsTubeMapper;

    public List<Map<String, Object>> alarm() {
        return pahcsTubeMapper.alarm();
    }

    public List<Map<String, Object>> pahcsTubeInfo() {
        return pahcsTubeMapper.pahcsTubeInfo();
    }

    public List<Map<String, Object>> currentInfo(DateForm dateForm) {
        return pahcsTubeMapper.currentInfo(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return pahcsTubeMapper.detailInfo(dateForm);
    }

}
