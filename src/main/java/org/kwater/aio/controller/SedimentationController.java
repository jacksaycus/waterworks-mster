package org.kwater.aio.controller;

import org.kwater.aio.ai_dto.*;
import org.kwater.aio.dto.*;
//import org.kwater.aio.kafka.//kafkaProducer;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.CommonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
//@EnableSwagger2
@Slf4j
public class SedimentationController
{
    @Autowired
    DatabaseServiceImpl databaseService;

//    @Autowired
//    //kafkaProducer //kafkaProducer;

    // 침전 공정 최근 데이터 조회
    @RequestMapping(value = "/sedimentation/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestSedimentation()
    {
        log.info("Recv getLatestSedimentation");

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());

        // get ai_sedimentation_realtime
        AiSedimentationRealtimeDTO aiSedimentationRealtime = databaseService.getLatestAiSedimentationRealtimeValue();
        log.info("getLatestAiSedimentationRealtimeValue, result:[{}]", aiSedimentationRealtime != null ? 1 : 0);

        // get ai_sedimentation_init(e_operation_mode)
        AiProcessInitDTO aiSedimentationInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE);
        log.info("getAiSedimentationInit, result:[{}]", aiSedimentationInit != null ? 1 : 0);

        // get ai_sedimentation_init
        List<AiProcessInitDTO> aiSedimentationInitList = databaseService.getAllAiSedimentationInit();
        log.info("getAllAiSedimentationInit, result:[{}]", aiSedimentationInitList.size());

        // get sedimentation_realtime
        List<ProcessRealtimeDTO> sedimentationRealtime = databaseService.getLatestSedimentationRealtimeValue(strPartitionName);
        log.info("getLatestSedimentationRealtimeValue, result:[{}]", sedimentationRealtime.size());

