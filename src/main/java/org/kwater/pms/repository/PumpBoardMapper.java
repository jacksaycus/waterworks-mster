package org.kwater.pms.repository;

import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PumpBoardMapper {

    List<Map<String, Object>> alarm();

    List<Map<String, Object>> count();

    List<Map<String, Object>> disCharge();

    List<Map<String, Object>> info();

    List<Map<String, Object>> detailInfo(DateForm dateForm);

}
