package org.kwater.pms.service;

import org.kwater.pms.repository.FilterBackWashMapper;
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
public class FilterBackWashService {

    private final FilterBackWashMapper filterBackWashMapper;

    public List<Map<String, Object>> alarm() {
        return filterBackWashMapper.alarm();
    }

    public List<Map<String, Object>> filterBackWashInfo() {
        return filterBackWashMapper.filterBackWashInfo();
    }

    public List<Map<String, Object>> distribution(RequestForm requestForm) {
        return filterBackWashMapper.distribution(requestForm);
    }

    public List<Map<String, Object>> count() {
        return filterBackWashMapper.count();
    }

    public List<Map<String, Object>> currentInfo(DateForm dateForm) {
        return filterBackWashMapper.currentInfo(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return filterBackWashMapper.detailInfo(dateForm);
    }

}
