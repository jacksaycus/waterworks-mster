package org.kwater.aio.controller;

import org.kwater.aio.ai_dto.*;
import org.kwater.aio.dto.*;
import org.kwater.aio.service.AlarmServiceImpl;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.*;
import org.kwater.aio.vo.HadoopClusterInfoDTO;
import org.kwater.aio.vo.HadoopClusterMetricsDTO;
import org.kwater.aio.vo.HadoopJmxBeans;
import org.kwater.aio.vo.SupervisorStateInfoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
public class InternalController
{
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    PropertiesControlCheck propertiesControlCheck;

    @Autowired
    PropertiesAlgorithmCheck propertiesAlgorithmCheck;

    @Autowired
    PropertiesStorage propertiesStorage;

    @Autowired
    PropertiesReceivingData propertiesReceivingData;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    AlarmServiceImpl alarmService;

    @Autowired
    AlarmInfoList alarmInfoList;

    @Autowired
    GlobalSystemConfig globalSystemConfig;

//    @Autowired
//    //kafkaProducer //kafkaProducer;

    private Date sensorDate = null;
    private Date analysisDate = null;
    private Date daqDate = null;
    private Date controlDate = null;
    private Date alarmDate = null;
    private Date algorithmDate = null;
    private Date databaseDate = null;

    private String lastCoagulants1 = "", lastCoagulants2 = "";

    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectionRequestTimeout(3 * CommonValue.ONE_SECOND)
            .build();

