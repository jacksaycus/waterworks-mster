package org.kwater.aio.service;

import org.kwater.aio.ai_dto.*;
import org.kwater.aio.dto.*;
import org.kwater.aio.resource_dto.CoagulantsDTO;

import java.util.Date;
import java.util.List;

public interface IDatabaseService
{
    ////////////////////////////////////////////
    // access_token
    ////////////////////////////////////////////
    int addToken(AccessTokenDTO dto);
    List<AccessTokenDTO> getAllTokens();
    AccessTokenDTO getToken(String token);
    int modToken(String token, Date expiration);
    int delToken(String token);
    int delToken(Date expiration);

    ////////////////////////////////////////////
    // user
    ////////////////////////////////////////////
    int addUser(UserDTO dto);
    UserDTO getUser(String userid, String password);
    UserDTO getUserFromUserid(String userid);
    List<UserDTO> getAllUser();
    int modUser(int authority, UserDTO dto);
    int modUserPw(String userid, String password);
    int delUser(String userid);

    ////////////////////////////////////////////
    // login_history
    ////////////////////////////////////////////
    int addLoginHistory(LoginHistoryDTO dto);
    List<LoginHistoryDTO> getAllLoginHistory();
    int delLoginHistory(Date date);

    ////////////////////////////////////////////
    // disk_info
    ////////////////////////////////////////////
    int addDiskInfo(DiskInfoDTO dto);
    List<DiskInfoDTO> getDiskInfoFromHostname(String hostname);
    int modDiskInfo(DiskInfoDTO dto);

    ////////////////////////////////////////////
    // partition_info
    ////////////////////////////////////////////
    int addPartitionInfo(PartitionInfoDTO dto);
    List<PartitionInfoDTO> getPartitionInfoFromHostname(String hostname);
    int modPartitionInfo(PartitionInfoDTO dto);

    ////////////////////////////////////////////
    // interface_info
    ////////////////////////////////////////////
    int addInterfaceInfo(InterfaceInfoDTO dto);
    List<InterfaceInfoDTO> getInterfaceInfoFromHostname(String hostname);
    List<InterfaceInfoDTO> getInterfaceInfoFromAddress(String address);
    int modInterfaceInfo(InterfaceInfoDTO dto);

    ////////////////////////////////////////////
    // system_config
    ////////////////////////////////////////////
    SystemConfigDTO getSystemConfig();
    int modSystemConfig(SystemConfigDTO dto);

    ////////////////////////////////////////////
    // system_info
    ////////////////////////////////////////////
    int addSystemInfo(SystemInfoDTO dto);
    SystemInfoDTO getSystemInfoFromHostname(String hostname);
    List<SystemInfoDTO> getAllSystemInfo();
    int modSystemInfo(SystemInfoDTO dto);
    int modSystemInfoName(String hostname, String name);

    ////////////////////////////////////////////
    // system_monitoring
    ////////////////////////////////////////////
    int addSystemMonitoring(SystemMonitoringDTO dto);
    List<SystemMonitoringDTO> getSystemMonitoringFromHostname(String hostname);
    List<SystemMonitoringDTO> getAllSystemMonitoring();
    List<SystemMonitoringDTO> getLatestSystemMonitoring(Date startDate);
    int delSystemMonitoring(Date date);

    ////////////////////////////////////////////
    // sensor
    ////////////////////////////////////////////
    int addSensor(SensorDTO dto);
    List<SensorDTO> getSensor(Date startDate);
    SensorDTO getLatestSensor();
    List<CoagulantsDTO> getHistorySensor();
    CoagulantsDTO getLatestCoagulants();
    List<TrendTbDTO> getTrendTb(InterfaceDateSearchDTO dto);
    List<TrendCodeDTO> getTrendCode(String code, InterfaceDateSearchDTO dto);
    List<FrequencyDTO> getDistinctCountHsE1Tb(Date startDate);
    List<FrequencyDTO> getDistinctCountHsE2Tb(Date startDate);
    List<FrequencyDTO> getDistinctCountHsFTb(Date startDate);
    int delSensor(Date updateTime);

    ////////////////////////////////////////////
    // diatom
    ////////////////////////////////////////////
    int addDiatom(DiatomDTO dto);
    List<DiatomDTO> getDiatom();
    DiatomDTO getLatestDiatom();
    int modDiatom(DiatomDTO dto);
    int delDiatom(int diatomIndex);

