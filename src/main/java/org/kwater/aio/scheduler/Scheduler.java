package org.kwater.aio.scheduler;

import org.kwater.aio.dto.*;
import org.kwater.aio.service.AlarmServiceImpl;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class Scheduler
{
    @Autowired
    PropertiesDelegate propertiesDelegate;

    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    PropertiesVisualizeCheck propertiesVisualizeCheck;

    @Autowired
    PropertiesSystemCheck propertiesSystemCheck;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    AlarmServiceImpl alarmService;

    @Autowired
    AlarmInfoList alarmInfoList;

    @Autowired
    TagDescriptionList tagDescriptionList;

    @Autowired
    GlobalSystemConfig globalSystemConfig;

    @Autowired
    ProcessCodeList processCodeList;

    @Value("${server.port}")
    int nServerPort;

    //private int nVisualizeHealthCheck = CommonValue.HEALTH_CHECK_NORMAL;

    @Autowired
    VisualizeHealthStatus visualizeHealthStatus;
//
//    @Autowired
//    //kafkaProducer //kafkaProducer;

    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectionRequestTimeout(3 * CommonValue.ONE_SECOND)
            .build();

    // Partitioning Table 관리를 위한 스케쥴러(매일 23시 50분 실행)
    @Scheduled(cron = "0 50 23 * * ?")
    public void checkDatabasePartition()
    {
        log.info("Check Database Partition...Thread[{}]", Thread.currentThread().getName());

        // Set default calendar(tomorrow)
        Calendar calendarAdd = Calendar.getInstance();
        calendarAdd.set(Calendar.MINUTE, 0);
        calendarAdd.set(Calendar.SECOND, 0);
        calendarAdd.set(Calendar.HOUR_OF_DAY, 0);
        calendarAdd.add(Calendar.HOUR_OF_DAY, 24);

        // Set partition name
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strAddPartitionName = partitionNameFormat.format(calendarAdd.getTime());

        // Set end time
        calendarAdd.add(Calendar.HOUR_OF_DAY, 24);
        SimpleDateFormat endTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strEndTime = endTimeFormat.format(calendarAdd.getTime());

        // Add Realtime table partition
        log.info("Add Database Partition...name:[{}]", strAddPartitionName);
        databaseService.addReceivingRealtimePartition(strAddPartitionName, strEndTime);
        databaseService.addCoagulantRealtimePartition(strAddPartitionName, strEndTime);
        databaseService.addMixingRealtimePartition(strAddPartitionName, strEndTime);
        databaseService.addSedimentationRealtimePartition(strAddPartitionName, strEndTime);
        databaseService.addFilterRealtimePartition(strAddPartitionName, strEndTime);
        databaseService.addGacRealtimePartition(strAddPartitionName, strEndTime);
        databaseService.addDisinfectionRealtimePartition(strAddPartitionName, strEndTime);
        databaseService.addOzoneRealtimePartition(strAddPartitionName, strEndTime);

        // Delete Realtime table partition(7 days)
        Calendar calendarDel = Calendar.getInstance();
        calendarDel.set(Calendar.MINUTE, 0);
        calendarDel.set(Calendar.SECOND, 0);
        calendarDel.set(Calendar.HOUR_OF_DAY, 0);
        calendarDel.add(Calendar.DAY_OF_MONTH, -7);
        String strDelPartitionName = partitionNameFormat.format(calendarDel.getTime());
        log.info("Del Database Partition...name:[{}]", strDelPartitionName);
        databaseService.delReceivingRealtimePartition(strDelPartitionName);
        databaseService.delCoagulantRealtimePartition(strDelPartitionName);
        databaseService.delMixingRealtimePartition(strDelPartitionName);
        databaseService.delSedimentationRealtimePartition(strDelPartitionName);
        databaseService.delFilterRealtimePartition(strDelPartitionName);
        databaseService.delGacRealtimePartition(strDelPartitionName);
        databaseService.delDisinfectionRealtimePartition(strDelPartitionName);
        databaseService.delOzoneRealtimePartition(strDelPartitionName);
    }

    // 내부 변수 관리를 위한 스케쥴러(default: 매 10분)
    @Scheduled(fixedDelayString = "${properties.check-process.period}")
    public void checkProcess()
    {
        log.info("Check Process Start...Thread[{}], version:[{}]",
                Thread.currentThread().getName(), propertiesDelegate.getVersion());

        // Get all alarm_info
        alarmInfoList.setAlarmInfoList(databaseService.getAlarmInfo());
        log.info("Get exist[{}] alarm_info", alarmInfoList.getSize());

        // Get system_config
        globalSystemConfig.setSystemConfig(databaseService.getSystemConfig());
        log.info("Get system_config:[{}]", globalSystemConfig);

        // Get all tag_description
        tagDescriptionList.setTagDescriptionList(databaseService.getAllTagDescription());
        log.info("Get exist[{}] tag_description", tagDescriptionList.getSize());

        // Get all process_code
        processCodeList.setProcessCodeList(databaseService.getAllProcessCode());
        log.info("get Process: [{}]", processCodeList.getSize());

        Calendar calendar = Calendar.getInstance();
        int nDeleteCount = databaseService.delToken(calendar.getTime());
        List<AccessTokenDTO> accessTokenList = databaseService.getAllTokens();
        log.info("Delete Token:[{}], Remain:[{}]", nDeleteCount, accessTokenList.size());

        // Database Check(send to internal)
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/database";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        HttpSend.send(httpGet);
    }

    // kafka에 실시간 AI 예측값 전달을 위해 /internal/sensors API 호출
    @Scheduled(fixedDelayString = "${properties.scada-check.period}", initialDelay = 3000)
    public void scadaCheck()
    {
        log.info("sendScada...Thread:[{}]", Thread.currentThread().getName());

        // sensors
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/sensors";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        HttpSend.send(httpGet);
    }

    // 분석 서버 Check를 위해 /internal/analysis API 호출
    @Scheduled(fixedDelayString = "${properties.analysis-check.period}", initialDelay = 3000)
    public void analysisCheck()
    {
        log.info("analysis HealthCheck...Thread[{}]", Thread.currentThread().getName());

        // Analysis Health Check(send to internal)
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/analysis";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        HttpSend.send(httpGet);
    }

    // 데이터 수집기 상태 확인을 위한 /internal/daq API 호출
    @Scheduled(fixedDelayString = "${properties.daq_check.period}", initialDelay = 3000)
    public void daqCheck()
    {
        log.info("daq HealthCheck...Thread[{}]", Thread.currentThread().getName());

        // DAQ Health Check(send to internal)
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/daq";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        HttpSend.send(httpGet);
    }

    // 시각화 시스템 API 이중화 상태 확인
    @Scheduled(fixedDelayString = "${properties.visualize-check.period}", initialDelay = 3000)
    public void visualizeCheck()
    {
        String strAddress = propertiesVisualizeCheck.getAddress();
        int nPort = propertiesVisualizeCheck.getPort();
        log.info("visualize HealthCheck address:[{}]...Thread[{}]", strAddress, Thread.currentThread().getName());
        if(Conversion.isValidInet4Address(strAddress) == true)
        {
            // Send Health Check Message
            // 상대방 API Server에 /hostname API 호출
            String strUri = "http://" + strAddress + ":" + nServerPort + "/hostname";
            HttpGet httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);
            httpGet.setHeader("Content-Type", "application/json");
            HttpResponse response = HttpSend.send(httpGet);
            if(response != null)
            {
                int nStatus = response.getStatusLine().getStatusCode();

                if(nStatus == HttpStatus.SC_OK)
                {
                    // 정상 상태인 경우 시스템 모니터링에 정상 상태 등록
                    visualizeHealthStatus.setStatus(CommonValue.HEALTH_CHECK_NORMAL);

                    List<InterfaceInfoDTO> interfaceInfoList = databaseService.getInterfaceInfoFromAddress(strAddress);
                    if(interfaceInfoList.size() == 0)
                    {
                        interfaceInfoList = databaseService.getInterfaceInfoFromAddress(propertiesDelegate.getAddress());
                    }

                    if(interfaceInfoList.size() > 0)
                    {
                        InterfaceInfoDTO interfaceInfo = interfaceInfoList.get(0);

                        log.info("Health Check Response:[{}][{}]", nStatus, interfaceInfo.getHostname());

                        // Insert system_monitoring
                        SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                        systemMonitoringDTO.setHostname(interfaceInfo.getHostname());
                        systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_VISUALIZE_API);
                        systemMonitoringDTO.setName(interfaceInfo.getHostname());
                        systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_ON);
                        systemMonitoringDTO.setUpdate_time(new Date());
                        databaseService.addSystemMonitoring(systemMonitoringDTO);
                    }
                }
                else
                {
                    // 잘못된 응답을 수신한 경우 Fail 상태로 변경
                    log.info("Health Check Response:[{}]", nStatus);
                    visualizeHealthStatus.setFailStatus();
                }
            }
            else
            {
                // 응답이 없으면 Fail 상태로 변경
                log.error("Health Check [{}:{}] Error...", strAddress, nServerPort);
                visualizeHealthStatus.setFailStatus();
            }

            // 상대방 시각화 시스템 API가 정상 상태가 아닐 경우
            if(visualizeHealthStatus.getStatus() > CommonValue.HEALTH_CHECK_NORMAL)
            {
                List<InterfaceInfoDTO> interfaceInfoList = databaseService.getInterfaceInfoFromAddress(strAddress);
                if(interfaceInfoList.size() > 0)
                {
                    InterfaceInfoDTO interfaceInfoDTO = interfaceInfoList.get(0);

                    // Insert System Monitoring(another visualize API)
                    SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                    systemMonitoringDTO.setHostname(interfaceInfoDTO.getHostname());
                    systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_VISUALIZE_API);
                    systemMonitoringDTO.setName(interfaceInfoDTO.getHostname());
                    systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_OFF);
                    systemMonitoringDTO.setUpdate_time(new Date());
                    databaseService.addSystemMonitoring(systemMonitoringDTO);

                    // 알람 상태인 경우 알람 발보
                    if(visualizeHealthStatus.getStatus() == CommonValue.HEALTH_CHECK_ALARM)
                    {
                        String strHostname = interfaceInfoDTO.getHostname();
                        String strAlarmCode;

                        if(strHostname.equalsIgnoreCase(CommonValue.ANALYSIS1_HOSTNAME) == true)
                        {
                            strAlarmCode = CommonValue.ALARM_CODE_VISUALIZE_API_OFF1;
                        }
                        else
                        {
                            strAlarmCode = CommonValue.ALARM_CODE_VISUALIZE_API_OFF2;
                        }
                        // Insert alarm_notify & SCADA send
                        alarmService.alarmNotify(
                                strAlarmCode,
                                strHostname,
                                CommonValue.ALARM_VALUE_OFF,
                                true);

                        // Send System Shutdown Request
//                        strUri = "http://" + strAddress + ":" + nPort + "/shutdown";
//                        HttpPost httpPost = new HttpPost(strUri);
//                        httpPost.setConfig(requestConfig);
//                        httpPost.setHeader("Content-Type", "application/json");
//                        HttpSend.send(httpPost);
                    }
                }
                else
                {
                    log.error("Unknown hostname:[{}]", strAddress);
                }

                // Insert system_monitoring(myself)
                // 상대방 시각화 시스템 API가 정상 상태가 아닌 경우
                // 상대방이 자신의 시각화 시스템 API 상태를 업데이트하지 못하기 때문에
                // 자기 자신의 상태를 업데이트
                try
                {
                    SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                    systemMonitoringDTO.setHostname(InetAddress.getLocalHost().getHostName());
                    systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_VISUALIZE_API);
                    systemMonitoringDTO.setName(InetAddress.getLocalHost().getHostName());
                    systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_ON);
                    systemMonitoringDTO.setUpdate_time(new Date());
                    databaseService.addSystemMonitoring(systemMonitoringDTO);
                }
                catch(UnknownHostException e)
                {
                    log.error("Unknown myself host");
                }
            }
        }
        else
        {
            log.error("Invalid HealthCheck Address:[{}]", strAddress);
        }
    }

    // 전체 시스템 상태 체그
    @Scheduled(fixedDelayString = "${properties.system-check.period}", initialDelay= 3000)
    public void systemCheck()
    {
        log.info("systemCheck start...Thread[{}]", Thread.currentThread().getName());
        // 시스템 정보 조회
        List<SystemInfoDTO> systemInfoList = databaseService.getAllSystemInfo();

        // 상태 체크 기준 시간 조회(Default : 5분)
        long different = propertiesSystemCheck.getReference();
        Date currentDate = new Date();

        if(systemInfoList.size() > 0)
        {
            for(SystemInfoDTO systemInfoDTO : systemInfoList)
            {
                // Check system_info update_time
                String strHostname = systemInfoDTO.getHostname();
                String strAlarmCode;

                if(strHostname.equalsIgnoreCase(CommonValue.ANALYSIS1_HOSTNAME) == true)
                {
                    strAlarmCode = CommonValue.ALARM_CODE_SYSTEM_OFF1;
                }
                else if(strHostname.equalsIgnoreCase(CommonValue.ANALYSIS2_HOSTNAME) == true)
                {
                    strAlarmCode = CommonValue.ALARM_CODE_SYSTEM_OFF2;
                }
                else if(strHostname.equalsIgnoreCase(CommonValue.ANALYSIS3_HOSTNAME) == true)
                {
                    strAlarmCode = CommonValue.ALARM_CODE_SYSTEM_OFF3;
                }
                else if(strHostname.equalsIgnoreCase(CommonValue.ANALYSIS4_HOSTNAME) == true)
                {
                    strAlarmCode = CommonValue.ALARM_CODE_SYSTEM_OFF4;
                }
                else if(strHostname.equalsIgnoreCase(CommonValue.ANALYSIS5_HOSTNAME) == true)
                {
                    strAlarmCode = CommonValue.ALARM_CODE_SYSTEM_OFF5;
                }
                else
                {
                    log.error("systemCheck(), unknown hostname:[{}]", strHostname);
                    continue;
                }

                // 시스템 정보의 업데이트 시간이 기준 시간을 초과했을 경우 알람 발보
                if(currentDate.getTime() - systemInfoDTO.getUpdate_time().getTime() > different)
                {
                    // Insert alarm_notify & SCADA send
                    alarmService.alarmNotify(
                            strAlarmCode,
                            strHostname,
                            CommonValue.ALARM_VALUE_OFF,
                            true);
                }
            }
        }
        else
        {
            log.error("Has no system info");
        }
    }

    // 공정별 AI 제어값을 Kafka로 전송하기 위한 /internal/control API 호출
    @Scheduled(fixedDelayString = "${properties.control-check.period}", initialDelay= 3000)
    public void controlCheck()
    {
        log.info("controlCheck start...Thread[{}]", Thread.currentThread().getName());

        // Control Check(send to internal)
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/control";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        HttpSend.send(httpGet);
    }

    // 통합 운영 시스템 알람을 Kafka로 전송하기 위한 /internal/alarm API 호출
    @Scheduled(fixedDelayString = "${properties.alarm-check.period}", initialDelay = 5000)
    public void alarmCheck()
    {
        log.info("alarmCheck start...Thread[{}]", Thread.currentThread().getName());

        // Alarm Check(send to internal)
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/alarm";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        HttpSend.send(httpGet);
    }

    // 알고리즘 상태 확인을 위한 /internal/algorithm API 호출
    @Scheduled(fixedDelayString = "${properties.algorithm-check.period}", initialDelay = 7000)
    public void algorithmCheck()
    {
        log.info("algorithmCheck Start...Thread[{}]", Thread.currentThread().getName());

        // Algorithm Check(send to internal)
        String strUri = "http://" + propertiesDelegate.getAddress() + ":" + nServerPort + "/internal/algorithm";
        HttpGet httpGet = new HttpGet(strUri);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("X-ACCESS-TOKEN", propertiesAuthentication.getInternalToken());
        HttpSend.send(httpGet);
    }
}
