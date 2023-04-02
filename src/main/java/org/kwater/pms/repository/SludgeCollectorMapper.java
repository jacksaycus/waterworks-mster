package org.kwater.pms.repository;

import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SludgeCollectorMapper {

    List<Map<String, Object>> alarm();

    List<Map<String, Object>> sludgeInfo();

    List<Map<String, Object>> count();

    List<Map<String, Object>> currentInfo(DateForm dateForm);

    List<Map<String, Object>> detailInfo(DateForm dateForm);

    List<Map<String, Object>> torqueInfo();
}
