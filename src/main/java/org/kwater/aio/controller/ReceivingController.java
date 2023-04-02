package org.kwater.aio.controller;

import org.kwater.aio.ai_dto.*;
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
public class ReceivingController
{
    @Autowired
    DatabaseServiceImpl databaseService;

//    @Autowired
//    //kafkaProducer //kafkaProducer;

    // 착수 공정 최근 데이터 조회
    @RequestMapping(value = "/receiving/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestReceiving()
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

        // get ai_receiving_int
        List<AiProcessInitDTO> aiReceivingInitList = databaseService.getAllAiReceivingInit();
        log.info("getAllAiReceivingInit, result:[{}]", aiReceivingInitList.size());

        // get ai_receiving_realtime
        AiReceivingRealtimeDTO aiReceivingRealtime = databaseService.getLatestAiReceivingRealtimeValue();
        log.info("getLatestAiReceivingValue, result:[{}]", aiReceivingRealtime != null ? 1 : 0);

        // get receiving_realtime
        List<ProcessRealtimeDTO> receivingRealtime = databaseService.getLatestReceivingRealtimeValue(strPartitionName);
        log.info("getLatestReceivingRealtimeValue, result:[{}]", receivingRealtime.size());

        // get tag_manage
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_RECEIVING);
        log.info("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_RECEIVING, tagManageList.size());

        if(aiReceivingRealtime != null)
        {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiReceivingInfo = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp;

            // update_time
            aiReceivingInfo.put("update_time", aiReceivingRealtime.getUpdate_time());

            try
            {
                // operation_mode
                if(aiReceivingInit != null)
                {
                    aiReceivingInfo.put("operation_mode", aiReceivingInit.getValue().intValue());
                }
                else
                {
                    aiReceivingInfo.put("operation_mode", aiReceivingRealtime.getB_operation_mode());
                }

                // ems_mode
                aiReceivingInfo.put("ems_mode", aiReceivingRealtime.getB_ems_mode());

                for(AiProcessInitDTO dto : aiReceivingInitList)
                {
                    if(dto.getItem().equalsIgnoreCase("h_target_le_max") == true)
                    {
                        // 정수지 최대 목표 수위
                        aiReceivingInfo.put(dto.getItem(), dto.getValue());
                    }
                    else if(dto.getItem().equalsIgnoreCase("h_target_le_min") == true)
                    {
                        // 정수지 최저 목표 수위
                        aiReceivingInfo.put(dto.getItem(), dto.getValue());
                    }
                }

                // Realtime data from SCADA
                for(TagManageDTO tagManage : tagManageList)
                {
                    for(ProcessRealtimeDTO dto : receivingRealtime)
                    {
                        if(tagManage.getItem().equalsIgnoreCase("b_in_fr") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 원수 유입 유량 순시
                            aiReceivingInfo.put("b_in_fr_i", Float.parseFloat(dto.getValue()));
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("b_in_fr_q") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 원수 유입 유량 적산
                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("b_in_pr") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 원수 유입 압력
                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("b1_vv_po") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 1계열 원수 조절 밸브 개도
                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("b2_vv_po") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 2계열 원수 조절 밸브 개도
                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("d1_in_fr") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 1계열 혼화지 유입 유량
                            aiReceivingInfo.put("b1_out_fr", (int)Float.parseFloat(dto.getValue()));
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("d2_in_fr") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 2계열 혼화지 유입 유량
                            aiReceivingInfo.put("b2_out_fr", (int)Float.parseFloat(dto.getValue()));
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("h_location_le1") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 정수지 #1 수위
                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("h_location_le2") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 정수지 #2 수위
                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("h_location_le3") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 정수지 #3 수위
                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("h_location_le4") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 정수지 #4 수위
                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("h_out_fr") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 정수지 유출 유량
                            aiReceivingInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        }
                    }
                }

                // AI 원수 유입 유량 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_in_fr(), LinkedHashMap.class);
                ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                Object objectTemp = mapTemp.get(keyList.get(0));
                JsonBSeriesInt ai_b_in_fr = objectMapper.convertValue(objectTemp, JsonBSeriesInt.class);
                aiReceivingInfo.put("ai_b1_in_fr", ai_b_in_fr.getSeries1());
                aiReceivingInfo.put("ai_b2_in_fr", ai_b_in_fr.getSeries2());

                // AI 원수 조절 밸브 개도 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_vv_po(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                JsonBSeriesInt ai_b_vv_po = objectMapper.convertValue(objectTemp, JsonBSeriesInt.class);
                aiReceivingInfo.put("ai_b1_vv_po", ai_b_vv_po.getSeries1());
                aiReceivingInfo.put("ai_b2_vv_po", ai_b_vv_po.getSeries2());

                // AI 원수 유입 유량 예측 트렌드
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiReceivingRealtime.getAi_b_in_fr_trend(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiReceivingInfo.put("ai_b_in_fr_trend", objectTemp);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiReceivingInfo);

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
            String strErrorBody = "{\"reason\":\"Empty ai_receiving\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 착수정 유출 유량 측정 이력 조회
    @RequestMapping(value = "/receiving/history/fr/out", method = RequestMethod.PUT)
    public ResponseEntity<String> getOutFrHistoryReceiving(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getOutFrHistoryReceiving, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 착수 공정 데이터 조회
        List<AiReceivingRealtimeDTO> aiReceivingRealtimeList =
                databaseService.getAiReceivingRealtimeValueFromUpdateTime(dateSearchDTO);
        log.info("getAiReceivingRealtimeValueFromUpdateTime, result:[{}]", aiReceivingRealtimeList.size());
        if(aiReceivingRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> series1 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> series2 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp;

            ObjectMapper objectMapper = new ObjectMapper();

            try
            {
                // aiReceivingRealtimeList에서 착수정 유출 유량을 조회하여 각 계열별로 저장
                for(AiReceivingRealtimeDTO dto : aiReceivingRealtimeList)
                {
                    mapTemp = objectMapper.readValue(dto.getB_out_fr(), LinkedHashMap.class);
                    ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                    Object objectTemp = mapTemp.get(keyList.get(0));
                    JsonBSeriesInt b_out_fr = objectMapper.convertValue(objectTemp, JsonBSeriesInt.class);

                    String strDate = simpleDateFormat.format(dto.getUpdate_time());
                    series1.put(strDate, b_out_fr.getSeries1());
                    series2.put(strDate, b_out_fr.getSeries2());
                }

                LinkedHashMap<String, Object> out_fr = new LinkedHashMap<>();
                out_fr.put("series1", series1);
                out_fr.put("series2", series2);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("out_fr", out_fr);

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
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_receiving_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 정수지 유출 유량 측정 이력 조회
    @RequestMapping(value = "/clear/history/fr/out", method = RequestMethod.PUT)
    public ResponseEntity<String> getOutFrHistoryClear(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getOutFrHistoryClear, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 착수 공정 데이터 조회
        List<AiReceivingRealtimeDTO> aiReceivingRealtimeList =
                databaseService.getAiReceivingRealtimeValueFromUpdateTime(dateSearchDTO);
        log.info("getAiReceivingRealtimeValueFromUpdateTime, result:[{}]", aiReceivingRealtimeList.size());
        if(aiReceivingRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> h_out_fr = new LinkedHashMap<>();

            // aiReceivingRealtimeList에서 정수지 유출 유량을 조회하여 h_out_fr에 등록
            for(AiReceivingRealtimeDTO dto : aiReceivingRealtimeList)
            {
                String strDate = simpleDateFormat.format(dto.getUpdate_time());
                h_out_fr.put(strDate, dto.getH_out_fr());
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("out_fr", h_out_fr);

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
            String strErrorBody = "{\"reason\":\"Empty ai_receiving_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 정수지 수위 측정 이력 조회
    @RequestMapping(value = "/clear/history/le", method = RequestMethod.PUT)
    public ResponseEntity<String> getLeHistoryClear(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getLeHistoryClear, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 착수 공정 데이터 조회
        List<AiReceivingRealtimeDTO> aiReceivingRealtimeList =
                databaseService.getAiReceivingRealtimeValueFromUpdateTime(dateSearchDTO);
        List<AiClearOperationBandDTO> aiClearOperationBandList;
        log.info("getAiReceivingRealtimeValueFromUpdateTime, result:[{}]", aiReceivingRealtimeList.size());


        if(aiReceivingRealtimeList.size() > 0)
        {
            // EMS 모드에 따라 정수지 수위 밴드 값을 다르게 불러옴
            if(aiReceivingRealtimeList.get(0).getB_ems_mode() == CommonValue.EMS_MODE_ON)
            {
                aiClearOperationBandList = databaseService.getAiClearEmsOperationBandFromTimeIndex(dateSearchDTO);
                log.info("getAiClearEmsOperationBandFromTimeIndex, result:[{}]", aiClearOperationBandList.size());
            }
            else
            {
                aiClearOperationBandList = databaseService.getAiClearOperationBandFromTimeIndex(dateSearchDTO);
                log.info("getAiClearOperationBandFromTimeIndex, result:[{}]", aiClearOperationBandList.size());
            }

            // Get Wide operation band
            List<AiClearOperationBandDTO> aiClearWideOperationBand =
                    databaseService.getAiClearWideOperationBandFromTimeIndex(dateSearchDTO);
            log.info("getAiClearWideOperationBandFromTimeIndex, result:[{}]", aiClearWideOperationBand.size());

            // Make Response Body
            // 그래프 처리를 위해 계열별 정수지 수위, 정수지 수위 up band, down band, wide up band, wide down band를 별도의 변수로 저장
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> h_le = new LinkedHashMap<>();
            LinkedHashMap<String, Object> location1 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> location2 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> location3 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> location4 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> upBand = new LinkedHashMap<>();
            LinkedHashMap<String, Object> downBand = new LinkedHashMap<>();
            LinkedHashMap<String, Object> wideUpBand = new LinkedHashMap<>();
            LinkedHashMap<String, Object> wideDownBand = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp;

            ObjectMapper objectMapper = new ObjectMapper();
            List<String> keyList;
            Object objectTemp;
            String strDate;
            try
            {
                // 계열별 정수지 수위값 등록
                for(AiReceivingRealtimeDTO receivingDTO : aiReceivingRealtimeList)
                {
                    strDate = simpleDateFormat.format(receivingDTO.getUpdate_time());
                    mapTemp = objectMapper.readValue(receivingDTO.getH_le(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));
                    JsonHLocationFloat h_location_le = objectMapper.convertValue(objectTemp, JsonHLocationFloat.class);
                    location1.put(strDate, h_location_le.getLocation1());
                    location2.put(strDate, h_location_le.getLocation2());
                    location3.put(strDate, h_location_le.getLocation3());
                    location4.put(strDate, h_location_le.getLocation4());
                }

                // 정수지 수위 밴드 값 등록
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateSearchDTO.getStart_time());
                for(AiClearOperationBandDTO operationBandDTO: aiClearOperationBandList)
                {
                    Calendar bandCalendar = Calendar.getInstance();
                    bandCalendar.setTime(operationBandDTO.getIndex());

                    calendar.set(Calendar.HOUR_OF_DAY, bandCalendar.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, bandCalendar.get(Calendar.MINUTE));
                    calendar.set(Calendar.SECOND, bandCalendar.get(Calendar.SECOND));
                    strDate = simpleDateFormat.format(calendar.getTime());
                    upBand.put(strDate, operationBandDTO.getUp());
                    downBand.put(strDate, operationBandDTO.getDown());
                }

                // 정수지 수위 넓은 밴드 값 등록
                for(AiClearOperationBandDTO operationBandDTO : aiClearWideOperationBand)
                {
                    Calendar bandCalendar = Calendar.getInstance();
                    bandCalendar.setTime(operationBandDTO.getIndex());

                    calendar.set(Calendar.HOUR_OF_DAY, bandCalendar.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, bandCalendar.get(Calendar.MINUTE));
                    calendar.set(Calendar.SECOND, bandCalendar.get(Calendar.SECOND));
                    strDate = simpleDateFormat.format(calendar.getTime());
                    wideUpBand.put(strDate, operationBandDTO.getUp());
                    wideDownBand.put(strDate, operationBandDTO.getDown());
                }

                h_le.put("location1", location1);
                h_le.put("location2", location2);
                h_le.put("location3", location3);
                h_le.put("location4", location4);
                h_le.put("up", upBand);
                h_le.put("down", downBand);
                h_le.put("wide_up", wideUpBand);
                h_le.put("wide_down", wideDownBand);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("le", h_le);

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
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_receiving_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 정수지 잔류염소 측정 이력 조회
    @RequestMapping(value = "/clear/history/cl", method = RequestMethod.PUT)
    public ResponseEntity<String> getClHistoryClear(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getClHistoryClear, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 소독 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList =
                databaseService.getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO);
        log.info("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());

        if(aiDisinfectionRealtimeList.size() > 0)
        {
            // Make Response Body
            // 정수지 유입/유출 잔류염소를 별도로 저장하기 위한 변수 선언
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> h_cl = new LinkedHashMap<>();
            LinkedHashMap<String, Object> in = new LinkedHashMap<>();
            LinkedHashMap<String, Object> out = new LinkedHashMap<>();

            // aiDisinfectionRealtimeList에서 정수지 유입/유출 잔류염소 값을 조회 및 map에 등록
            for(AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList)
            {
                String strDate = simpleDateFormat.format(dto.getUpdate_time());
                in.put(strDate, dto.getH_in_cl());
                out.put(strDate, dto.getH_out_cl());
            }
            h_cl.put("in", in);
            h_cl.put("out", out);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("h_cl", h_cl);

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

    // 원수 수온 측정 이력 조회
    @RequestMapping(value = "/raw/history/te", method = RequestMethod.PUT)
    public ResponseEntity<String> getTeHistoryRaw(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getTeHistoryRaw, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 혼화응집 공정 데이터 조회
        List<AiMixingRealtimeDTO> aiMixingRealtimeList =
                databaseService.getAiMixingRealtimeValueFromUpdateTime(dateSearchDTO);
        log.info("getAiMixingRealtimeValueFromUpdateTime, result:[{}]", aiMixingRealtimeList.size());

        if(aiMixingRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> b_te = new LinkedHashMap<>();

            // aiMixingRealtimeList에서 원수 수온 값을 조회하여 map에 등록
            for(AiMixingRealtimeDTO dto : aiMixingRealtimeList)
            {
                String strDate = simpleDateFormat.format(dto.getUpdate_time());
                b_te.put(strDate, dto.getB_te());
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("te", b_te);

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
            String strErrorBody = "{\"reason\":\"Empty ai_mixing_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 착수 공정 제어모드 변경
    @RequestMapping(value = "/receiving/control/operation", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlReceiving(@RequestBody InterfaceOperationModeDTO operationMode)
    {
        log.info("putOperationControlReceiving, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_receiving_init's operation_mode
//        log.info("update aiReceivingOperationMode:[{}], mode:[{}]",
//                databaseService.modAiReceivingOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(b_operation_mode)
        AiProcessInitDTO aiReceivingInit = databaseService.getAiReceivingInit(CommonValue.B_OPERATION_MODE);
        log.info("getAiReceivingInit, result:[{}]", aiReceivingInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            // Kafka에 전송할 값 정의
            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
            controlMap.put("tag", aiReceivingInit.getName());
            controlMap.put("value", nOperationMode);
            controlMap.put("time", strDate);

            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = objectMapper.writeValueAsString(controlMap);
            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

            // Kafka에 착수 공정 제어모드 변경 알람 전송
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
            for(TagManageDTO dto : tagManageList)
            {
                if(dto.getItem().equalsIgnoreCase("b_operation_mode_a") == true)
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
            log.error("JsonProcessingException Occurred in /receiving/control/operation API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 착수 공정 정수지 목표 수위 설정
    @RequestMapping(value = "/receiving/control/le", method = RequestMethod.PUT)
    public ResponseEntity<String> putLeControlReceiving(@RequestBody InterfaceClearLeDTO clearLe)
    {
        log.info("putLeControlReceiving, le:[{}]", clearLe);

        // 잘못된 수위 값 검사
        if(clearLe.getH_target_le_max() < 0 || clearLe.getH_target_le_min() < 0)
        {
            log.error("Invalid le:[{}]", clearLe);

            String strErrorBody = "{\"reason\":\"Invalid le\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        boolean result = true;

        // 정수지 최대 목표 수위
        result = (databaseService.modAiReceivingInit("h_target_le_max", clearLe.getH_target_le_max()) == 1) && result;

        // 정수지 최저 목표 수위
        result = (databaseService.modAiReceivingInit("h_target_le_min", clearLe.getH_target_le_min()) == 1) && result;

        if(result == true)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_receiving_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
