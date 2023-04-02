package org.kwater.pms.service;

import org.kwater.pms.repository.RapidAgitatorMapper;
import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RapidAgitatorService {

    private final RapidAgitatorMapper rapidAgitatorMapper;

    public List<Map<String, Object>> alarm() {
        return rapidAgitatorMapper.alarm();
    }

    public List<Map<String, Object>> rapidAgitatorInfo() {
        return rapidAgitatorMapper.rapidAgitatorInfo();
    }

    public List<Map<String, Object>> rapidAgitatorCount() {
        return rapidAgitatorMapper.rapidAgitatorCount();
    }

    public List<Map<String, Object>> currentInfo(DateForm dateForm) {
        return rapidAgitatorMapper.currentInfo(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return rapidAgitatorMapper.detailInfo(dateForm);
    }

}