    // Kafka에 실시간 AI 예측값 전달
    @RequestMapping(value = "/internal/sensors", method = RequestMethod.GET)
    public void getSensors(@RequestHeader("X-ACCESS-TOKEN") String token)
    {

        /*
        * // Coagulant Change Alarm
                    // Compare to before Coagulants
                    if(lastCoagulants1.equalsIgnoreCase("") == true ||
                            lastCoagulants2.equalsIgnoreCase("") == true)
                    {
                        lastCoagulants1 = coagulantsAnalysisDTO.getChemical1();
                        lastCoagulants2 = coagulantsAnalysisDTO.getChemical2();
                    }

                    String strHostname;
                    try
                    {
                        strHostname = InetAddress.getLocalHost().getHostName();
                    }
                    catch(UnknownHostException e)
                    {
                        strHostname = CommonValue.ANALYSIS1_HOSTNAME;
                    }

                    if(lastCoagulants1.equalsIgnoreCase(coagulantsAnalysisDTO.getChemical1()) == false)
                    {
                        lastCoagulants1 = coagulantsAnalysisDTO.getChemical1();

                        // Insert alarm_notify & SCADA send
                        alarmService.alarmNotify(
                                CommonValue.ALARM_CODE_COAGULANTS_CHANGE1,
                                strHostname,
                                CommonValue.ALARM_VALUE_CHANGE,
                                false);
                    }

                    if(lastCoagulants2.equalsIgnoreCase(coagulantsAnalysisDTO.getChemical2()) == false)
                    {
                        lastCoagulants2 = coagulantsAnalysisDTO.getChemical2();

                        // Insert alarm_notify & SCADA send
                        alarmService.alarmNotify(
                                CommonValue.ALARM_CODE_COAGULANTS_CHANGE2,
                                strHostname,
                                CommonValue.ALARM_VALUE_CHANGE,
                                false);
                    }
        * */

        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("getSensors, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getSensor() initialization sensorDate before one hour
        if(sensorDate == null)
        {
            sensorDate = new Date();
            sensorDate.setTime(sensorDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.info("[internal] getSensors");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - sensorDate.getTime() > CommonValue.ONE_MINUTE)
        {
            sensorDate = new Date();

            // 실시간 데이터 처리를 Kafka로 하면서 삭제
//            // GET /sensors to SCADA1
//            String strSCADAUri = "http://" + globalSystemConfig.getScada1_information() + "/sensors";
//            HttpGet httpGet = new HttpGet(strSCADAUri);
//            httpGet.setConfig(requestConfig);
//
//            HttpResponse response = HttpSend.send(httpGet);
//            if(response == null)
//            {
//                log.error("Send /sensors to SCADA1 response null");
//
//                // Insert alarm_notify & SCADA send
//                alarmService.alarmNotify(
//                        CommonValue.ALARM_CODE_COLLECTOR_OFF1,
//                        globalSystemConfig.getScada1_information(),
//                        CommonValue.ALARM_VALUE_OFF,
//                        false);
//            }
//
//            // GET /sensors to SCADA2
//            strSCADAUri = "http://" + globalSystemConfig.getScada2_information() + "/sensors";
//            httpGet = new HttpGet(strSCADAUri);
//            httpGet.setConfig(requestConfig);
//
//            response = HttpSend.send(httpGet);
//            if(response == null)
//            {
//                log.error("Send /sensors to SCADA2 response null");
//
//                // Insert alarm_notify & SCADA send
//                alarmService.alarmNotify(
//                        CommonValue.ALARM_CODE_COLLECTOR_OFF2,
//                        globalSystemConfig.getScada2_information(),
//                        CommonValue.ALARM_VALUE_OFF,
//                        false);
//            }

            // get tag_manage(UI)
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);

            ///////////////////////////////////////////////
            // Receiving Process
            ///////////////////////////////////////////////
            // get ai_receiving_realtime
            AiReceivingRealtimeDTO aiReceivingRealtime = databaseService.getLatestAiReceivingRealtimeValue();
            log.info("getLatestAiReceivingRealtimeValue, result:[{}]", aiReceivingRealtime != null ? 1: 0);
            if(aiReceivingRealtime != null)
            {
                try
                {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiReceivingRealtime.getUpdate_time());
                    String strBody = "";

                    // b_operation_mode
                    AiProcessInitDTO aiReceivingInit = databaseService.getAiReceivingInit(CommonValue.B_OPERATION_MODE);
                    if(aiReceivingInit != null)
                    {
                        // 운전모드 태그는 전달하지 않음
//                    controlMap = new LinkedHashMap<>();
//                    controlMap.put("tag", aiReceivingInit.getName());
//                    controlMap.put("value", aiReceivingInit.getValue().intValue());
//                    controlMap.put("time", strDate);
//                    strBody = objectMapper.writeValueAsString(controlMap);
//                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                        // 운전모드 알람 태그는 항상 전송
                        for(TagManageDTO dto : tagManageList)
                        {
                            if(dto.getItem().equalsIgnoreCase("b_operation_mode_a") == true)
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", aiReceivingInit.getValue().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                break;
                            }
                        }
                    }

                    // AI 원수 유입 유량 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_in_fr(), LinkedHashMap.class);
                    List<String> keyList = new ArrayList<>(mapTemp.keySet());
                    Object objectTemp = mapTemp.get(keyList.get(0));
                    JsonBSeriesInt ai_b_in_fr = objectMapper.convertValue(objectTemp, JsonBSeriesInt.class);

                    // AI 원수 조절 밸브 개도 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_vv_po(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    JsonBSeriesInt ai_b_vv_po = objectMapper.convertValue(objectTemp, JsonBSeriesInt.class);

                    for(TagManageDTO dto : tagManageList)
                    {
                        if(dto.getProcess_code().equalsIgnoreCase(CommonValue.PROCESS_RECEIVING) != true)
                        {
                            continue;
                        }

                        if(dto.getItem().equalsIgnoreCase("ai_b_ti") == true)
                        {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

//                            log.info("Send AI_RESULT kafka ai_b_ti tag:[{}], value:[{}]", dto.getName(), strDate);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_b1_in_fr") == true)
                        {
                            // 1계열 원수 유입 유량 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", ai_b_in_fr.getSeries1());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_b2_in_fr") == true)
                        {
                            // 2계열 원수 유입 유량 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", ai_b_in_fr.getSeries2());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_b1_vv_po") == true)
                        {
                            // 1계열 원수 조절 밸브 개도 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", ai_b_vv_po.getSeries1());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_b2_vv_po") == true)
                        {
                            // 2계열 원수 조절 밸브 개도 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", ai_b_vv_po.getSeries2());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Receiving Process");
                }
            }

            ///////////////////////////////////////////////
            // Coagulant Process
            ///////////////////////////////////////////////
            // get ai_coagulant_realtime
            AiCoagulantRealtimeDTO aiCoagulantRealtime = databaseService.getLatestAiCoagulantRealtimeValue();
            log.info("getLatestAiCoagulantRealtimeValue, result:[{}]", aiCoagulantRealtime != null ? 1 : 0);
            if(aiCoagulantRealtime != null)
            {
                try
                {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiCoagulantRealtime.getUpdate_time());
                    String strBody;

                    // c_operation_mode
                    AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE);
                    if(aiCoagulantInit != null)
                    {
                        // 운전모드 태그는 전달하지 않음
//                        controlMap = new LinkedHashMap<>();
//                        controlMap.put("tag", aiCoagulantInit.getName());
//                        controlMap.put("value", aiCoagulantInit.getValue().intValue());
//                        controlMap.put("time", strDate);
//                        strBody = objectMapper.writeValueAsString(controlMap);
//                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                        // 운전모드 알람 태그는 항상 전송
                        for(TagManageDTO dto : tagManageList)
                        {
                            if(dto.getItem().equalsIgnoreCase("c_operation_mode_a") == true)
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", aiCoagulantInit.getValue().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                break;
                            }
                        }
                    }

                    // AI 약품 주입률 예측 최종값
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiCoagulantRealtime.getAi_c_final(), LinkedHashMap.class);
                    List<String> keyList = new ArrayList<>(mapTemp.keySet());
                    Object objectTemp = mapTemp.get(keyList.get(0));
                    JsonCSeriesFloat ai_c_final = objectMapper.convertValue(objectTemp, JsonCSeriesFloat.class);

                    // AI 약품 종류 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiCoagulantRealtime.getAi_c_coagulant(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    JsonCSeriesString ai_c_coagulant = objectMapper.convertValue(objectTemp, JsonCSeriesString.class);

                    for(TagManageDTO dto : tagManageList)
                    {
                        if(dto.getProcess_code().equalsIgnoreCase(CommonValue.PROCESS_COAGULANT) != true)
                        {
                            continue;
                        }

                        if(dto.getItem().equalsIgnoreCase("ai_c_ti") == true)
                        {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_c1_cf_coagulant") == true)
                        {
                            // AI 1계열 약품 종류 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", ai_c_coagulant.getSeries1());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_c2_cf_coagulant") == true)
                        {
                            // AI 2계열 약품 종류 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", ai_c_coagulant.getSeries2());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_c1_cf_final") == true)
                        {
                            // AI 1계열 약품 주입률 예측 최종값
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", ai_c_final.getSeries1());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_c2_cf_final") == true)
                        {
                            // AI 2계열 약품 주입률 예측 최종값
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", ai_c_final.getSeries2());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Coagulant Process");
                }
            }

            ///////////////////////////////////////////////
            // Mixing Process
            ///////////////////////////////////////////////
            // get ai_mixing_realtime
            AiMixingRealtimeDTO aiMixingRealtime = databaseService.getLatestAiMixingRealtimeValue();
            log.info("getLatestAiMixingRealtimeValue, result:[{}]", aiMixingRealtime != null ? 1 : 0);

            // get location number(지 번호)
            TagManageRangeDTO mixingRange = databaseService.getTagManageRange(CommonValue.PROCESS_MIXING);
            log.info("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_MIXING, mixingRange != null ? 1 : 0);
            if(aiMixingRealtime != null)
            {
                int nLocationMin = 0, nLocationMax = 0;
                if(mixingRange != null)
                {
                    nLocationMin = mixingRange.getMin();
                    nLocationMax = mixingRange.getMax();
                }

                try
                {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiMixingRealtime.getUpdate_time());
                    String strBody;

                    // d_operation_mode
                    AiProcessInitDTO aiMixingInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE);
                    if(aiMixingInit != null)
                    {
                        // 운전모드 태그는 전달하지 않음
//                    controlMap = new LinkedHashMap<>();
//                    controlMap.put("tag", aiMixingInit.getName());
//                    controlMap.put("value", aiMixingInit.getValue().intValue());
//                    controlMap.put("time", strDate);
//                    strBody = objectMapper.writeValueAsString(controlMap);
//                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                        // 운전모드 알람 태그는 항상 전송
                        for(TagManageDTO dto : tagManageList)
                        {
                            if(dto.getItem().equalsIgnoreCase("d_operation_mode_a") == true)
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", aiMixingInit.getValue().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                break;
                            }
                        }
                    }

                    for(TagManageDTO dto : tagManageList)
                    {
                        if (dto.getProcess_code().equalsIgnoreCase(CommonValue.PROCESS_MIXING) != true) {
                            continue;
                        }

                        if (dto.getItem().equalsIgnoreCase("ai_d_ti") == true)
                        {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

//                            log.info("Send AI_RESULT kafka ai_d_ti tag:[{}], value:[{}]", dto.getName(), strDate);
                        }
                        else if(dto.getItem().equalsIgnoreCase("b_de") == true)
                        {
                            // 물의 밀도
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiMixingRealtime.getB_de());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("b_viscosity") == true)
                        {
                            // 물의 점성계수
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiMixingRealtime.getB_viscosity());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("d_fc_lt") == true)
                        {
                            // 임펠러 직경
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiMixingRealtime.getD_fc_lt2());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("d_rq") == true)
                        {
                            // 혼화지 용량
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiMixingRealtime.getD_rq());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("d_pn") == true)
                        {
                            // Power Number
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiMixingRealtime.getD_pn2());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                    }

                    // AI 지별 응집기 속도 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiMixingRealtime.getAi_d_fc_location_sp2(), LinkedHashMap.class);
                    List<String> keyList = new ArrayList<>(mapTemp.keySet());
                    Object objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());    // location2...9

                    for(String key : keyList)
                    {
                        LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(key), LinkedHashMap.class);
                        List<String> locationKeyList = new ArrayList<>(locationMapTemp.keySet());   // step1...3

                        for(String locationKey : locationKeyList)
                        {
                            LinkedHashMap<String, Object> stepMapTemp =
                                    objectMapper.convertValue(locationMapTemp.get(locationKey), LinkedHashMap.class);
                            List<String> stepKeyList = new ArrayList<>(stepMapTemp.keySet());   // 1...3

                            for(int i = nLocationMin; i <= nLocationMax; i++)
                            {
                                for(int j = 1; j <= 3; j++) // step count 1...3
                                {
                                    String strItemName = "ai_d_fc_sp" + i + "_" + j;
                                    if(key.equalsIgnoreCase("location"+i) == true &&
                                            locationKey.equalsIgnoreCase("step"+j) == true)
                                    {

                                        // tagManageList에서 strItemName을 검색
                                        TagManageDTO dto = tagManageList.stream()
                                                .filter(tagManage -> strItemName.equalsIgnoreCase(tagManage.getItem()))
                                                .findAny()
                                                .orElse(null);

                                        if(dto == null)
                                        {
                                            continue;
                                        }

                                        // Kafka에 전송할 값 정의
                                        controlMap = new LinkedHashMap<>();
                                        controlMap.put("tag", dto.getName());
                                        controlMap.put("value", stepMapTemp.get(stepKeyList.get(0)));
                                        controlMap.put("time", strDate);
                                        strBody = objectMapper.writeValueAsString(controlMap);
                                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Mixing Process");
                }
            }

            ///////////////////////////////////////////////
            // Sedimentation Process
            ///////////////////////////////////////////////
            // get ai_sedimentation_realtime
            AiSedimentationRealtimeDTO aiSedimentationRealtime = databaseService.getLatestAiSedimentationRealtimeValue();
            log.info("getLatestAiSedimentationRealtimeValue, result:[{}]", aiSedimentationRealtime != null ? 1 : 0);
            if(aiSedimentationRealtime != null)
            {
                try
                {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    List<LinkedHashMap<String, Object>> locationMap;
                    ObjectMapper objectMapper = new ObjectMapper();
                    Date dateTemp;

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat valueDateFormat = new SimpleDateFormat("MM-dd HH:mm");
                    SimpleDateFormat resultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String strDate = simpleDateFormat.format(aiSedimentationRealtime.getUpdate_time());
                    String strBody;

                    // e_operation_mode
                    AiProcessInitDTO aiSedimentationInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE);
                    if(aiSedimentationInit != null)
                    {
                        // 운전모드 태그는 전달하지 않음
//                    controlMap = new LinkedHashMap<>();
//                    controlMap.put("tag", aiSedimentationInit.getName());
//                    controlMap.put("value", aiSedimentationInit.getValue().intValue());
//                    controlMap.put("time", strDate);
//                    strBody = objectMapper.writeValueAsString(controlMap);
//                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                        // 운전모드 알람 태그는 항상 전송
                        for(TagManageDTO dto : tagManageList)
                        {
                            if(dto.getItem().equalsIgnoreCase("e_operation_mode_a") == true)
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", aiSedimentationInit.getValue().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                break;
                            }
                        }
                    }

                    for(TagManageDTO dto : tagManageList)
                    {
                        if (dto.getProcess_code().equalsIgnoreCase(CommonValue.PROCESS_SEDIMENTATION) != true)
                        {
                            continue;
                        }

                        if (dto.getItem().equalsIgnoreCase("ai_e_ti") == true)
                        {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

//                            log.info("Send AI_RESULT kafka ai_e_ti tag:[{}], value:[{}]", dto.getName(), strDate);

                            break;
                        }
                    }

                    // 1계열 침전지 슬러지 양 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2021");
                    controlMap.put("value", aiSedimentationRealtime.getAIE_5301());
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // 2계열 침전지 슬러지 양 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2022");
                    controlMap.put("value", aiSedimentationRealtime.getAIE_5302());
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // 2지 데이터를 추출하여 location2Dto에 저장
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiSedimentationRealtime.getAIE_9002(), LinkedHashMap.class);
                    List<String> keyList = new ArrayList<>(mapTemp.keySet());
                    Object objectTemp = mapTemp.get(keyList.get(0));
                    locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                    mapTemp.clear();
                    for(Map<String, Object> map : locationMap)
                    {
                        mapTemp.putAll(map);
                    }
                    AiSedimentationLocation2RealtimeDTO location2Dto =
                            objectMapper.convertValue(mapTemp, AiSedimentationLocation2RealtimeDTO.class);

                    // location2 - AI 수집기 대차 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2102");
                    if(location2Dto.getAIE_6002().getStart().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location2Dto.getAIE_6002().getStart());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location2 - AI 수집기 대차 종료 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2112");
                    if(location2Dto.getAIE_6002().getStop().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location2Dto.getAIE_6002().getStop());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location2 - AI 다음 수집기 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2132");
                    if(location2Dto.getAIE_6002().getNext_start().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location2Dto.getAIE_6002().getNext_start());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location2 - AI 인발 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2142");
                    if(location2Dto.getAIE_6002().getInbal().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location2Dto.getAIE_6002().getInbal());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // 3지 데이터를 추출하여 location3Dto에 저장
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiSedimentationRealtime.getAIE_9003(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                    mapTemp.clear();
                    for(Map<String, Object> map : locationMap)
                    {
                        mapTemp.putAll(map);
                    }
                    AiSedimentationLocation3RealtimeDTO location3Dto =
                            objectMapper.convertValue(mapTemp, AiSedimentationLocation3RealtimeDTO.class);

                    // location3 - AI 수집기 대차 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2103");
                    if(location3Dto.getAIE_6003().getStart().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location3Dto.getAIE_6003().getStart());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location3 - AI 수집기 대차 종료 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2113");
                    if(location3Dto.getAIE_6003().getStop().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location3Dto.getAIE_6003().getStop());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location3 - AI 다음 수집기 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2133");
                    if(location3Dto.getAIE_6003().getNext_start().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location3Dto.getAIE_6003().getNext_start());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location3 - AI 인발 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2143");
                    if(location3Dto.getAIE_6003().getInbal().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location3Dto.getAIE_6003().getInbal());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // 4지 데이터를 추출하여 location4Dto에 저장
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiSedimentationRealtime.getAIE_9004(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                    mapTemp.clear();
                    for(Map<String, Object> map : locationMap)
                    {
                        mapTemp.putAll(map);
                    }
                    AiSedimentationLocation4RealtimeDTO location4Dto =
                            objectMapper.convertValue(mapTemp, AiSedimentationLocation4RealtimeDTO.class);

                    // location4 - AI 수집기 대차 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2104");
                    if(location4Dto.getAIE_6004().getStart().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location4Dto.getAIE_6004().getStart());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location4 - AI 수집기 대차 종료 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2114");
                    if(location4Dto.getAIE_6004().getStop().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location4Dto.getAIE_6004().getStop());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location4 - AI 다음 수집기 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2134");
                    if(location4Dto.getAIE_6004().getNext_start().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location4Dto.getAIE_6004().getNext_start());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location4 - AI 인발 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2144");
                    if(location4Dto.getAIE_6004().getInbal().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location4Dto.getAIE_6004().getInbal());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // 5지 데이터를 추출하여 location5Dto에 저장
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiSedimentationRealtime.getAIE_9005(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                    mapTemp.clear();
                    for(Map<String, Object> map : locationMap)
                    {
                        mapTemp.putAll(map);
                    }
                    AiSedimentationLocation5RealtimeDTO location5Dto =
                            objectMapper.convertValue(mapTemp, AiSedimentationLocation5RealtimeDTO.class);

                    // location5 - AI 수집기 대차 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2105");
                    if(location5Dto.getAIE_6005().getStart().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location5Dto.getAIE_6005().getStart());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location5 - AI 수집기 대차 종료 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2115");
                    if(location5Dto.getAIE_6005().getStop().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location5Dto.getAIE_6005().getStop());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location5 - AI 다음 수집기 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2135");
                    if(location5Dto.getAIE_6005().getNext_start().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location5Dto.getAIE_6005().getNext_start());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location5 - AI 인발 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2145");
                    if(location5Dto.getAIE_6005().getInbal().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location5Dto.getAIE_6005().getInbal());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // 6지 데이터를 추출하여 location6Dto에 저장
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiSedimentationRealtime.getAIE_9006(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                    mapTemp.clear();
                    for(Map<String, Object> map : locationMap)
                    {
                        mapTemp.putAll(map);
                    }
                    AiSedimentationLocation6RealtimeDTO location6Dto =
                            objectMapper.convertValue(mapTemp, AiSedimentationLocation6RealtimeDTO.class);

                    // location6 - AI 수집기 대차 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2106");
                    if(location6Dto.getAIE_6006().getStart().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location6Dto.getAIE_6006().getStart());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location6 - AI 수집기 대차 종료 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2116");
                    if(location6Dto.getAIE_6006().getStop().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location6Dto.getAIE_6006().getStop());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location6 - AI 다음 수집기 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2136");
                    if(location6Dto.getAIE_6006().getNext_start().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location6Dto.getAIE_6006().getNext_start());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location6 - AI 인발 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2146");
                    if(location6Dto.getAIE_6006().getInbal().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location6Dto.getAIE_6006().getInbal());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // 7지 데이터를 추출하여 location7Dto에 저장
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiSedimentationRealtime.getAIE_9007(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                    mapTemp.clear();
                    for(Map<String, Object> map : locationMap)
                    {
                        mapTemp.putAll(map);
                    }
                    AiSedimentationLocation7RealtimeDTO location7Dto =
                            objectMapper.convertValue(mapTemp, AiSedimentationLocation7RealtimeDTO.class);

                    // location7 - AI 수집기 대차 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2107");
                    if(location7Dto.getAIE_6007().getStart().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location7Dto.getAIE_6007().getStart());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location7 - AI 수집기 대차 종료 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2117");
                    if(location7Dto.getAIE_6007().getStop().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location7Dto.getAIE_6007().getStop());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location7 - AI 다음 수집기 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2137");
                    if(location7Dto.getAIE_6007().getNext_start().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location7Dto.getAIE_6007().getNext_start());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location7 - AI 인발 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2147");
                    if(location7Dto.getAIE_6007().getInbal().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location7Dto.getAIE_6007().getInbal());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // 8지 데이터를 추출하여 location8Dto에 저장
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiSedimentationRealtime.getAIE_9008(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                    mapTemp.clear();
                    for(Map<String, Object> map : locationMap)
                    {
                        mapTemp.putAll(map);
                    }
                    AiSedimentationLocation8RealtimeDTO location8Dto =
                            objectMapper.convertValue(mapTemp, AiSedimentationLocation8RealtimeDTO.class);

                    // location8 - AI 수집기 대차 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2108");
                    if(location8Dto.getAIE_6008().getStart().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location8Dto.getAIE_6008().getStart());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location8 - AI 수집기 대차 종료 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2118");
                    if(location8Dto.getAIE_6008().getStop().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location8Dto.getAIE_6008().getStop());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location8 - AI 다음 수집기 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2138");
                    if(location8Dto.getAIE_6008().getNext_start().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location8Dto.getAIE_6008().getNext_start());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location8 - AI 인발 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2148");
                    if(location8Dto.getAIE_6008().getInbal().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location8Dto.getAIE_6008().getInbal());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // 9지 데이터를 추출하여 location9Dto에 저장
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiSedimentationRealtime.getAIE_9009(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    locationMap = objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                    mapTemp.clear();
                    for(Map<String, Object> map : locationMap)
                    {
                        mapTemp.putAll(map);
                    }
                    AiSedimentationLocation9RealtimeDTO location9Dto =
                            objectMapper.convertValue(mapTemp, AiSedimentationLocation9RealtimeDTO.class);

                    // location9 - AI 수집기 대차 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2109");
                    if(location9Dto.getAIE_6009().getStart().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location9Dto.getAIE_6009().getStart());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location9 - AI 수집기 대차 종료 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2119");
                    if(location9Dto.getAIE_6009().getStop().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location9Dto.getAIE_6009().getStop());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location9 - AI 다음 수집기 시작 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2139");
                    if(location9Dto.getAIE_6009().getNext_start().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location9Dto.getAIE_6009().getNext_start());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                    // location9 - AI 인발 시간 예측
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", "745-617-SWI-2149");
                    if(location9Dto.getAIE_6009().getInbal().equalsIgnoreCase("") == true)
                    {
                        controlMap.put("value", "--");
                    }
                    else
                    {
                        dateTemp = resultDateFormat.parse(location9Dto.getAIE_6009().getInbal());
                        controlMap.put("value", valueDateFormat.format(dateTemp));
                    }
                    controlMap.put("time", strDate);
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Sedimentation Process");
                }
                catch(ParseException e)
                {
                    log.error("TimeParsingException Occurred in Sedimentation Process");
                }
            }

            ///////////////////////////////////////////////
            // Filter Process
            ///////////////////////////////////////////////
            // get ai_filter_realtime
            AiFilterRealtimeDTO aiFilterRealtime = databaseService.getLatestAiFilterRealtimeValue();
            log.info("getLatestAiFilterRealtimeValue, result:[{}]", aiFilterRealtime != null ? 1 : 0);

            // get location number(지 번호)
            TagManageRangeDTO filterRange = databaseService.getTagManageRange(CommonValue.PROCESS_FILTER);
            log.info("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_FILTER, filterRange != null ? 1: 0);
            if(aiFilterRealtime != null)
            {
                int nLocationMin = 0, nLocationMax = 0;
                if(filterRange != null)
                {
                    nLocationMin = filterRange.getMin();
                    nLocationMax = filterRange.getMax();
                }

                try
                {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat aiDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    SimpleDateFormat valueDateFormat = new SimpleDateFormat("MM-dd HH:mm");
                    String strDate = simpleDateFormat.format(aiFilterRealtime.getUpdate_time());
                    String strBody, strValueDate;

                    Date valueDate;

                    // f_operation_mode
                    AiProcessInitDTO aiFilterInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE);
                    if(aiFilterInit != null)
                    {
                        // 운전모드 태그는 전달하지 않음
//                    controlMap = new LinkedHashMap<>();
//                    controlMap.put("tag", aiFilterInit.getName());
//                    controlMap.put("value", aiFilterInit.getValue().intValue());
//                    controlMap.put("time", strDate);
//                    strBody = objectMapper.writeValueAsString(controlMap);
//                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                        // 운전모드 알람 태그는 항상 전송
                        for(TagManageDTO dto : tagManageList)
                        {
                            if(dto.getItem().equalsIgnoreCase("f_operation_mode_a") == true)
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", aiFilterInit.getValue().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                break;
                            }
                        }
                    }

                    for(TagManageDTO dto : tagManageList)
                    {
                        if (dto.getProcess_code().equalsIgnoreCase(CommonValue.PROCESS_FILTER) != true)
                        {
                            continue;
                        }

                        if (dto.getItem().equalsIgnoreCase("ai_f_ti") == true)
                        {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

//                            log.info("Send AI_RESULT kafka ai_f_ti tag:[{}], value:[{}]", dto.getName(), strDate);

                            break;
                        }
                    }

                    // AI 지별 여과 지속 시간 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_ti(), LinkedHashMap.class);
                    ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                    Object objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());

                    for(String key : keyList)
                    {
                        for(int i = nLocationMin; i <= nLocationMax; i++)
                        {
                            String strItemName = "ai_f_ti" + i;
                            if(key.equalsIgnoreCase("location" + i) == true)
                            {
                                // tagManageList에서 strItemName을 검색
                                TagManageDTO dto = tagManageList.stream()
                                        .filter(tagManage -> strItemName.equalsIgnoreCase(tagManage.getItem()))
                                        .findAny()
                                        .orElse(null);

                                if(dto == null)
                                {
                                    continue;
                                }

                                // Kafka에 전송할 값 정의
                                int value = (int)mapTemp.get(key);
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", Math.round((float)value / 60));
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                continue;
                            }
                        }
                    }

                    // AI 지별 수위 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_le(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    for(String key : keyList)
                    {
                        LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(key), LinkedHashMap.class);
                        ArrayList<String> locationKeyList = new ArrayList<>(locationMapTemp.keySet());

                        for(int i = nLocationMin; i <= nLocationMax; i++)
                        {
                            String strItemName = "ai_f_le" + i;
                            if(key.equalsIgnoreCase("location" + i) == true)
                            {
                                // tagManageList에서 strItemName을 검색
                                TagManageDTO dto = tagManageList.stream()
                                        .filter(tagManage -> strItemName.equalsIgnoreCase(tagManage.getItem()))
                                        .findAny()
                                        .orElse(null);

                                if(dto == null)
                                {
                                    continue;
                                }

                                // Kafka에 전송할 값 정의
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", locationMapTemp.get(locationKeyList.get(0)));
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                continue;
                            }
                        }
                    }

                    // tagManageList에서 ai_f_operation_count 키 값 검색
                    TagManageDTO aiFOperationCount = tagManageList.stream()
                            .filter(tagManage -> "ai_f_operation_count".equalsIgnoreCase(tagManage.getItem()))
                            .findAny()
                            .orElse(null);
                    if(aiFOperationCount != null)
                    {
                        // AI 운영지 수 예측
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_operation_count(), LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", aiFOperationCount.getName());
                        controlMap.put("value", objectTemp);
                        controlMap.put("time", strDate);
                        strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                    }

                    // AI 지별 운영 스케쥴 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_operation(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    for(String key : keyList)
                    {
                        LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(key), LinkedHashMap.class);
                        ArrayList<String> locationKeyList = new ArrayList<>(locationMapTemp.keySet());

                        for(String locationKey : locationKeyList)
                        {
                            for(int i = nLocationMin; i <= nLocationMax; i++)
                            {
                                String strStartTiName = "ai_f_start_ti" + i;
                                String strEndTiName = "ai_f_end_ti" + i;
                                String strBwStartTiName = "ai_f_bw_start_ti" + i;
                                if(key.equalsIgnoreCase("location" + i) == true &&
                                        locationKey.equalsIgnoreCase("start") == true)
                                {
                                    // AI 여과 시작 시간 예측
                                    // tagManageList에서 strItemName을 검색
                                    TagManageDTO dto = tagManageList.stream()
                                            .filter(tagManage -> strStartTiName.equalsIgnoreCase(tagManage.getItem()))
                                            .findAny()
                                            .orElse(null);

                                    if(dto == null)
                                    {
                                        continue;
                                    }

                                    String strValue = locationMapTemp.get(locationKey).toString();
                                    if(strValue.equalsIgnoreCase("0") == true)
                                    {
                                        strValueDate = "--";
                                    }
                                    else
                                    {
                                        valueDate = aiDateFormat.parse(strValue);
                                        strValueDate = valueDateFormat.format(valueDate);
                                    }
                                    // Kafka에 전송할 값 정의
                                    controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    controlMap.put("value", strValueDate);
                                    controlMap.put("time", strDate);
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                    continue;
                                }
                                else if(key.equalsIgnoreCase("location" + i) == true &&
                                        locationKey.equalsIgnoreCase("end") == true)
                                {
                                    // AI 여과 종료 시간 예측
                                    // tagManageList에서 strItemName을 검색
                                    TagManageDTO dto = tagManageList.stream()
                                            .filter(tagManage -> strEndTiName.equalsIgnoreCase(tagManage.getItem()))
                                            .findAny()
                                            .orElse(null);

                                    if(dto == null)
                                    {
                                        continue;
                                    }

                                    String strValue = locationMapTemp.get(locationKey).toString();
                                    if(strValue.equalsIgnoreCase("0") == true)
                                    {
                                        strValueDate = "--";
                                    }
                                    else
                                    {
                                        valueDate = aiDateFormat.parse(strValue);
                                        strValueDate = valueDateFormat.format(valueDate);
                                    }
                                    // Kafka에 전송할 값 정의
                                    controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    controlMap.put("value", strValueDate);
                                    controlMap.put("time", strDate);
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                    continue;
                                }
                                else if(key.equalsIgnoreCase("location" + i) == true &&
                                        locationKey.equalsIgnoreCase("bw_start") == true)
                                {
                                    // AI 역세 시작 시간 예측
                                    // tagManageList에서 strItemName을 검색
                                    TagManageDTO dto = tagManageList.stream()
                                            .filter(tagManage -> strBwStartTiName.equalsIgnoreCase(tagManage.getItem()))
                                            .findAny()
                                            .orElse(null);

                                    if(dto == null)
                                    {
                                        continue;
                                    }

                                    String strValue = locationMapTemp.get(locationKey).toString();
                                    if(strValue.equalsIgnoreCase("0") == true)
                                    {
                                        strValueDate = "--";
                                    }
                                    else
                                    {
                                        valueDate = aiDateFormat.parse(strValue);
                                        strValueDate = valueDateFormat.format(valueDate);
                                    }
                                    // Kafka에 전송할 값 정의
                                    controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    controlMap.put("value", strValueDate);
                                    controlMap.put("time", strDate);
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                    continue;
                                }
                            }
                        }
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Filter Process");
                }
                catch(ParseException e)
                {
                    log.error("TextParse Exception Occurred in Filter Process");
                }
            }

            ///////////////////////////////////////////////
            // GAC Filter Process
            ///////////////////////////////////////////////
            // get ai_gac_realtime
            AiGacRealtimeDTO aiGacRealtime = databaseService.getLatestAiGacRealtimeValue();
            log.info("getLatestAiGacRealtimeValue, result:[{}]", aiGacRealtime != null ? 1: 0);

            // get location number(지 번호)
            TagManageRangeDTO gacRange = databaseService.getTagManageRange(CommonValue.PROCESS_GAC);
            log.info("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_GAC, gacRange != null ? 1 : 0);
            if(aiGacRealtime != null)
            {
                int nLocationMin = 0, nLocationMax = 0;
                if(gacRange != null)
                {
                    nLocationMin = gacRange.getMin();
                    nLocationMax = gacRange.getMax();
                }

                try
                {
                    LinkedHashMap<String, Object> controlMap, mapTemp;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat aiDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    SimpleDateFormat valueDateFormat = new SimpleDateFormat("MM-dd HH:mm");
                    String strDate = simpleDateFormat.format(aiGacRealtime.getUpdate_time());
                    String strBody, strValueDate;

                    Date valueDate;

                    // i_operation_mode
                    AiProcessInitDTO aiGacInit = databaseService.getAiGacInit(CommonValue.I_OPERATION_MODE);
                    if(aiGacInit != null)
                    {
                        // 운전모드 태그는 전달하지 않음
//                    controlMap = new LinkedHashMap<>();
//                    controlMap.put("tag", aiGacInit.getName());
//                    controlMap.put("value", aiGacInit.getValue().intValue());
//                    controlMap.put("time", strDate);
//                    strBody = objectMapper.writeValueAsString(controlMap);
//                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                        // 운전모드 알람 태그는 항상 전송
                        for(TagManageDTO dto : tagManageList)
                        {
                            if(dto.getItem().equalsIgnoreCase("i_operation_mode_a") == true)
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", aiGacInit.getValue().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                break;
                            }
                        }
                    }

                    for(TagManageDTO dto : tagManageList)
                    {
                        if (dto.getProcess_code().equalsIgnoreCase(CommonValue.PROCESS_GAC) != true)
                        {
                            continue;
                        }

                        if (dto.getItem().equalsIgnoreCase("ai_i_ti") == true)
                        {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

//                            log.info("Send AI_RESULT kafka ai_i_ti tag:[{}], value:[{}]", dto.getName(), strDate);

                            break;
                        }
                    }

                    // tagManageList에서 ai_i_operation_count 키 값 검색
                    TagManageDTO aiIOperationCount = tagManageList.stream()
                            .filter(tagManage -> "ai_i_operation_count".equalsIgnoreCase(tagManage.getItem()))
                            .findAny()
                            .orElse(null);
                    if(aiIOperationCount != null)
                    {
                        // AI 운영지 수 예측
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        mapTemp = objectMapper.readValue(aiGacRealtime.getAi_i_operation_count(), LinkedHashMap.class);
                        ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                        Object objectTemp = mapTemp.get(keyList.get(0));

                        mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", aiIOperationCount.getName());
                        controlMap.put("value", objectTemp);
                        controlMap.put("time", strDate);
                        strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                    }

                    // AI 지별 여과 지속 시간 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiGacRealtime.getAi_i_location_ti(), LinkedHashMap.class);
                    ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                    Object objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());

                    for(String key : keyList)
                    {
                        for(int i = nLocationMin; i <= nLocationMax; i++)
                        {
                            String strItemName = "ai_i_ti" + i;
                            if(key.equalsIgnoreCase("location" + i) == true)
                            {
                                // tagManageList에서 strItemName을 검색
                                TagManageDTO dto = tagManageList.stream()
                                        .filter(tagManage -> strItemName.equalsIgnoreCase(tagManage.getItem()))
                                        .findAny()
                                        .orElse(null);

                                if(dto == null)
                                {
                                    continue;
                                }

                                // Kafka에 전송할 값 정의
                                int value = (int)mapTemp.get(key);
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", Math.round((float)value / 60));
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                continue;
                            }
                        }
                    }

                    // AI 지별 운영 스케쥴 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiGacRealtime.getAi_i_location_operation(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    for(String key : keyList)
                    {
                        LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(key), LinkedHashMap.class);
                        ArrayList<String> locationKeyList = new ArrayList<>(locationMapTemp.keySet());

                        for(String locationKey : locationKeyList)
                        {
                            for(int i = nLocationMin; i <= nLocationMax; i++)
                            {
                                String strStartTiName = "ai_i_start_ti" + i;
                                String strEndTiName = "ai_i_end_ti" + i;
                                String strBwStartTiName = "ai_i_bw_start_ti" + i;
                                if(key.equalsIgnoreCase("location" + i) == true &&
                                        locationKey.equalsIgnoreCase("start") == true)
                                {
                                    // AI 여과 시작 시간 예측
                                    // tagManageList에서 strItemName을 검색
                                    TagManageDTO dto = tagManageList.stream()
                                            .filter(tagManage -> strStartTiName.equalsIgnoreCase(tagManage.getItem()))
                                            .findAny()
                                            .orElse(null);

                                    if(dto == null)
                                    {
                                        continue;
                                    }

                                    String strValue = locationMapTemp.get(locationKey).toString();
                                    if(strValue.equalsIgnoreCase("0") == true)
                                    {
                                        strValueDate = "--";
                                    }
                                    else
                                    {
                                        valueDate = aiDateFormat.parse(strValue);
                                        strValueDate = valueDateFormat.format(valueDate);
                                    }
                                    // Kafka에 전송할 값 정의
                                    controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    controlMap.put("value", strValueDate);
                                    controlMap.put("time", strDate);
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                    continue;
                                }
                                else if(key.equalsIgnoreCase("location" + i) == true &&
                                        locationKey.equalsIgnoreCase("end") == true)
                                {
                                    // AI 여과 종료 시간 예측
                                    // tagManageList에서 strItemName을 검색
                                    TagManageDTO dto = tagManageList.stream()
                                            .filter(tagManage -> strEndTiName.equalsIgnoreCase(tagManage.getItem()))
                                            .findAny()
                                            .orElse(null);

                                    if(dto == null)
                                    {
                                        continue;
                                    }

                                    String strValue = locationMapTemp.get(locationKey).toString();
                                    if(strValue.equalsIgnoreCase("0") == true)
                                    {
                                        strValueDate = "--";
                                    }
                                    else
                                    {
                                        valueDate = aiDateFormat.parse(strValue);
                                        strValueDate = valueDateFormat.format(valueDate);
                                    }
                                    // Kafka에 전송할 값 정의
                                    controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    controlMap.put("value", strValueDate);
                                    controlMap.put("time", strDate);
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                    continue;
                                }
                                else if(key.equalsIgnoreCase("location" + i) == true &&
                                        locationKey.equalsIgnoreCase("bw_start") == true)
                                {
                                    // AI 역세 시작 시간 예측
                                    // tagManageList에서 strItemName을 검색
                                    TagManageDTO dto = tagManageList.stream()
                                            .filter(tagManage -> strBwStartTiName.equalsIgnoreCase(tagManage.getItem()))
                                            .findAny()
                                            .orElse(null);

                                    if(dto == null)
                                    {
                                        continue;
                                    }

                                    String strValue = locationMapTemp.get(locationKey).toString();
                                    if(strValue.equalsIgnoreCase("0") == true)
                                    {
                                        strValueDate = "--";
                                    }
                                    else
                                    {
                                        valueDate = aiDateFormat.parse(strValue);
                                        strValueDate = valueDateFormat.format(valueDate);
                                    }
                                    // Kafka에 전송할 값 정의
                                    controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    controlMap.put("value", strValueDate);
                                    controlMap.put("time", strDate);
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                    continue;
                                }
                            }
                        }
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in GAC Process");
                }
                catch(ParseException e)
                {
                    log.error("TextParseException Occurred in GAC Process");
                }
            }

            ///////////////////////////////////////////////
            // Disinfection Process
            ///////////////////////////////////////////////
            // get ai_disinfection_realtime
            AiDisinfectionRealtimeDTO aiDisinfectionRealtime = databaseService.getLatestAiDisinfectionRealtimeValue();
            log.info("getLatestAiDisinfectionRealtimeValue, result:[{}]", aiDisinfectionRealtime != null ? 1 : 0);
            if(aiDisinfectionRealtime != null)
            {
                try
                {
                    LinkedHashMap<String, Object> controlMap;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiDisinfectionRealtime.getUpdate_time());
                    String strBody;

                    // g_pre_operation_mode
                    AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE);
                    if(aiDisinfectionInit != null)
                    {
                        // 운전모드 태그는 전달하지 않음
//                    controlMap = new LinkedHashMap<>();
//                    controlMap.put("tag", aiDisinfectionInit.getName());
//                    controlMap.put("value", aiDisinfectionInit.getValue().intValue());
//                    controlMap.put("time", strDate);
//                    strBody = objectMapper.writeValueAsString(controlMap);
//                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                        // 운전모드 알람 태그는 항상 전송
                        for(TagManageDTO dto : tagManageList)
                        {
                            if(dto.getItem().equalsIgnoreCase("g_pre_operation_mode_a") == true)
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", aiDisinfectionInit.getValue().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                break;
                            }
                        }
                    }

                    // g_peri_operation_mode
                    aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PERI_OPERATION_MODE);
                    if(aiDisinfectionInit != null)
                    {
                        for(TagManageDTO dto : tagManageList)
                        {
                            // 운전모드 알람 태그 전송
                            if(dto.getItem().equalsIgnoreCase("g_peri_operation_mode_a") == true)
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", aiDisinfectionInit.getValue().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                break;
                            }
                        }
                    }