        // get tag_manage
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_SEDIMENTATION);
        log.info("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_SEDIMENTATION, tagManageList.size());

        if(aiSedimentationRealtime != null)
        {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiSedimentationInfo = new LinkedHashMap<>();

            // update_time
            aiSedimentationInfo.put("update_time", aiSedimentationRealtime.getUpdate_time());

            // operation mode
            if(aiSedimentationInit != null)
            {
                aiSedimentationInfo.put("operation_mode", aiSedimentationInit.getValue().intValue());
            }
            else
            {
                aiSedimentationInfo.put("operation_mode", aiSedimentationRealtime.getAIE_1000());
            }

            for(AiProcessInitDTO dto : aiSedimentationInitList)
            {
                if(dto.getItem().equalsIgnoreCase("e_sc_set_sludge_q") == true)
                {
                    // 슬러지 수집기 운행 기준 적산 슬러지 양
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e_sc_set_max_wait") == true)
                {
                    // 슬러지 수집기 운행 대기 최대 일
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e_set_lt") == true)
                {
                    // 슬러지 수집기 편도 운전 거리
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e_sc_set_ti") == true)
                {
                    // 슬러지 수집기 운전 시간
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e_sc_set2") == true)
                {
                    // 2지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue().intValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e_sc_set3") == true)
                {
                    // 3지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue().intValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e_sc_set4") == true)
                {
                    // 4지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue().intValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e_sc_set5") == true)
                {
                    // 5지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue().intValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e_sc_set6") == true)
                {
                    // 6지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue().intValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e_sc_set7") == true)
                {
                    // 7지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue().intValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e_sc_set8") == true)
                {
                    // 8지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue().intValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e_sc_set9") == true)
                {
                    // 9지 AI 운영모드
                    aiSedimentationInfo.put(dto.getItem(), dto.getValue().intValue());
                }
            }

            // Realtime data from SCADA
            Float d1_mm_fr_apac = 0.0f;
            Float d2_mm_fr_apac = 0.0f;
            Float d1_mm_fr_polymax = 0.0f;
            Float d2_mm_fr_polymax = 0.0f;
            // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiSedimenationInfo에 등록
            for(TagManageDTO tagManage : tagManageList)
            {
                for(ProcessRealtimeDTO dto : sedimentationRealtime)
                {
                    if(tagManage.getItem().equalsIgnoreCase("b_in_fr") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 원수 유입 유량
                        aiSedimentationInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("b_tb") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 원수 탁도
                        aiSedimentationInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("d1_mm_fr_apac") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 1계열 APAC 혼화기 유량
                        d1_mm_fr_apac = Float.parseFloat(dto.getValue());
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("d2_mm_fr_apac") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 2계열 APAC 혼화기 유량
                        d2_mm_fr_apac = Float.parseFloat(dto.getValue());
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("d1_mm_fr_polymax") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 1계열 polymax 혼화기 유량
                        d1_mm_fr_polymax = Float.parseFloat(dto.getValue());
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("d2_mm_fr_polymax") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 2계열 polymax 혼화기 유량
                        d2_mm_fr_polymax = Float.parseFloat(dto.getValue());
                    }
                }
            }

            // 계열별 혼화기 약품 종류
            aiSedimentationInfo.put("d1_mm_coagulant", d1_mm_fr_apac > d1_mm_fr_polymax ? "APAC" : "POLYMAX");
            aiSedimentationInfo.put("d2_mm_coagulant", d2_mm_fr_apac > d2_mm_fr_polymax ? "APAC" : "POLYMAX");

            // 계열별 혼화기 유량
            aiSedimentationInfo.put("d1_mm_fr", d1_mm_fr_apac > d1_mm_fr_polymax ? d1_mm_fr_apac : d1_mm_fr_polymax);
            aiSedimentationInfo.put("d2_mm_fr", d2_mm_fr_apac > d2_mm_fr_polymax ? d2_mm_fr_apac : d2_mm_fr_polymax);

            // AI 계열별 슬러지 발생량 예측
            aiSedimentationInfo.put("ai_e1_sludge", aiSedimentationRealtime.getAIE_5301());
            aiSedimentationInfo.put("ai_e2_sludge", aiSedimentationRealtime.getAIE_5302());

            // 계열별 계면계 수위
            aiSedimentationInfo.put("e1_interface_le", aiSedimentationRealtime.getAIE_9901());
            aiSedimentationInfo.put("e2_interface_le", aiSedimentationRealtime.getAIE_9902());

            try
            {
                // AI 총 슬러지 발생량 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                String strTemp = aiSedimentationRealtime.getAIE_5200();
                LinkedHashMap<String, Object> mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);

                ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                Object objectTemp = mapTemp.get(keyList.get(0));
                LinkedHashMap<String, Float> aiSludgeMap = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                aiSedimentationInfo.put("ai_e_total_sludge", aiSludgeMap);

                // 2지 데이터를 추출하여 location2Dto에 저장
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                strTemp = aiSedimentationRealtime.getAIE_9002();
                strTemp = strTemp.replaceAll("NaN", "\"\"");
                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));

                List<LinkedHashMap<String, Object>> locationMap =
                        objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                mapTemp.clear();

                for(Map<String, Object> map : locationMap)
                {
                    mapTemp.putAll(map);
                }
                AiSedimentationLocation2RealtimeDTO location2Dto =
                        objectMapper.convertValue(mapTemp, AiSedimentationLocation2RealtimeDTO.class);

                // 2지 인발밸브 상태
                aiSedimentationInfo.put("e_drawing_vv2_1", location2Dto.getVVB_2213());
                aiSedimentationInfo.put("e_drawing_vv2_2", location2Dto.getVVB_2216());
                aiSedimentationInfo.put("e_drawing_vv2_3", location2Dto.getVVB_2219());
                aiSedimentationInfo.put("e_drawing_vv2_4", location2Dto.getVVB_2222());

                // 2지 수집기 대차 위치
                aiSedimentationInfo.put("e_sc2_f", location2Dto.getSCI_2209().getF());
                aiSedimentationInfo.put("e_sc2_b", location2Dto.getSCI_2209().getB());

                // 2지 수집기 운영 스케쥴
                Map<String, String> scScheduleMap = new HashMap<>();
                scScheduleMap.put("latest", location2Dto.getAIE_6002().getLatest());
                scScheduleMap.put("start", location2Dto.getAIE_6002().getStart());
                scScheduleMap.put("stop", location2Dto.getAIE_6002().getStop());
                scScheduleMap.put("next_start", location2Dto.getAIE_6002().getNext_start());
                scScheduleMap.put("next_end", location2Dto.getAIE_6002().getNext_end());
                scScheduleMap.put("inbal", location2Dto.getAIE_6002().getInbal());
                aiSedimentationInfo.put("e_sc2_schedule", scScheduleMap);

                // 3지 데이터를 추출하여 location3Dto에 저장
                strTemp = aiSedimentationRealtime.getAIE_9003();
                strTemp = strTemp.replaceAll("NaN", "\"\"");
                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
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

                // 3지 인발밸브 상태
                aiSedimentationInfo.put("e_drawing_vv3_1", location3Dto.getVVB_2313());
                aiSedimentationInfo.put("e_drawing_vv3_2", location3Dto.getVVB_2316());
                aiSedimentationInfo.put("e_drawing_vv3_3", location3Dto.getVVB_2319());
                aiSedimentationInfo.put("e_drawing_vv3_4", location3Dto.getVVB_2322());

                // 3지 수집기 대차 위치
                aiSedimentationInfo.put("e_sc3_f", location3Dto.getSCI_2309().getF());
                aiSedimentationInfo.put("e_sc3_b", location3Dto.getSCI_2309().getB());

                // 3지 수집기 운영 스케쥴
                scScheduleMap = new HashMap<>();
                scScheduleMap.put("latest", location3Dto.getAIE_6003().getLatest());
                scScheduleMap.put("start", location3Dto.getAIE_6003().getStart());
                scScheduleMap.put("stop", location3Dto.getAIE_6003().getStop());
                scScheduleMap.put("next_start", location3Dto.getAIE_6003().getNext_start());
                scScheduleMap.put("next_end", location3Dto.getAIE_6003().getNext_end());
                scScheduleMap.put("inbal", location3Dto.getAIE_6003().getInbal());
                aiSedimentationInfo.put("e_sc3_schedule", scScheduleMap);

                // 4지 데이터를 추출하여 location4Dto에 저장
                strTemp = aiSedimentationRealtime.getAIE_9004();
                strTemp = strTemp.replaceAll("NaN", "\"\"");
                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
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

                // 4지 인발밸브 상태
                aiSedimentationInfo.put("e_drawing_vv4_1", location4Dto.getVVB_2413());
                aiSedimentationInfo.put("e_drawing_vv4_2", location4Dto.getVVB_2416());
                aiSedimentationInfo.put("e_drawing_vv4_3", location4Dto.getVVB_2419());
                aiSedimentationInfo.put("e_drawing_vv4_4", location4Dto.getVVB_2422());

                // 4지 수집기 대차 위치
                aiSedimentationInfo.put("e_sc4_f", location4Dto.getSCI_2409().getF());
                aiSedimentationInfo.put("e_sc4_b", location4Dto.getSCI_2409().getB());

                // 4지 수집기 운영 스케쥴
                scScheduleMap = new HashMap<>();
                scScheduleMap.put("latest", location4Dto.getAIE_6004().getLatest());
                scScheduleMap.put("start", location4Dto.getAIE_6004().getStart());
                scScheduleMap.put("stop", location4Dto.getAIE_6004().getStop());
                scScheduleMap.put("next_start", location4Dto.getAIE_6004().getNext_start());
                scScheduleMap.put("next_end", location4Dto.getAIE_6004().getNext_end());
                scScheduleMap.put("inbal", location4Dto.getAIE_6004().getInbal());
                aiSedimentationInfo.put("e_sc4_schedule", scScheduleMap);

                // 5지 데이터를 추출하여 location5Dto에 저장
                strTemp = aiSedimentationRealtime.getAIE_9005();
                strTemp = strTemp.replaceAll("NaN", "\"\"");
                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
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

                // 5지 인발밸브 상태
                aiSedimentationInfo.put("e_drawing_vv5_1", location5Dto.getVVB_2513());
                aiSedimentationInfo.put("e_drawing_vv5_2", location5Dto.getVVB_2516());
                aiSedimentationInfo.put("e_drawing_vv5_3", location5Dto.getVVB_2519());
                aiSedimentationInfo.put("e_drawing_vv5_4", location5Dto.getVVB_2522());

                // 5지 수집기 대차 위치
                aiSedimentationInfo.put("e_sc5_f", location5Dto.getSCI_2509().getF());
                aiSedimentationInfo.put("e_sc5_b", location5Dto.getSCI_2509().getB());

                // 5지 수집기 운영 스케쥴
                scScheduleMap = new HashMap<>();
                scScheduleMap.put("latest", location5Dto.getAIE_6005().getLatest());
                scScheduleMap.put("start", location5Dto.getAIE_6005().getStart());
                scScheduleMap.put("stop", location5Dto.getAIE_6005().getStop());
                scScheduleMap.put("next_start", location5Dto.getAIE_6005().getNext_start());
                scScheduleMap.put("next_end", location5Dto.getAIE_6005().getNext_end());
                scScheduleMap.put("inbal", location5Dto.getAIE_6005().getInbal());
                aiSedimentationInfo.put("e_sc5_schedule", scScheduleMap);

                // 6지 데이터를 추출하여 location6Dto에 저장
                strTemp = aiSedimentationRealtime.getAIE_9006();
                strTemp = strTemp.replaceAll("NaN", "\"\"");
                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
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

                // 6지 인발밸브 상태
                aiSedimentationInfo.put("e_drawing_vv6_1", location6Dto.getVVB_2613());
                aiSedimentationInfo.put("e_drawing_vv6_2", location6Dto.getVVB_2616());
                aiSedimentationInfo.put("e_drawing_vv6_3", location6Dto.getVVB_2619());
                aiSedimentationInfo.put("e_drawing_vv6_4", location6Dto.getVVB_2622());

                // 6지 수집기 대차 위치
                aiSedimentationInfo.put("e_sc6_f", location6Dto.getSCI_2609().getF());
                aiSedimentationInfo.put("e_sc6_b", location6Dto.getSCI_2609().getB());

                // 6지 수집기 운영 스케쥴
                scScheduleMap = new HashMap<>();
                scScheduleMap.put("latest", location6Dto.getAIE_6006().getLatest());
                scScheduleMap.put("start", location6Dto.getAIE_6006().getStart());
                scScheduleMap.put("stop", location6Dto.getAIE_6006().getStop());
                scScheduleMap.put("next_start", location6Dto.getAIE_6006().getNext_start());
                scScheduleMap.put("next_end", location6Dto.getAIE_6006().getNext_end());
                scScheduleMap.put("inbal", location6Dto.getAIE_6006().getInbal());
                aiSedimentationInfo.put("e_sc6_schedule", scScheduleMap);

                // 7지 데이터를 추출하여 location7Dto에 저장
                strTemp = aiSedimentationRealtime.getAIE_9007();
                strTemp = strTemp.replaceAll("NaN", "\"\"");
                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
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

                // 7지 인발밸브 상태
                aiSedimentationInfo.put("e_drawing_vv7_1", location7Dto.getVVB_2713());
                aiSedimentationInfo.put("e_drawing_vv7_2", location7Dto.getVVB_2716());
                aiSedimentationInfo.put("e_drawing_vv7_3", location7Dto.getVVB_2719());
                aiSedimentationInfo.put("e_drawing_vv7_4", location7Dto.getVVB_2722());

                // 7지 수집기 대차 위치
                aiSedimentationInfo.put("e_sc7_f", location7Dto.getSCI_2709().getF());
                aiSedimentationInfo.put("e_sc7_b", location7Dto.getSCI_2709().getB());

                // 7지 수집기 운영 스케쥴
                scScheduleMap = new HashMap<>();
                scScheduleMap.put("latest", location7Dto.getAIE_6007().getLatest());
                scScheduleMap.put("start", location7Dto.getAIE_6007().getStart());
                scScheduleMap.put("stop", location7Dto.getAIE_6007().getStop());
                scScheduleMap.put("next_start", location7Dto.getAIE_6007().getNext_start());
                scScheduleMap.put("next_end", location7Dto.getAIE_6007().getNext_end());
                scScheduleMap.put("inbal", location7Dto.getAIE_6007().getInbal());
                aiSedimentationInfo.put("e_sc7_schedule", scScheduleMap);

                // 8지 데이터를 추출하여 location8Dto에 저장
                strTemp = aiSedimentationRealtime.getAIE_9008();
                strTemp = strTemp.replaceAll("NaN", "\"\"");
                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
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

                // 8지 인발밸브 상태
                aiSedimentationInfo.put("e_drawing_vv8_1", location8Dto.getVVB_2813());
                aiSedimentationInfo.put("e_drawing_vv8_2", location8Dto.getVVB_2816());
                aiSedimentationInfo.put("e_drawing_vv8_3", location8Dto.getVVB_2819());
                aiSedimentationInfo.put("e_drawing_vv8_4", location8Dto.getVVB_2822());

                // 8지 수집기 대차 위치
                aiSedimentationInfo.put("e_sc8_f", location8Dto.getSCI_2809().getF());
                aiSedimentationInfo.put("e_sc8_b", location8Dto.getSCI_2809().getB());

                // 8지 수집기 운영 스케쥴
                scScheduleMap = new HashMap<>();
                scScheduleMap.put("latest", location8Dto.getAIE_6008().getLatest());
                scScheduleMap.put("start", location8Dto.getAIE_6008().getStart());
                scScheduleMap.put("stop", location8Dto.getAIE_6008().getStop());
                scScheduleMap.put("next_start", location8Dto.getAIE_6008().getNext_start());
                scScheduleMap.put("next_end", location8Dto.getAIE_6008().getNext_end());
                scScheduleMap.put("inbal", location8Dto.getAIE_6008().getInbal());
                aiSedimentationInfo.put("e_sc8_schedule", scScheduleMap);

                // 9지 데이터를 추출하여 location9Dto에 저장
                strTemp = aiSedimentationRealtime.getAIE_9009();
                strTemp = strTemp.replaceAll("NaN", "\"\"");
                mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
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

                // 9지 인발밸브 상태
                aiSedimentationInfo.put("e_drawing_vv9_1", location9Dto.getVVB_2913());
                aiSedimentationInfo.put("e_drawing_vv9_2", location9Dto.getVVB_2916());
                aiSedimentationInfo.put("e_drawing_vv9_3", location9Dto.getVVB_2919());
                aiSedimentationInfo.put("e_drawing_vv9_4", location9Dto.getVVB_2922());

                // 9지 수집기 대차 위치
                aiSedimentationInfo.put("e_sc9_f", location9Dto.getSCI_2909().getF());
                aiSedimentationInfo.put("e_sc9_b", location9Dto.getSCI_2909().getB());

                // 9지 수집기 운영 스케쥴
                scScheduleMap = new HashMap<>();
                scScheduleMap.put("latest", location9Dto.getAIE_6009().getLatest());
                scScheduleMap.put("start", location9Dto.getAIE_6009().getStart());
                scScheduleMap.put("stop", location9Dto.getAIE_6009().getStop());
                scScheduleMap.put("next_start", location9Dto.getAIE_6009().getNext_start());
                scScheduleMap.put("next_end", location9Dto.getAIE_6009().getNext_end());
                scScheduleMap.put("inbal", location9Dto.getAIE_6009().getInbal());
                aiSedimentationInfo.put("e_sc9_schedule", scScheduleMap);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiSedimentationInfo);


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
            String strErrorBody = "{\"reason\":\"Empty ai_sedimentation\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 실시간 침전 공정 지별 세부 항목 조회
    @RequestMapping(value = "/sedimentation/location/{locationNumber}", method = RequestMethod.GET)
    public ResponseEntity<String> getLocationSedimentation(@PathVariable int locationNumber)
    {
        log.info("getLocationSedimentation, locationNumber:[{}]", locationNumber);

        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_SEDIMENTATION);
        if(tagManageList.size() > 0)
        {
            // Get series number
            // Front-end로부터 전달받은 지 번호가 tag_manage에 등록되었는지 검사
            int nSeriesNumber = 0;
            for(TagManageDTO dto : tagManageList)
            {
                if(dto.getLocation() == locationNumber)
                {
                    nSeriesNumber = dto.getSeries();
                    break;
                }
            }

            // 등록되지 않은 지 번호는 에러처리
            if(nSeriesNumber == 0)
            {
                String strErrorBody = "{\"reason\":\"Invalid location number.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // Get ai_sedimentation_realtime
            AiSedimentationRealtimeDTO aiSedimentationRealtime = databaseService.getLatestAiSedimentationRealtimeValue();
            log.info("getLatestAiSedimentationRealtimeValue, result:[{}]", aiSedimentationRealtime != null ? 1 : 0);
            if(aiSedimentationRealtime != null)
            {
                // JSON 처리를 위한 ObjectMapper 선언
                ObjectMapper objectMapper = new ObjectMapper();

                // Make ResponseBody
                LinkedHashMap<String, Object> aiSedimentationLocationInfo = new LinkedHashMap<>();

                // update_time
                aiSedimentationLocationInfo.put("update_time", aiSedimentationRealtime.getUpdate_time());

                // 원수 유입 유량
                aiSedimentationLocationInfo.put("b_in_fr", aiSedimentationRealtime.getFRI_2000());

                // 원수 탁도
                aiSedimentationLocationInfo.put("b_tb", aiSedimentationRealtime.getTBI_2000());

                // 지별 데이터 추출
                if(nSeriesNumber == 1)
                {
                    // 혼화기 약품 종류
                    aiSedimentationLocationInfo.put("d_mm_coagulant",
                            aiSedimentationRealtime.getFRI_2053() > aiSedimentationRealtime.getFRI_2055() ? "APAC" : "POLYMAX");

                    // 혼화기 유량
                    aiSedimentationLocationInfo.put("d_mm_fr",
                            aiSedimentationRealtime.getFRI_2053() > aiSedimentationRealtime.getFRI_2055()
                                    ? aiSedimentationRealtime.getFRI_2053() : aiSedimentationRealtime.getFRI_2055());

                    // 계면계 수위
                    aiSedimentationLocationInfo.put("e_interface_le", aiSedimentationRealtime.getAIE_9901());

                    // 침전지 전탁도
                    aiSedimentationLocationInfo.put("e_tb_f", aiSedimentationRealtime.getAIE_9903());

                    // 침전지 후탁도
                    aiSedimentationLocationInfo.put("e_tb_b", aiSedimentationRealtime.getTBI_2001());
                }
                else
                {
                    // 혼화기 약품 종류
                    aiSedimentationLocationInfo.put("d_mm_coagulant",
                            aiSedimentationRealtime.getFRI_2054() > aiSedimentationRealtime.getFRI_2056() ? "APAC" : "POLYMAX");

                    // 혼화기 유량
                    aiSedimentationLocationInfo.put("d_mm_fr",
                            aiSedimentationRealtime.getFRI_2054() > aiSedimentationRealtime.getFRI_2056()
                                    ? aiSedimentationRealtime.getFRI_2054() : aiSedimentationRealtime.getFRI_2056());

                    // 계면계 수위
                    aiSedimentationLocationInfo.put("e_interface_le", aiSedimentationRealtime.getAIE_9902());

                    // 침전지 전탁도
                    aiSedimentationLocationInfo.put("e_tb_f", aiSedimentationRealtime.getAIE_9904());

                    // 침전지 후탁도
                    aiSedimentationLocationInfo.put("e_tb_b", aiSedimentationRealtime.getTBI_2002());
                }

                // 지별 데이터
                String strTemp;
                LinkedHashMap<String, Object> mapTemp;
                ArrayList<String> keyList;
                Object objectTemp;
                try
                {
                    if(locationNumber == 2)
                    {
                        // 2지 데이터를 추출하여 location2Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9002();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation2RealtimeDTO location2Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation2RealtimeDTO.class);

                        // 슬러지 발생량
                        aiSedimentationLocationInfo.put("e_location_sludge", location2Dto.getAIE_5002());

                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location2Dto.getSCI_2209().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location2Dto.getSCI_2209().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location2Dto.getAIE_6002().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location2Dto.getAIE_6002().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location2Dto.getVVB_2213());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location2Dto.getVVB_2216());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location2Dto.getVVB_2219());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location2Dto.getVVB_2222());

                        // 슬러지 발생량 트렌드
                        objectTemp = location2Dto.getAIE_5102();
                        LinkedHashMap<String, Float> location2SludgeMap =
                                objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        aiSedimentationLocationInfo.put("e_location_sludge_trend", location2SludgeMap);
                    }
                    else if(locationNumber == 3)
                    {
                        // 3지 데이터를 추출하여 location3Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9003();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation3RealtimeDTO location3Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation3RealtimeDTO.class);

                        // 슬러지 발생량
                        aiSedimentationLocationInfo.put("e_location_sludge", location3Dto.getAIE_5003());

                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location3Dto.getSCI_2309().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location3Dto.getSCI_2309().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location3Dto.getAIE_6003().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location3Dto.getAIE_6003().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location3Dto.getVVB_2313());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location3Dto.getVVB_2316());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location3Dto.getVVB_2319());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location3Dto.getVVB_2322());

                        // 슬러지 발생량 트렌드
                        objectTemp = location3Dto.getAIE_5103();
                        LinkedHashMap<String, Float> locationSludgeMap =
                                objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        aiSedimentationLocationInfo.put("e_location_sludge_trend", locationSludgeMap);
                    }
                    else if(locationNumber == 4)
                    {
                        // 4지 데이터를 추출하여 location4Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9004();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation4RealtimeDTO location4Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation4RealtimeDTO.class);

                        // 슬러지 발생량
                        aiSedimentationLocationInfo.put("e_location_sludge", location4Dto.getAIE_5004());

                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location4Dto.getSCI_2409().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location4Dto.getSCI_2409().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location4Dto.getAIE_6004().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location4Dto.getAIE_6004().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location4Dto.getVVB_2413());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location4Dto.getVVB_2416());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location4Dto.getVVB_2419());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location4Dto.getVVB_2422());

                        // 슬러지 발생량 트렌드
                        objectTemp = location4Dto.getAIE_5104();
                        LinkedHashMap<String, Float> locationSludgeMap =
                                objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        aiSedimentationLocationInfo.put("e_location_sludge_trend", locationSludgeMap);
                    }
                    else if(locationNumber == 5)
                    {
                        // 5지 데이터를 추출하여 location5Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9005();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation5RealtimeDTO location5Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation5RealtimeDTO.class);

                        // 슬러지 발생량
                        aiSedimentationLocationInfo.put("e_location_sludge", location5Dto.getAIE_5005());

                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location5Dto.getSCI_2509().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location5Dto.getSCI_2509().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location5Dto.getAIE_6005().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location5Dto.getAIE_6005().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location5Dto.getVVB_2513());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location5Dto.getVVB_2516());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location5Dto.getVVB_2519());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location5Dto.getVVB_2522());

                        // 슬러지 발생량 트렌드
                        objectTemp = location5Dto.getAIE_5105();
                        LinkedHashMap<String, Float> locationSludgeMap =
                                objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        aiSedimentationLocationInfo.put("e_location_sludge_trend", locationSludgeMap);
                    }
                    else if(locationNumber == 6)
                    {
                        // 6지 데이터를 추출하여 location6Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9006();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation6RealtimeDTO location6Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation6RealtimeDTO.class);

                        // 슬러지 발생량
                        aiSedimentationLocationInfo.put("e_location_sludge", location6Dto.getAIE_5006());

                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location6Dto.getSCI_2609().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location6Dto.getSCI_2609().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location6Dto.getAIE_6006().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location6Dto.getAIE_6006().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location6Dto.getVVB_2613());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location6Dto.getVVB_2616());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location6Dto.getVVB_2619());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location6Dto.getVVB_2622());

                        // 슬러지 발생량 트렌드
                        objectTemp = location6Dto.getAIE_5106();
                        LinkedHashMap<String, Float> locationSludgeMap =
                                objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        aiSedimentationLocationInfo.put("e_location_sludge_trend", locationSludgeMap);
                    }
                    else if(locationNumber == 7)
                    {
                        // 7지 데이터를 추출하여 location7Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9007();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation7RealtimeDTO location7Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation7RealtimeDTO.class);

                        // 슬러지 발생량
                        aiSedimentationLocationInfo.put("e_location_sludge", location7Dto.getAIE_5007());

                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location7Dto.getSCI_2709().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location7Dto.getSCI_2709().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location7Dto.getAIE_6007().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location7Dto.getAIE_6007().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location7Dto.getVVB_2713());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location7Dto.getVVB_2716());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location7Dto.getVVB_2719());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location7Dto.getVVB_2722());

                        // 슬러지 발생량 트렌드
                        objectTemp = location7Dto.getAIE_5107();
                        LinkedHashMap<String, Float> locationSludgeMap =
                                objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        aiSedimentationLocationInfo.put("e_location_sludge_trend", locationSludgeMap);
                    }
                    else if(locationNumber == 8)
                    {
                        // 8지 데이터를 추출하여 location8Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9008();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation8RealtimeDTO location8Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation8RealtimeDTO.class);

                        // 슬러지 발생량
                        aiSedimentationLocationInfo.put("e_location_sludge", location8Dto.getAIE_5008());

                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location8Dto.getSCI_2809().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location8Dto.getSCI_2809().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location8Dto.getAIE_6008().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location8Dto.getAIE_6008().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location8Dto.getVVB_2813());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location8Dto.getVVB_2816());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location8Dto.getVVB_2819());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location8Dto.getVVB_2822());

                        // 슬러지 발생량 트렌드
                        objectTemp = location8Dto.getAIE_5108();
                        LinkedHashMap<String, Float> locationSludgeMap =
                                objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        aiSedimentationLocationInfo.put("e_location_sludge_trend", locationSludgeMap);
                    }
                    else if(locationNumber == 9)
                    {
                        // 9지 데이터를 추출하여 location9Dto에 저장
                        // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                        strTemp = aiSedimentationRealtime.getAIE_9009();
                        strTemp = strTemp.replaceAll("NaN", "\"\"");
                        mapTemp = objectMapper.readValue(strTemp, LinkedHashMap.class);
                        keyList = new ArrayList<>(mapTemp.keySet());
                        objectTemp = mapTemp.get(keyList.get(0));

                        List<LinkedHashMap<String, Object>> locationMap =
                                objectMapper.convertValue(objectTemp, new TypeReference<List<LinkedHashMap<String, Object>>>(){});
                        mapTemp.clear();
                        for(Map<String, Object> map : locationMap)
                        {
                            mapTemp.putAll(map);
                        }
                        AiSedimentationLocation9RealtimeDTO location9Dto =
                                objectMapper.convertValue(mapTemp, AiSedimentationLocation9RealtimeDTO.class);

                        // 슬러지 발생량
                        aiSedimentationLocationInfo.put("e_location_sludge", location9Dto.getAIE_5009());

                        // 수집기 위치
                        aiSedimentationLocationInfo.put("e_sc_f", location9Dto.getSCI_2909().getF());
                        aiSedimentationLocationInfo.put("e_sc_b", location9Dto.getSCI_2909().getB());

                        // 이전 대차 시작 시간
                        aiSedimentationLocationInfo.put("e_sc_latest", location9Dto.getAIE_6009().getStart());

                        // AI 다음 대차 시작 시간 예측
                        aiSedimentationLocationInfo.put("ai_e_sc_next", location9Dto.getAIE_6009().getNext_start());

                        // 인발밸브 상태
                        aiSedimentationLocationInfo.put("e_drawing_vv_1", location9Dto.getVVB_2913());
                        aiSedimentationLocationInfo.put("e_drawing_vv_2", location9Dto.getVVB_2916());
                        aiSedimentationLocationInfo.put("e_drawing_vv_3", location9Dto.getVVB_2919());
                        aiSedimentationLocationInfo.put("e_drawing_vv_4", location9Dto.getVVB_2922());

                        // 슬러지 발생량 트렌드
                        objectTemp = location9Dto.getAIE_5109();
                        LinkedHashMap<String, Float> locationSludgeMap =
                                objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                        aiSedimentationLocationInfo.put("e_location_sludge_trend", locationSludgeMap);
                    }

                }
                catch(JsonProcessingException e)
                {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }


                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("location", aiSedimentationLocationInfo);

                String strBody;
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
                String strErrorBody = "{\"reason\":\"Empty ai_sedimentation\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }


        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty tag_manage\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 침전 공정 계면계 측정 이력 조회
    // locationNumber가 0일 경우 전체 조회
    @RequestMapping(value = "/sedimentation/history/interface/{locationNumber}", method = RequestMethod.PUT)
    public ResponseEntity<String> getInterfaceHistorySedimentation(
            @PathVariable int locationNumber, @RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getInterfaceHistorySedimentation, location:[{}], start:[{}], end:[{}]",
                locationNumber, dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 침전 공정 데이터 조회
        List<AiSedimentationInterfaceRealtimeDTO> aiSedimentationInterfaceRealtimeList =
                databaseService.getAiSedimentationInterfaceRealtimeValueFromUpdateTime(dateSearchDTO);
        log.info("getAiSedimentationInterfaceRealtimeValueFromUpdateTime, result:[{}]", aiSedimentationInterfaceRealtimeList.size());
        if(aiSedimentationInterfaceRealtimeList.size() > 0)
        {
            // Make Response Body
            LinkedHashMap<String, Object> seriesInterfaceInfo = new LinkedHashMap<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate;

            // 계열별 계면계 측정 이력 정보를 분리 저장하기 위한 변수 선언
            LinkedHashMap<String, Float> series1InterfaceMap = new LinkedHashMap<>();
            LinkedHashMap<String, Float> series2InterfaceMap = new LinkedHashMap<>();
            for(AiSedimentationInterfaceRealtimeDTO dto : aiSedimentationInterfaceRealtimeList)
            {
                strDate = simpleDateFormat.format(dto.getUpdate_time());
                series1InterfaceMap.put(strDate, dto.getAIE_9901());
                series2InterfaceMap.put(strDate, dto.getAIE_9902());
            }

            // Whole sedimentation interface history
            if(locationNumber == 0)
            {
                seriesInterfaceInfo.put("series1", series1InterfaceMap);
                seriesInterfaceInfo.put("series2", series2InterfaceMap);
            }
            else
            {
                List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_SEDIMENTATION);
                if(tagManageList.size() > 0)
                {
                    // Get series number
                    int nSeriesNumber = 0;
                    for(TagManageDTO dto : tagManageList)
                    {
                        if(dto.getLocation() == locationNumber)
                        {
                            nSeriesNumber = dto.getSeries();
                            break;
                        }
                    }

                    if(nSeriesNumber == 1)
                    {
                        seriesInterfaceInfo.put("series1", series1InterfaceMap);
                    }
                    else if(nSeriesNumber == 2)
                    {
                        seriesInterfaceInfo.put("series2", series2InterfaceMap);
                    }
                    else
                    {
                        String strErrorBody = "{\"reason\":\"Invalid location number.\"}";
                        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                    }
                }
                else
                {
                    String strErrorBody = "{\"reason\":\"Empty tag_manage\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("interface", seriesInterfaceInfo);

            ObjectMapper objectMapper = new ObjectMapper();
            String strBody;
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
            String strErrorBody = "{\"reason\":\"Empty ai_sedimentation_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 침전수 잔류염소 측정 이력 조회
    @RequestMapping(value = "/sedimentation/history/cl", method = RequestMethod.PUT)
    public ResponseEntity<String> getClHistorySedimentation(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getClHistorySedimentation, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 소독 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList =
                databaseService.getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO);
        log.info("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());
        if(aiDisinfectionRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> series1 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> series2 = new LinkedHashMap<>();

            // aiDisinfectionRealtimeList에서 계열별 침전지 잔류염소 를 조회하여 계열별 map에 저장
            for(AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList)
            {
                String strDate = simpleDateFormat.format(dto.getUpdate_time());
                series1.put(strDate, dto.getE1_cl());
                series2.put(strDate, dto.getE2_cl());
            }

            LinkedHashMap<String, Object> seriesClInfo = new LinkedHashMap<>();
            seriesClInfo.put("series1", series1);
            seriesClInfo.put("series2", series2);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("cl", seriesClInfo);

            ObjectMapper objectMapper = new ObjectMapper();
            String strBody;
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
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 침전수 탁도 측정 이력 조회
    @RequestMapping(value = "/sedimentation/history/tb", method = RequestMethod.PUT)
    public ResponseEntity<String> getTbHistorySedimentation(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getTbHistorySedimentation, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 약품 공정 데이터 조회
        List<AiCoagulantRealtimeDTO> aiCoagulantRealtimeList =
                databaseService.getAiCoagulantRealtimeValueFromUpdateTime(dateSearchDTO);
        log.info("getAiCoagulantRealtimeValueFromUpdateTime, result:[{}]", aiCoagulantRealtimeList.size());
        if(aiCoagulantRealtimeList.size() > 0)
        {
            // Make Response Body
            // 계열별 데이터를 분리 저장하기 위한 변수 선언
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> series1 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> series2 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp;
            ObjectMapper objectMapper = new ObjectMapper();

            try
            {
                // aiCoagulantRealtimeList에서 침전지 탁도 정보를 조회하여 계열별 map에 등록
                for(AiCoagulantRealtimeDTO dto : aiCoagulantRealtimeList)
                {
                    String strDate = simpleDateFormat.format(dto.getUpdate_time());

                    mapTemp = objectMapper.readValue(dto.getE_tb_b(), LinkedHashMap.class);
                    ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                    Object objectTemp = mapTemp.get(keyList.get(0));

                    JsonCSeriesFloat e_tb_b = objectMapper.convertValue(objectTemp, JsonCSeriesFloat.class);
                    series1.put(strDate, e_tb_b.getSeries1());
                    series2.put(strDate, e_tb_b.getSeries2());
                }

                LinkedHashMap<String, Object> seriesTbInfo = new LinkedHashMap<>();
                seriesTbInfo.put("series1", series1);
                seriesTbInfo.put("series2", series2);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("tb", seriesTbInfo);

                // ObjectMapper를 통해 JSON 값을 String으로 변환
                String strBody = objectMapper.writeValueAsString(responseBody);
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            }
            catch(JsonProcessingException e)
            {
                log.error("JsonProcessingException Occurred in /sedimentation/history/tb API");
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_coagulant_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 테스트 코드
//    @RequestMapping(value = "/sedimentation/distribution/tb2", method = RequestMethod.GET)
//    public ResponseEntity<String> getTb2DistributionSedimentation()
//    {
//        log.info("Recv getTbDistributionSedimentation");
//
//        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_SEDIMENTATION);
//        log.info("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_SEDIMENTATION, tagManageList.size());
//
//        // Get start_time(before 24 hours)
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, -1);
//        Date startTime = calendar.getTime();
//
//        // Get distribution data
//        List<FrequencyDTO> series1 = new ArrayList<>();
//        List<FrequencyDTO> series2 = new ArrayList<>();
//
//        for(TagManageDTO dto : tagManageList)
//        {
//            if(dto.getItem().equalsIgnoreCase("e1_tb_b") == true)
//            {
//                series1 = databaseService.getDistribution(startTime, dto.getName());
//                log.info("getDistribution[{}], result:[{}]", dto.getName(), series1.size());
//            }
//            else if(dto.getItem().equalsIgnoreCase("e2_tb_b") == true)
//            {
//                series2 = databaseService.getDistribution(startTime, dto.getName());
//                log.info("getDistribution[{}], result:[{}]", dto.getName(), series2.size());
//            }
//        }
//
//        if(series1.size() + series2.size() == 0)
//        {
//            String strErrorBody = "{\"reason\":\"Empty TB Trend\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
//
//        // Make series body
//        LinkedHashMap<String, Object> seriesInfo = new LinkedHashMap<>();
//        Map<String, Integer> countBody = new LinkedHashMap<>();
//        for(FrequencyDTO dto : series1)
//        {
//            if(dto.getValue() == null)
//            {
//                continue;
//            }
//            countBody.put(dto.getValue(), dto.getCount());
//        }
//        if(countBody.isEmpty() == true)
//        {
//            seriesInfo.put("series1", null);
//        }
//        else
//        {
//            seriesInfo.put("series1", countBody);
//        }
//
//        countBody = new LinkedHashMap<>();
//        for(FrequencyDTO dto : series2)
//        {
//            if(dto.getValue() == null)
//            {
//                continue;
//            }
//            countBody.put(dto.getValue(), dto.getCount());
//        }
//        if(countBody.isEmpty() == true)
//        {
//            seriesInfo.put("series2", null);
//        }
//        else
//        {
//            seriesInfo.put("series2", countBody);
//        }
//
//        Map<String, Object> responseBody = new HashMap<>();
//        responseBody.put("tb", seriesInfo);
//
//        String strBody = "";
//        ObjectMapper objectMapper = new ObjectMapper();
//        try
//        {
//            strBody = objectMapper.writeValueAsString(responseBody);
//        }
//        catch(JsonProcessingException e)
//        {
//            String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(strBody, HttpStatus.OK);
//    }

    // 침전지 탁도 정규분포 데이터 조회
    @RequestMapping(value = "/sedimentation/distribution/tb", method = RequestMethod.GET)
    public ResponseEntity<String> getTbDistributionSedimentation()
    {
        // Use coagulant_realtime table
        log.info("Recv getTbDistributionSedimentation");

        // get tag_manage
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_COAGULANT);
        log.info("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_COAGULANT, tagManageList.size());

        // Get start_time(before seven days)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        Date startTime = calendar.getTime();

        // Make Response Body
        int nTotalSize = 0;
        LinkedHashMap<String, Object> seriesTbInfo = new LinkedHashMap<>();
        for(TagManageDTO tagManage: tagManageList)
        {
            if(tagManage.getItem().equalsIgnoreCase("e1_tb_b") == true)
            {
                // 1계열 침전지 탁도 정규분포 데이터를 조회하여 series1에 저장
                List<FrequencyDTO> frequencyList = databaseService.getCoagulantDistribution(startTime, tagManage.getName());
                nTotalSize += frequencyList.size();

                Map<String, Integer> countBody = new LinkedHashMap<>();
                for(FrequencyDTO dto : frequencyList)
                {
                    if(dto.getValue() == null)
                    {
                        continue;
                    }
                    countBody.put(dto.getValue(), dto.getCount());
                }
                if(countBody.isEmpty() == true)
                {
                    seriesTbInfo.put("series1", null);
                }
                else
                {
                    seriesTbInfo.put("series1", countBody);
                }
            }
            else if(tagManage.getItem().equalsIgnoreCase("e2_tb_b") == true)
            {
                // 2계열 침전지 탁도 정규분포 데이터를 조회하여 series2에 저장
                List<FrequencyDTO> frequencyList = databaseService.getCoagulantDistribution(startTime, tagManage.getName());
                nTotalSize += frequencyList.size();

                Map<String, Integer> countBody = new LinkedHashMap<>();
                for(FrequencyDTO dto : frequencyList)
                {
                    if(dto.getValue() == null)
                    {
                        continue;
                    }
                    countBody.put(dto.getValue(), dto.getCount());
                }
                if(countBody.isEmpty() == true)
                {
                    seriesTbInfo.put("series2", null);
                }
                else
                {
                    seriesTbInfo.put("series2", countBody);
                }
            }
        }

        if(nTotalSize == 0)
        {
            String strErrorBody = "{\"reason\":\"Empty TB Trend\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("tb", seriesTbInfo);

        String strBody = "";
        ObjectMapper objectMapper = new ObjectMapper();
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

    // 테스트 코드
    @RequestMapping(value = "/sedimentation/distribution/tbtest", method = RequestMethod.GET)
    public ResponseEntity<String> getTbTestDistributionSedimentation()
    {
        // Use sedimentation_realtime Table
        log.info("Recv getTbDistributionSedimentation");

        // Get start_time(before one month)
        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MONTH, -1);
        calendar.add(Calendar.DATE, -7);
        Date startTime = calendar.getTime();

        // Make Body
        // get TBI_2001 distinct count
        int nTotalSize = 0;
        List<FrequencyDTO> frequencyList = databaseService.getDistributionE1Tb(startTime);
        log.info("getDistributionE1Tb, result:[{}]", frequencyList.size());
        nTotalSize += frequencyList.size();

        LinkedHashMap<String, Object> seriesTbInfo = new LinkedHashMap<>();

        Map<String, Integer> countBody = new LinkedHashMap<>();
        for(FrequencyDTO dto : frequencyList)
        {
            if(dto.getValue() == null)
            {
                continue;
            }
            countBody.put(dto.getValue(), dto.getCount());
        }
        if(countBody.isEmpty() == true)
        {
            seriesTbInfo.put("series1", null);
        }
        else
        {
            seriesTbInfo.put("series1", countBody);
        }

        // get TBI_2002 distinct count
        frequencyList = databaseService.getDistributionE2Tb(startTime);
        log.info("getDistributionE2Tb, result:[{}]", frequencyList.size());
        nTotalSize += frequencyList.size();

        countBody = new LinkedHashMap<>();
        for(FrequencyDTO dto : frequencyList)
        {
            if(dto.getValue() == null)
            {
                continue;
            }
            countBody.put(dto.getValue(), dto.getCount());
        }
        if(countBody.isEmpty() == true)
        {
            seriesTbInfo.put("series2", null);
        }
        else
        {
            seriesTbInfo.put("series2", countBody);
        }

        if(nTotalSize == 0)
        {
            String strErrorBody = "{\"reason\":\"Empty TB Trend\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("tb", seriesTbInfo);

        String strBody = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            strBody = objectMapper.writeValueAsString(responseBody);
        }
        catch(JsonProcessingException e)
        {
            String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(strBody, HttpStatus.OK);
    }

    // 침전 공정 제어모드 변경
    @RequestMapping(value = "/sedimentation/control/operation", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlSedimentation(@RequestBody InterfaceOperationModeDTO operationMode)
    {
        log.info("putOperationControlSedimentation, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_sedimentation_init's operation_mode
//        log.info("update aiSedimentationOperationMode:[{}], mode:[{}]",
//                databaseService.modAiSedimentationOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(e_operation_mode)
        AiProcessInitDTO aiSedimentationInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE);
        log.info("getAiSedimentationInit, result:[{}]", aiSedimentationInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            // Kafka에 전송할 값 정의
            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
            controlMap.put("tag", aiSedimentationInit.getName());
            controlMap.put("value", nOperationMode);
            controlMap.put("time", strDate);

            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = objectMapper.writeValueAsString(controlMap);
            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

            // Kafka에 침전 공정 제어모드 변경 알람 전송
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
            for(TagManageDTO dto : tagManageList)
            {
                if(dto.getItem().equalsIgnoreCase("e_operation_mode_a") == true)
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
            log.error("JsonProcessingException Occurred in /sedimentation/control/operation API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 침전 알고리즘 설정값 변경
    @RequestMapping(value = "/sedimentation/control/sc", method = RequestMethod.PUT)
    public ResponseEntity<String> putScControlSedimentation(@RequestBody InterfaceSedimentationScDTO sedimentationSc)
    {
        log.info("putScControlSedimentation, sc:[{}]", sedimentationSc);

        boolean result = true;

        // update 슬러지 수집기 운행 기준 적산 슬러지 양
        result = (databaseService.modAiSedimentationInit("e_sc_set_sludge_q", sedimentationSc.getE_sc_set_sludge_q()) == 1) && result;

        // update 슬러지 수집기 운행 대기 최대 일
        result = (databaseService.modAiSedimentationInit("e_sc_set_max_wait", sedimentationSc.getE_sc_set_max_wait()) ==1) && result;

        // update 슬러지 수집기 편도 운전 거리
        result = (databaseService.modAiSedimentationInit("e_set_lt", sedimentationSc.getE_set_lt()) == 1) && result;

        // update 슬러지 수집기 운전 시간
        result = (databaseService.modAiSedimentationInit("e_sc_set_ti", sedimentationSc.getE_sc_set_ti()) == 1) && result;

        if(result == true)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_sedimentation_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 침전 공정 지별 AI 모드 변경
    @RequestMapping(value = "/sedimentation/control/location/{locationNumber}", method = RequestMethod.PUT)
    public ResponseEntity<String> putLocationControlSedimentation(@PathVariable int locationNumber, @RequestBody InterfaceAiOnOffDTO aiOnOff)
    {
        log.info("putLocationControlSedimentation, location:[{}], AI:[{}]", locationNumber, aiOnOff.getAi());

        // get location number(지 번호)
        TagManageRangeDTO sedimentationRange = databaseService.getTagManageRange(CommonValue.PROCESS_SEDIMENTATION);
        log.info("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_SEDIMENTATION, sedimentationRange != null ? 1 : 0);

        int nLocationMin = 0, nLocationMax = 0;
        if(sedimentationRange != null)
        {
            nLocationMin = sedimentationRange.getMin();
            nLocationMax = sedimentationRange.getMax();
       }

        // 지 번호 검사
        if(locationNumber < nLocationMin || locationNumber > nLocationMax)
        {
            log.info("invalid location number:[{}]", locationNumber);
            String strErrorBody = "{\"reason\":\"invalid location number\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // ON/OFF 값 검사
        if(aiOnOff.getAi() < CommonValue.AI_OFF || aiOnOff.getAi() > CommonValue.AI_ON)
        {
            log.info("invalid AI on/off:[{}]", aiOnOff.getAi());
            String strErrorBody = "{\"reason\":\"invalid on/off value\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // 지별 AI 모드 업데이트
        if(databaseService.modAiSedimentationInit("e_sc_set" + locationNumber, aiOnOff.getAi()) == 1)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_sedimentation_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}