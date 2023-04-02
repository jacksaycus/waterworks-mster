package org.kwater.aio.controller;

import org.kwater.aio.ai_dto.AiGacRealtimeDTO;
import org.kwater.aio.ai_dto.AiProcessInitDTO;
import org.kwater.aio.ai_dto.JsonDSeriesFloat;
import org.kwater.aio.ai_dto.JsonESeriesFloat;
import org.kwater.aio.dto.*;
//import org.kwater.aio.kafka.//kafkaProducer;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.CommonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
//@EnableSwagger2
@Slf4j
public class GacController
{
    @Autowired
    DatabaseServiceImpl databaseService;

//    @Autowired
//    //kafkaProducer //kafkaProducer;

    // GAC 여과 공정 최근 데이터 조회
    @RequestMapping(value = "/gac/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestGac()
    {
        log.info("Recv getLatestGac");

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());

        // get ai_gac_init(i_operation_mode)
        AiProcessInitDTO aiGacInit = databaseService.getAiGacInit(CommonValue.I_OPERATION_MODE);
        log.info("getAiGacInit(operation_mode), result:[{}]", aiGacInit != null ? 1 : 0);

        // get ai_gac_init(i_location_ti_set_max)
        AiProcessInitDTO aiGacTi = databaseService.getAiGacInit(CommonValue.I_LOCATION_TI_SET_MAX);
        log.info("getAiGacInit(i_location_ti_set_max), result:[{]]", aiGacTi != null ? 1 : 0);

        // get ai_gac_realtime
        AiGacRealtimeDTO aiGacRealtime = databaseService.getLatestAiGacRealtimeValue();
        log.info("getLatestAiGacRealtimeValue, result:[{}]", aiGacRealtime != null ? 1 : 0);

        // get gac_realtime
        List<ProcessRealtimeDTO> gacRealtime = databaseService.getLatestGacRealtimeValue(strPartitionName);
        log.info("getLatestGacRealtimeValue, result:[{}]", gacRealtime.size());

        // get tag_manage(gac)
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_GAC);
        log.info("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_GAC, tagManageList.size());

        // get location number(지 번호)
        TagManageRangeDTO gacRange = databaseService.getTagManageRange(CommonValue.PROCESS_GAC);
        log.info("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_GAC, gacRange != null ? 1 : 0);

        int nLocationMin = 0, nLocationMax = 0;
        if(gacRange != null)
        {
            nLocationMin = gacRange.getMin();
            nLocationMax = gacRange.getMax();
        }

        if(aiGacRealtime != null)
        {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiGacInfo = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp;

            // update_time
            aiGacInfo.put("update_time", aiGacRealtime.getUpdate_time());

            // operation_mode
            if(aiGacInit != null)
            {
                aiGacInfo.put("operation_mode", aiGacInit.getValue().intValue());
            }
            else
            {
                aiGacInfo.put("operation_mode", aiGacRealtime.getI_operation_mode());
            }

            // peak_mode
            aiGacInfo.put("peak_mode", aiGacRealtime.getI_peak_mode());

            // i_location_ti_set_max
            if(aiGacTi != null)
            {
                aiGacInfo.put("i_location_ti_set_max", aiGacTi.getValue().intValue());
            }
            else
            {
                aiGacInfo.put("i_location_ti_set_max", null);
            }

            // Realtime data from SCADA
            // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiGacInfo에 등록
            for(TagManageDTO tagManage : tagManageList)
            {
                for (ProcessRealtimeDTO dto : gacRealtime)
                {
                    if(tagManage.getItem().equalsIgnoreCase("d1_in_fr") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 1계열 혼화지 유입 유량
                        aiGacInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("d2_in_fr") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 2계열 혼화지 유입 유량
                        aiGacInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("e1_tb_b") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 1계열 침전지 후탁도
                        aiGacInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("e2_tb_b") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 2계열 침전지 후탁도
                        aiGacInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("i_in_fr") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 활성탄 흡착지 유입 유량
                        aiGacInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("i_out_fr") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 활성탄 흡착지 유출 유량
                        aiGacInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("i_sp") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 여과 속도
                        aiGacInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("i_tb") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 활성탄 흡착지 통합 탁도
                        aiGacInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                }
            }

            // 지별 상태, 지별 수위, 지별 여과 지속 시간, 운영지 수를 저장하기 위한 변수 선언
            LinkedHashMap<String, Object> locationStateMap = new LinkedHashMap<>();
            LinkedHashMap<String, Object> locationLeMap = new LinkedHashMap<>();
            LinkedHashMap<String, Object> locationTiMap = new LinkedHashMap<>();
            int nOperationCount = 0;

            for(int i = nLocationMin; i <= nLocationMax; i++)
            {
                String strStateName = "i_location_state" + i + "_";
                String strLeName = "i_location_le" + i;
                String strTiHName = "i_location_ti_h" + i;
                String strTiMName = "i_location_ti_m" + i;

                int nLocationState = 0;         // 지별 현재 상태를 저장하기 위한 변수
                int nLocationStateCount = 0;    // 지별 현재 상태가 중복 임을 확인하기 위한 변수
                int nLocationTi = 0;            // 지별 여과 지속 시간을 계산하기 위한 변수

                for(TagManageDTO tagManage : tagManageList)
                {
                    for(ProcessRealtimeDTO dto : gacRealtime)
                    {
                        if(tagManage.getItem().equalsIgnoreCase(strStateName + CommonValue.GAC_STATE_FILTER) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true &&
                                Float.parseFloat(dto.getValue()) > 0)
                        {
                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
                            nLocationState = CommonValue.GAC_STATE_FILTER;
                            nLocationStateCount ++;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strStateName + CommonValue.GAC_STATE_RELAX) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true &&
                                Float.parseFloat(dto.getValue()) > 0)
                        {
                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
                            nLocationState = CommonValue.GAC_STATE_RELAX;
                            nLocationStateCount ++;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strStateName + CommonValue.GAC_STATE_BW) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true &&
                                Float.parseFloat(dto.getValue()) > 0)
                        {
                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
                            nLocationState = CommonValue.GAC_STATE_BW;
                            nLocationStateCount ++;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strStateName + CommonValue.GAC_STATE_BW_STANDBY) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true &&
                                Float.parseFloat(dto.getValue()) > 0)
                        {
                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
                            nLocationState = CommonValue.GAC_STATE_BW_STANDBY;
                            nLocationStateCount ++;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strLeName) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            //  지별 수위 값 등록
                            locationLeMap.put("location" + i, Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strTiHName) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 지별 여과 지속 시간 계산(시)
                            nLocationTi += (int)Float.parseFloat(dto.getValue()) * 60;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strTiMName) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 지별 여과 지속 시간 계산(분)
                            nLocationTi += (int)Float.parseFloat(dto.getValue());
                            break;
                        }
                    }
                }

