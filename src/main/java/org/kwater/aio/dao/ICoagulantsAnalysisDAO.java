package org.kwater.aio.dao;

import org.kwater.aio.dto.CoagulantsAnalysisDTO;

import java.util.Date;
import java.util.List;

public interface ICoagulantsAnalysisDAO
{
    List<CoagulantsAnalysisDTO> select();
    CoagulantsAnalysisDTO select(Date log_time);
    CoagulantsAnalysisDTO selectLatest();
    List<CoagulantsAnalysisDTO> select2Latest();
    List<CoagulantsAnalysisDTO> selectMinute(Date start_time);
}
