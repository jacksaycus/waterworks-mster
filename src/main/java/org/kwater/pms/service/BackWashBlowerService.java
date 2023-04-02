package org.kwater.pms.service;

import org.kwater.pms.repository.BackWashBlowerMapper;
import org.kwater.pms.web.common.DateForm;
import org.kwater.pms.web.common.RequestForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BackWashBlowerService {

    private final BackWashBlowerMapper backWashBlowerMapper;

    public List<Map<String, Object>> alarm() {
        return backWashBlowerMapper.alarm();
    }

    public List<Map<String, Object>> blowerInfo() {
        return backWashBlowerMapper.blowerInfo();
    }

    public List<Map<String, Object>> distribution(RequestForm requestForm) {
        return backWashBlowerMapper.distribution(requestForm);
    }

    public List<Map<String, Object>> count() {
        return backWashBlowerMapper.count();
    }

    public List<Map<String, Object>> currentInfo(DateForm dateForm) {
        return backWashBlowerMapper.currentInfo(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return backWashBlowerMapper.detailInfo(dateForm);
    }

}