    ////////////////////////////////////////////
    // alarm_info
    ////////////////////////////////////////////
    int addAlarmInfo(AlarmInfoDTO dto);
    List<AlarmInfoDTO> getAlarmInfo();
    int modAlarmInfo(AlarmInfoDTO dto);
    int delAlarmInfo(int alarmId);

    ////////////////////////////////////////////
    // alarm_notify
    ////////////////////////////////////////////
    int addAlarmNotify(AlarmNotifyDTO dto);
    List<AlarmNotifyDTO> getAllAlarmNotify();
    List<AlarmNotifyDTO> getAlarmNotify(Date startTime);
//    List<AlarmNotifyDTO> getAlarmNotifyFromAckState(boolean ackState);    // 알람 네비게이터 삭제
    AlarmNotifyDTO getLatestAlarmNotify(int alarmId, Date alarmDate, String hostname);
    List<AlarmNotifyDTO> getSearchAlarmNotify(InterfaceDateSearchDTO dto);
    int modAlarmNotifyAckState(int alarmNotifyIndex, boolean ackState);

    ////////////////////////////////////////////
    // chemical_info
    ////////////////////////////////////////////
    int addChemicalInfo(ChemicalInfoDTO dto);
    List<ChemicalInfoDTO> getAllChemicalInfo();
    ChemicalInfoDTO getChemicalInfoFromCode(String code);
    int modChemicalInfo(ChemicalInfoDTO dto);
    int delChemicalInfo(String code);

    ////////////////////////////////////////////
    // water_purification_info
    ////////////////////////////////////////////
    int addWaterPurificationInfo(WaterPurificationInfoDTO dto);
    List<WaterPurificationInfoDTO> getAllWaterPurificationInfo();
    WaterPurificationInfoDTO getWaterPurificationInfoFromCode(String code);
    int modWaterPurificationInfo(WaterPurificationInfoDTO dto);
    int delWaterPurificationInfo(String code);

    ////////////////////////////////////////////
    // coagulants_analysis
    ////////////////////////////////////////////
    List<CoagulantsAnalysisDTO> getAllCoagulantsAnalysis();
    CoagulantsAnalysisDTO getCoagulantsAnalysisFromLogTime(Date logTime);
    CoagulantsAnalysisDTO getLatestCoagulantsAnalysis();
    List<CoagulantsAnalysisDTO> get2LatestCoagulantsAnalysis();
    List<CoagulantsAnalysisDTO> getMinuteCoagulantsAnalysis(Date startTime);
    int addRawWaterClassInfo(ClassInfoDTO dto);
    List<ClassInfoDTO> getAllRawWaterClassInfo();
    int modRawWaterClassInfo(ClassInfoDTO dto);
    int delRawWaterClassInfo(int classIndex);
    List<ClusterInfoDTO> getAllCoagulantsClusterInfo();

    ////////////////////////////////////////////
    // coagulants_simulation
    ////////////////////////////////////////////
    int addCoagulantsSimulation(CoagulantsSimulationDTO dto);
    List<CoagulantsSimulationDTO> getAllCoagulantsSimulation();
    List<CoagulantsSimulationDTO> getCoagulantsSimulationUpperState(int state);
    List<CoagulantsSimulationDTO> getCoagulantsSimulationLowerState(int state);

    ////////////////////////////////////////////
    // dashboard_info
    ////////////////////////////////////////////
    int addDashboardInfo(String data);
    DashboardIdDTO getLatestDashboardInfo();
    DashboardDataDTO getDashboardInfo(int dashboard_id);
    int modDashboardInfo(int dashboard_id, String data);
    int delDashboardInfo(int dashboard_id);

    ////////////////////////////////////////////
    // tag_description
    ////////////////////////////////////////////
    int addTagDescription(TagDescriptionDTO dto);
    List<TagDescriptionDTO> getAllTagDescription();
    int modTagDescription(TagDescriptionDTO dto);
    int delTagDescription(int tagIndex);

    ////////////////////////////////////////////
    // tag_manage
    ////////////////////////////////////////////
    int addTagManage(TagManageDTO dto);
    List<TagManageDTO> getAllTagManage();
    List<TagManageDTO> getTagManageFromType(int type);
    List<TagManageDTO> getTagManageFromCode(String process);
    TagManageRangeDTO getTagManageRange(String process);
    int modTagManage(TagManageDTO dto);
    int delTagManage(TagManageDTO dto);

    ////////////////////////////////////////////
    // process_code
    ////////////////////////////////////////////
    List<ProcessCodeDTO> getAllProcessCode();