                if(nLocationStateCount == 1)
                {
                    // 지별 상태가 중복되어 있지 않을 경우 현재 지별 상태를 locationStateMap에 등록
                    locationStateMap.put("location"+i, nLocationState);
                    if(nLocationState == CommonValue.GAC_STATE_FILTER)
                    {
                        nOperationCount ++;
                    }
                }
                else
                {
                    // 지별 상태가 중복되어 있다면 상태 없음 처리
                    locationStateMap.put("location"+i, CommonValue.GAC_STATE_NONE);
                }

                locationTiMap.put("location"+i, nLocationTi);
            }

            try
            {

                // 현재 운영지 수
                aiGacInfo.put("i_operation_count", nOperationCount);

                // AI 운영지 수 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiGacRealtime.getAi_i_operation_count(), LinkedHashMap.class);
                ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                Object objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiGacInfo.put("ai_i_operation_count", objectTemp);

                // 지별 현재 상태
                aiGacInfo.put("i_location_state", locationStateMap);

                // 지별 수위
                aiGacInfo.put("i_location_le", locationLeMap);

                // 지별 여과 지속 시간
                aiGacInfo.put("i_location_ti", locationTiMap);

                // AI 지별 여과 지속 시간 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiGacRealtime.getAi_i_location_ti(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiGacInfo.put("ai_i_location_ti", objectTemp);

                // AI 지별 역세 시작 시간 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiGacRealtime.getAi_i_location_bw_start_ti(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiGacInfo.put("ai_i_location_bw_start_ti", objectTemp);

                // AI 지별 운영 스케쥴 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiGacRealtime.getAi_i_location_operation(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiGacInfo.put("ai_i_location_schedule", objectTemp);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiGacInfo);

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
            String strErrorBody = "{\"reason\":\"Empty ai_filter\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // GAC 여과 공정 제어모드 변경
    @RequestMapping(value = "/gac/control/operation", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlGac(@RequestBody InterfaceOperationModeDTO operationMode)
    {
        log.info("putOperationControlGac, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_gac_init's operation_mode
//        log.info("update aiGacOperationMode:[{}], mode:[{}]",
//                databaseService.modAiGacOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(i_operation_mode)
        AiProcessInitDTO aiGacInit = databaseService.getAiGacInit(CommonValue.I_OPERATION_MODE);
        log.info("getAiGacInit, result:[{}]", aiGacInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            // Kafka에 전송할 값 정의
            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
            controlMap.put("tag", aiGacInit.getName());
            controlMap.put("value", nOperationMode);
            controlMap.put("time", strDate);

            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = objectMapper.writeValueAsString(controlMap);
            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

            // Kafka에 여과 공정 제어모드 변경 알람 전송
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
            for(TagManageDTO dto : tagManageList)
            {
                if(dto.getItem().equalsIgnoreCase("i_operation_mode_a") == true)
                {
                    // Kafka에 전송할 값 정의
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", dto.getName());
                    controlMap.put("value", nOperationMode == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                    controlMap.put("time", strDate);

                    // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
                    objectMapper = new ObjectMapper();
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                    break;
                }
            }
        }
        catch(JsonProcessingException e)
        {
            log.error("JsonProcessingException Occurred in /gac/control/operation API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // GAC 여과 공정 최대 여과 지속 시간 설정
    @RequestMapping(value = "/gac/control/ti", method = RequestMethod.PUT)
    public ResponseEntity<String> putTiControlGac(@RequestBody InterfaceGacTiDTO gacTi)
    {
        int nTi = gacTi.getI_location_ti_set_max();
        log.info("putTiControlGac, ti:[{}]", nTi);

        // 잘못된 시간 값 검사
        if(nTi < 0)
        {
            log.error("Invalid ti:[{}]", nTi);

            String strErrorBody = "{\"reason\":\"Invalid ti\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        int nResult = databaseService.modAiGacInitTi(nTi);
        log.info("modAiGacInitTi, result:[{}]", nResult);

        if(nResult > 0)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_filter_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
