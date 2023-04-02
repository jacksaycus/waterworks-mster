package org.kwater.pms.repository;

import org.kwater.pms.web.common.DateForm;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AgglomerateMapper {
    List<Map<String, Object>> alram();
    List<Map<String, Object>> agglomerateInfo(String id);
    List<Map<String, Object>> agglomerateCount();
    List<Map<String, Object>> currentInfo(DateForm dateForm);
    List<Map<String, Object>> detailInfo(DateForm dateForm);
}
