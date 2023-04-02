package org.kwater.aio.controller;

import org.kwater.aio.dto.*;
import org.kwater.aio.resource_dto.*;
import org.kwater.aio.service.AlarmServiceImpl;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
//@EnableSwagger2
@Slf4j
public class ResourcesController
{
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    AlarmServiceImpl alarmService;

    @Autowired
    AlarmInfoList alarmInfoList;

    @Autowired
    GlobalSystemConfig globalSystemConfig;

    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectionRequestTimeout(3 * CommonValue.ONE_SECOND)
            .build();

    // 전체 시스템 정보 조회
    @RequestMapping(value="/resources/info", method = RequestMethod.GET)
    public ResponseEntity<String> getAllResources()
    {
        log.info("Recv getAllResources");

        // Get system information
        List<SystemInfoDTO> systemInfoList = databaseService.getAllSystemInfo();
        log.info("getAllSystemInfo, result:[{}]", systemInfoList.size());

        if(systemInfoList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            List<Map<String, Object>> resultArray = new ArrayList<>();

            for(SystemInfoDTO systemInfoDTO : systemInfoList)
            {
                String strHostname = systemInfoDTO.getHostname();
                Map<String, Object> singleBody = new HashMap<>();
                singleBody.put("systemInfo", systemInfoDTO);

                // Get disk information
                List<DiskInfoDTO> diskInfoList = databaseService.getDiskInfoFromHostname(strHostname);
                if(diskInfoList.size() > 0)
                {
                    singleBody.put("diskInfo", diskInfoList);
                }

                // Get partition information
                List<PartitionInfoDTO> partitionInfoList = databaseService.getPartitionInfoFromHostname(strHostname);
                if(partitionInfoList.size() > 0)
                {
                    singleBody.put("partitionInfo", partitionInfoList);
                }

                // Get network interface information
                List<InterfaceInfoDTO> interfaceInfoList = databaseService.getInterfaceInfoFromHostname(strHostname);
                if(interfaceInfoList.size() > 0)
                {
                    singleBody.put("interfaceInfo", interfaceInfoList);
                }

                resultArray.add(singleBody);
            }
            responseBody.put("resources", resultArray);

            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = "";
            try
            {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty system_info\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 특정 시스템 정보 조회
    @RequestMapping(value = "/resources/info/{hostname}", method = RequestMethod.GET)
    public ResponseEntity<String> getResources(@PathVariable String hostname)
    {
        log.info("Recv getResources, Hostname:[{}]", hostname);

        // Get system information
        SystemInfoDTO systemInfoDTO = databaseService.getSystemInfoFromHostname(hostname);
        log.info("getSystemInfoFromHostname:[{}], result:[{}]", hostname, systemInfoDTO != null ? 1 : 0);

        if(systemInfoDTO != null)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("systemInfo", systemInfoDTO);

            // Get disk information
            List<DiskInfoDTO> diskInfoList = databaseService.getDiskInfoFromHostname(hostname);
            if(diskInfoList.size() > 0)
            {
                responseBody.put("diskInfo", diskInfoList);
            }

            // Get partition information
            List<PartitionInfoDTO> partitionInfoList = databaseService.getPartitionInfoFromHostname(hostname);
            if(partitionInfoList.size() > 0)
            {
                responseBody.put("partitionInfo", partitionInfoList);
            }

            // Get network interface information
            List<InterfaceInfoDTO> interfaceInfoList = databaseService.getInterfaceInfoFromHostname(hostname);
            if(interfaceInfoList.size() > 0)
            {
                responseBody.put("interfaceInfo", interfaceInfoList);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = "";
            try
            {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
        }
    }

    // 시스템 정보 수정 - 관리자 전용
    @RequestMapping(value = "/resources/info/{hostname}", method = RequestMethod.PUT)
    public ResponseEntity<String> putResourcesInfo(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable String hostname,
            @RequestBody SystemNameDTO systemName)
    {
        log.info("Recv putResourcesInfo[{}]", hostname);
        if(systemName == null)
        {
            String strErrorBody = "{\"reason\":\"name must not null.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            // 관리자 권한인지 확인
            nAuthority = accessTokenDTO.getAuthority();
            if(nAuthority == CommonValue.USER)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            else if(nAuthority == CommonValue.NONE_AUTHORITY)
            {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            else
            {
                bFindToken = true;
            }
        }

        if(bFindToken == true)
        {
            // 시스템 정보 업데이트
            int nResult = databaseService.modSystemInfoName(hostname, systemName.getName());
            log.info("modSystemInfoName, result:[{}]", nResult);
            if(nResult > 0)
            {
                return new ResponseEntity<>("", HttpStatus.OK);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    // 시스템 정보 수정 - 리소스 모니터 전용
    @RequestMapping(value = "/resources/{hostname}", method = RequestMethod.PUT)
    public ResponseEntity<String> putResources(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable String hostname,
            @RequestBody ResourceMonitorDTO resourceMonitorDTO)
    {
        // 리소스 모니터 Access Token 인지 확인
        if(propertiesAuthentication.getResourceMonitorToken().equalsIgnoreCase(token) == false)
        {
            log.error("Invalid {}'s X-ACCESS-TOKEN:[{}]", hostname, token);
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
        log.info("Recv putResources[{}]", hostname);
        //log.info("{}", resourceMonitorDTO);

        String strCPUAlarmCode, strMemoryAlarmCode, strDiskAlarmCode, strPasswordAlarmCode;
        // Set Alarm Code(CPU, Memory, DISK, Server Password)
        if(hostname.equalsIgnoreCase(CommonValue.ANALYSIS1_HOSTNAME) == true)
        {
            strCPUAlarmCode = CommonValue.ALARM_CODE_CPU_UPPER1;
            strMemoryAlarmCode = CommonValue.ALARM_CODE_MEMORY_UPPER1;
            strDiskAlarmCode = CommonValue.ALARM_CODE_DISK_UPPER1;
            strPasswordAlarmCode = CommonValue.ALARM_CODE_SERVER_PASSWORD1;
        }
        else if(hostname.equalsIgnoreCase(CommonValue.ANALYSIS2_HOSTNAME) == true)
        {
            strCPUAlarmCode = CommonValue.ALARM_CODE_CPU_UPPER2;
            strMemoryAlarmCode = CommonValue.ALARM_CODE_MEMORY_UPPER2;
            strDiskAlarmCode = CommonValue.ALARM_CODE_DISK_UPPER2;
            strPasswordAlarmCode = CommonValue.ALARM_CODE_SERVER_PASSWORD2;
        }
        else if(hostname.equalsIgnoreCase(CommonValue.ANALYSIS3_HOSTNAME) == true)
        {
            strCPUAlarmCode = CommonValue.ALARM_CODE_CPU_UPPER3;
            strMemoryAlarmCode = CommonValue.ALARM_CODE_MEMORY_UPPER3;
            strDiskAlarmCode = CommonValue.ALARM_CODE_DISK_UPPER3;
            strPasswordAlarmCode = CommonValue.ALARM_CODE_SERVER_PASSWORD3;
        }
        else if(hostname.equalsIgnoreCase(CommonValue.ANALYSIS4_HOSTNAME) == true)
        {
            strCPUAlarmCode = CommonValue.ALARM_CODE_CPU_UPPER4;
            strMemoryAlarmCode = CommonValue.ALARM_CODE_MEMORY_UPPER4;
            strDiskAlarmCode = CommonValue.ALARM_CODE_DISK_UPPER4;
            strPasswordAlarmCode = CommonValue.ALARM_CODE_SERVER_PASSWORD4;
        }
        else if(hostname.equalsIgnoreCase(CommonValue.ANALYSIS5_HOSTNAME) == true)
        {
            strCPUAlarmCode = CommonValue.ALARM_CODE_CPU_UPPER5;
            strMemoryAlarmCode = CommonValue.ALARM_CODE_MEMORY_UPPER5;
            strDiskAlarmCode = CommonValue.ALARM_CODE_DISK_UPPER5;
            strPasswordAlarmCode = CommonValue.ALARM_CODE_SERVER_PASSWORD5;
        }
        else
        {
            strCPUAlarmCode = "";
            strMemoryAlarmCode = "";
            strDiskAlarmCode = "";
            strPasswordAlarmCode = "";
        }

        // Check Disk Info
        // 디스크 정보를 조회하여 존재하는 것이면 새로운 디스크 정보 생성
        List<DiskInfoDTO> diskInfoList = databaseService.getDiskInfoFromHostname(hostname);
        for(DiskDTO disk : resourceMonitorDTO.getStorage().getDiskList())
        {
            boolean bFind = false;
            for(DiskInfoDTO diskInfo : diskInfoList)
            {
                if(disk.getModel().equalsIgnoreCase(diskInfo.getModel()) == true && disk.getSize() == diskInfo.getSize())
                {
                    bFind = true;
                    break;
                }
            }

            if(bFind == false)
            {
                DiskInfoDTO diskInfoDTO = new DiskInfoDTO();
                diskInfoDTO.setHostname(hostname);
                diskInfoDTO.setModel(disk.getModel());
                diskInfoDTO.setSize(disk.getSize());

                log.info("Not exist disk_info[{}], insert new record", hostname);
                databaseService.addDiskInfo(diskInfoDTO);
            }
        }

        // Update Partition Info
        long lTotalSize = 0L;
        long lUsableSize = 0L;
        for(PartitionDTO partition : resourceMonitorDTO.getStorage().getPartitionList())
        {
            if(partition.getTotalSize() > 0)
            {
                // 파티션 정보 업데이트
                lTotalSize += partition.getTotalSize();
                lUsableSize += partition.getUsableSize();

                PartitionInfoDTO partitionInfoDTO = new PartitionInfoDTO();
                partitionInfoDTO.setHostname(hostname);
                partitionInfoDTO.setName(partition.getName());
                partitionInfoDTO.setTotal_size(partition.getTotalSize());
                partitionInfoDTO.setUsable_size(partition.getUsableSize());

                if(databaseService.modPartitionInfo(partitionInfoDTO) <= 0)
                {
                    log.info("Not exist partition_info[{}], insert new record", hostname);
                    databaseService.addPartitionInfo(partitionInfoDTO);
                }

                // Insert system_monitoring
                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHostname(hostname);
                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_PARTITON_USED);
                systemMonitoringDTO.setName(partition.getName());
                String strUsed = String.format("%.2f", partition.getUsed());
                systemMonitoringDTO.setValue(strUsed);
                systemMonitoringDTO.setUpdate_time(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);
            }
        }
        if(lTotalSize > 0)
        {
            // 전체 파티션 사용량을 계산하여 알람 발보 기준일 경우 알람 생성
            double fUsed = ((double)(lTotalSize - lUsableSize) / lTotalSize) * 100;
            String strUsed = String.format("%.2f", (float)fUsed);
            log.info("host:[{}], disk used:[{}]", hostname, strUsed);
            if(alarmInfoList.isAlarm(strDiskAlarmCode, (float)fUsed) == true)
            {
                // Insert alarm_notify & SCADA send
//                String strUsed = String.format("%.2f", (float)fUsed);
                alarmService.alarmNotify(strDiskAlarmCode, hostname, strUsed, false);
            }
        }

        // Update Interface Info
        for(InterfaceDTO interfaceDTO : resourceMonitorDTO.getInterfaces())
        {
            if(interfaceDTO.getName().indexOf("docker") > 0)
            {
                // Except docker address
                continue;
            }

            // 네트워크 인터페이스 정보 업데이트
            InterfaceInfoDTO interfaceInfoDTO = new InterfaceInfoDTO();
            interfaceInfoDTO.setHostname(hostname);
            interfaceInfoDTO.setName(interfaceDTO.getName());
            interfaceInfoDTO.setDisplay_name(interfaceDTO.getDisplayName());
            interfaceInfoDTO.setIpv4(interfaceDTO.getIpv4());
            interfaceInfoDTO.setMac(interfaceDTO.getMac());

            if(databaseService.modInterfaceInfo(interfaceInfoDTO) <= 0)
            {
                log.info("Not exist interface_info[{}], insert new record", hostname);
                databaseService.addInterfaceInfo(interfaceInfoDTO);
            }

            // Insert system_monitoring(Sent throughput)
            SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHostname(hostname);
            systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_SENT_THROUGHPUT);
            systemMonitoringDTO.setName(interfaceDTO.getName());
            systemMonitoringDTO.setValue(Long.toString(interfaceDTO.getSentBytes()));
            systemMonitoringDTO.setUpdate_time(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            // Insert system_monitoring(Recv throughput)
            systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHostname(hostname);
            systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_RECV_THROUGHPUT);
            systemMonitoringDTO.setName(interfaceDTO.getName());
            systemMonitoringDTO.setValue(Long.toString(interfaceDTO.getRecvBytes()));
            systemMonitoringDTO.setUpdate_time(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);
        }

        // Update System Info
        SystemInfoDTO systemInfoDTO = new SystemInfoDTO();
        systemInfoDTO.setHostname(hostname);

        OperatingSystemDTO operatingSystemDTO = resourceMonitorDTO.getOperatingSystem();
        systemInfoDTO.setOs(operatingSystemDTO.getName());

        ComputerSystemDTO computerSystemDTO = resourceMonitorDTO.getComputerSystem();
        systemInfoDTO.setModel(computerSystemDTO.getModel());

        ProcessorDTO processorDTO = resourceMonitorDTO.getProcessor();
        systemInfoDTO.setProcessor_name(processorDTO.getName());
        systemInfoDTO.setPackage_count(processorDTO.getPackageCount());
        systemInfoDTO.setCore_count(processorDTO.getPhysicalCount());
        systemInfoDTO.setLogical_count(processorDTO.getLogicalCount());
        // 리소스 모니터를 통해 전달받은 주파수 값이 측정 불가 일 경우
        // 프로세서 명에서 최대 주파수 값 설정
        if(processorDTO.getMaxFrequency() == -1)
        {
            String strName = processorDTO.getName();
            String strFrequency = strName.substring(strName.indexOf("@")+1, strName.indexOf("GHz"));
            log.info("Modify MaxFrequency -1 to [{}]", strFrequency);
            float fFrequency = Float.parseFloat(strFrequency);
            long lFrequency = (long)(fFrequency * 1000000000L);
            processorDTO.setMaxFrequency(lFrequency);
        }
        systemInfoDTO.setMax_frequency(processorDTO.getMaxFrequency());

        MemoryDTO memoryDTO = resourceMonitorDTO.getMemory();
        systemInfoDTO.setTotal_memory(memoryDTO.getTotalMemory());
        systemInfoDTO.setAvailable_memory(memoryDTO.getAvailableMemory());

        // If NoSQL set db storage to 0
        DbStorageDTO dbStorageDTO = new DbStorageDTO();
        if(resourceMonitorDTO.getDbStorage() == null)
        {
            dbStorageDTO.setUsed(0);
            dbStorageDTO.setFree(0);
        }
        else
        {
            dbStorageDTO = resourceMonitorDTO.getDbStorage();
        }

        systemInfoDTO.setDb_used(dbStorageDTO.getUsed());
        systemInfoDTO.setDb_free(dbStorageDTO.getFree());
        systemInfoDTO.setUpdate_time(new Date());

        // update system information
        if(databaseService.modSystemInfo(systemInfoDTO) <= 0)
        {
            log.info("Not exist system_info[{}], insert new record", hostname);
            systemInfoDTO.setName(hostname);
            databaseService.addSystemInfo(systemInfoDTO);
        }

        //////////////////////////////////////////
        // Insert system_monitoring & Check Alarm
        //////////////////////////////////////////
        String strUsed;

        // 1. Process used
        SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
        systemMonitoringDTO.setHostname(hostname);
        systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_PROCESSOR_USED);
        systemMonitoringDTO.setName(processorDTO.getName());
        strUsed = String.format("%.2f", processorDTO.getUsed());
        systemMonitoringDTO.setValue(strUsed);
        systemMonitoringDTO.setUpdate_time(new Date());
        databaseService.addSystemMonitoring(systemMonitoringDTO);

        if(alarmInfoList.isAlarm(strCPUAlarmCode, processorDTO.getUsed()) == true)
        {
            // Insert alarm_notify & SCADA send
            alarmService.alarmNotify(strCPUAlarmCode, hostname, strUsed, false);
        }

        // 2. Memory used
        systemMonitoringDTO = new SystemMonitoringDTO();
        systemMonitoringDTO.setHostname(hostname);
        systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_MEMORY_USED);
        systemMonitoringDTO.setName("Memory");
        strUsed = String.format("%.2f", memoryDTO.getUsed());
        systemMonitoringDTO.setValue(strUsed);
        databaseService.addSystemMonitoring(systemMonitoringDTO);

        if(alarmInfoList.isAlarm(strMemoryAlarmCode, memoryDTO.getUsed()) == true)
        {
            // Insert alarm_notify & SCADA send
            alarmService.alarmNotify(strMemoryAlarmCode, hostname, strUsed, false);
        }

        // 3. Partition used and Network Throughput are already add.

        // 4. Password expiration check
        if(resourceMonitorDTO.getPasswd() != null)
        {
            // Get Passwd string & tokenize
            String strPasswd = resourceMonitorDTO.getPasswd();
            log.info("Passwd: [{}]", strPasswd);
            StringTokenizer strToken = new StringTokenizer(strPasswd, " ");
            List<String> passwdList = new ArrayList<>();
            while(strToken.hasMoreTokens() == true)
            {
                passwdList.add(strToken.nextToken().trim());
            }

            if(passwdList.size() != CommonValue.PASSWD_SIZE)
            {
                // Check Passwd string size
                log.error("Invalid Passwd data, [{}]", strPasswd);
            }
            else if (Conversion.isNumber(passwdList.get(CommonValue.PASSWD_MAXIMUM_COUNT)) == false)
            {
                // Maximum Count type check
                log.error("Maximum Count must number, [{}]", passwdList.get(CommonValue.PASSWD_MAXIMUM_COUNT));
            }
            else
            {
                String strDate = passwdList.get(CommonValue.PASSWD_LATEST_DATE);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                try
                {
                    int nMaximumCount = Integer.parseInt(passwdList.get(CommonValue.PASSWD_MAXIMUM_COUNT));
                    Date latestChangeDate = simpleDateFormat.parse(strDate);
                    Calendar expiredCalendar = Calendar.getInstance();
                    expiredCalendar.setTime(latestChangeDate);
                    expiredCalendar.add(Calendar.DAY_OF_YEAR, nMaximumCount);
                    // To set an alarm at 9
                    expiredCalendar.add(Calendar.HOUR, 9);
                    Calendar currentCalendar = Calendar.getInstance();

                    long lDifferentMilli = expiredCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
                    long lDifferentDay = lDifferentMilli / CommonValue.ONE_DAY;
                    log.info("password change remain [{}] days", lDifferentDay);

                    // 비밀번호 만료 시기가 도래(default 10일 전)하면 알람 발보
                    if(alarmInfoList.isAlarm(strPasswordAlarmCode, lDifferentDay) == true)
                    {
                        // Insert alarm_notify & SCADA Send
                        String strValue = Long.toString(lDifferentDay);
                        alarmService.alarmNotify(strPasswordAlarmCode, hostname, strValue, true);
                    }
                }
                catch (ParseException e)
                {
                    log.error("Invalid Date format, [{}]", strDate);
                }
            }
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 리소스 모니터링 정보 조회
    @RequestMapping(value = "/resources/monitoring", method = RequestMethod.GET)
    public ResponseEntity<String> getAllMonitoring()
    {
        log.info("Recv getAllMonitoring");

        // 전체 시스템 리소스 모니터링 정보 조회
        List<SystemMonitoringDTO> systemMonitoringList = databaseService.getAllSystemMonitoring();
        log.info("getAllSystemMonitoring, result:[{}]", systemMonitoringList.size());

        if(systemMonitoringList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("monitoring", systemMonitoringList);

            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = "";
            try
            {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty system_monitoring\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 특정 시스템 리소스 모니터링 정보 조회
    @RequestMapping(value = "/resources/monitoring/{hostname}", method = RequestMethod.GET)
    public ResponseEntity<String> getMonitoring(@PathVariable String hostname)
    {
        log.info("Recv getMonitoring:[{}]", hostname);

        // 특정 시스템 리소스 모니터링 정보 조회
        List<SystemMonitoringDTO> systemMonitoringList = databaseService.getSystemMonitoringFromHostname(hostname);
        log.info("getSystemMonitoringFromHostname:[{}], result:[{}]", hostname, systemMonitoringList.size());

        if(systemMonitoringList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("monitoring", systemMonitoringList);

            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = "";
            try
            {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty system_monitoring\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 전체 시스템 최근 리소스 모니터링 정보 조회
    @RequestMapping(value = "/resources/monitoring/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestMonitoring()
    {
        log.info("Recv getLatestMonitoring");

        // Get start_time(5minutes ago)
        // 각 항목 별 최근 5분 간의 데이터를 받기 위해 startTime 선언
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        Date startTime = calendar.getTime();

        // 리소스 모니터링 정보 조회
        List<SystemMonitoringDTO> systemMonitoringList = databaseService.getLatestSystemMonitoring(startTime);
        log.info("getLatestSystemMonitoring, result:[{}]", systemMonitoringList.size());

        if(systemMonitoringList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("monitoring", systemMonitoringList);

            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = "";
            try
            {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty system_monitoring\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
