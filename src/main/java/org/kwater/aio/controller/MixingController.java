package org.kwater.aio.controller;

import org.kwater.aio.ai_dto.AiMixingRealtimeDTO;
import org.kwater.aio.ai_dto.JsonDStepFloat;
import org.kwater.aio.ai_dto.AiProcessInitDTO;
import org.kwater.aio.dto.*;
//import org.kwater.aio.kafka.KafkaProducer;
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
public class MixingController
{
    @Autowired
    DatabaseServiceImpl databaseService;

//    @Autowired
//    KafkaProducer //kafkaProducer;

    // 혼화응집 공정 최근 데이터 조회
    @RequestMapping(value = "/mixing/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestMixing()
    {
        log.info("Recv getLatestMixing");

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());

        // get ai_mixing_init(d_operation_mode)
        AiProcessInitDTO aiMixingInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE);
        log.info("getAiMixingInit, result:[{}]", aiMixingInit != null ? 1 : 0);

        // get ai_mixing_init
        List<AiProcessInitDTO> aiMixingInitList = databaseService.getAllAiMixingInit();
        log.info("getAllAiMixingInit, result:[{}]", aiMixingInitList.size());

        // get ai_mixing_realtime
        AiMixingRealtimeDTO aiMixingRealtime = databaseService.getLatestAiMixingRealtimeValue();
        log.info("getLatestMixingRealtimeValue, result:[{}]", aiMixingRealtime != null ? 1 : 0);

        // get mixing_realtime
        List<ProcessRealtimeDTO> mixingRealtime = databaseService.getLatestMixingRealtimeValue(strPartitionName);
        log.info("getLatestMixingRealtimeValue, result:[{}]", mixingRealtime.size());