    ////////////////////////////////////////////
    // receiving_realtime
    ////////////////////////////////////////////
    int addReceivingRealtimeValue(List<ProcessRealtimeDTO> dtos);
    List<ProcessRealtimeDTO> getLatestReceivingRealtimeValue(String partitionName);
    List<ProcessRealtimeDTO> getAllReceivingRealtimeValueFromTime(Date startTime);
    List<ProcessRealtimeDTO> getReceivingRealtimeValueFromTag(String name, Date startTime, Date endTime);
    ProcessRealtimeDTO getLatestReceivingRealtimeValueFromTag(String name);
    void addReceivingRealtimePartition(String partitionName, String endTime);
    void delReceivingRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_receiving_realtime
    ////////////////////////////////////////////
    List<AiReceivingRealtimeDTO> getAiReceivingRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto);
    AiReceivingRealtimeDTO getLatestAiReceivingRealtimeValue();
    int delAiReceivingRealtimeValue(Date updateTime);
    List<AiClearOperationBandDTO> getAiClearOperationBandFromTimeIndex(InterfaceDateSearchDTO dto);
    List<AiClearOperationBandDTO> getAiClearEmsOperationBandFromTimeIndex(InterfaceDateSearchDTO dto);
    List<AiClearOperationBandDTO> getAiClearWideOperationBandFromTimeIndex(InterfaceDateSearchDTO dto);

    ////////////////////////////////////////////
    // ai_receiving_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiReceivingControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiReceivingControl(AiProcessControlDTO dto);
    int modAiReceivingControlKafkaFlag(AiProcessControlDTO dto);
    int delAiReceivingControl(Date date);

    ////////////////////////////////////////////
    // ai_receiving_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiReceivingInit();
    AiProcessInitDTO getAiReceivingInit(String item);
    int modAiReceivingOperationMode(int operationMode);
    int modAiReceivingInit(String item, float value);

    ////////////////////////////////////////////
    // ai_receiving_data
    ////////////////////////////////////////////
    int addAiReceivingDataValue(List<ProcessRealtimeDTO> dtos);
    int delAiReceivingDataValue(Date updateTime);

    ////////////////////////////////////////////
    // ai_receiving_alarm
    ////////////////////////////////////////////
    int addAiReceivingAlarm(AiProcessAlarmDTO dto);
    List<AiProcessAlarmDTO> getAllAiReceivingAlarm(Date alarm_time, int kafka_flag);
    int modAiReceivingAlarmKafkaFlag(AiProcessAlarmDTO dto);
    int delAiReceivingAlarm(Date date);

    ////////////////////////////////////////////
    // coagulant_realtime
    ////////////////////////////////////////////
    int addCoagulantRealtimeValue(List<ProcessRealtimeDTO> dtos);
    List<ProcessRealtimeDTO> getLatestCoagulantRealtimeValue(String partitionName);
    List<ProcessRealtimeDTO> getAllCoagulantRealtimeValueFromTime(Date startTime);
    List<ProcessRealtimeDTO> getCoagulantRealtimeValueFromTag(String name, Date startTime, Date endTime);
    List<FrequencyDTO> getCoagulantDistribution(Date startTime, String name);
    void addCoagulantRealtimePartition(String partitionName, String endTime);
    void delCoagulantRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_coagulant_realtime
    ////////////////////////////////////////////
    List<AiCoagulantRealtimeDTO> getAiCoagulantRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto);
    AiCoagulantRealtimeDTO getLatestAiCoagulantRealtimeValue();
    int delAiCoagulantRealtimeValue(Date updateTime);

    ////////////////////////////////////////////
    // ai_coagulant_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiCoagulantControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiCoagulantControl(AiProcessControlDTO dto);
    int modAiCoagulantControlKafkaFlag(AiProcessControlDTO dto);
    int delAiCoagulantControl(Date date);

    ////////////////////////////////////////////
    // ai_coagulant_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiCoagulantInit();
    AiProcessInitDTO getAiCoagulantInit(String item);
    int modAiCoagulantOperationMode(int operationMode);
    int modAiCoagulantInit(String item, float value);

    ////////////////////////////////////////////
    // ai_coagulant_alarm
    ////////////////////////////////////////////
    int addAiCoagulantAlarm(AiProcessAlarmDTO dto);
    List<AiProcessAlarmDTO> getAllAiCoagulantAlarm(Date alarm_time, int kafka_flag);
    int modAiCoagulantAlarmKafkaFlag(AiProcessAlarmDTO dto);
    int delAiCoagulantAlarm(Date date);

    ////////////////////////////////////////////
    // ai_coagulant_simulation
    ////////////////////////////////////////////
    int addAiCoagulantSimulation(AiCoagulantSimulationDTO dto);
    List<AiCoagulantSimulationDTO> getAiCoagulantSimulation(InterfaceDateSearchDTO dto);

    ////////////////////////////////////////////
    // mixing_realtime
    ////////////////////////////////////////////
    int addMixingRealtimeValue(List<ProcessRealtimeDTO> dtos);
    List<ProcessRealtimeDTO> getLatestMixingRealtimeValue(String partitionName);
    List<ProcessRealtimeDTO> getAllMixingRealtimeValueFromTime(Date startTime);
    List<ProcessRealtimeDTO> getMixingRealtimeValueFromTag(String name, Date startTime, Date endTime);
    ProcessRealtimeDTO getLatestMixingRealtimeValueFromTag(String name);
    void addMixingRealtimePartition(String partitionName, String endTime);
    void delMixingRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_mixing_realtime
    ////////////////////////////////////////////
    List<AiMixingRealtimeDTO> getAiMixingRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto);
    AiMixingRealtimeDTO getLatestAiMixingRealtimeValue();
    int delAiMixingRealtimeValue(Date updateTime);

    ////////////////////////////////////////////
    // ai_mixing_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiMixingControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiMixingControl(AiProcessControlDTO dto);
    int modAiMixingControlKafkaFlag(AiProcessControlDTO dto);
    int delAiMixingControl(Date date);

    ////////////////////////////////////////////
    // ai_mixing_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiMixingInit();
    AiProcessInitDTO getAiMixingInit(String item);
    int modAiMixingOperationMode(int operationMode);
    int modAiMixingInit(String item, float value);

    ////////////////////////////////////////////
    // ai_mixing_alarm
    ////////////////////////////////////////////
    int addAiMixingAlarm(AiProcessAlarmDTO dto);
    List<AiProcessAlarmDTO> getAllAiMixingAlarm(Date alarm_time, int kafka_flag);
    int modAiMixingAlarmKafkaFlag(AiProcessAlarmDTO dto);
    int delAiMixingAlarm(Date date);

    ////////////////////////////////////////////
    // sedimentation_realtime
    ////////////////////////////////////////////
    int addSedimentationRealtimeValue(List<ProcessRealtimeDTO> dtos);
    List<ProcessRealtimeDTO> getLatestSedimentationRealtimeValue(String partitionName);
    List<ProcessRealtimeDTO> getAllSedimentationRealtimeValueFromTime(Date startTime);
    List<ProcessRealtimeDTO> getSedimentationRealtimeValueFromTag(String name, Date startTime, Date endTime);
    ProcessRealtimeDTO getLatestSedimentationRealtimeValueFromTag(String name);
    void addSedimentationRealtimePartition(String partitionName, String endTime);
    void delSedimentationRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_sedimentation_realtime
    ////////////////////////////////////////////
    List<AiSedimentationRealtimeDTO> getAiSedimentationRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto);
    List<FrequencyDTO> getDistributionE1Tb(Date startTime);
    List<FrequencyDTO> getDistributionE2Tb(Date startTime);
    List<FrequencyDTO> getDistribution(Date startTime, String name);
    List<AiSedimentationInterfaceRealtimeDTO> getAiSedimentationInterfaceRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto);
    AiSedimentationRealtimeDTO getLatestAiSedimentationRealtimeValue();
    int delAiSedimentationRealtimeValue(Date updateTime);

    ////////////////////////////////////////////
    // ai_sedimentation_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiSedimentationControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiSedimentationControl(AiProcessControlDTO dto);
    int modAiSedimentationControlKafkaFlag(AiProcessControlDTO dto);
    int delAiSedimentationControl(Date date);

    ////////////////////////////////////////////
    // ai_sedimentation_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiSedimentationInit();
    AiProcessInitDTO getAiSedimentationInit(String item);
    int modAiSedimentationOperationMode(int operationMode);
    int modAiSedimentationInit(String item, float value);

    ////////////////////////////////////////////
    // ai_sedimentation_alarm
    ////////////////////////////////////////////
    int addAiSedimentationAlarm(AiProcessAlarmDTO dto);
    List<AiProcessAlarmDTO> getAllAiSedimentationAlarm(Date alarm_time, int kafka_flag);
    int modAiSedimentationAlarmKafkaFlag(AiProcessAlarmDTO dto);
    int delAiSedimentationAlarm(Date date);

    ////////////////////////////////////////////
    // filter_realtime
    ////////////////////////////////////////////
    int addFilterRealtimeValue(List<ProcessRealtimeDTO> dtos);
    List<ProcessRealtimeDTO> getLatestFilterRealtimeValue(String partitionName);
    List<ProcessRealtimeDTO> getAllFilterRealtimeValueFromTime(Date startTime);
    List<ProcessRealtimeDTO> getFilterRealtimeValueFromTag(String name, Date startTime, Date endTime);
    ProcessRealtimeDTO getLatestFilterRealtimeValueFromTag(String name);
    void addFilterRealtimePartition(String partitionName, String endTime);
    void delFilterRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_filter_realtime
    ////////////////////////////////////////////
    List<AiFilterRealtimeDTO> getAiFilterRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto);
    AiFilterRealtimeDTO getLatestAiFilterRealtimeValue();
    int delAiFilterRealtimeValue(Date updateTime);

    ////////////////////////////////////////////
    // ai_filter_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiFilterControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiFilterControl(AiProcessControlDTO dto);
    int modAiFilterControlKafkaFlag(AiProcessControlDTO dto);
    int delAiFilterControl(Date date);


    ////////////////////////////////////////////
    // ai_filter_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiFilterInit();
    AiProcessInitDTO getAiFilterInit(String item);
    int modAiFilterOperationMode(int operationMode);
    int modAiFilterInit(String item, float value);
    int modAiFilterInitTi(float value);

    ////////////////////////////////////////////
    // ai_filter_alarm
    ////////////////////////////////////////////
    int addAiFilterAlarm(AiProcessAlarmDTO dto);
    List<AiProcessAlarmDTO> getAllAiFilterAlarm(Date alarm_time, int kafka_flag);
    int modAiFilterAlarmKafkaFlag(AiProcessAlarmDTO dto);
    int delAiFilterAlarm(Date date);

    ////////////////////////////////////////////
    // gac_realtime
    ////////////////////////////////////////////
    int addGacRealtimeValue(List<ProcessRealtimeDTO> dtos);
    List<ProcessRealtimeDTO> getLatestGacRealtimeValue(String partitionName);
    List<ProcessRealtimeDTO> getAllGacRealtimeValueFromTime(Date startTime);
    List<ProcessRealtimeDTO> getGacRealtimeValueFromTag(String name, Date startTime, Date endTime);
    ProcessRealtimeDTO getLatestGacRealtimeValueFromTag(String name);
    void addGacRealtimePartition(String partitionName, String endTime);
    void delGacRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_gac_realtime
    ////////////////////////////////////////////
    List<AiGacRealtimeDTO> getAiGacRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto);
    AiGacRealtimeDTO getLatestAiGacRealtimeValue();
    int delAiGacRealtimeValue(Date updateTime);

    ////////////////////////////////////////////
    // ai_gac_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiGacControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiGacControl(AiProcessControlDTO dto);
    int modAiGacControlKafkaFlag(AiProcessControlDTO dto);
    int delAiGacControl(Date date);


    ////////////////////////////////////////////
    // ai_gac_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiGacInit();
    AiProcessInitDTO getAiGacInit(String item);
    int modAiGacOperationMode(int operationMode);
    int modAiGacInit(String item, float value);
    int modAiGacInitTi(float value);

    ////////////////////////////////////////////
    // ai_gac_alarm
    ////////////////////////////////////////////
    int addAiGacAlarm(AiProcessAlarmDTO dto);
    List<AiProcessAlarmDTO> getAllAiGacAlarm(Date alarm_time, int kafka_flag);
    int modAiGacAlarmKafkaFlag(AiProcessAlarmDTO dto);
    int delAiGacAlarm(Date date);

    ////////////////////////////////////////////
    // disinfection_realtime
    ////////////////////////////////////////////
    int addDisinfectionRealtimeValue(List<ProcessRealtimeDTO> dtos);
    List<ProcessRealtimeDTO> getLatestDisinfectionRealtimeValue(String partitionName);
    List<ProcessRealtimeDTO> getAllDisinfectionRealtimeValueFromTime(Date startTime);
    List<ProcessRealtimeDTO> getDisinfectionRealtimeValueFromTag(String name, Date startTime, Date endTime);
    ProcessRealtimeDTO getLatestDisinfectionRealtimeValueFromTag(String name);
    void addDisinfectionRealtimePartition(String partitionName, String endTime);
    void delDisinfectionRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_disinfection_realtime
    ////////////////////////////////////////////
    List<AiDisinfectionRealtimeDTO> getAiDisinfectionRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto);
    AiDisinfectionRealtimeDTO getLatestAiDisinfectionRealtimeValue();
    int delAiDisinfectionRealtimeValue(Date updateTime);

    ////////////////////////////////////////////
    // ai_disinfection_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiPreDisinfectionControl(AiProcessControlDTO dto);
    AiProcessControlDTO getOneAiPeriDisinfectionControl(AiProcessControlDTO dto);
    AiProcessControlDTO getOneAiPostDisinfectionControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiPreDisinfectionControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiPeriDisinfectionControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiPostDisinfectionControl(AiProcessControlDTO dto);
    int modAiPreDisinfectionControlKafkaFlag(AiProcessControlDTO dto);
    int modAiPeriDisinfectionControlKafkaFlag(AiProcessControlDTO dto);
    int modAiPostDisinfectionControlKafkaFlag(AiProcessControlDTO dto);
    int delAiPreDisinfectionControl(Date date);
    int delAiPeriDisinfectionControl(Date date);
    int delAiPostDisinfectionControl(Date date);

    ////////////////////////////////////////////
    // ai_disinfection_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiDisinfectionInit();
    AiProcessInitDTO getAiDisinfectionInit(String item);
    int modAiDisinfectionInit(String item, float value);

    ////////////////////////////////////////////
    // ai_disinfection_alarm
    ////////////////////////////////////////////
    int addAiDisinfectionAlarm(AiProcessAlarmDTO dto);
    List<AiProcessAlarmDTO> getAllAiDisinfectionAlarm(Date alarm_time, int kafka_flag);
    int modAiDisinfectionAlarmKafkaFlag(AiProcessAlarmDTO dto);
    int delAiDisinfectionAlarm(Date date);

    ////////////////////////////////////////////
    // ozone_realtime
    ////////////////////////////////////////////
    int addOzoneRealtimeValue(List<ProcessRealtimeDTO> dtos);
    List<ProcessRealtimeDTO> getLatestOzoneRealtimeValue(String partitionName);
    List<ProcessRealtimeDTO> getAllOzoneRealtimeValueFromTime(Date startTime);
    List<ProcessRealtimeDTO> getOzoneRealtimeValueFromTag(String name, Date startTime, Date endTime);
    ProcessRealtimeDTO getLatestOzoneRealtimeValueFromTag(String name);
    void addOzoneRealtimePartition(String partitionName, String endTime);
    void delOzoneRealtimePartition(String partitionName);

    ////////////////////////////////////////////
    // ai_ozone_realtime
    ////////////////////////////////////////////
    List<AiOzoneRealtimeDTO> getAiOzoneRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto);
    AiOzoneRealtimeDTO getLatestAiOzoneRealtimeValue();
    int delAiOzoneRealtimeValue(Date updateTime);

    ////////////////////////////////////////////
    // ai_ozone_control
    ////////////////////////////////////////////
    AiProcessControlDTO getOneAiOzoneControl(AiProcessControlDTO dto);
    List<AiProcessControlDTO> getListAiOzoneControl(AiProcessControlDTO dto);
    int modAiOzoneControlKafkaFlag(AiProcessControlDTO dto);
    int delAiOzoneControl(Date date);

    ////////////////////////////////////////////
    // ai_ozone_init
    ////////////////////////////////////////////
    List<AiProcessInitDTO> getAllAiOzoneInit();
    AiProcessInitDTO getAiOzoneInit(String item);
    int modAiOzoneOperationMode(int operationMode);
    int modAiOzoneInit(String item, float value);

    ////////////////////////////////////////////
    // ai_ozone_alarm
    ////////////////////////////////////////////
    int addAiOzoneAlarm(AiProcessAlarmDTO dto);
    List<AiProcessAlarmDTO> getAllAiOzoneAlarm(Date alarm_time, int kafka_flag);
    int modAiOzoneAlarmKafkaFlag(AiProcessAlarmDTO dto);
    int delAiOzoneAlarm(Date date);

    ////////////////////////////////////////////
    // ems_realtime
    ////////////////////////////////////////////
    int addEmsRealtimeValue(List<ProcessRealtimeDTO> dtos);
    List<ProcessRealtimeDTO> getLatestEmsRealtimeValue();

    ////////////////////////////////////////////
    // pms_realtime
    ////////////////////////////////////////////
    List<PmsAiDTO> getLatestPmsAiValue();
    List<PmsScadaDTO> getLatestPmsScadaValue();
}
