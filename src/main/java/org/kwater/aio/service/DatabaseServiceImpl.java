package org.kwater.aio.service;

import org.kwater.aio.dao.*;
import org.kwater.aio.dto.*;
import org.kwater.aio.ai_dto.*;
import org.kwater.aio.resource_dto.CoagulantsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class DatabaseServiceImpl implements IDatabaseService
{
    @Autowired
    AccessTokenDAOImpl accessToken;

    @Autowired
    UserDAOImpl user;

    @Autowired
    LoginHistoryDAOImpl loginHistory;

    @Autowired
    DiskInfoDAOImpl diskInfo;

    @Autowired
    PartitionInfoDAOImpl partitionInfo;

    @Autowired
    InterfaceInfoDAOImpl interfaceInfo;

    @Autowired
    SystemConfigDAOImpl systemConfig;

    @Autowired
    SystemInfoDAOImpl systemInfo;

    @Autowired
    SystemMonitoringDAOImpl systemMonitoring;

    @Autowired
    SensorDAOImpl sensor;

    @Autowired
    DiatomDAOImpl diatom;

    @Autowired
    AlarmInfoDAOImpl alarmInfo;

    @Autowired
    AlarmNotifyDAOImpl alarmNotify;

    @Autowired
    ChemicalInfoDAOImpl chemicalInfo;

    @Autowired
    WaterPurificationInfoDAOImpl waterPurificationInfo;

    @Autowired
    CoagulantsAnalysisDAOImpl coagulantsAnalysis;

    @Autowired
    CoagulantsSimulationDAOImpl coagulantsSimulation;

    @Autowired
    ClassInfoDAOImpl classInfo;

    @Autowired
    ClusterInfoDAOImpl clusterInfo;

    @Autowired
    DashboardInfoDAOImpl dashboardInfo;

    @Autowired
    TagDescriptionDAOImpl tagDescription;

    @Autowired
    TagManageDAOImpl tagManage;

    @Autowired
    ProcessCodeDAOImpl processCode;

    @Autowired
    ReceivingRealtimeDAOImpl receivingRealtime;

    @Autowired
    AiReceivingRealtimeDAOImpl aiReceivingRealtime;

    @Autowired
    AiReceivingControlDAOImpl aiReceivingControl;

    @Autowired
    AiReceivingInitDAOImpl aiReceivingInit;

    @Autowired
    AiReceivingDataDAOImpl aiReceivingData;

    @Autowired
    AiReceivingAlarmDAOImpl aiReceivingAlarm;

    @Autowired
    AiClearOperationBandDAOImpl aiClearOperationBand;

    @Autowired
    AiClearEmsOperationBandDAOImpl aiClearEmsOperationBand;

    @Autowired
    AiClearWideOperationBandDAOImpl aiClearWideOperationBand;

    @Autowired
    CoagulantRealtimeDAOImpl coagulantRealtime;

    @Autowired
    AiCoagulantRealtimeDAOImpl aiCoagulantRealtime;

    @Autowired
    AiCoagulantControlDAOImpl aiCoagulantControl;

    @Autowired
    AiCoagulantInitDAOImpl aiCoagulantInit;

    @Autowired
    AiCoagulantAlarmDAOImpl aiCoagulantAlarm;

    @Autowired
    AiCoagulantSimulationDAOImpl aiCoagulantSimulation;

    @Autowired
    MixingRealtimeDAOImpl mixingRealtime;

    @Autowired
    AiMixingRealtimeDAOImpl aiMixingRealtime;

    @Autowired
    AiMixingControlDAOImpl aiMixingControl;

    @Autowired
    AiMixingInitDAOImpl aiMixingInit;

    @Autowired
    AiMixingAlarmDAOImpl aiMixingAlarm;

    @Autowired
    SedimentationRealtimeDAOImpl sedimentationRealtime;

    @Autowired
    AiSedimentationRealtimeDAOImpl aiSedimentationRealtime;

    @Autowired
    AiSedimentationControlDAOImpl aiSedimentationControl;

    @Autowired
    AiSedimentationInitDAOImpl aiSedimentationInit;

    @Autowired
    AiSedimentationAlarmDAOImpl aiSedimentationAlarm;

    @Autowired
    FilterRealtimeDAOImpl filterRealtime;

    @Autowired
    AiFilterRealtimeDAOImpl aiFilterRealtime;

    @Autowired
    AiFilterControlDAOImpl aiFilterControl;

    @Autowired
    AiFilterInitDAOImpl aiFilterInit;

    @Autowired
    AiFilterAlarmDAOImpl aiFilterAlarm;

    @Autowired
    GacRealtimeDAOImpl gacRealtime;

    @Autowired
    AiGacRealtimeDAOImpl aiGacRealtime;

    @Autowired
    AiGacControlDAOImpl aiGacControl;

    @Autowired
    AiGacInitDAOImpl aiGacInit;

    @Autowired
    AiGacAlarmDAOImpl aiGacAlarm;

    @Autowired
    DisinfectionRealtimeDAOImpl disinfectionRealtime;

    @Autowired
    AiDisinfectionRealtimeDAOImpl aiDisinfectionRealtime;

    @Autowired
    AiPreDisinfectionControlDAOImpl aiPreDisinfectionControl;

    @Autowired
    AiPeriDisinfectionControlDAOImpl aiPeriDisinfectionControl;

    @Autowired
    AiPostDisinfectionControlDAOImpl aiPostDisinfectionControl;

    @Autowired
    AiDisinfectionInitDAOImpl aiDisinfectionInit;

    @Autowired
    AiDisinfectionAlarmDAOImpl aiDisinfectionAlarm;

    @Autowired
    OzoneRealtimeDAOImpl ozoneRealtime;

    @Autowired
    AiOzoneRealtimeDAOImpl aiOzoneRealtime;

    @Autowired
    AiOzoneControlDAOImpl aiOzoneControl;

    @Autowired
    AiOzoneInitDAOImpl aiOzoneInit;

    @Autowired
    AiOzoneAlarmDAOImpl aiOzoneAlarm;

    @Autowired
    EmsRealtimeDAOImpl emsRealtime;

    @Autowired
    PmsRealtimeDAOImpl pmsRealtime;

    ////////////////////////////////////////////
    // access_token
    ////////////////////////////////////////////
    @Override
    public int addToken(AccessTokenDTO dto)
    {
        return accessToken.insert(dto);
    }

    @Override
    public List<AccessTokenDTO> getAllTokens()
    {
        return accessToken.select();
    }

    @Override
    public AccessTokenDTO getToken(String token)
    {
        return accessToken.select(token);
    }

    @Override
    public int modToken(String token, Date expiration)
    {
        return accessToken.update(token, expiration);
    }

    @Override
    public int delToken(String token)
    {
        return accessToken.delete(token);
    }

    @Override
    public int delToken(Date expiration)
    {
        return accessToken.delete(expiration);
    }

    ////////////////////////////////////////////
    // user
    ////////////////////////////////////////////
    @Override
    public int addUser(UserDTO dto)
    {
        return user.insert(dto);
    }

    @Override
    public UserDTO getUser(String userid, String password)
    {
        return user.selectUser(userid, password);
    }

    @Override
    public UserDTO getUserFromUserid(String userid)
    {
        return user.selectUserFromUserid(userid);
    }

    @Override
    public List<UserDTO> getAllUser() {
        return user.selectAll();
    }

    @Override
    public int modUser(int authority, UserDTO dto)
    {
        return user.update(authority, dto);
    }

    @Override
    public int modUserPw(String userid, String password)
    {
        return user.updatePw(userid, password);
    }

    @Override
    public int delUser(String userid)
    {
        return user.delete(userid);
    }

    ////////////////////////////////////////////
    // login_history
    ////////////////////////////////////////////
    @Override
    public int addLoginHistory(LoginHistoryDTO dto) {
        return loginHistory.insert(dto);
    }

    @Override
    public List<LoginHistoryDTO> getAllLoginHistory() {
        return loginHistory.select();
    }

    @Override
    public int delLoginHistory(Date date)
    {
        return loginHistory.delete(date);
    }

    ////////////////////////////////////////////
    // disk_info
    ////////////////////////////////////////////
    @Override
    public int addDiskInfo(DiskInfoDTO dto)
    {
        return diskInfo.insert(dto);
    }

    @Override
    public List<DiskInfoDTO> getDiskInfoFromHostname(String hostname)
    {
        return diskInfo.select(hostname);
    }

    @Override
    public int modDiskInfo(DiskInfoDTO dto)
    {
        return diskInfo.update(dto);
    }

    ////////////////////////////////////////////
    // partition_info
    ////////////////////////////////////////////
    @Override
    public int addPartitionInfo(PartitionInfoDTO dto)
    {
        return partitionInfo.insert(dto);
    }

    @Override
    public List<PartitionInfoDTO> getPartitionInfoFromHostname(String hostname) {
        return partitionInfo.select(hostname);
    }

    @Override
    public int modPartitionInfo(PartitionInfoDTO dto) {
        return partitionInfo.update(dto);
    }

    ////////////////////////////////////////////
    // interface_info
    ////////////////////////////////////////////
    @Override
    public int addInterfaceInfo(InterfaceInfoDTO dto)
    {
        return interfaceInfo.insert(dto);
    }

    @Override
    public List<InterfaceInfoDTO> getInterfaceInfoFromHostname(String hostname) {
        return interfaceInfo.select(hostname);
    }

    @Override
    public List<InterfaceInfoDTO> getInterfaceInfoFromAddress(String address)
    {
        return interfaceInfo.selectWhereAddress(address);
    }

    @Override
    public int modInterfaceInfo(InterfaceInfoDTO dto) {
        return interfaceInfo.update(dto);
    }

    ////////////////////////////////////////////
    // system_config
    ////////////////////////////////////////////
    @Override
    public SystemConfigDTO getSystemConfig()
    {
        return systemConfig.select();
    }

    @Override
    public int modSystemConfig(SystemConfigDTO dto)
    {
        return systemConfig.update(dto);
    }

    ////////////////////////////////////////////
    // system_info
    ////////////////////////////////////////////
    @Override
    public int addSystemInfo(SystemInfoDTO dto)
    {
        return systemInfo.insert(dto);
    }

    @Override
    public SystemInfoDTO getSystemInfoFromHostname(String hostname) {
        return systemInfo.select(hostname);
    }

    @Override
    public List<SystemInfoDTO> getAllSystemInfo()
    {
        return systemInfo.select();
    }

    @Override
    public int modSystemInfo(SystemInfoDTO dto) {
        return systemInfo.update(dto);
    }

    @Override
    public int modSystemInfoName(String hostname, String name)
    {
        return systemInfo.updateName(hostname, name);
    }

    ////////////////////////////////////////////
    // system_monitoring
    ////////////////////////////////////////////
    @Override
    public int addSystemMonitoring(SystemMonitoringDTO dto)
    {
        return systemMonitoring.insert(dto);
    }

    @Override
    public List<SystemMonitoringDTO> getAllSystemMonitoring()
    {
        return systemMonitoring.select();
    }

    @Override
    public List<SystemMonitoringDTO> getSystemMonitoringFromHostname(String hostname)
    {
        return systemMonitoring.select(hostname);
    }

    @Override
    public List<SystemMonitoringDTO> getLatestSystemMonitoring(Date startDate)
    {
        return systemMonitoring.selectLatest(startDate);
    }

    @Override
    public int delSystemMonitoring(Date date)
    {
        return systemMonitoring.delete(date);
    }

    ////////////////////////////////////////////
    // sensor
    ////////////////////////////////////////////
    @Override
    public int addSensor(SensorDTO dto)
    {
        return sensor.insert(dto);
    }

    @Override
    public List<SensorDTO> getSensor(Date startDate)
    {
        return sensor.select(startDate);
    }

    @Override
    public SensorDTO getLatestSensor()
    {
        return sensor.selectLatest();
    }

    @Override
    public List<CoagulantsDTO> getHistorySensor()
    {
        return sensor.selectHistory();
    }

    @Override
    public CoagulantsDTO getLatestCoagulants()
    {
        return sensor.selectCoagulants();
    }

    @Override
    public List<TrendTbDTO> getTrendTb(InterfaceDateSearchDTO dto)
    {
        return sensor.selectTb(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public List<TrendCodeDTO> getTrendCode(String code, InterfaceDateSearchDTO dto)
    {
        return sensor.selectCode(code, dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public List<FrequencyDTO> getDistinctCountHsE1Tb(Date startDate)
    {
        return sensor.selectDistinctCountHsE1Tb(startDate);
    }

    @Override
    public List<FrequencyDTO> getDistinctCountHsE2Tb(Date startDate)
    {
        return sensor.selectDistinctCountHsE2Tb(startDate);
    }

    @Override
    public List<FrequencyDTO> getDistinctCountHsFTb(Date startDate)
    {
        return sensor.selectDistinctCountHsFTb(startDate);
    }

    @Override
    public int delSensor(Date updateTime)
    {
        return sensor.delete(updateTime);
    }

    ////////////////////////////////////////////
    // diatom
    ////////////////////////////////////////////
    @Override
    public int addDiatom(DiatomDTO dto)
    {
        return diatom.insert(dto);
    }

    @Override
    public List<DiatomDTO> getDiatom()
    {
        return diatom.select();
    }

    @Override
    public DiatomDTO getLatestDiatom()
    {
        return diatom.selectLatest();
    }

    @Override
    public int modDiatom(DiatomDTO dto)
    {
        return diatom.update(dto);
    }

    @Override
    public int delDiatom(int diatomIndex)
    {
        return diatom.delete(diatomIndex);
    }

    ////////////////////////////////////////////
    // alarm_info
    ////////////////////////////////////////////
    @Override
    public int addAlarmInfo(AlarmInfoDTO dto)
    {
        return alarmInfo.insert(dto);
    }

    @Override
    public List<AlarmInfoDTO> getAlarmInfo()
    {
        return alarmInfo.select();
    }

    @Override
    public int modAlarmInfo(AlarmInfoDTO dto)
    {
        return alarmInfo.update(dto);
    }

    @Override
    public int delAlarmInfo(int alarmId)
    {
        return alarmInfo.delete(alarmId);
    }

    ////////////////////////////////////////////
    // alarm_notify
    ////////////////////////////////////////////
    @Override
    public int addAlarmNotify(AlarmNotifyDTO dto)
    {
        return alarmNotify.insert(dto);
    }

    @Override
    public List<AlarmNotifyDTO> getAllAlarmNotify()
    {
        return alarmNotify.select();
    }
// 알람 네비게이터 삭제
//    @Override
//    public List<AlarmNotifyDTO> getAlarmNotifyFromAckState(boolean ackState)
//    {
//        return alarmNotify.select(ackState);
//    }

    @Override
    public List<AlarmNotifyDTO> getAlarmNotify(Date startTime)
    {
        return alarmNotify.select(startTime);
    }

    @Override
    public List<AlarmNotifyDTO> getSearchAlarmNotify(InterfaceDateSearchDTO dto)
    {
        return alarmNotify.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AlarmNotifyDTO getLatestAlarmNotify(int alarmId, Date alarmDate, String hostname)
    {
        return alarmNotify.select(alarmId, alarmDate, hostname);
    }

    @Override
    public int modAlarmNotifyAckState(int alarmNotifyIndex, boolean ackState)
    {
        return alarmNotify.updateAckState(alarmNotifyIndex, ackState);
    }

    ////////////////////////////////////////////
    // chemical_info
    ////////////////////////////////////////////
    @Override
    public int addChemicalInfo(ChemicalInfoDTO dto)
    {
        return chemicalInfo.insert(dto);
    }

    @Override
    public List<ChemicalInfoDTO> getAllChemicalInfo() {
        return chemicalInfo.select();
    }

    @Override
    public ChemicalInfoDTO getChemicalInfoFromCode(String code)
    {
        return chemicalInfo.select(code);
    }

    @Override
    public int modChemicalInfo(ChemicalInfoDTO dto)
    {
        return chemicalInfo.update(dto);
    }

    @Override
    public int delChemicalInfo(String code)
    {
        return chemicalInfo.delete(code);
    }

    ////////////////////////////////////////////
    // water_purification_info
    ////////////////////////////////////////////
    @Override
    public int addWaterPurificationInfo(WaterPurificationInfoDTO dto)
    {
        return waterPurificationInfo.insert(dto);
    }

    @Override
    public List<WaterPurificationInfoDTO> getAllWaterPurificationInfo()
    {
        return waterPurificationInfo.select();
    }

    @Override
    public WaterPurificationInfoDTO getWaterPurificationInfoFromCode(String code)
    {
        return waterPurificationInfo.select(code);
    }

    @Override
    public int modWaterPurificationInfo(WaterPurificationInfoDTO dto)
    {
        return waterPurificationInfo.update(dto);
    }

    @Override
    public int delWaterPurificationInfo(String code)
    {
        return waterPurificationInfo.delete(code);
    }

    ////////////////////////////////////////////
    // coagulants_analysis
    ////////////////////////////////////////////
    @Override
    public List<CoagulantsAnalysisDTO> getAllCoagulantsAnalysis()
    {
        return coagulantsAnalysis.select();
    }

    @Override
    public CoagulantsAnalysisDTO getCoagulantsAnalysisFromLogTime(Date logTime)
    {
        return coagulantsAnalysis.select(logTime);
    }

    @Override
    public CoagulantsAnalysisDTO getLatestCoagulantsAnalysis()
    {
        return coagulantsAnalysis.selectLatest();
    }

    @Override
    public List<CoagulantsAnalysisDTO> get2LatestCoagulantsAnalysis()
    {
        return coagulantsAnalysis.select2Latest();
    }

    @Override
    public List<CoagulantsAnalysisDTO> getMinuteCoagulantsAnalysis(Date startTime)
    {
        return coagulantsAnalysis.selectMinute(startTime);
    }

    @Override
    public int addRawWaterClassInfo(ClassInfoDTO dto)
    {
        return classInfo.insert(dto);
    }

    @Override
    public List<ClassInfoDTO> getAllRawWaterClassInfo()
    {
        return classInfo.select();
    }

    @Override
    public int modRawWaterClassInfo(ClassInfoDTO dto)
    {
        return classInfo.update(dto);
    }

    @Override
    public int delRawWaterClassInfo(int classIndex)
    {
        return classInfo.delete(classIndex);
    }

    @Override
    public List<ClusterInfoDTO> getAllCoagulantsClusterInfo()
    {
        return clusterInfo.select();
    }

    ////////////////////////////////////////////
    // coagulants_simulation
    ////////////////////////////////////////////

    @Override
    public int addCoagulantsSimulation(CoagulantsSimulationDTO dto)
    {
        return coagulantsSimulation.insert(dto);
    }

    @Override
    public List<CoagulantsSimulationDTO> getAllCoagulantsSimulation()
    {
        return coagulantsSimulation.select();
    }

    @Override
    public List<CoagulantsSimulationDTO> getCoagulantsSimulationUpperState(int state)
    {
        return coagulantsSimulation.select(true, state);
    }

    @Override
    public List<CoagulantsSimulationDTO> getCoagulantsSimulationLowerState(int state)
    {
        return coagulantsSimulation.select(false, state);
    }

    ////////////////////////////////////////////
    // dashboard_info
    ////////////////////////////////////////////

    @Override
    public int addDashboardInfo(String data)
    {
        return dashboardInfo.insert(data);
    }

    @Override
    public DashboardIdDTO getLatestDashboardInfo()
    {
        return dashboardInfo.selectLatest();
    }

    @Override
    public DashboardDataDTO getDashboardInfo(int dashboard_index)
    {
        return dashboardInfo.select(dashboard_index);
    }

    @Override
    public int modDashboardInfo(int dashboard_id, String data)
    {
        return dashboardInfo.update(dashboard_id, data);
    }

    @Override
    public int delDashboardInfo(int dashboard_id)
    {
        return dashboardInfo.delete(dashboard_id);
    }

    ////////////////////////////////////////////
    // tag_description
    ////////////////////////////////////////////
    @Override
    public int addTagDescription(TagDescriptionDTO dto)
    {
        return tagDescription.insert(dto);
    }

    @Override
    public List<TagDescriptionDTO> getAllTagDescription()
    {
        return tagDescription.select();
    }

    @Override
    public int modTagDescription(TagDescriptionDTO dto)
    {
        return tagDescription.update(dto);
    }

    @Override
    public int delTagDescription(int tagIndex)
    {
        return tagDescription.delete(tagIndex);
    }

    ////////////////////////////////////////////
    // tag_manage
    ////////////////////////////////////////////
    @Override
    public int addTagManage(TagManageDTO dto)
    {
        return tagManage.insert(dto);
    }

    @Override
    public List<TagManageDTO> getAllTagManage()
    {
        return tagManage.select();
    }

    @Override
    public List<TagManageDTO> getTagManageFromType(int type)
    {
        return tagManage.select(type);
    }

    @Override
    public List<TagManageDTO> getTagManageFromCode(String process)
    {
        return tagManage.select(process);
    }

    @Override
    public TagManageRangeDTO getTagManageRange(String process)
    {
        return tagManage.selectRange(process);
    }

    @Override
    public int modTagManage(TagManageDTO dto)
    {
        return tagManage.update(dto);
    }

    @Override
    public int delTagManage(TagManageDTO dto)
    {
        return tagManage.delete(dto);
    }

    ////////////////////////////////////////////
    // process_code
    ////////////////////////////////////////////
    @Override
    public List<ProcessCodeDTO> getAllProcessCode()
    {
        return processCode.select();
    }

    ////////////////////////////////////////////
    // receiving_realtime
    ////////////////////////////////////////////
    @Override
    public int addReceivingRealtimeValue(List<ProcessRealtimeDTO> dtos)
    {
        return receivingRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestReceivingRealtimeValue(String partitionName)
    {
        return receivingRealtime.select(partitionName);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllReceivingRealtimeValueFromTime(Date startTime)
    {
        return receivingRealtime.select(startTime);
    }

    @Override
    public List<ProcessRealtimeDTO> getReceivingRealtimeValueFromTag(String name, Date startTime, Date endTime)
    {
        return receivingRealtime.select(name, startTime, endTime);
    }

    @Override
    public ProcessRealtimeDTO getLatestReceivingRealtimeValueFromTag(String name)
    {
        return receivingRealtime.selectLatest(name);
    }

    @Override
    public void addReceivingRealtimePartition(String partitionName, String endTime)
    {
        receivingRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delReceivingRealtimePartition(String partitionName)
    {
        receivingRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_receiving_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiReceivingRealtimeDTO> getAiReceivingRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto)
    {
        return aiReceivingRealtime.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AiReceivingRealtimeDTO getLatestAiReceivingRealtimeValue()
    {
        return aiReceivingRealtime.select();
    }

    @Override
    public int delAiReceivingRealtimeValue(Date updateTime)
    {
        return aiReceivingRealtime.delete(updateTime);
    }

    @Override
    public List<AiClearOperationBandDTO> getAiClearOperationBandFromTimeIndex(InterfaceDateSearchDTO dto)
    {
        return aiClearOperationBand.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public List<AiClearOperationBandDTO> getAiClearEmsOperationBandFromTimeIndex(InterfaceDateSearchDTO dto)
    {
        return aiClearEmsOperationBand.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public List<AiClearOperationBandDTO> getAiClearWideOperationBandFromTimeIndex(InterfaceDateSearchDTO dto)
    {
        return aiClearWideOperationBand.select(dto.getStart_time(), dto.getEnd_time());
    }

    ////////////////////////////////////////////
    // ai_receiving_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiReceivingControl(AiProcessControlDTO dto)
    {
        return aiReceivingControl.select(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessControlDTO> getListAiReceivingControl(AiProcessControlDTO dto)
    {
        return aiReceivingControl.select(dto.getRun_time(), dto.getKafka_flag());
    }

    @Override
    public int modAiReceivingControlKafkaFlag(AiProcessControlDTO dto)
    {
        return aiReceivingControl.updateKafkaFlag(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public int delAiReceivingControl(Date date)
    {
        return aiReceivingControl.delete(date);
    }

    ////////////////////////////////////////////
    // ai_receiving_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiReceivingInit()
    {
        return aiReceivingInit.select();
    }

    @Override
    public AiProcessInitDTO getAiReceivingInit(String item)
    {
        return aiReceivingInit.select(item);
    }

    @Override
    public int modAiReceivingOperationMode(int operationMode)
    {
        return aiReceivingInit.updateOperationMode(operationMode);
    }

    @Override
    public int modAiReceivingInit(String item, float value)
    {
        return aiReceivingInit.update(item, value);
    }

    ////////////////////////////////////////////
    // ai_receiving_data
    ////////////////////////////////////////////
    @Override
    public int addAiReceivingDataValue(List<ProcessRealtimeDTO> dtos)
    {
        return aiReceivingData.insert(dtos);
    }

    @Override
    public int delAiReceivingDataValue(Date updateTime)
    {
        return aiReceivingData.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_receiving_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiReceivingAlarm(AiProcessAlarmDTO dto)
    {
        return aiReceivingAlarm.insert(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiReceivingAlarm(Date alarm_time, int kafka_flag)
    {
        return aiReceivingAlarm.select(alarm_time, kafka_flag);
    }

    @Override
    public int modAiReceivingAlarmKafkaFlag(AiProcessAlarmDTO dto)
    {
        return aiReceivingAlarm.updateKafkaFlag(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public int delAiReceivingAlarm(Date date)
    {
        return aiReceivingAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // coagulant_realtime
    ////////////////////////////////////////////
    @Override
    public int addCoagulantRealtimeValue(List<ProcessRealtimeDTO> dtos)
    {
        return coagulantRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestCoagulantRealtimeValue(String partitionName)
    {
        return coagulantRealtime.select(partitionName);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllCoagulantRealtimeValueFromTime(Date startTime)
    {
        return coagulantRealtime.select(startTime);
    }

    @Override
    public List<ProcessRealtimeDTO> getCoagulantRealtimeValueFromTag(String name, Date startTime, Date endTime)
    {
        return coagulantRealtime.select(name, startTime, endTime);
    }

    @Override
    public List<FrequencyDTO> getCoagulantDistribution(Date startTime, String name)
    {
        return coagulantRealtime.selectDistribution(startTime, name);
    }

    @Override
    public void addCoagulantRealtimePartition(String partitionName, String endTime)
    {
        coagulantRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delCoagulantRealtimePartition(String partitionName)
    {
        coagulantRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_coagulant_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiCoagulantRealtimeDTO> getAiCoagulantRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto)
    {
        return aiCoagulantRealtime.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AiCoagulantRealtimeDTO getLatestAiCoagulantRealtimeValue()
    {
        return aiCoagulantRealtime.select();
    }

    @Override
    public int delAiCoagulantRealtimeValue(Date updateTime)
    {
        return aiCoagulantRealtime.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_coagulant_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiCoagulantControl(AiProcessControlDTO dto)
    {
        return aiCoagulantControl.select(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessControlDTO> getListAiCoagulantControl(AiProcessControlDTO dto)
    {
        return aiCoagulantControl.select(dto.getRun_time(), dto.getKafka_flag());
    }

    @Override
    public int modAiCoagulantControlKafkaFlag(AiProcessControlDTO dto)
    {
        return aiCoagulantControl.updateKafkaFlag(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public int delAiCoagulantControl(Date date)
    {
        return aiCoagulantControl.delete(date);
    }

    ////////////////////////////////////////////
    // ai_coagulant_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiCoagulantInit()
    {
        return aiCoagulantInit.select();
    }

    @Override
    public AiProcessInitDTO getAiCoagulantInit(String item)
    {
        return aiCoagulantInit.select(item);
    }

    @Override
    public int modAiCoagulantOperationMode(int operationMode)
    {
        return aiCoagulantInit.updateOperationMode(operationMode);
    }

    @Override
    public int modAiCoagulantInit(String item, float value)
    {
        return aiCoagulantInit.update(item, value);
    }

    ////////////////////////////////////////////
    // ai_coagulant_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiCoagulantAlarm(AiProcessAlarmDTO dto)
    {
        return aiCoagulantAlarm.insert(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiCoagulantAlarm(Date alarm_time, int kafka_flag)
    {
        return aiCoagulantAlarm.select(alarm_time, kafka_flag);
    }

    @Override
    public int modAiCoagulantAlarmKafkaFlag(AiProcessAlarmDTO dto)
    {
        return aiCoagulantAlarm.updateKafkaFlag(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public int delAiCoagulantAlarm(Date date)
    {
        return aiCoagulantAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // ai_coagulant_simulation
    ////////////////////////////////////////////
    @Override
    public int addAiCoagulantSimulation(AiCoagulantSimulationDTO dto)
    {
        return aiCoagulantSimulation.insert(dto);
    }

    @Override
    public List<AiCoagulantSimulationDTO> getAiCoagulantSimulation(InterfaceDateSearchDTO dto)
    {
        return aiCoagulantSimulation.select(dto.getStart_time(), dto.getEnd_time());
    }

    ////////////////////////////////////////////
    // mixing_realtime
    ////////////////////////////////////////////
    @Override
    public int addMixingRealtimeValue(List<ProcessRealtimeDTO> dtos)
    {
        return mixingRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestMixingRealtimeValue(String partitionName)
    {
        return mixingRealtime.select(partitionName);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllMixingRealtimeValueFromTime(Date startTime)
    {
        return mixingRealtime.select(startTime);
    }

    @Override
    public List<ProcessRealtimeDTO> getMixingRealtimeValueFromTag(String name, Date startTime, Date endTime)
    {
        return mixingRealtime.select(name, startTime, endTime);
    }

    @Override
    public ProcessRealtimeDTO getLatestMixingRealtimeValueFromTag(String name)
    {
        return mixingRealtime.selectLatest(name);
    }

    @Override
    public void addMixingRealtimePartition(String partitionName, String endTime)
    {
        mixingRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delMixingRealtimePartition(String partitionName)
    {
        mixingRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_mixing_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiMixingRealtimeDTO> getAiMixingRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto)
    {
        return aiMixingRealtime.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AiMixingRealtimeDTO getLatestAiMixingRealtimeValue()
    {
        return aiMixingRealtime.select();
    }

    @Override
    public int delAiMixingRealtimeValue(Date updateTime)
    {
        return aiMixingRealtime.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_mixing_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiMixingControl(AiProcessControlDTO dto)
    {
        return aiMixingControl.select(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessControlDTO> getListAiMixingControl(AiProcessControlDTO dto)
    {
        return aiMixingControl.select(dto.getRun_time(), dto.getKafka_flag());
    }

    @Override
    public int modAiMixingControlKafkaFlag(AiProcessControlDTO dto)
    {
        return aiMixingControl.updateKafkaFlag(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public int delAiMixingControl(Date date)
    {
        return aiMixingControl.delete(date);
    }

    ////////////////////////////////////////////
    // ai_mixing_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiMixingInit()
    {
        return aiMixingInit.select();
    }

    @Override
    public AiProcessInitDTO getAiMixingInit(String item)
    {
        return aiMixingInit.select(item);
    }

    @Override
    public int modAiMixingOperationMode(int operationMode)
    {
        return aiMixingInit.updateOperationMode(operationMode);
    }

    @Override
    public int modAiMixingInit(String item, float value)
    {
        return aiMixingInit.update(item, value);
    }

    ////////////////////////////////////////////
    // ai_mixing_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiMixingAlarm(AiProcessAlarmDTO dto)
    {
        return aiMixingAlarm.insert(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiMixingAlarm(Date alarm_time, int kafka_flag)
    {
        return aiMixingAlarm.select(alarm_time, kafka_flag);
    }

    @Override
    public int modAiMixingAlarmKafkaFlag(AiProcessAlarmDTO dto)
    {
        return aiMixingAlarm.updateKafkaFlag(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public int delAiMixingAlarm(Date date)
    {
        return aiMixingAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // sedimentation_realtime
    ////////////////////////////////////////////
    @Override
    public int addSedimentationRealtimeValue(List<ProcessRealtimeDTO> dtos)
    {
        return sedimentationRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestSedimentationRealtimeValue(String partitionName)
    {
        return sedimentationRealtime.select(partitionName);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllSedimentationRealtimeValueFromTime(Date startTime)
    {
        return sedimentationRealtime.select(startTime);
    }

    @Override
    public List<ProcessRealtimeDTO> getSedimentationRealtimeValueFromTag(String name, Date startTime, Date endTime)
    {
        return sedimentationRealtime.select(name, startTime, endTime);
    }

    @Override
    public ProcessRealtimeDTO getLatestSedimentationRealtimeValueFromTag(String name)
    {
        return sedimentationRealtime.selectLatest(name);
    }

    @Override
    public void addSedimentationRealtimePartition(String partitionName, String endTime)
    {
        sedimentationRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delSedimentationRealtimePartition(String partitionName)
    {
        sedimentationRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_sedimentation_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiSedimentationRealtimeDTO> getAiSedimentationRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto) {
        return aiSedimentationRealtime.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public List<FrequencyDTO> getDistributionE1Tb(Date startTime)
    {
        return aiSedimentationRealtime.selectE1Tb(startTime);
    }

    @Override
    public List<FrequencyDTO> getDistributionE2Tb(Date startTime)
    {
        return aiSedimentationRealtime.selectE2Tb(startTime);
    }

    @Override
    public List<FrequencyDTO> getDistribution(Date startTime, String name)
    {
        return aiSedimentationRealtime.selectDistribution(startTime, name);
    }

    @Override
    public List<AiSedimentationInterfaceRealtimeDTO> getAiSedimentationInterfaceRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto)
    {
        return aiSedimentationRealtime.selectInterface(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AiSedimentationRealtimeDTO getLatestAiSedimentationRealtimeValue() {
        return aiSedimentationRealtime.select();
    }

    @Override
    public int delAiSedimentationRealtimeValue(Date updateTime)
    {
        return aiSedimentationRealtime.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_sedimentation_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiSedimentationControl(AiProcessControlDTO dto)
    {
        return aiSedimentationControl.select(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessControlDTO> getListAiSedimentationControl(AiProcessControlDTO dto)
    {
        return aiSedimentationControl.select(dto.getRun_time(), dto.getKafka_flag());
    }

    @Override
    public int modAiSedimentationControlKafkaFlag(AiProcessControlDTO dto)
    {
        return aiSedimentationControl.updateKafkaFlag(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public int delAiSedimentationControl(Date date)
    {
        return aiSedimentationControl.delete(date);
    }

    ////////////////////////////////////////////
    // ai_sedimentation_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiSedimentationInit()
    {
        return aiSedimentationInit.select();
    }

    @Override
    public AiProcessInitDTO getAiSedimentationInit(String item)
    {
        return aiSedimentationInit.select(item);
    }

    @Override
    public int modAiSedimentationOperationMode(int operationMode)
    {
        return aiSedimentationInit.updateOperationMode(operationMode);
    }

    @Override
    public int modAiSedimentationInit(String item, float value)
    {
        return aiSedimentationInit.update(item, value);
    }

    ////////////////////////////////////////////
    // ai_sedimentation_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiSedimentationAlarm(AiProcessAlarmDTO dto)
    {
        return aiSedimentationAlarm.insert(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiSedimentationAlarm(Date alarm_time, int kafka_flag)
    {
        return aiSedimentationAlarm.select(alarm_time, kafka_flag);
    }

    @Override
    public int modAiSedimentationAlarmKafkaFlag(AiProcessAlarmDTO dto)
    {
        return aiSedimentationAlarm.updateKafkaFlag(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public int delAiSedimentationAlarm(Date date)
    {
        return aiSedimentationAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // filter_realtime
    ////////////////////////////////////////////
    @Override
    public int addFilterRealtimeValue(List<ProcessRealtimeDTO> dtos)
    {
        return filterRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestFilterRealtimeValue(String partitionName)
    {
        return filterRealtime.select(partitionName);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllFilterRealtimeValueFromTime(Date startTime)
    {
        return filterRealtime.select(startTime);
    }

    @Override
    public List<ProcessRealtimeDTO> getFilterRealtimeValueFromTag(String name, Date startTime, Date endTime)
    {
        return filterRealtime.select(name, startTime, endTime);
    }

    @Override
    public ProcessRealtimeDTO getLatestFilterRealtimeValueFromTag(String name)
    {
        return filterRealtime.selectLatest(name);
    }

    @Override
    public void addFilterRealtimePartition(String partitionName, String endTime)
    {
        filterRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delFilterRealtimePartition(String partitionName)
    {
        filterRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_filter_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiFilterRealtimeDTO> getAiFilterRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto) {
        return aiFilterRealtime.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AiFilterRealtimeDTO getLatestAiFilterRealtimeValue() {
        return aiFilterRealtime.select();
    }

    @Override
    public int delAiFilterRealtimeValue(Date updateTime)
    {
        return aiFilterRealtime.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_filter_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiFilterControl(AiProcessControlDTO dto)
    {
        return aiFilterControl.select(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessControlDTO> getListAiFilterControl(AiProcessControlDTO dto)
    {
        return aiFilterControl.select(dto.getRun_time(), dto.getKafka_flag());
    }

    @Override
    public int modAiFilterControlKafkaFlag(AiProcessControlDTO dto)
    {
        return aiFilterControl.updateKafkaFlag(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public int delAiFilterControl(Date date)
    {
        return aiFilterControl.delete(date);
    }

    ////////////////////////////////////////////
    // ai_filter_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiFilterInit()
    {
        return aiFilterInit.select();
    }

    @Override
    public AiProcessInitDTO getAiFilterInit(String item)
    {
        return aiFilterInit.select(item);
    }

    @Override
    public int modAiFilterOperationMode(int operationMode)
    {
        return aiFilterInit.updateOperationMode(operationMode);
    }

    @Override
    public int modAiFilterInit(String item, float value)
    {
        return aiFilterInit.update(item, value);
    }

    @Override
    public int modAiFilterInitTi(float value)
    {
        return aiFilterInit.updateTi(value);
    }

    ////////////////////////////////////////////
    // ai_filter_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiFilterAlarm(AiProcessAlarmDTO dto)
    {
        return aiFilterAlarm.insert(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiFilterAlarm(Date alarm_time, int kafka_flag)
    {
        return aiFilterAlarm.select(alarm_time, kafka_flag);
    }

    @Override
    public int modAiFilterAlarmKafkaFlag(AiProcessAlarmDTO dto)
    {
        return aiFilterAlarm.updateKafkaFlag(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public int delAiFilterAlarm(Date date)
    {
        return aiFilterAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // gac_realtime
    ////////////////////////////////////////////
    @Override
    public int addGacRealtimeValue(List<ProcessRealtimeDTO> dtos)
    {
        return gacRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestGacRealtimeValue(String partitionName)
    {
        return gacRealtime.select(partitionName);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllGacRealtimeValueFromTime(Date startTime)
    {
        return gacRealtime.select(startTime);
    }

    @Override
    public List<ProcessRealtimeDTO> getGacRealtimeValueFromTag(String name, Date startTime, Date endTime)
    {
        return gacRealtime.select(name, startTime, endTime);
    }

    @Override
    public ProcessRealtimeDTO getLatestGacRealtimeValueFromTag(String name)
    {
        return gacRealtime.selectLatest(name);
    }

    @Override
    public void addGacRealtimePartition(String partitionName, String endTime)
    {
        gacRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delGacRealtimePartition(String partitionName)
    {
        gacRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_gac_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiGacRealtimeDTO> getAiGacRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto)
    {
        return aiGacRealtime.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AiGacRealtimeDTO getLatestAiGacRealtimeValue()
    {
        return aiGacRealtime.select();
    }

    @Override
    public int delAiGacRealtimeValue(Date updateTime)
    {
        return aiGacRealtime.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_gac_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiGacControl(AiProcessControlDTO dto)
    {
        return aiGacControl.select(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessControlDTO> getListAiGacControl(AiProcessControlDTO dto)
    {
        return aiGacControl.select(dto.getRun_time(), dto.getKafka_flag());
    }

    @Override
    public int modAiGacControlKafkaFlag(AiProcessControlDTO dto)
    {
        return aiGacControl.updateKafkaFlag(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public int delAiGacControl(Date date)
    {
        return aiGacControl.delete(date);
    }

    ////////////////////////////////////////////
    // ai_gac_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiGacInit()
    {
        return aiGacInit.select();
    }

    @Override
    public AiProcessInitDTO getAiGacInit(String item)
    {
        return aiGacInit.select(item);
    }

    @Override
    public int modAiGacOperationMode(int operationMode)
    {
        return aiGacInit.updateOperationMode(operationMode);
    }

    @Override
    public int modAiGacInit(String item, float value)
    {
        return aiGacInit.update(item, value);
    }

    @Override
    public int modAiGacInitTi(float value)
    {
        return aiGacInit.updateTi(value);
    }

    ////////////////////////////////////////////
    // ai_gac_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiGacAlarm(AiProcessAlarmDTO dto)
    {
        return aiGacAlarm.insert(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiGacAlarm(Date alarm_time, int kafka_flag)
    {
        return aiGacAlarm.select(alarm_time, kafka_flag);
    }

    @Override
    public int modAiGacAlarmKafkaFlag(AiProcessAlarmDTO dto)
    {
        return aiGacAlarm.updateKafkaFlag(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public int delAiGacAlarm(Date date)
    {
        return aiGacAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // disinfection_realtime
    ////////////////////////////////////////////
    @Override
    public int addDisinfectionRealtimeValue(List<ProcessRealtimeDTO> dtos)
    {
        return disinfectionRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestDisinfectionRealtimeValue(String partitionName)
    {
        return disinfectionRealtime.select(partitionName);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllDisinfectionRealtimeValueFromTime(Date startTime)
    {
        return disinfectionRealtime.select(startTime);
    }

    @Override
    public List<ProcessRealtimeDTO> getDisinfectionRealtimeValueFromTag(String name, Date startTime, Date endTime)
    {
        return disinfectionRealtime.select(name, startTime, endTime);
    }

    @Override
    public ProcessRealtimeDTO getLatestDisinfectionRealtimeValueFromTag(String name)
    {
        return disinfectionRealtime.selectLatest(name);
    }

    @Override
    public void addDisinfectionRealtimePartition(String partitionName, String endTime)
    {
        disinfectionRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delDisinfectionRealtimePartition(String partitionName)
    {
        disinfectionRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_disinfection_realtime
    ////////////////////////////////////////////

    @Override
    public List<AiDisinfectionRealtimeDTO> getAiDisinfectionRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto)
    {
        return aiDisinfectionRealtime.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AiDisinfectionRealtimeDTO getLatestAiDisinfectionRealtimeValue()
    {
        return aiDisinfectionRealtime.select();
    }

    @Override
    public int delAiDisinfectionRealtimeValue(Date updateTime)
    {
        return aiDisinfectionRealtime.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_disinfection_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiPreDisinfectionControl(AiProcessControlDTO dto)
    {
        return aiPreDisinfectionControl.select(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public AiProcessControlDTO getOneAiPeriDisinfectionControl(AiProcessControlDTO dto)
    {
        return aiPeriDisinfectionControl.select(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public AiProcessControlDTO getOneAiPostDisinfectionControl(AiProcessControlDTO dto)
    {
        return aiPostDisinfectionControl.select(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessControlDTO> getListAiPreDisinfectionControl(AiProcessControlDTO dto)
    {
        return aiPreDisinfectionControl.select(dto.getRun_time(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessControlDTO> getListAiPeriDisinfectionControl(AiProcessControlDTO dto)
    {
        return aiPeriDisinfectionControl.select(dto.getRun_time(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessControlDTO> getListAiPostDisinfectionControl(AiProcessControlDTO dto)
    {
        return aiPostDisinfectionControl.select(dto.getRun_time(), dto.getKafka_flag());
    }

    @Override
    public int modAiPreDisinfectionControlKafkaFlag(AiProcessControlDTO dto)
    {
        return aiPreDisinfectionControl.updateKafkaFlag(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public int modAiPeriDisinfectionControlKafkaFlag(AiProcessControlDTO dto)
    {
        return aiPeriDisinfectionControl.updateKafkaFlag(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public int modAiPostDisinfectionControlKafkaFlag(AiProcessControlDTO dto)
    {
        return aiPostDisinfectionControl.updateKafkaFlag(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public int delAiPreDisinfectionControl(Date date)
    {
        return aiPreDisinfectionControl.delete(date);
    }

    @Override
    public int delAiPeriDisinfectionControl(Date date)
    {
        return aiPeriDisinfectionControl.delete(date);
    }

    @Override
    public int delAiPostDisinfectionControl(Date date)
    {
        return aiPostDisinfectionControl.delete(date);
    }

    ////////////////////////////////////////////
    // ai_disinfection_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiDisinfectionInit()
    {
        return aiDisinfectionInit.select();
    }

    @Override
    public AiProcessInitDTO getAiDisinfectionInit(String item)
    {
        return aiDisinfectionInit.select(item);
    }

    @Override
    public int modAiDisinfectionInit(String item, float value)
    {
        return aiDisinfectionInit.update(item, value);
    }

    ////////////////////////////////////////////
    // ai_disinfection_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiDisinfectionAlarm(AiProcessAlarmDTO dto)
    {
        return aiDisinfectionAlarm.insert(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiDisinfectionAlarm(Date alarm_time, int kafka_flag)
    {
        return aiDisinfectionAlarm.select(alarm_time, kafka_flag);
    }

    @Override
    public int modAiDisinfectionAlarmKafkaFlag(AiProcessAlarmDTO dto)
    {
        return aiDisinfectionAlarm.updateKafkaFlag(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public int delAiDisinfectionAlarm(Date date)
    {
        return aiDisinfectionAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // ozone_realtime
    ////////////////////////////////////////////
    @Override
    public int addOzoneRealtimeValue(List<ProcessRealtimeDTO> dtos)
    {
        return ozoneRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestOzoneRealtimeValue(String partitionName)
    {
        return ozoneRealtime.select(partitionName);
    }

    @Override
    public List<ProcessRealtimeDTO> getAllOzoneRealtimeValueFromTime(Date startTime)
    {
        return ozoneRealtime.select(startTime);
    }

    @Override
    public List<ProcessRealtimeDTO> getOzoneRealtimeValueFromTag(String name, Date startTime, Date endTime)
    {
        return ozoneRealtime.select(name, startTime, endTime);
    }

    @Override
    public ProcessRealtimeDTO getLatestOzoneRealtimeValueFromTag(String name)
    {
        return ozoneRealtime.selectLatest(name);
    }

    @Override
    public void addOzoneRealtimePartition(String partitionName, String endTime)
    {
        ozoneRealtime.addPartition(partitionName, endTime);
    }

    @Override
    public void delOzoneRealtimePartition(String partitionName)
    {
        ozoneRealtime.dropPartition(partitionName);
    }

    ////////////////////////////////////////////
    // ai_ozone_realtime
    ////////////////////////////////////////////
    @Override
    public List<AiOzoneRealtimeDTO> getAiOzoneRealtimeValueFromUpdateTime(InterfaceDateSearchDTO dto)
    {
        return aiOzoneRealtime.select(dto.getStart_time(), dto.getEnd_time());
    }

    @Override
    public AiOzoneRealtimeDTO getLatestAiOzoneRealtimeValue()
    {
        return aiOzoneRealtime.select();
    }

    @Override
    public int delAiOzoneRealtimeValue(Date updateTime)
    {
        return aiOzoneRealtime.delete(updateTime);
    }

    ////////////////////////////////////////////
    // ai_ozone_control
    ////////////////////////////////////////////
    @Override
    public AiProcessControlDTO getOneAiOzoneControl(AiProcessControlDTO dto)
    {
        return aiOzoneControl.select(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessControlDTO> getListAiOzoneControl(AiProcessControlDTO dto)
    {
        return aiOzoneControl.select(dto.getRun_time(), dto.getKafka_flag());
    }

    @Override
    public int modAiOzoneControlKafkaFlag(AiProcessControlDTO dto)
    {
        return aiOzoneControl.updateKafkaFlag(dto.getUpdate_time(), dto.getRun_time(), dto.getName(), dto.getKafka_flag());
    }

    @Override
    public int delAiOzoneControl(Date date)
    {
        return aiOzoneControl.delete(date);
    }

    ////////////////////////////////////////////
    // ai_ozone_init
    ////////////////////////////////////////////
    @Override
    public List<AiProcessInitDTO> getAllAiOzoneInit()
    {
        return aiOzoneInit.select();
    }

    @Override
    public AiProcessInitDTO getAiOzoneInit(String item)
    {
        return aiOzoneInit.select(item);
    }

    @Override
    public int modAiOzoneOperationMode(int operationMode)
    {
        return aiOzoneInit.updateOperationMode(operationMode);
    }

    @Override
    public int modAiOzoneInit(String item, float value)
    {
        return aiOzoneInit.update(item, value);
    }

    ////////////////////////////////////////////
    // ai_ozone_alarm
    ////////////////////////////////////////////
    @Override
    public int addAiOzoneAlarm(AiProcessAlarmDTO dto)
    {
        return aiOzoneAlarm.insert(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public List<AiProcessAlarmDTO> getAllAiOzoneAlarm(Date alarm_time, int kafka_flag)
    {
        return aiOzoneAlarm.select(alarm_time, kafka_flag);
    }

    @Override
    public int modAiOzoneAlarmKafkaFlag(AiProcessAlarmDTO dto)
    {
        return aiOzoneAlarm.updateKafkaFlag(dto.getAlarm_id(), dto.getAlarm_time(), dto.getKafka_flag());
    }

    @Override
    public int delAiOzoneAlarm(Date date)
    {
        return aiOzoneAlarm.delete(date);
    }

    ////////////////////////////////////////////
    // ems_realtime
    ////////////////////////////////////////////
    @Override
    public int addEmsRealtimeValue(List<ProcessRealtimeDTO> dtos)
    {
        return emsRealtime.insert(dtos);
    }

    @Override
    public List<ProcessRealtimeDTO> getLatestEmsRealtimeValue()
    {
        return emsRealtime.select("");
    }

    ////////////////////////////////////////////
    // pms_realtime
    ////////////////////////////////////////////
    @Override
    public List<PmsAiDTO> getLatestPmsAiValue()
    {
        return pmsRealtime.selectAi();
    }

    @Override
    public List<PmsScadaDTO> getLatestPmsScadaValue()
    {
        return pmsRealtime.selectScada();
    }
}
