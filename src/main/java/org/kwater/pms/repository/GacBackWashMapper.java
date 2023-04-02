package org.kwater.pms.repository;

import org.kwater.pms.web.common.DateForm;
import org.kwater.pms.web.common.RequestForm;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GacBackWashMapper {

    List<Map<String, Object>> alarm();

    List<Map<String, Object>> backwashInfo();

    List<Map<String, Object>> distribution(RequestForm dateForm);

    List<Map<String, Object>> backwashCount();

    List<Map<String, Object>> currentInfo(DateForm dateForm);

    List<Map<String, Object>> detailInfo(DateForm dateForm);

}
