package org.kwater.web.aio;

import lombok.extern.slf4j.Slf4j;
import org.kwater.domain.aio.AiProcessInitDTO;
import org.kwater.utils.aio.CommonValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.kwater.service.aio.DatabaseService;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
public class SampleController {

    private DatabaseService databaseService;
    public SampleController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    // 착수 공정 최근 데이터 조회
    @RequestMapping(value = "/receiving/latestsample", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestReceivingsample()
    {
        log.info("Recv getLatestReceiving");

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());

        // get ai_receiving_init(b_operation_mode)
        AiProcessInitDTO aiReceivingInit = databaseService.getAiReceivingInit(CommonValue.B_OPERATION_MODE);
        log.info("getAiReceivingInit, result:[{}]", aiReceivingInit != null ? 1 : 0);
return null;
        // get ai_receiving_int
//        List<AiProcessInitDTO> aiReceivingInitList = databaseService.getAllAiReceivingInit();
//        log.info("getAllAiReceivingInit, result:[{}]", aiReceivingInitList.size());
//
//        // get ai_receiving_realtime
//        AiReceivingRealtimeDTO aiReceivingRealtime = databaseService.getLatestAiReceivingRealtimeValue();
//        log.info("getLatestAiReceivingValue, result:[{}]", aiReceivingRealtime != null ? 1 : 0);
//
//        // get receiving_realtime
//        List<ProcessRealtimeDTO> receivingRealtime = databaseService.getLatestReceivingRealtimeValue(strPartitionName);
//        log.info("getLatestReceivingRealtimeValue, result:[{}]", receivingRealtime.size());
//
//        // get tag_manage
//        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_RECEIVING);
//        log.info("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_RECEIVING, tagManageList.size());
//
//        if(aiReceivingRealtime != null)
//        {
//            // JSON 처리를 위한 ObjectMapper 선언
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            // Make Response Body
//            LinkedHashMap<String, Object> aiReceivingInfo = new LinkedHashMap<>();
//            LinkedHashMap<String, Object> mapTemp;
//
//            // update_time
//            aiReceivingInfo.put("update_time", aiReceivingRealtime.getUpdate_time());
//
//            try
//            {
//                // operation_mode
//                if(aiReceivingInit != null)
//                {
//                    aiReceivingInfo.put("operation_mode", aiReceivingInit.getValue().intValue());
//                }
//                else
//                {
//                    aiReceivingInfo.put("operation_mode", aiReceivingRealtime.getB_operation_mode());
//                }
//
//                // ems_mode
//                aiReceivingInfo.put("ems_mode", aiReceivingRealtime.getB_ems_mode());
//
//                for(AiProcessInitDTO dto : aiReceivingInitList)
//                {
//                    if(dto.getItem().equalsIgnoreCase("h_target_le_max") == true)
//                    {
//                        // 정수지 최대 목표 수위
//                        aiReceivingInfo.put(dto.getItem(), dto.getValue());
//                    }
//                    else if(dto.getItem().equalsIgnoreCase("h_target_le_min") == true)
//                    {
//                        // 정수지 최저 목표 수위
//                        aiReceivingInfo.put(dto.getItem(), dto.getValue());
//                    }
//                }
//
//                // Realtime data from SCADA
//                for(TagManageDTO tagManage : tagManageList)
//                {
//                    for(ProcessRealtimeDTO dto : receivingRealtime)
//                    {
//                        if(tagManage.getItem().equalsIgnoreCase("b_in_fr") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 원수 유입 유량 순시
//                            aiReceivingInfo.put("b_in_fr_i", Float.parseFloat(dto.getValue()));
//                        }
//                        else if(tagManage.getItem().equalsIgnoreCase("b_in_fr_q") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 원수 유입 유량 적산
//                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
//                        }
//                        else if(tagManage.getItem().equalsIgnoreCase("b_in_pr") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 원수 유입 압력
//                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
//                        }
//                        else if(tagManage.getItem().equalsIgnoreCase("b1_vv_po") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 1계열 원수 조절 밸브 개도
//                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
//                        }
//                        else if(tagManage.getItem().equalsIgnoreCase("b2_vv_po") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 2계열 원수 조절 밸브 개도
//                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
//                        }
//                        else if(tagManage.getItem().equalsIgnoreCase("d1_in_fr") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 1계열 혼화지 유입 유량
//                            aiReceivingInfo.put("b1_out_fr", (int)Float.parseFloat(dto.getValue()));
//                        }
//                        else if(tagManage.getItem().equalsIgnoreCase("d2_in_fr") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 2계열 혼화지 유입 유량
//                            aiReceivingInfo.put("b2_out_fr", (int)Float.parseFloat(dto.getValue()));
//                        }
//                        else if(tagManage.getItem().equalsIgnoreCase("h_location_le1") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 정수지 #1 수위
//                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
//                        }
//                        else if(tagManage.getItem().equalsIgnoreCase("h_location_le2") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 정수지 #2 수위
//                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
//                        }
//                        else if(tagManage.getItem().equalsIgnoreCase("h_location_le3") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 정수지 #3 수위
//                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
//                        }
//                        else if(tagManage.getItem().equalsIgnoreCase("h_location_le4") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 정수지 #4 수위
//                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
//                        }
//                        else if(tagManage.getItem().equalsIgnoreCase("h_out_fr") == true &&
//                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
//                        {
//                            // 정수지 유출 유량
//                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
//                        }
//                    }
//                }
//
//                // AI 원수 유입 유량 예측
//                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
//                mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_in_fr(), LinkedHashMap.class);
//                ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
//                Object objectTemp = mapTemp.get(keyList.get(0));
//                JsonBSeriesInt ai_b_in_fr = objectMapper.convertValue(objectTemp, JsonBSeriesInt.class);
//                aiReceivingInfo.put("ai_b1_in_fr", ai_b_in_fr.getSeries1());
//                aiReceivingInfo.put("ai_b2_in_fr", ai_b_in_fr.getSeries2());
//
//                // AI 원수 조절 밸브 개도 예측
//                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
//                mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_vv_po(), LinkedHashMap.class);
//                keyList = new ArrayList<>(mapTemp.keySet());
//                objectTemp = mapTemp.get(keyList.get(0));
//                JsonBSeriesInt ai_b_vv_po = objectMapper.convertValue(objectTemp, JsonBSeriesInt.class);
//                aiReceivingInfo.put("ai_b1_vv_po", ai_b_vv_po.getSeries1());
//                aiReceivingInfo.put("ai_b2_vv_po", ai_b_vv_po.getSeries2());
//
//                // AI 원수 유입 유량 예측 트렌드
//                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
//                mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_in_fr_trend(), LinkedHashMap.class);
//                keyList = new ArrayList<>(mapTemp.keySet());
//                objectTemp = mapTemp.get(keyList.get(0));
//                aiReceivingInfo.put("ai_b_in_fr_trend", objectTemp);
//            }
//            catch(JsonProcessingException e)
//            {
//                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//
//            Map<String, Object> responseBody = new HashMap<>();
//            responseBody.put("latest", aiReceivingInfo);
//
//            String strBody = "";
//            try
//            {
//                // ObjectMapper를 통해 JSON 값을 String으로 변환
//                strBody = objectMapper.writeValueAsString(responseBody);
//            }
//            catch(JsonProcessingException e)
//            {
//                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
//                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//            return new ResponseEntity<>(strBody, HttpStatus.OK);
//        }
//        else
//        {
//            String strErrorBody = "{\"reason\":\"Empty ai_receiving\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
//        }
    }
}
