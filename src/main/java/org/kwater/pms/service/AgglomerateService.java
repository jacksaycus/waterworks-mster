package org.kwater.pms.service;

import org.kwater.pms.repository.AgglomerateMapper;
import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AgglomerateService {

    private final AgglomerateMapper agglomerateMapper;

    public List<Map<String, Object>> alram() {
        return agglomerateMapper.alram();
    }

    public List<Map<String, Object>> agglomerateInfo(String id) {
        return agglomerateMapper.agglomerateInfo(id);
    }

    public List<Map<String, Object>> agglomerateCount() {
        return agglomerateMapper.agglomerateCount();
    }

    public List<Map<String, Object>> currentInfo(DateForm dateForm) {
        return agglomerateMapper.currentInfo(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return agglomerateMapper.detailInfo(dateForm);
    }

}