        // get tag_manage
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_MIXING);
        log.info("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_MIXING, tagManageList.size());

        // get location number(지 번호)
        TagManageRangeDTO mixingRange = databaseService.getTagManageRange(CommonValue.PROCESS_MIXING);
        log.info("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_MIXING, mixingRange != null ? 1 : 0);

        int nLocationMin = 0, nLocationMax = 0;
        if(mixingRange != null)
        {
            nLocationMin = mixingRange.getMin();
            nLocationMax = mixingRange.getMax();
        }

        if(aiMixingRealtime != null)
        {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiMixingInfo = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp;

            // update_time
            aiMixingInfo.put("update_time", aiMixingRealtime.getUpdate_time());

            // operation_mode
            if(aiMixingInit != null)
            {
                aiMixingInfo.put("operation_mode", aiMixingInit.getValue().intValue());
            }
            else
            {
                aiMixingInfo.put("operation_mode", aiMixingRealtime.getD_operation_mode());
            }

            // Realtime data from SCADA
            boolean bFind = false;
            for(TagManageDTO tagManage : tagManageList)
            {
                for(ProcessRealtimeDTO dto : mixingRealtime)
                {
                    if(tagManage.getItem().equalsIgnoreCase("b_te") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 원수 수온
                        aiMixingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        bFind = true;
                        break;
                    }
                }
                if(bFind == true)
                {
                    break;
                }
            }


            // 물의 밀도
            aiMixingInfo.put("b_de", aiMixingRealtime.getB_de());

            // 물의 점성계수
            aiMixingInfo.put("b_viscosity", aiMixingRealtime.getB_viscosity());

            // 임펠러 직경
            aiMixingInfo.put("d_fc_lt", aiMixingRealtime.getD_fc_lt2());

            // 혼화지 용량
            aiMixingInfo.put("d_rq", aiMixingRealtime.getD_rq());

            // Power Number
            aiMixingInfo.put("d_pn", aiMixingRealtime.getD_pn2());

            // G 값
            for(AiProcessInitDTO dto : aiMixingInitList)
            {
                if(dto.getItem().equalsIgnoreCase("d_g_value_step1") == true)
                {
                    // 응집기 1단
                    aiMixingInfo.put("d_g_value1", dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("d_g_value_step2") == true)
                {
                    // 응집기 2단
                    aiMixingInfo.put("d_g_value2", dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("d_g_value_step3") == true)
                {
                    // 응집기 3단
                    aiMixingInfo.put("d_g_value3", dto.getValue());
                }
            }

            try
            {
                // G 값
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
//                mapTemp = objectMapper.readValue(aiMixingRealtime.getD_g_value(), LinkedHashMap.class);
//                List<String> keyList = new ArrayList<>(mapTemp.keySet());
//                Object objectTemp = mapTemp.get(keyList.get(0));
//                JsonDStepFloat d_g_value = objectMapper.convertValue(objectTemp, JsonDStepFloat.class);
//                aiMixingInfo.put("d_g_value1", d_g_value.getStep1());
//                aiMixingInfo.put("d_g_value2", d_g_value.getStep2());
//                aiMixingInfo.put("d_g_value3", d_g_value.getStep3());

                // 지별 응집기 상태, 응집기 속도
                for(int i = nLocationMin; i <= nLocationMax; i++)
                {
                    LinkedHashMap<String, Object> locationStateMap = new LinkedHashMap<>();
                    LinkedHashMap<String, Object> locationSpMap = new LinkedHashMap<>();

                    for(int j = 1; j <= 3; j++)
                    {
                        HashMap<String, Object> stepStateMap = new HashMap<>();
                        HashMap<String, Object> stepSpMap = new HashMap<>();

                        for(int k = 1; k <= 3; k++)
                        {
                            String strStateName = "d_fc_on" + i + "_" + j + "_" + k;
                            String strSpName = "d_fc_sp" + i + "_" + j + "_" + k;

                            for(TagManageDTO tagManage : tagManageList)
                            {
                                for (ProcessRealtimeDTO dto : mixingRealtime)
                                {
                                    if(tagManage.getItem().equalsIgnoreCase(strStateName) == true &&
                                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                                    {
                                        stepStateMap.put(String.format("%d", k), Float.parseFloat(dto.getValue()));
                                    }
                                    else if(tagManage.getItem().equalsIgnoreCase(strSpName) == true &&
                                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                                    {
                                        stepSpMap.put(String.format("%d", k), Float.parseFloat(dto.getValue()));
                                    }
                                }
                            }

                        }
                        locationStateMap.put("step"+j, stepStateMap);
                        locationSpMap.put("step"+j, stepSpMap);
                    }
                    aiMixingInfo.put("d_fc_location_state"+i, locationStateMap);
                    aiMixingInfo.put("d_fc_location_sp"+i, locationSpMap);
                }

                // AI 지별 응집기 속도 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiMixingRealtime.getAi_d_fc_location_sp2(), LinkedHashMap.class);
                List<String> keyList = new ArrayList<>(mapTemp.keySet());
                Object objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                for(String key : keyList)
                {
                    LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(key), LinkedHashMap.class);
                    aiMixingInfo.put(key.replaceAll("location", "ai_d_fc_location_sp"), locationMapTemp);
                }
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiMixingInfo);

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
            String strErrorBody = "{\"reason\":\"Empty ai_mixing\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 응집기 설정 속도 예측 이력 조회
    @RequestMapping(value = "/mixing/history/fc/sp", method = RequestMethod.PUT)
    public ResponseEntity<String> getFcAgHistoryMixing(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getSpFcHistoryMixing, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 혼화응집 공정 데이터 조회
        List<AiMixingRealtimeDTO> aiMixingRealtimeList =
                databaseService.getAiMixingRealtimeValueFromUpdateTime(dateSearchDTO);
        log.info("getAiMixingRealtimeValueFromUpdateTime, result:[{}]", aiMixingRealtimeList.size());

        if(aiMixingRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> d_fc_sp = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp, locationMapTemp;
            LinkedHashMap<String, Object> locationMap, stepMap;
            ObjectMapper objectMapper = new ObjectMapper();

            ArrayList<String> keyList;
            Object objectTemp;
            String strDate;

            int nLocationCount = 0, nStepCount = 0;

            try
            {
                // aiMixinfRealtimeList에서 응집기 속도 예측 이력을 조회하여 JSON Map 처리
                mapTemp = objectMapper.readValue(aiMixingRealtimeList.get(0).getAi_d_fc_location_sp2(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));

                // 전송할 운영지 수 저장
                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                ArrayList<String> locationKeyList = new ArrayList<>(mapTemp.keySet());
                nLocationCount = nLocationCount > locationKeyList.size() ? nLocationCount : locationKeyList.size();

                // 지별 단 수 저장
                objectTemp = mapTemp.get(locationKeyList.get(0));
                locationMapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                ArrayList<String> stepKeyList = new ArrayList<>(locationMapTemp.keySet());
                nStepCount = nStepCount > stepKeyList.size() ? nStepCount : stepKeyList.size();

                for(int i = 0; i < nLocationCount; i++)
                {
                    locationMap = new LinkedHashMap<>();
                    for(int j = 0; j < nStepCount; j++)
                    {
                        stepMap = new LinkedHashMap<>();
                        for(AiMixingRealtimeDTO dto : aiMixingRealtimeList)
                        {
                            strDate = simpleDateFormat.format(dto.getUpdate_time());

                            /*
                            * JSON 구조
                            * {                                         // d_fc_location_sp
                                "ai_d_fc_location_sp2": {               // root
                                    "location2": {                      // location
                                        "step1": {                      // step
                                            "1": 24.565403479393588,    // first
                                            "2": 24.565403479393588,
                                            "3": 24.565403479393588
                                        },
                                        "step2": {
                                            "1": 18.746911491287346,
                                            "2": 18.746911491287346,
                                            "3": 18.746911491287346
                                        },
                                        "step3": {
                                            "1": 11.809814204194506,
                                            "2": 11.809814204194506,
                                            "3": 11.809814204194506
                                        }
                                    }...
                                }
                            * */
                            // d_fc_location_sp
                            mapTemp = objectMapper.readValue(dto.getAi_d_fc_location_sp2(), LinkedHashMap.class);
                            keyList = new ArrayList<>(mapTemp.keySet());

                            // root
                            objectTemp = mapTemp.get(keyList.get(0));
                            mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);

                            // location
                            objectTemp = mapTemp.get(locationKeyList.get(i));
                            locationMapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);

                            // step
                            objectTemp = locationMapTemp.get(stepKeyList.get(j));
                            mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                            keyList = new ArrayList<>(mapTemp.keySet());

                            // first
                            stepMap.put(strDate, mapTemp.get(keyList.get(0)));
                        }
                        locationMap.put(stepKeyList.get(j), stepMap);
                    }
                    d_fc_sp.put(locationKeyList.get(i), locationMap);
                }

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("fc_sp", d_fc_sp);

                // ObjectMapper를 통해 JSON 값을 String으로 변환
                String strBody = "";
                strBody = objectMapper.writeValueAsString(responseBody);
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_mixing_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 혼화응집 공정 제어모드 변경
    @RequestMapping(value = "/mixing/control/operation", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlMixing(@RequestBody InterfaceOperationModeDTO operationMode)
    {
        log.info("putOperationControlMixing, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_mixing_init's operation_mode
//        log.info("update aiMixingOperationMode:[{}], mode:[{}]",
//                databaseService.modAiMixingOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(d_operation_mode)
        AiProcessInitDTO aiMixingInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE);
        log.info("getAiMixingInit, result:[{}]", aiMixingInit != null ? 1 : 0);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            // Kafka에 전송할 값 정의
            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
            controlMap.put("tag", aiMixingInit.getName());
            controlMap.put("value", nOperationMode);
            controlMap.put("time", strDate);

            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = objectMapper.writeValueAsString(controlMap);
//            kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

            // Kafka에 혼화응집 공정 제어모드 변경 알람 전송
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
            for(TagManageDTO dto : tagManageList)
            {
                if(dto.getItem().equalsIgnoreCase("d_operation_mode_a") == true)
                {
                    // Kafka에 전송할 값 정의
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", dto.getName());
                    controlMap.put("value", nOperationMode == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                    controlMap.put("time", strDate);

                    // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
                    objectMapper = new ObjectMapper();
                    strBody = objectMapper.writeValueAsString(controlMap);
//                    kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                    break;
                }
            }
        }
        catch(JsonProcessingException e)
        {
            log.error("JsonProcessingException Occurred in /mixing/control/operation API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 혼화응집 알고리즘 설정값 변경
    @RequestMapping(value = "/mixing/control/ai", method = RequestMethod.PUT)
    public ResponseEntity<String> putAiControlMixing(@RequestBody InterfaceMixingAiDTO mixingAi)
    {
        log.info("putAiControlMixing, ai:[{}]", mixingAi);

        boolean result = true;

        // update 응집기 1단 교반강도 G 값
        result = (databaseService.modAiMixingInit("d_g_value_step1", mixingAi.getD_g_value_step1()) == 1) && result;

        // update 응집기 2단 교반강도 G 값
        result = (databaseService.modAiMixingInit("d_g_value_step2", mixingAi.getD_g_value_step2()) == 1) && result;

        // update 응집기 3단 교반강도 G 값
        result = (databaseService.modAiMixingInit("d_g_value_step3", mixingAi.getD_g_value_step3()) == 1) && result;

        if(result == true)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        {
            String strErrorBody = "{\"reason\":\"ai_mixing_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