                    // g_post_operation_mode
                    aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE);
                    if(aiDisinfectionInit != null)
                    {
                        for(TagManageDTO dto : tagManageList)
                        {
                            // 운전모드 알람 태그 전송
                            if(dto.getItem().equalsIgnoreCase("g_post_operation_mode_a") == true)
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", aiDisinfectionInit.getValue().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                break;
                            }
                        }
                    }

                    for(TagManageDTO dto : tagManageList)
                    {
                        if(dto.getProcess_code().equalsIgnoreCase(CommonValue.PROCESS_DISINFECTION) != true)
                        {
                            continue;
                        }

                        if(dto.getItem().equalsIgnoreCase("ai_g_ti") == true)
                        {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

//                            log.info("Send AI_RESULT kafka ai_g_ti tag:[{}], value:[{}]", dto.getName(), strDate);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_g_pre1_evaporation") == true)
                        {
                            // 1계열 전염소 증발량 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiDisinfectionRealtime.getAi_g_pre1_evaporation());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_g_pre2_evaporation") == true)
                        {
                            // 2계열 전염소 증발량 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiDisinfectionRealtime.getAi_g_pre2_evaporation());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_g_pre1_chlorination") == true)
                        {
                            // 1계열 전염소 주입률 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiDisinfectionRealtime.getAi_g_pre1_chlorination());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_g_pre2_chlorination") == true)
                        {
                            // 2계열 전염소 주입률 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiDisinfectionRealtime.getAi_g_pre2_chlorination());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_g_post_corrected") == true)
                        {
                            // 후염소 보정값 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiDisinfectionRealtime.getAi_g_post_corrected());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_g_post_chlorination") == true)
                        {
                            // 후염소 주입률 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiDisinfectionRealtime.getAi_g_post_chlorination());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Disinfection Process");
                }
            }

            ///////////////////////////////////////////////
            // Ozone Process
            ///////////////////////////////////////////////
            // get ai_ozone_realtime
            AiOzoneRealtimeDTO aiOzoneRealtime = databaseService.getLatestAiOzoneRealtimeValue();
            log.info("getLatestAiOzoneRealtimeValue, result:[{}]", aiOzoneRealtime != null ? 1 : 0);
            if(aiOzoneRealtime != null)
            {
                try
                {
                    LinkedHashMap<String, Object> controlMap;
                    ObjectMapper objectMapper = new ObjectMapper();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = simpleDateFormat.format(aiOzoneRealtime.getUpdate_time());
                    String strBody;

                    // io_operation_mode
                    AiProcessInitDTO aiOzoneInit = databaseService.getAiOzoneInit(CommonValue.IO_OPERATION_MODE);
                    if(aiOzoneInit != null)
                    {
                        // 운전모드 태그는 전달하지 않음
//                    controlMap = new LinkedHashMap<>();
//                    controlMap.put("tag", aiOzoneInit.getName());
//                    controlMap.put("value", aiOzoneInit.getValue().intValue());
//                    controlMap.put("time", strDate);
//                    strBody = objectMapper.writeValueAsString(controlMap);
//                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                        // 운전모드 알람 태그는 항상 전송
                        for(TagManageDTO dto : tagManageList)
                        {
                            if(dto.getItem().equalsIgnoreCase("io_operation_mode_a") == true)
                            {
                                controlMap = new LinkedHashMap<>();
                                controlMap.put("tag", dto.getName());
                                controlMap.put("value", aiOzoneInit.getValue().intValue() == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                                controlMap.put("time", strDate);
                                strBody = objectMapper.writeValueAsString(controlMap);
                                //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

                                break;
                            }
                        }
                    }

                    for(TagManageDTO dto : tagManageList)
                    {
                        if(dto.getProcess_code().equalsIgnoreCase(CommonValue.PROCESS_OZONE) != true)
                        {
                            continue;
                        }

                        if(dto.getItem().equalsIgnoreCase("ai_io_ti") == true)
                        {
                            // ai update_time
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", strDate);
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);

//                            log.info("Send AI_RESULT kafka ai_io_ti tag:[{}], value:[{}]", dto.getName(), strDate);
                        }
                        else if(dto.getItem().equalsIgnoreCase("ai_io_injection") == true)
                        {
                            // AI 오존 주입률 예측
                            controlMap = new LinkedHashMap<>();
                            controlMap.put("tag", dto.getName());
                            controlMap.put("value", aiOzoneRealtime.getAi_io_injection());
                            controlMap.put("time", strDate);
                            strBody = objectMapper.writeValueAsString(controlMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_RESULT, strBody);
                        }
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Ozone Process");
                }
            }
        }
    }

    // 분석 서버 Check
    @RequestMapping(value = "/internal/analysis", method = RequestMethod.GET)
    public void getAnalysis(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("getAnalysis, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getAnalysis() initialization analysisDate before one hour
        if(analysisDate == null)
        {
            analysisDate = new Date();
            analysisDate.setTime(analysisDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.info("[internal] getAnalysis");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - analysisDate.getTime() > CommonValue.ONE_MINUTE)
        {
            analysisDate = new Date();

            // Resource Manager1 : [GET] cluster/info
            int nActiveState = CommonValue.ACTIVE_STATE_NONE;
            String strHaState, strActiveNodes;
            String strUri = "http://" + globalSystemConfig.getAnalysis1_ResourceManager() + "/ws/v1/cluster/info";
            HttpGet httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);
            StringBuffer stringBuffer;
            ObjectMapper objectMapper = new ObjectMapper();

            HttpResponse response = HttpSend.send(httpGet);
            if(response != null)
            {
                int nStatus = response.getStatusLine().getStatusCode();
                if(nStatus == HttpStatus.SC_OK)
                {
                    // 하둡 클러스터 정보 response에 대한 Parsing
                    stringBuffer = new StringBuffer();

                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try
                    {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while((strLine = bufferedReader.readLine()) != null)
                        {
                            stringBuffer.append(strLine);
                        }

                        // ObjectMapper를 통해 하둡 클러스터 정보 저장
                        HadoopClusterInfoDTO clusterInfo = objectMapper.readValue(stringBuffer.toString(), HadoopClusterInfoDTO.class);
                        strHaState = clusterInfo.getClusterInfo().getHaState();
                        log.info("clusterInfo1, haState:[{}]", strHaState);

                        if(strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true)
                        {
                            nActiveState = CommonValue.ACTIVE_STATE_FIRST;
                        }
                    }
                    catch(IOException e)
                    {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                    }
                    finally
                    {
                        if(inputStreamReader != null)
                        {
                            try
                            {
                                inputStreamReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if(bufferedReader != null)
                        {
                            try
                            {
                                bufferedReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                }
                else
                {
                    strHaState = CommonValue.HASTATE_ERROR;
                }
            }
            else
            {
                strHaState = CommonValue.HASTATE_ERROR;
            }

            // Insert system_monitoring(Hadoop Resource Manager)
            SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHostname(CommonValue.ANALYSIS1_HOSTNAME);
            systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setName(CommonValue.HADOOP_RM);
            systemMonitoringDTO.setValue(strHaState.toUpperCase());
            systemMonitoringDTO.setUpdate_time(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            // Resource Manager2 : cluster/info
            strUri = "http://" + globalSystemConfig.getAnalysis2_ResourceManager() + "/ws/v1/cluster/info";
            httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);

            response = HttpSend.send(httpGet);
            if(response != null)
            {
                int nStatus = response.getStatusLine().getStatusCode();
                if(nStatus == HttpStatus.SC_OK)
                {
                    // 하둡 클러스터 정보 Response에 대한 Parsing
                    stringBuffer = new StringBuffer();

                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try
                    {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while((strLine = bufferedReader.readLine()) != null)
                        {
                            stringBuffer.append(strLine);
                        }

                        // ObjectMapper를 통해 하둡 클러스터 정보 저장
                        HadoopClusterInfoDTO clusterInfo = objectMapper.readValue(stringBuffer.toString(), HadoopClusterInfoDTO.class);
                        strHaState = clusterInfo.getClusterInfo().getHaState();
                        log.info("clusterInfo2, haState:[{}]", strHaState);

                        if(strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true)
                        {
                            nActiveState = CommonValue.ACTIVE_STATE_SECOND;
                        }
                    }
                    catch(IOException e)
                    {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                    }
                    finally
                    {
                        if(inputStreamReader != null)
                        {
                            try
                            {
                                inputStreamReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if(bufferedReader != null)
                        {
                            try
                            {
                                bufferedReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                }
                else
                {
                    strHaState = CommonValue.HASTATE_ERROR;
                }
            }
            else
            {
                strHaState = CommonValue.HASTATE_ERROR;
            }

            // Insert system_monitoring(Hadoop Resource Manager)
            systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHostname(CommonValue.ANALYSIS2_HOSTNAME);
            systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setName(CommonValue.HADOOP_RM);
            systemMonitoringDTO.setValue(strHaState.toUpperCase());
            systemMonitoringDTO.setUpdate_time(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            // Resource Manager1 : cluster/metrics
            if(nActiveState > CommonValue.ACTIVE_STATE_NONE)
            {
                // ACTIVE Server의 주소로 요청
                String strHostname;
                if(nActiveState == CommonValue.ACTIVE_STATE_FIRST)
                {
                    strUri = "http://" + globalSystemConfig.getAnalysis1_ResourceManager() + "/ws/v1/cluster/metrics";
                    strHostname = CommonValue.ANALYSIS1_HOSTNAME;
                }
                else
                {
                    strUri = "http://" + globalSystemConfig.getAnalysis2_ResourceManager() + "/ws/v1/cluster/metrics";
                    strHostname = CommonValue.ANALYSIS2_HOSTNAME;
                }

                httpGet = new HttpGet(strUri);
                httpGet.setConfig(requestConfig);

                response = HttpSend.send(httpGet);
                if(response != null)
                {
                    int nStatus = response.getStatusLine().getStatusCode();
                    if(nStatus == HttpStatus.SC_OK)
                    {
                        // Metric 정보 Response에 대한 Parsing
                        stringBuffer = new StringBuffer();

                        BufferedReader bufferedReader = null;
                        InputStreamReader inputStreamReader = null;
                        try
                        {
                            inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                            bufferedReader = new BufferedReader(inputStreamReader);

                            String strLine;
                            while((strLine = bufferedReader.readLine()) != null)
                            {
                                stringBuffer.append(strLine);
                            }

                            // Cluster Metric 정보를 저장하여 active node 계산
                            HadoopClusterMetricsDTO clusterMetrics =
                                    objectMapper.readValue(stringBuffer.toString(), HadoopClusterMetricsDTO.class);

                            int nTemp = clusterMetrics.getClusterMetrics().getActiveNodes();
                            strActiveNodes = String.format("%d", nTemp);
                            log.info("clusterMetrics, activeNodes:[{}]", clusterMetrics.getClusterMetrics().getActiveNodes());
                        }
                        catch(IOException e)
                        {
                            log.error("Invalid Body or BufferedReader...");
                            strActiveNodes = "-";
                        }
                        finally
                        {
                            if(inputStreamReader != null)
                            {
                                try
                                {
                                    inputStreamReader.close();
                                }
                                catch(IOException e)
                                {
                                    log.error("inputStreamReader Close Exception occurred");
                                }
                            }
                            if(bufferedReader != null)
                            {
                                try
                                {
                                    bufferedReader.close();
                                }
                                catch(IOException e)
                                {
                                    log.error("bufferedReader Close Exception occurred");
                                }
                            }
                        }
                    }
                    else
                    {
                        strActiveNodes = "-";
                    }
                }
                else
                {
                    strActiveNodes = "-";
                }

                // Insert system_monitoring(Node Manager)
                systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHostname(strHostname);
                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
                systemMonitoringDTO.setName(CommonValue.HADOOP_NM);
                systemMonitoringDTO.setValue(strActiveNodes);
                systemMonitoringDTO.setUpdate_time(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);
            }
            else
            {
                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_ANALYSIS_OFF,
                        CommonValue.ANALYSIS1_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }


            // NameNode, DataNode1 : jmx
            nActiveState = CommonValue.ACTIVE_STATE_NONE;
            strUri = "http://" + globalSystemConfig.getAnalysis1_NameNode() + "/jmx?qry=Hadoop:service=NameNode,name=FSNamesystem";
            httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);

            response = HttpSend.send(httpGet);
            if(response != null)
            {
                int nStatus = response.getStatusLine().getStatusCode();
                if(nStatus == HttpStatus.SC_OK)
                {
                    // JMX Response에 대한 Parsing
                    stringBuffer = new StringBuffer();
                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try
                    {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while((strLine = bufferedReader.readLine()) != null)
                        {
                            stringBuffer.append(strLine);
                        }

                        // JMX 정보를 저장하고 data node 저장
                        HadoopJmxBeans jmxBeans = objectMapper.readValue(stringBuffer.toString(), HadoopJmxBeans.class);
                        strHaState = jmxBeans.getBeans().get(0).getTagHaSate();
                        int nTemp = jmxBeans.getBeans().get(0).getNumLiveDataNodes();
                        strActiveNodes = String.format("%d", nTemp);
                        log.info("jmxBeans, HAState:[{}], NumLiveDataNodes:[{}]",
                                jmxBeans.getBeans().get(0).getTagHaSate(),
                                jmxBeans.getBeans().get(0).getNumLiveDataNodes());
                    }
                    catch(IOException e)
                    {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                        strActiveNodes = "-";
                    }
                    finally
                    {
                        if(inputStreamReader != null)
                        {
                            try
                            {
                                inputStreamReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if(bufferedReader != null)
                        {
                            try
                            {
                                bufferedReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                }
                else
                {
                    strHaState = CommonValue.HASTATE_ERROR;
                    strActiveNodes = "-";
                }
            }
            else
            {
                strHaState = CommonValue.HASTATE_ERROR;
                strActiveNodes = "-";
            }

            // Insert system_monitoring(Name Node)
            systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHostname(CommonValue.ANALYSIS1_HOSTNAME);
            systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setName(CommonValue.HADOOP_NN);
            systemMonitoringDTO.setValue(strHaState.toUpperCase());
            systemMonitoringDTO.setUpdate_time(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            if(strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true)
            {
                nActiveState = CommonValue.ACTIVE_STATE_FIRST;

                // Insert system_monitoring
                systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHostname(CommonValue.ANALYSIS1_HOSTNAME);
                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
                systemMonitoringDTO.setName(CommonValue.HADOOP_DN);
                systemMonitoringDTO.setValue(strActiveNodes);
                systemMonitoringDTO.setUpdate_time(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);
            }

            // NameNode, DataNode2 : jmx
            strUri = "http://" + globalSystemConfig.getAnalysis2_NameNode() + "/jmx?qry=Hadoop:service=NameNode,name=FSNamesystem";
            httpGet = new HttpGet(strUri);
            httpGet.setConfig(requestConfig);

            response = HttpSend.send(httpGet);
            if(response != null)
            {
                int nStatus = response.getStatusLine().getStatusCode();
                if(nStatus == HttpStatus.SC_OK)
                {
                    // JMX Response에 대한 Parsing
                    stringBuffer = new StringBuffer();
                    BufferedReader bufferedReader = null;
                    InputStreamReader inputStreamReader = null;
                    try
                    {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while((strLine = bufferedReader.readLine()) != null)
                        {
                            stringBuffer.append(strLine);
                        }

                        // JMX 정보를 저장하고 data node 저장
                        HadoopJmxBeans jmxBeans = objectMapper.readValue(stringBuffer.toString(), HadoopJmxBeans.class);
                        strHaState = jmxBeans.getBeans().get(0).getTagHaSate();
                        int nTemp = jmxBeans.getBeans().get(0).getNumLiveDataNodes();
                        strActiveNodes = String.format("%d", nTemp);
                        log.info("jmxBeans, HAState:[{}], NumLiveDataNodes:[{}]",
                                jmxBeans.getBeans().get(0).getTagHaSate(),
                                jmxBeans.getBeans().get(0).getNumLiveDataNodes());
                    }
                    catch(IOException e)
                    {
                        log.error("Invalid Body or BufferedReader...");
                        strHaState = CommonValue.HASTATE_ERROR;
                        strActiveNodes = "-";
                    }
                    finally
                    {
                        if(inputStreamReader != null)
                        {
                            try
                            {
                                inputStreamReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("inputStreamReader Close Exception occurred");
                            }
                        }
                        if(bufferedReader != null)
                        {
                            try
                            {
                                bufferedReader.close();
                            }
                            catch(IOException e)
                            {
                                log.error("bufferedReader Close Exception occurred");
                            }
                        }
                    }
                }
                else
                {
                    strHaState = CommonValue.HASTATE_ERROR;
                    strActiveNodes = "-";
                }
            }
            else
            {
                strHaState = CommonValue.HASTATE_ERROR;
                strActiveNodes = "-";
            }

            // Insert system_monitoring(Name Node)
            systemMonitoringDTO = new SystemMonitoringDTO();
            systemMonitoringDTO.setHostname(CommonValue.ANALYSIS2_HOSTNAME);
            systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
            systemMonitoringDTO.setName(CommonValue.HADOOP_NN);
            systemMonitoringDTO.setValue(strHaState.toUpperCase());
            systemMonitoringDTO.setUpdate_time(new Date());
            databaseService.addSystemMonitoring(systemMonitoringDTO);

            if(strHaState.equalsIgnoreCase(CommonValue.HASTATE_ACTIVE) == true)
            {
                nActiveState = CommonValue.ACTIVE_STATE_SECOND;

                // Insert system_monitoring
                systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHostname(CommonValue.ANALYSIS2_HOSTNAME);
                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_ANALYSIS_DB);
                systemMonitoringDTO.setName(CommonValue.HADOOP_DN);
                systemMonitoringDTO.setValue(strActiveNodes);
                systemMonitoringDTO.setUpdate_time(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);
            }

            // Name Node Active Check(Insert alarm_notify, same resource manager's alarm)
            if(nActiveState == CommonValue.ACTIVE_STATE_NONE)
            {
                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_ANALYSIS_OFF,
                        CommonValue.ANALYSIS1_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }
        }
    }

    // 데이터 수집기 상태 확인
    @RequestMapping(value = "/internal/daq", method = RequestMethod.GET)
    public void getDaq(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("getDaq, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getDaq() initialization daqDate before one hour
        if(daqDate == null)
        {
            daqDate = new Date();
            daqDate.setTime(daqDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.info("[internal] getDaq");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - daqDate.getTime() > CommonValue.ONE_MINUTE)
        {
            daqDate = new Date();

            // 최근 5분 간 데이터 수집기 HealthCheck 이력을 조회하기 위한 Date 선언
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            Date startTime = calendar.getTime();

            List<SystemMonitoringDTO> systemMonitoringList = databaseService.getLatestSystemMonitoring(startTime);
            log.info("getLatestSystemMonitoring, result:[{}]", systemMonitoringList.size());

            String strDaq1Value = CommonValue.ALARM_VALUE_OFF;
            String strDaq2Value = CommonValue.ALARM_VALUE_OFF;

            // 최근 5분 간 데이터 수집기 측정 이력이 있다면 해당 값 저장
            for(SystemMonitoringDTO dto : systemMonitoringList)
            {
                if(dto.getType() == CommonValue.MONITORING_TYPE_COLLECTOR)
                {
                    if(dto.getHostname().equalsIgnoreCase(CommonValue.COLLECTOR1_HOSTNAME) == true)
                    {
                        strDaq1Value = dto.getValue();
                    }
                    else if(dto.getHostname().equalsIgnoreCase(CommonValue.COLLECTOR2_HOSTNAME) == true)
                    {
                        strDaq2Value = dto.getValue();
                    }
                }
            }

            // If alarm value is 'OFF' then, insert alarm_notify and insert system_monitoring
            if(strDaq1Value.equalsIgnoreCase(CommonValue.ALARM_VALUE_OFF) == true)
            {
                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHostname(CommonValue.COLLECTOR1_HOSTNAME);
                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
                systemMonitoringDTO.setName(CommonValue.COLLECTOR1_HOSTNAME);
                systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_OFF);
                systemMonitoringDTO.setUpdate_time(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);

                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_COLLECTOR_OFF1,
                        CommonValue.COLLECTOR1_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }

            // If alarm value is 'OFF' then, insert alarm_notify and insert system_monitoring
            if(strDaq2Value.equalsIgnoreCase(CommonValue.ALARM_VALUE_OFF) == true)
            {
                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
                systemMonitoringDTO.setHostname(CommonValue.COLLECTOR2_HOSTNAME);
                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
                systemMonitoringDTO.setName(CommonValue.COLLECTOR2_HOSTNAME);
                systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_OFF);
                systemMonitoringDTO.setUpdate_time(new Date());
                databaseService.addSystemMonitoring(systemMonitoringDTO);

                // Insert alarm_notify & SCADA send
                alarmService.alarmNotify(
                        CommonValue.ALARM_CODE_COLLECTOR_OFF2,
                        CommonValue.COLLECTOR2_HOSTNAME,
                        CommonValue.ALARM_VALUE_OFF,
                        true);
            }
//            boolean bResponseOK = false;
//
//            // DAQ1 API Check
//            String strUri = "http://" + globalSystemConfig.getScada1_daq() + "/api/plugins.json";
//            HttpGet httpGet = new HttpGet(strUri);
//            httpGet.setConfig(requestConfig);
//
//            HttpResponse response = HttpSend.send(httpGet);
//            if(response != null)
//            {
//                int nStatus = response.getStatusLine().getStatusCode();
//                log.info("DAQ1 HealthCheck...Response:[{}]", nStatus);
//                if(nStatus == HttpStatus.SC_OK)
//                {
//                    bResponseOK = true;
//                }
//                else
//                {
//                    bResponseOK = false;
//                }
//            }
//            else
//            {
//                bResponseOK = false;
//                log.info("DAQ1 HealthCheck...Response:[ERROR]");
//            }
//
//            // According to bResponseOK value, Insert system_monitoring
//            if(bResponseOK == true)
//            {
//                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
//                systemMonitoringDTO.setHostname(CommonValue.COLLECTOR1_HOSTNAME);
//                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
//                systemMonitoringDTO.setName(CommonValue.COLLECTOR1_HOSTNAME);
//                systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_ON);
//                systemMonitoringDTO.setUpdate_time(new Date());
//                databaseService.addSystemMonitoring(systemMonitoringDTO);
//            }
//            else
//            {
//                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
//                systemMonitoringDTO.setHostname(CommonValue.COLLECTOR1_HOSTNAME);
//                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
//                systemMonitoringDTO.setName(CommonValue.COLLECTOR1_HOSTNAME);
//                systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_OFF);
//                systemMonitoringDTO.setUpdate_time(new Date());
//                databaseService.addSystemMonitoring(systemMonitoringDTO);
//
//                // Insert alarm_notify & SCADA send
//                alarmService.alarmNotify(
//                        CommonValue.ALARM_CODE_DAQ_OFF1,
//                        CommonValue.COLLECTOR1_HOSTNAME,
//                        CommonValue.ALARM_VALUE_OFF,
//                        true);
//            }
//
//            // DAQ2 API Check
//            bResponseOK = false;
//            strUri = "http://" + globalSystemConfig.getScada2_daq() + "/api/plugins.json";
//            httpGet = new HttpGet(strUri);
//            httpGet.setConfig(requestConfig);
//
//            response = HttpSend.send(httpGet);
//            if(response != null)
//            {
//                int nStatus = response.getStatusLine().getStatusCode();
//                log.info("DAQ2 HealthCheck...Response:[{}]", nStatus);
//                if(nStatus == HttpStatus.SC_OK)
//                {
//                    bResponseOK = true;
//                }
//                else
//                {
//                    bResponseOK = false;
//                }
//            }
//            else
//            {
//                bResponseOK = false;
//                log.info("DAQ2 HealthCheck...Response:[ERROR]");
//            }
//
//            // According to bResponseOK value, Insert system_monitoring
//            if(bResponseOK == true)
//            {
//                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
//                systemMonitoringDTO.setHostname(CommonValue.COLLECTOR2_HOSTNAME);
//                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
//                systemMonitoringDTO.setName(CommonValue.COLLECTOR2_HOSTNAME);
//                systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_ON);
//                systemMonitoringDTO.setUpdate_time(new Date());
//                databaseService.addSystemMonitoring(systemMonitoringDTO);
//            }
//            else
//            {
//                SystemMonitoringDTO systemMonitoringDTO = new SystemMonitoringDTO();
//                systemMonitoringDTO.setHostname(CommonValue.COLLECTOR2_HOSTNAME);
//                systemMonitoringDTO.setType(CommonValue.MONITORING_TYPE_COLLECTOR);
//                systemMonitoringDTO.setName(CommonValue.COLLECTOR2_HOSTNAME);
//                systemMonitoringDTO.setValue(CommonValue.ALARM_VALUE_OFF);
//                systemMonitoringDTO.setUpdate_time(new Date());
//                databaseService.addSystemMonitoring(systemMonitoringDTO);
//
//                // Insert alarm_notify & SCADA send
//                alarmService.alarmNotify(
//                        CommonValue.ALARM_CODE_DAQ_OFF2,
//                        CommonValue.COLLECTOR2_HOSTNAME,
//                        CommonValue.ALARM_VALUE_OFF,
//                        true);
//            }
        }
    }

    // 공정별 AI 제어값을 Kafka로 전송
    @RequestMapping(value = "/internal/control", method = RequestMethod.GET)
    public void getControl(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("getControl, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getControl() initialization controlDate before one hour
        if(controlDate == null)
        {
            controlDate = new Date();
            controlDate.setTime(controlDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.info("[internal] getControl");

        // Check ten seconds after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - controlDate.getTime() > propertiesControlCheck.getPeriod())
        {
            controlDate = new Date();

            //////////////////////////////////////////////////////////////////////
            // Receiving Process
            //////////////////////////////////////////////////////////////////////
            // 1. get operation mode
            AiProcessInitDTO aiReceivingInit = databaseService.getAiReceivingInit(CommonValue.B_OPERATION_MODE);
            log.info("getAiReceivingInit, result:[{}]", aiReceivingInit != null ? 1 : 0);

            if(aiReceivingInit != null)
            {
                int nOperationMode = aiReceivingInit.getValue().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL)
                {
                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRun_time(runTime);
                    queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);

                    List<AiProcessControlDTO> aiReceivingControlList = databaseService.getListAiReceivingControl(queryDto);
                    if(aiReceivingControlList.size() > 0)
                    {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try
                        {
                            for(AiProcessControlDTO dto : aiReceivingControlList)
                            {
                                if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO)
                                {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo =
                                            alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_RECEIVING_AI_CONTROL);
                                    if(alarmInfo != null)
                                    {
                                        // KAFKA topic is called only once.
                                        if(bFirst == true)
                                        {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                                            popupMap.put("message", alarmInfo.getDisplay_name());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }

                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                                        databaseService.modAiReceivingControlKafkaFlag(updateDto);
                                    }
                                    else
                                    {
                                        log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_RECEIVING_AI_CONTROL);
                                    }
                                }
                                else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO)
                                {
                                    // 4. if operation_mode==2 (full_auto)

                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    if(dto.getValue().equalsIgnoreCase(CommonValue.CONTROL_TRUE) == true)
                                    {
                                        controlMap.put("value", true);
                                    }
                                    else
                                    {
                                        controlMap.put("value", Float.parseFloat(dto.getValue()));
                                    }
                                    controlMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2. update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                                    databaseService.modAiReceivingControlKafkaFlag(updateDto);
                                }
                            }
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Receiving Control Process");
                        }
                        catch(NumberFormatException e)
                        {
                            log.error("NumberException Occurred in Receiving Control Process");
                        }
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // Coagulant Process
            //////////////////////////////////////////////////////////////////////
            // 1. get operation mode
            AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE);
            log.info("getAiCoagulantInit, result:[{}]", aiCoagulantInit != null ? 1 : 0);

            if(aiCoagulantInit != null)
            {
                int nOperationMode = aiCoagulantInit.getValue().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL)
                {
                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRun_time(runTime);
                    queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);

                    List<AiProcessControlDTO> aiCoagulantControlList = databaseService.getListAiCoagulantControl(queryDto);
                    if(aiCoagulantControlList.size() > 0)
                    {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try
                        {
                            for(AiProcessControlDTO dto : aiCoagulantControlList)
                            {
                                if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO)
                                {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo =
                                            alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL);
                                    if(alarmInfo != null)
                                    {
                                        // KAFKA topic is called only once.
                                        if(bFirst == true)
                                        {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                                            popupMap.put("message", alarmInfo.getDisplay_name());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }

                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                                        databaseService.modAiCoagulantControlKafkaFlag(updateDto);
                                    }
                                    else
                                    {
                                        log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_COAGULANT_AI_CONTROL);
                                    }
                                }
                                else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO)
                                {
                                    // 4. if operation_mode==2 (full_auto)

                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    if(dto.getValue().equalsIgnoreCase(CommonValue.CONTROL_TRUE) == true)
                                    {
                                        controlMap.put("value", true);
                                    }
                                    else
                                    {
                                        controlMap.put("value", Float.parseFloat(dto.getValue()));
                                    }
                                    controlMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2. update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                                    databaseService.modAiCoagulantControlKafkaFlag(updateDto);
                                }
                            }
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Receiving Control Process");
                        }
                        catch(NumberFormatException e)
                        {
                            log.error("NumberException Occurred in Receiving Control Process");
                        }
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // Mixing Process
            //////////////////////////////////////////////////////////////////////
            // 1. get operation mode
            AiProcessInitDTO aiMixingInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE);
            log.info("getAiMixingInit, result:[{}]", aiMixingInit != null ? 1 : 0);

            if(aiMixingInit != null)
            {
                int nOperationMode = aiMixingInit.getValue().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL)
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRun_time(runTime);
                    queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);

                    // 2. get latest(10 minutes) control value(kafka_flag = 0)
                    List<AiProcessControlDTO> aiMixingControlList = databaseService.getListAiMixingControl(queryDto);
                    if(aiMixingControlList.size() > 0)
                    {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try
                        {
                            for(AiProcessControlDTO dto : aiMixingControlList)
                            {
                                if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO)
                                {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo =
                                            alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_MIXING_AI_CONTROL);
                                    if(alarmInfo != null)
                                    {
                                        // KAFKA topic is called only once.
                                        if(bFirst == true)
                                        {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                                            popupMap.put("message", alarmInfo.getDisplay_name());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }

                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                                        databaseService.modAiMixingControlKafkaFlag(updateDto);
                                    }
                                    else
                                    {
                                        log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_MIXING_AI_CONTROL);
                                    }
                                }
                                else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO)
                                {
                                    // 4. if operation_mode==2 (full_auto)

                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    controlMap.put("value", Float.parseFloat(dto.getValue()));
                                    controlMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2. update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                                    databaseService.modAiMixingControlKafkaFlag(updateDto);
                                }
                            }
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Mixing Control Process");
                        }
                        catch(NumberFormatException e)
                        {
                            log.error("NumberException Occurred in Mixing Control Process");
                        }
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // Sedimentation Process
            //////////////////////////////////////////////////////////////////////
            // 1. get operation mode
            AiProcessInitDTO aiSedimentationInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE);
            log.info("getAiSedimentationInit, result:[{}]", aiSedimentationInit != null ? 1 : 0);

            if(aiSedimentationInit != null)
            {
                int nOperationMode = aiSedimentationInit.getValue().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL)
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRun_time(runTime);
                    queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);

                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    List<AiProcessControlDTO> aiSedimentationControlList = databaseService.getListAiSedimentationControl(queryDto);
                    if(aiSedimentationControlList.size() > 0)
                    {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try
                        {
                            for(AiProcessControlDTO dto : aiSedimentationControlList)
                            {
                                if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO)
                                {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo =
                                            alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_SEDIMENTATION_AI_CONTROL);
                                    if(alarmInfo != null)
                                    {
                                        // KAFKA topic is called only once.
                                        if(bFirst == true)
                                        {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                                            popupMap.put("message", alarmInfo.getDisplay_name());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }

                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                                        databaseService.modAiSedimentationControlKafkaFlag(updateDto);
                                    }
                                    else
                                    {
                                        log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_SEDIMENTATION_AI_CONTROL);
                                    }
                                }
                                else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO)
                                {
                                    // 4. if operation_mode==2 (full_auto)

                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    if(dto.getValue().equalsIgnoreCase(CommonValue.CONTROL_TRUE) == true)
                                    {
                                        controlMap.put("value", true);
                                    }
                                    else
                                    {
                                        controlMap.put("value", Integer.parseInt(dto.getValue()));
                                    }
                                    controlMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2.update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                                    databaseService.modAiSedimentationControlKafkaFlag(updateDto);
                                }
                            }
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Sedimentation Control Process");
                        }
                        catch(NumberFormatException e)
                        {
                            log.error("NumberException Occurred in Sedimentation Control Process");
                        }
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // Filter Process
            //////////////////////////////////////////////////////////////////////
            // 1. get operation mode
            AiProcessInitDTO aiFilterInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE);
            log.info("getAiFilterInit, result:[{}]", aiFilterInit != null ? 1 : 0);

            if(aiFilterInit != null)
            {
                int nOperationMode = aiFilterInit.getValue().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL)
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRun_time(runTime);
                    queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);

                    // 2. get latest(10 minutes) control value(kafka_flag = 0)
                    List<AiProcessControlDTO> aiFilterControlList = databaseService.getListAiFilterControl(queryDto);
                    if(aiFilterControlList.size() > 0)
                    {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try
                        {
                            for(AiProcessControlDTO dto : aiFilterControlList)
                            {
                                if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO)
                                {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo =
                                            alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_FILTER_AI_CONTROL);
                                    if(alarmInfo != null)
                                    {
                                        // KAFKA topic is called only once.
                                        if(bFirst == true)
                                        {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                                            popupMap.put("message", alarmInfo.getDisplay_name());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }

                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                                        databaseService.modAiFilterControlKafkaFlag(updateDto);
                                    }
                                    else
                                    {
                                        log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_FILTER_AI_CONTROL);
                                    }
                                }
                                else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO)
                                {
                                    // 4. if operation_mode==2 (full_auto)
                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    if(dto.getValue().equalsIgnoreCase(CommonValue.CONTROL_TRUE) == true)
                                    {
                                        controlMap.put("value", true);
                                    }
                                    else
                                    {
                                        controlMap.put("value", Integer.parseInt(dto.getValue()));
                                    }
                                    controlMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2. update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                                    databaseService.modAiFilterControlKafkaFlag(updateDto);
                                }
                            }
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Filter Control Process");
                        }
                        catch(NumberFormatException e)
                        {
                            log.error("NumberException Occurred in Filter Control Process");
                        }
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // GAC Process
            //////////////////////////////////////////////////////////////////////
            // 1. get operation mode
            AiProcessInitDTO aiGacInit = databaseService.getAiGacInit(CommonValue.I_OPERATION_MODE);
            log.info("getAiGacInit, result:[{}]", aiGacInit != null ? 1 : 0);

            if(aiGacInit != null)
            {
                int nOperationMode = aiGacInit.getValue().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL)
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRun_time(runTime);
                    queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);

                    // 2. get latest(10 minutes) control value(kafka_flag = 0)
                    List<AiProcessControlDTO> aiGacControlList = databaseService.getListAiGacControl(queryDto);
                    if(aiGacControlList.size() > 0)
                    {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try
                        {
                            for(AiProcessControlDTO dto : aiGacControlList)
                            {
                                if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO)
                                {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo =
                                            alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_GAC_AI_CONTROL);
                                    if(alarmInfo != null)
                                    {
                                        // KAFKA topic is called only once.
                                        if(bFirst == true)
                                        {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                                            popupMap.put("message", alarmInfo.getDisplay_name());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }

                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                                        databaseService.modAiGacControlKafkaFlag(updateDto);
                                    }
                                    else
                                    {
                                        log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_GAC_AI_CONTROL);
                                    }
                                }
                                else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO)
                                {
                                    // 4. if operation_mode==2 (full_auto)
                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    if(dto.getValue().equalsIgnoreCase(CommonValue.CONTROL_TRUE) == true)
                                    {
                                        controlMap.put("value", true);
                                    }
                                    else
                                    {
                                        controlMap.put("value", Integer.parseInt(dto.getValue()));
                                    }
                                    controlMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2. update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                                    databaseService.modAiGacControlKafkaFlag(updateDto);
                                }
                            }
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessException Occurred in GAC Control Process");
                        }
                        catch(NumberFormatException e)
                        {
                            log.error("NumberException Occurred in GAC Control Process");
                        }
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // Disinfection Process
            //////////////////////////////////////////////////////////////////////
            // 1. get operation mode - pre disinfection
            AiProcessInitDTO aiPreDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE);
            log.info("getAiDisinfectionInit, result:[{}]", aiPreDisinfectionInit != null ? 1 : 0);

            if(aiPreDisinfectionInit != null)
            {
                int nOperationMode = aiPreDisinfectionInit.getValue().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL)
                {
                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRun_time(runTime);
                    queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);

                    List<AiProcessControlDTO> aiDisinfectionControlList = databaseService.getListAiPreDisinfectionControl(queryDto);
                    if(aiDisinfectionControlList.size() > 0)
                    {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try
                        {
                            for(AiProcessControlDTO dto : aiDisinfectionControlList)
                            {
                                if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO)
                                {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo =
                                            alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_DISINFECTION_AI_CONTROL);
                                    if(alarmInfo != null)
                                    {
                                        // KAFKA topic is called only once.
                                        if(bFirst == true)
                                        {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                                            popupMap.put("message", alarmInfo.getDisplay_name());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }

                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                                        databaseService.modAiPreDisinfectionControlKafkaFlag(updateDto);
                                    }
                                    else
                                    {
                                        log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_DISINFECTION_AI_CONTROL);
                                    }
                                }
                                else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO)
                                {
                                    // 4. if operation_mode==2 (full_auto)

                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    controlMap.put("value", Float.parseFloat(dto.getValue()));
                                    controlMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2. update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                                    databaseService.modAiPreDisinfectionControlKafkaFlag(updateDto);
                                }
                            }
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Pre Disinfection Control Process");
                        }
                        catch(NumberFormatException e)
                        {
                            log.error("NumberException Occurred in Pre Disinfection Control Process");
                        }
                    }
                }
            }

            // 1. get operation mode - peri disinfection
            AiProcessInitDTO aiPeriDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PERI_OPERATION_MODE);
            log.info("getAiDisinfectionInit, result:[{}]", aiPeriDisinfectionInit != null ? 1 : 0);

            if(aiPeriDisinfectionInit != null)
            {
                int nOperationMode = aiPeriDisinfectionInit.getValue().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL)
                {
                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRun_time(runTime);
                    queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);

                    List<AiProcessControlDTO> aiDisinfectionControlList = databaseService.getListAiPeriDisinfectionControl(queryDto);
                    if(aiDisinfectionControlList.size() > 0)
                    {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try
                        {
                            for(AiProcessControlDTO dto : aiDisinfectionControlList)
                            {
                                if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO)
                                {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo =
                                            alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_DISINFECTION_AI_CONTROL);
                                    if(alarmInfo != null)
                                    {
                                        // KAFKA topic is called only once.
                                        if(bFirst == true)
                                        {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                                            popupMap.put("message", alarmInfo.getDisplay_name());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }

                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                                        databaseService.modAiPeriDisinfectionControlKafkaFlag(updateDto);
                                    }
                                    else
                                    {
                                        log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_DISINFECTION_AI_CONTROL);
                                    }
                                }
                                else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO)
                                {
                                    // 4. if operation_mode==2 (full_auto)

                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    controlMap.put("value", Float.parseFloat(dto.getValue()));
                                    controlMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2. update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                                    databaseService.modAiPeriDisinfectionControlKafkaFlag(updateDto);
                                }
                            }
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Peri Disinfection Control Process");
                        }
                        catch(NumberFormatException e)
                        {
                            log.error("NumberException Occurred in Peri Disinfection Control Process");
                        }
                    }
                }
            }

            // 1. get operation mode - post disinfection
            AiProcessInitDTO aiPostDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE);
            log.info("getAiDisinfectionInit, result:[{}]", aiPostDisinfectionInit != null ? 1 : 0);

            if(aiPostDisinfectionInit != null)
            {
                int nOperationMode = aiPostDisinfectionInit.getValue().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL)
                {
                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRun_time(runTime);
                    queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);

                    List<AiProcessControlDTO> aiDisinfectionControlList = databaseService.getListAiPostDisinfectionControl(queryDto);
                    if(aiDisinfectionControlList.size() > 0)
                    {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try
                        {
                            for(AiProcessControlDTO dto : aiDisinfectionControlList)
                            {
                                if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO)
                                {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo =
                                            alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_DISINFECTION_AI_CONTROL);
                                    if(alarmInfo != null)
                                    {
                                        // KAFKA topic is called only once.
                                        if(bFirst == true)
                                        {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                                            popupMap.put("message", alarmInfo.getDisplay_name());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }

                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                                        databaseService.modAiPostDisinfectionControlKafkaFlag(updateDto);
                                    }
                                    else
                                    {
                                        log.error("Does not exist alarmInfo:[{]]", CommonValue.ALARM_CODE_DISINFECTION_AI_CONTROL);
                                    }
                                }
                                else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO)
                                {
                                    // 4. if operation_mode==2 (full_auto)

                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    controlMap.put("value", Float.parseFloat(dto.getValue()));
                                    controlMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2. update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                                    databaseService.modAiPostDisinfectionControlKafkaFlag(updateDto);
                                }
                            }
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Post Disinfection Control Process");
                        }
                        catch(NumberFormatException e)
                        {
                            log.error("NumberException Occurred in Post Disinfection Control Process");
                        }
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // Ozone Process
            //////////////////////////////////////////////////////////////////////
            // 1. get operation mode
            AiProcessInitDTO aiOzoneInit = databaseService.getAiOzoneInit(CommonValue.IO_OPERATION_MODE);
            log.info("getAiOzoneInit, result:[{}]", aiOzoneInit != null ? 1 : 0);

            if(aiOzoneInit != null)
            {
                int nOperationMode = aiOzoneInit.getValue().intValue();

                // 수동 모드일 경우 전송하지 않음
                if(nOperationMode > CommonValue.OPERATION_MODE_MANUAL)
                {
                    // 2. get latest(10minutes) control value(kafka_flag = 0)
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, -10);
                    Date runTime = calendar.getTime();

                    AiProcessControlDTO queryDto = new AiProcessControlDTO();
                    queryDto.setRun_time(runTime);
                    queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);

                    List<AiProcessControlDTO> aiOzoneControlList = databaseService.getListAiOzoneControl(queryDto);
                    if(aiOzoneControlList.size() > 0)
                    {
                        String strBody;
                        boolean bFirst = true;
                        ObjectMapper objectMapper = new ObjectMapper();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try
                        {
                            for(AiProcessControlDTO dto : aiOzoneControlList)
                            {
                                if(nOperationMode == CommonValue.OPERATION_MODE_SEMI_AUTO)
                                {
                                    // 3. if operation_mode==1 (semi_auto)
                                    // 3-1. send control value to kafka ai_popup
                                    AlarmInfoDTO alarmInfo =
                                            alarmInfoList.getAlarmInfoFromAlarmCode(CommonValue.ALARM_CODE_OZONE_AI_CONTROL);
                                    if(alarmInfo != null)
                                    {
                                        // KAFKA topic is called only once.
                                        if(bFirst == true)
                                        {
                                            LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                                            popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                                            popupMap.put("message", alarmInfo.getDisplay_name());
                                            popupMap.put("url", alarmInfo.getUrl());
                                            popupMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                            strBody = objectMapper.writeValueAsString(popupMap);
                                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);

                                            bFirst = false;
                                        }

                                        // 3-2. update kafka_flag=1 (kafka_popup)
                                        AiProcessControlDTO updateDto = dto;
                                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                                        databaseService.modAiOzoneControlKafkaFlag(updateDto);
                                    }
                                    else
                                    {
                                        log.error("Does not exist alarmInfo:[{}]", CommonValue.ALARM_CODE_OZONE_AI_CONTROL);
                                    }
                                }
                                else if(nOperationMode == CommonValue.OPERATION_MODE_FULL_AUTO)
                                {
                                    // 4. if operation_mode==2 (full_auto)

                                    // 4-1. send control value to kafka ai_control
                                    LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                                    controlMap.put("tag", dto.getName());
                                    if(dto.getValue().equalsIgnoreCase(CommonValue.CONTROL_TRUE) == true)
                                    {
                                        controlMap.put("value", true);
                                    }
                                    else
                                    {
                                        controlMap.put("value", Float.parseFloat(dto.getValue()));
                                    }
                                    controlMap.put("time", simpleDateFormat.format(dto.getRun_time()));
                                    strBody = objectMapper.writeValueAsString(controlMap);
                                    //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                                    // 4-2. update kafka_flag=3 (kafka_send)
                                    AiProcessControlDTO updateDto = dto;
                                    updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                                    databaseService.modAiOzoneControlKafkaFlag(updateDto);
                                }
                            }
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Receiving Control Process");
                        }
                        catch(NumberFormatException e)
                        {
                            log.error("NumberException Occurred in Receiving Control Process");
                        }
                    }
                }
            }
        }
    }

    // 통합 운영 시스템 알람을 Kafka로 전송
    @RequestMapping(value = "/internal/alarm", method = RequestMethod.GET)
    public void getAlarm(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("getAlarm, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getAlarm() initialization alarmDate before one hour
        if(alarmDate == null)
        {
            alarmDate = new Date();
            alarmDate.setTime(alarmDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.info("[internal] getAlarm");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - alarmDate.getTime() > CommonValue.THIRTY_SECOND)
        {
            alarmDate = new Date();

            // 1. get latest(1minute) control value(kafka_flag = 0)
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -1);
            Date alarmTime = calendar.getTime();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String strBody;
            ObjectMapper objectMapper = new ObjectMapper();

            //////////////////////////////////////////////////////////////////////
            // 2. Receiving Process
            //////////////////////////////////////////////////////////////////////
            // get ai_receiving_alarm
            List<AiProcessAlarmDTO> aiReceivingAlarmList =
                    databaseService.getAllAiReceivingAlarm(alarmTime, CommonValue.KAFKA_FLAG_INIT);
            if(aiReceivingAlarmList.size() > 0)
            {
                for(AiProcessAlarmDTO dto : aiReceivingAlarmList)
                {
                    AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(dto.getAlarm_id());
                    if(alarmInfo != null)
                    {
                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                        popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                        popupMap.put("message", alarmInfo.getDisplay_name());
                        popupMap.put("url", alarmInfo.getUrl());
                        popupMap.put("time", simpleDateFormat.format(dto.getAlarm_time()));

                        // 3. Send Kafka ai_popup
                        try
                        {
                            strBody = objectMapper.writeValueAsString(popupMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Filter Alarm Process");
                        }

                        // 4. Update kafka_flag=1
                        AiProcessAlarmDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                        databaseService.modAiReceivingAlarmKafkaFlag(updateDto);
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // 2. Coagulant Process
            //////////////////////////////////////////////////////////////////////
            // get ai_coagulant_alarm
            List<AiProcessAlarmDTO> aiCoagulantAlarmList =
                    databaseService.getAllAiCoagulantAlarm(alarmTime, CommonValue.KAFKA_FLAG_INIT);
            if(aiCoagulantAlarmList.size() > 0)
            {
                for(AiProcessAlarmDTO dto : aiCoagulantAlarmList)
                {
                    AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(dto.getAlarm_id());
                    if(alarmInfo != null)
                    {
                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                        popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                        popupMap.put("message", alarmInfo.getDisplay_name());
                        popupMap.put("url", alarmInfo.getUrl());
                        popupMap.put("time", simpleDateFormat.format(dto.getAlarm_time()));

                        // 3. Send Kafka ai_popup
                        try
                        {
                            strBody = objectMapper.writeValueAsString(popupMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Filter Alarm Process");
                        }

                        // 4. Update kafka_flag=1
                        AiProcessAlarmDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                        databaseService.modAiCoagulantAlarmKafkaFlag(updateDto);
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // 2. Mixing Process
            //////////////////////////////////////////////////////////////////////
            // get ai_mixing_alarm
            List<AiProcessAlarmDTO> aiMixingAlarmList =
                    databaseService.getAllAiMixingAlarm(alarmTime, CommonValue.KAFKA_FLAG_INIT);
            if(aiMixingAlarmList.size() > 0)
            {
                for(AiProcessAlarmDTO dto : aiMixingAlarmList)
                {
                    AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(dto.getAlarm_id());
                    if(alarmInfo != null)
                    {
                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                        popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                        popupMap.put("message", alarmInfo.getDisplay_name());
                        popupMap.put("url", alarmInfo.getUrl());
                        popupMap.put("time", simpleDateFormat.format(dto.getAlarm_time()));

                        // 3. Send Kafka ai_popup
                        try
                        {
                            strBody = objectMapper.writeValueAsString(popupMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Filter Alarm Process");
                        }

                        // 4. Update kafka_flag=1
                        AiProcessAlarmDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                        databaseService.modAiMixingAlarmKafkaFlag(updateDto);
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // 2. Sedimentation Process
            //////////////////////////////////////////////////////////////////////
            // get ai_sedimentation_alarm
            List<AiProcessAlarmDTO> aiSedimentationAlarmList =
                    databaseService.getAllAiSedimentationAlarm(alarmTime, CommonValue.KAFKA_FLAG_INIT);
            if(aiSedimentationAlarmList.size() > 0)
            {
                for(AiProcessAlarmDTO dto : aiSedimentationAlarmList)
                {
                    AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(dto.getAlarm_id());
                    if(alarmInfo != null)
                    {
                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                        popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                        popupMap.put("message", alarmInfo.getDisplay_name());
                        popupMap.put("url", alarmInfo.getUrl());
                        popupMap.put("time", simpleDateFormat.format(dto.getAlarm_time()));

                        // 3. Send Kafka ai_popup
                        try
                        {
                            strBody = objectMapper.writeValueAsString(popupMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Filter Alarm Process");
                        }

                        // 4. Update kafka_flag=1
                        AiProcessAlarmDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                        databaseService.modAiSedimentationAlarmKafkaFlag(updateDto);
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // 2. Filter Process
            //////////////////////////////////////////////////////////////////////
            // get ai_filter_alarm
            List<AiProcessAlarmDTO> aiFilterAlarmList =
                    databaseService.getAllAiFilterAlarm(alarmTime, CommonValue.KAFKA_FLAG_INIT);
            if(aiFilterAlarmList.size() > 0)
            {
                for(AiProcessAlarmDTO dto : aiFilterAlarmList)
                {
                    AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(dto.getAlarm_id());
                    if(alarmInfo != null)
                    {
                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                        popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                        popupMap.put("message", alarmInfo.getDisplay_name());
                        popupMap.put("url", alarmInfo.getUrl());
                        popupMap.put("time", simpleDateFormat.format(dto.getAlarm_time()));

                        // 3. Send Kafka ai_popup
                        try
                        {
                            strBody = objectMapper.writeValueAsString(popupMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Filter Alarm Process");
                        }

                        // 4. Update kafka_flag=1
                        AiProcessAlarmDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                        databaseService.modAiFilterAlarmKafkaFlag(updateDto);
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // 2. GAC Process
            //////////////////////////////////////////////////////////////////////
            // get ai_gac_alarm
            List<AiProcessAlarmDTO> aiGacAlarmList =
                    databaseService.getAllAiGacAlarm(alarmTime, CommonValue.KAFKA_FLAG_INIT);
            if(aiGacAlarmList.size() > 0)
            {
                for(AiProcessAlarmDTO dto : aiGacAlarmList)
                {
                    AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(dto.getAlarm_id());
                    if(alarmInfo != null)
                    {
                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                        popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                        popupMap.put("message", alarmInfo.getDisplay_name());
                        popupMap.put("url", alarmInfo.getUrl());
                        popupMap.put("time", simpleDateFormat.format(dto.getAlarm_time()));

                        // 3. Send Kafka ai_popup
                        try
                        {
                            strBody = objectMapper.writeValueAsString(popupMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Filter Alarm Process");
                        }

                        // 4. Update kafka_flag=1
                        AiProcessAlarmDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                        databaseService.modAiGacAlarmKafkaFlag(updateDto);
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // 2. Disinfection Process
            //////////////////////////////////////////////////////////////////////
            // get ai_disinfection_alarm
            List<AiProcessAlarmDTO> aiDisinfectionAlarmList =
                    databaseService.getAllAiDisinfectionAlarm(alarmTime, CommonValue.KAFKA_FLAG_INIT);
            if(aiDisinfectionAlarmList.size() > 0)
            {
                for(AiProcessAlarmDTO dto : aiDisinfectionAlarmList)
                {
                    AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(dto.getAlarm_id());
                    if(alarmInfo != null)
                    {
                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                        popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                        popupMap.put("message", alarmInfo.getDisplay_name());
                        popupMap.put("url", alarmInfo.getUrl());
                        popupMap.put("time", simpleDateFormat.format(dto.getAlarm_time()));

                        // 3. Send Kafka ai_popup
                        try
                        {
                            strBody = objectMapper.writeValueAsString(popupMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Filter Alarm Process");
                        }

                        // 4. Update kafka_flag=1
                        AiProcessAlarmDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                        databaseService.modAiDisinfectionAlarmKafkaFlag(updateDto);
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////
            // 2. Ozone Process
            //////////////////////////////////////////////////////////////////////
            // get ai_ozone_alarm
            List<AiProcessAlarmDTO> aiOzoneAlarmList =
                    databaseService.getAllAiOzoneAlarm(alarmTime, CommonValue.KAFKA_FLAG_INIT);
            if(aiOzoneAlarmList.size() > 0)
            {
                for(AiProcessAlarmDTO dto : aiOzoneAlarmList)
                {
                    AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(dto.getAlarm_id());
                    if(alarmInfo != null)
                    {
                        LinkedHashMap<String, Object> popupMap = new LinkedHashMap<>();
                        popupMap.put("alarm_id", alarmInfo.getAlarm_id());
                        popupMap.put("message", alarmInfo.getDisplay_name());
                        popupMap.put("url", alarmInfo.getUrl());
                        popupMap.put("time", simpleDateFormat.format(dto.getAlarm_time()));

                        // 3. Send Kafka ai_popup
                        try
                        {
                            strBody = objectMapper.writeValueAsString(popupMap);
                            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_POPUP, strBody);
                        }
                        catch(JsonProcessingException e)
                        {
                            log.error("JsonProcessingException Occurred in Filter Alarm Process");
                        }

                        // 4. Update kafka_flag=1
                        AiProcessAlarmDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
                        databaseService.modAiOzoneAlarmKafkaFlag(updateDto);
                    }
                }
            }
        }
    }

    // 데이터베이스 정리
    @RequestMapping(value = "/internal/database", method = RequestMethod.GET)
    public void getDatabase(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("getDatabase, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getDatabase() initialization databaseDate before one hour
        if(databaseDate == null)
        {
            databaseDate = new Date();
            databaseDate.setTime(databaseDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.info("[internal] getDatabase");
        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - databaseDate.getTime() > CommonValue.ONE_MINUTE) {
            databaseDate = new Date();

            // Database check
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -propertiesStorage.getStorage());
            Date deleteTime = calendar.getTime();

            //log.info("Delete Login History:[{}]", databaseService.delLoginHistory(deleteTime));
            log.info("Delete System Monitoring:[{}]", databaseService.delSystemMonitoring(deleteTime));
            log.info("Delete Sensor:[{}]", databaseService.delSensor(deleteTime));
            log.info("Delete ai_receiving_realtime:[{}]", databaseService.delAiReceivingRealtimeValue(deleteTime));
            log.info("Delete ai_receiving_control:[{}]", databaseService.delAiReceivingControl(deleteTime));
            log.info("Delete ai_coagulant_realtime:[{}]", databaseService.delAiCoagulantRealtimeValue(deleteTime));
            log.info("Delete ai_coagulant_control:[{}]", databaseService.delAiCoagulantControl(deleteTime));
            log.info("Delete ai_mixing_realtime:[{}]", databaseService.delAiMixingRealtimeValue(deleteTime));
            log.info("Delete ai_mixing_control:[{}]", databaseService.delAiMixingControl(deleteTime));
            log.info("Delete ai_sedimentation_realtime:[{}]", databaseService.delAiSedimentationRealtimeValue(deleteTime));
            log.info("Delete ai_sedimentation_control:[{}]", databaseService.delAiSedimentationControl(deleteTime));
            log.info("Delete ai_filter_realtime:[{}]", databaseService.delAiFilterRealtimeValue(deleteTime));
            log.info("Delete ai_filter_control:[{}]", databaseService.delAiFilterControl(deleteTime));
            log.info("Delete ai_filter_alarm:[{}]", databaseService.delAiFilterAlarm(deleteTime));
            log.info("Delete ai_gac_realtime:[{}]", databaseService.delAiGacRealtimeValue(deleteTime));
            log.info("Delete ai_gac_control:[{}]", databaseService.delAiGacControl(deleteTime));
            log.info("Delete ai_disinfection_realtime:[{}]", databaseService.delAiDisinfectionRealtimeValue(deleteTime));
            log.info("Delete ai_pre_disinfection_control:[{}]", databaseService.delAiPreDisinfectionControl(deleteTime));
            log.info("Delete ai_peri_disinfection_control:[{}]", databaseService.delAiPeriDisinfectionControl(deleteTime));
            log.info("Delete ai_post_disinfection_control:[{}]", databaseService.delAiPostDisinfectionControl(deleteTime));
            log.info("Delete ai_ozone_realtime:[{}]", databaseService.delAiOzoneRealtimeValue(deleteTime));
            log.info("Delete ai_ozone_control:[{}]", databaseService.delAiOzoneControl(deleteTime));

            // ai_receiving_data delete
            calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -propertiesReceivingData.getReceivingData());
            deleteTime = calendar.getTime();
            log.info("Delete ai_receiving_data:[{}]", databaseService.delAiReceivingDataValue(deleteTime));
        }
    }

    // 알고리즘 상태 확인
    @RequestMapping(value = "/internal/algorithm", method = RequestMethod.GET)
    public void getAlgorithm(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        // Token Check
        if(propertiesAuthentication.getInternalToken().equalsIgnoreCase(token) == false)
        {
            log.error("getDatabase, Invalid X-ACCESS-TOKEN:[{}]", token);
            return;
        }

        // If first call getAlgorithm() initialization algorithmDate before one hour
        if(algorithmDate == null)
        {
            algorithmDate = new Date();
            algorithmDate.setTime(algorithmDate.getTime() - CommonValue.ONE_HOUR);
        }

        log.info("[internal] getAlgorithm");

        // Check one minute after previous transfer
        Date currentDate = new Date();
        if(currentDate.getTime() - algorithmDate.getTime() > CommonValue.ONE_MINUTE)
        {
            algorithmDate = new Date();

            // get algorithm health check URL
            List<String> strUri = propertiesAlgorithmCheck.getAlgorithmHealth();

            StringBuffer stringBuffer = new StringBuffer();
            ObjectMapper objectMapper = new ObjectMapper();
            BufferedReader bufferedReader = null;
            InputStreamReader inputStreamReader = null;
            AlgorithmHealthStatus algorithmHealthStatus = new AlgorithmHealthStatus();

            try
            {
                for(String uri : strUri)
                {
                    HttpGet httpGet = new HttpGet(uri);
                    httpGet.setConfig(requestConfig);

                    HttpResponse response = HttpSend.send(httpGet);
                    if(response == null)
                    {
                        continue;
                    }

                    if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                    {
                        inputStreamReader = new InputStreamReader(response.getEntity().getContent());
                        bufferedReader = new BufferedReader(inputStreamReader);

                        String strLine;
                        while((strLine = bufferedReader.readLine()) != null)
                        {
                            stringBuffer.append(strLine);
                        }

                        // Algorithm Healthcheck Response에 대한 Parsing
                        ArrayList<SupervisorStateInfoDTO> algorithmList =
                                objectMapper.readValue(stringBuffer.toString(), new TypeReference<ArrayList<SupervisorStateInfoDTO>>(){});
                        for(SupervisorStateInfoDTO processState : algorithmList)
                        {
                            if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_RECEIVING_NAME) == true)
                            {
                                // 착수 공정 알고리즘 프로세스 상태 저장
                                if(algorithmHealthStatus.getReceiving() == CommonValue.PROCESS_STATE_RUNNING)
                                {
                                    continue;
                                }
                                algorithmHealthStatus.setReceiving(processState.getState());
                            }
                            else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_COAGULANT_NAME) == true)
                            {
                                // 약품 공장 알고리즘 프로세스 상태 저장
                                if(algorithmHealthStatus.getCoagulant() == CommonValue.PROCESS_STATE_RUNNING)
                                {
                                    continue;
                                }
                                algorithmHealthStatus.setCoagulant(processState.getState());
                            }
                            else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_MIXING_NAME) == true)
                            {
                                // 혼화응집 공정 알고리즘 프로세스 상태 저장
                                if(algorithmHealthStatus.getMixing() == CommonValue.PROCESS_STATE_RUNNING)
                                {
                                    continue;
                                }
                                algorithmHealthStatus.setMixing(processState.getState());
                            }
                            else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_SEDIMENTATION_NAME) == true)
                            {
                                // 침전 공정 알고리즘 프로세스 상태 저장
                                if(algorithmHealthStatus.getSedimentation() == CommonValue.PROCESS_STATE_RUNNING)
                                {
                                    continue;
                                }
                                algorithmHealthStatus.setSedimentation(processState.getState());
                            }
                            else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_FILTER_NAME) == true)
                            {
                                // 여과 공정 알고리즘 프로세스 상태 저장
                                if(algorithmHealthStatus.getFilter() == CommonValue.PROCESS_STATE_RUNNING)
                                {
                                    continue;
                                }
                                algorithmHealthStatus.setFilter(processState.getState());
                            }
                            else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_GAC_NAME) == true)
                            {
                                // GAC 여과 공정 알고리즘 프로세스 상태 저장
                                if(algorithmHealthStatus.getGac() == CommonValue.PROCESS_STATE_RUNNING)
                                {
                                    continue;
                                }
                                algorithmHealthStatus.setGac(processState.getState());
                            }
                            else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_DISINFECTION_NAME) == true)
                            {
                                // 소독 공정 알고리즘 프로세스 상태 저장
                                if(algorithmHealthStatus.getDisinfection() == CommonValue.PROCESS_STATE_RUNNING)
                                {
                                    continue;
                                }
                                algorithmHealthStatus.setDisinfection(processState.getState());
                            }
                            else if(processState.getName().equalsIgnoreCase(CommonValue.PROCESS_OZONE_NAME) == true)
                            {
                                // 오존 공정 알고리즘 프로세스 상태 저장
                                if(algorithmHealthStatus.getOzone() == CommonValue.PROCESS_STATE_RUNNING)
                                {
                                    continue;
                                }
                                algorithmHealthStatus.setOzone(processState.getState());
                            }
                        }
                    }
                }

                // Receiving Algorithm Health Check & Send Alarm
                if(algorithmHealthStatus.getReceiving() != CommonValue.PROCESS_STATE_RUNNING)
                {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlarm_time(new Date());
                    aiProcessAlarm.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlarm_id(CommonValue.ALARM_RECEIVING_AI_MODULE_ERROR);
                    log.info("insert receiving algorithm module error alarm:[{}]",
                            databaseService.addAiReceivingAlarm(aiProcessAlarm));
                }

                // Coagulant Algorithm Health Check & Send Alarm
                if(algorithmHealthStatus.getCoagulant() != CommonValue.PROCESS_STATE_RUNNING)
                {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlarm_time(new Date());
                    aiProcessAlarm.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlarm_id(CommonValue.ALARM_COAGULANT_AI_MODULE_ERROR);
                    log.info("insert coagulant algorithm module error alarm:[{}]",
                            databaseService.addAiCoagulantAlarm(aiProcessAlarm));
                }

                // Mixing Algorithm Health Check & Send Alarm
                if(algorithmHealthStatus.getMixing() != CommonValue.PROCESS_STATE_RUNNING)
                {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlarm_time(new Date());
                    aiProcessAlarm.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlarm_id(CommonValue.ALARM_MIXING_AI_MODULE_ERROR);
                    log.info("insert mixing algorithm module error alarm:[{}]",
                            databaseService.addAiMixingAlarm(aiProcessAlarm));
                }

                // Sedimentation Algorithm Health Check & Send Alarm
                if(algorithmHealthStatus.getSedimentation() != CommonValue.PROCESS_STATE_RUNNING)
                {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlarm_time(new Date());
                    aiProcessAlarm.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlarm_id(CommonValue.ALARM_SEDIMENTATION_AI_MODULE_ERROR);
                    log.info("insert sedimentation algorithm module error alarm:[{}]",
                            databaseService.addAiSedimentationAlarm(aiProcessAlarm));
                }

                // Filter Algorithm Health Check & Send Alarm
                if(algorithmHealthStatus.getFilter() != CommonValue.PROCESS_STATE_RUNNING)
                {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlarm_time(new Date());
                    aiProcessAlarm.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlarm_id(CommonValue.ALARM_FILTER_AI_MODULE_ERROR);
                    log.info("insert filter algorithm module error alarm:[{}]",
                            databaseService.addAiFilterAlarm(aiProcessAlarm));
                }

                // GAC Algorithm Health Check & Send Alarm
                if(algorithmHealthStatus.getGac() != CommonValue.PROCESS_STATE_RUNNING)
                {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlarm_time(new Date());
                    aiProcessAlarm.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlarm_id(CommonValue.ALARM_GAC_AI_MODULE_ERROR);
                    log.info("insert gac algorithm module error alarm:[{]]",
                            databaseService.addAiGacAlarm(aiProcessAlarm));
                }

                // Disinfection Algorithm Health Check & Send Alarm
                if(algorithmHealthStatus.getDisinfection() != CommonValue.PROCESS_STATE_RUNNING)
                {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlarm_time(new Date());
                    aiProcessAlarm.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlarm_id(CommonValue.ALARM_DISINFECTION_AI_MODULE_ERROR);
                    log.info("insert disinfection algorithm module error alarm:[{}]",
                            databaseService.addAiDisinfectionAlarm(aiProcessAlarm));
                }

                // Ozone Algorithm Health Check & Send Alarm
                if(algorithmHealthStatus.getOzone() != CommonValue.PROCESS_STATE_RUNNING)
                {
                    AiProcessAlarmDTO aiProcessAlarm = new AiProcessAlarmDTO();
                    aiProcessAlarm.setAlarm_time(new Date());
                    aiProcessAlarm.setKafka_flag(CommonValue.KAFKA_FLAG_INIT);
                    aiProcessAlarm.setAlarm_id(CommonValue.ALARM_OZONE_AI_MODULE_ERROR);
                    log.info("insert ozone algorithm module error alarm:[{}]",
                            databaseService.addAiOzoneAlarm(aiProcessAlarm));
                }
            }
            catch(IOException e)
            {
                log.error("Invalid Body or BufferedReader...");
            }
            finally
            {
                if(inputStreamReader != null)
                {
                    try
                    {
                        inputStreamReader.close();
                    }
                    catch(IOException e)
                    {
                        log.error("inputStreamReader Close Exception occurred");
                    }
                }
                if(bufferedReader != null)
                {
                    try
                    {
                        bufferedReader.close();
                    }
                    catch(IOException e)
                    {
                        log.error("bufferedReader Close Exception occurred");
                    }
                }
            }
        }
    }
}
