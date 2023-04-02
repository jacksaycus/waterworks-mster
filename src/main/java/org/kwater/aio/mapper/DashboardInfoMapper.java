package org.kwater.aio.mapper;

import org.apache.ibatis.annotations.*;
import org.kwater.aio.dto.DashboardDataDTO;
import org.kwater.aio.dto.DashboardIdDTO;

@Mapper
public interface DashboardInfoMapper {
    @Insert("insert into dashboard_info values (null, #{data})")
    public int insert(@Param("data") String data);

    @Select("select dashboard_id from dashboard_info order by dashboard_id desc limit 1")
    DashboardIdDTO selectLatest();

    @Select("select data from dashboard_info where dashboard_id = #{dashboardId}")
    DashboardDataDTO select(@Param("dashboardId") int dashboardId);

    @Update("update dashboard_info set data = #{dashboard_id} where dashboard_id = #{data}")
    int update(@Param("dashboard_id") int dashboard_id, @Param("data") String data);

    @Delete("delete from dashboard_info where dashboard_id = #{dashboard_id}")
    int delete(@Param("dashboard_id") int dashboard_id);
}
