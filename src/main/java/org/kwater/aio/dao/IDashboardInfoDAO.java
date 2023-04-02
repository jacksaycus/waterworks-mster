package org.kwater.aio.dao;

import org.kwater.aio.dto.DashboardDataDTO;
import org.kwater.aio.dto.DashboardIdDTO;

public interface IDashboardInfoDAO
{
    int insert(String data);
    DashboardIdDTO selectLatest();
    DashboardDataDTO select(int dashboard_id);
    int update(int dashboard_id, String data);
    int delete(int dashboard_id);
}
