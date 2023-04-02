package org.kwater.pms.service;

import org.kwater.pms.repository.GacBackWashMapper;
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
public class GacBackWashService {

    private final GacBackWashMapper gacBackWashMapper;

    public List<Map<String, Object>> alarm() {
        return gacBackWashMapper.alarm();
    }

    public List<Map<String, Object>> backwashInfo() {
        return gacBackWashMapper.backwashInfo();
    }

    public List<Map<String, Object>> distribution(RequestForm dateForm) {
        return gacBackWashMapper.distribution(dateForm);
    }

    public List<Map<String, Object>> backwashCount() {
        return gacBackWashMapper.backwashCount();
    }

    public List<Map<String, Object>> currentInfo(DateForm dateForm) {
        return gacBackWashMapper.currentInfo(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return gacBackWashMapper.detailInfo(dateForm);
    }

}
