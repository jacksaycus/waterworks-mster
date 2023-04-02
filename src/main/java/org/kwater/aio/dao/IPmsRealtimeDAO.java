package org.kwater.aio.dao;

import org.kwater.aio.dto.PmsAiDTO;
import org.kwater.aio.dto.PmsScadaDTO;

import java.util.List;

public interface IPmsRealtimeDAO
{
    List<PmsScadaDTO> selectScada();
    List<PmsAiDTO> selectAi();
}
