package org.kwater.pms.service;

import org.kwater.pms.repository.PumpBoardMapper;
import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PumpBoardService {

    private final PumpBoardMapper pumpBoardMapper;

    public List<Map<String, Object>> alarm() {
        return pumpBoardMapper.alarm();
    }

    public List<Map<String, Object>> count() {
        return pumpBoardMapper.count();
    }

    public List<Map<String, Object>> disCharge() {
        return pumpBoardMapper.disCharge();
    }

    public List<Map<String, Object>> info() {
        return pumpBoardMapper.info();
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return pumpBoardMapper.detailInfo(dateForm);
    }


}
