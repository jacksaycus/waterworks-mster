package org.kwater.pms.service;

import org.kwater.pms.repository.PacTubeMapper;
import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PacTubeService {

    private final PacTubeMapper pacTubeMapper;

    public List<Map<String, Object>> alarm() {
        return pacTubeMapper.alarm();
    }

    public List<Map<String, Object>> pacTubeInfo() {
        return pacTubeMapper.pacTubeInfo();
    }

    public List<Map<String, Object>> currentInfo(DateForm dateForm) {
        return pacTubeMapper.currentInfo(dateForm);
    }

    public List<Map<String, Object>> detailInfo(DateForm dateForm) {
        return pacTubeMapper.detailInfo(dateForm);
    }


}
