package org.kwater.pms.repository;

import org.kwater.pms.web.common.RequestForm;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WaterControlValveMapper {

    List<HashMap<String, Object>> alarm();

    List<HashMap<String, Object>> eqOpen();

    List<HashMap<String, Object>> currentGraph(RequestForm requestForm);

    List<HashMap<String, Object>> statusInfo();

    List<HashMap<String, Object>> overCurrentGraph(RequestForm requestForm);

    List<HashMap<String, Object>> voltageFluctuationGraph(RequestForm requestForm);

    List<HashMap<String, Object>> currentAndOpen();

    List<HashMap<String, Object>> flow();
}
