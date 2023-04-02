package org.kwater.aio.controller;

import org.kwater.aio.ai_dto.*;
import org.kwater.aio.dto.*;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.CommonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class FilterController
{
    @Autowired
    DatabaseServiceImpl databaseService;

//    @Autowired
//    //kafkaProducer //kafkaProducer;

    // 여과 공정 최근 데이터 조회
    @RequestMapping(value = "/filter/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestFilter()
    {
        log.info("Recv getLatestFilter");

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());

        // get ai_filter_init(f_operation_mode)
        AiProcessInitDTO aiFilterInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE);
        log.info("getAiFilterInit(operation_mode), result:[{}]", aiFilterInit != null ? 1 : 0);

        // get ai_filter_init(f_location_ti_set_max)
        AiProcessInitDTO aiFilterTi = databaseService.getAiFilterInit(CommonValue.F_LOCATION_TI_SET_MAX);
        log.info("getAiFilterInit(f_location_ti_set_max), result:[{}]", aiFilterTi != null ? 1 : 0);

        // get ai_filter_realtime
        AiFilterRealtimeDTO aiFilterRealtime = databaseService.getLatestAiFilterRealtimeValue();
        log.info("getLatestAiFilterRealtimeValue, result: [{}]", aiFilterRealtime != null ? 1 : 0);

        // get filter_realtime
        List<ProcessRealtimeDTO> filterRealtime = databaseService.getLatestFilterRealtimeValue(strPartitionName);
        log.info("getLatestFilterRealtimeValue, result:[{}]", filterRealtime.size());

        // get tag_manage(filter)
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_FILTER);
        log.info("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_FILTER, tagManageList.size());

        // get location number(지 번호)
        TagManageRangeDTO filterRange = databaseService.getTagManageRange(CommonValue.PROCESS_FILTER);
        log.info("getTagManageRange:[{}], result:[{}]", CommonValue.PROCESS_FILTER, filterRange != null ? 1 : 0);

        int nLocationMin = 0, nLocationMax = 0;
        if(filterRange != null)
        {
            nLocationMin = filterRange.getMin();
            nLocationMax = filterRange.getMax();
        }

        if(aiFilterRealtime != null)
        {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiFilterInfo = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp, locationTemp;

            // update_time
            aiFilterInfo.put("update_time", aiFilterRealtime.getUpdate_time());

            // operation_mode
            if(aiFilterInit != null)
            {
                aiFilterInfo.put("operation_mode", aiFilterInit.getValue().intValue());
            }
            else
            {
                aiFilterInfo.put("operation_mode", aiFilterRealtime.getF_operation_mode());
            }

            // peak_mode
            aiFilterInfo.put("peak_mode", aiFilterRealtime.getF_peak_mode());

            // f_location_ti_set_max
            if(aiFilterTi != null)
            {
                aiFilterInfo.put("f_location_ti_set_max", aiFilterTi.getValue().intValue());
            }
            else
            {
                aiFilterInfo.put("f_location_ti_set_max", null);
            }

            // Realtime data from SCADA
            // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiFilterInfo에 등록
            for(TagManageDTO tagManage : tagManageList)
            {
                for(ProcessRealtimeDTO dto : filterRealtime)
                {
                    if(tagManage.getItem().equalsIgnoreCase("d1_in_fr") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 1계열 혼화지 유입 유량
                        aiFilterInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("d2_in_fr") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 2계열 혼화지 유입 유량
                        aiFilterInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("e1_tb_b") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 1계열 침전지 후탁도
                        aiFilterInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("e2_tb_b") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 2계열 침전지 후탁도
                        aiFilterInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("f_sp") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 여과 속도
                        aiFilterInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("f_out_fr") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 여과지 유출 유량
                        aiFilterInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                }
            }

            // 지별 수위, 지별 탁도, 지별 여과 지속 시간, 운영 지수, 지별 상태를 저장하기 위한 변수 선언
            LinkedHashMap<String, Object> locationLeMap = new LinkedHashMap<>();
            LinkedHashMap<String, Object> locationTbMap = new LinkedHashMap<>();
            LinkedHashMap<String, Object> locationTiMap = new LinkedHashMap<>();
            LinkedHashMap<String, Object> locationStateMap = new LinkedHashMap<>();
            int nOperationCount = 0;

            for(int i = nLocationMin; i <= nLocationMax; i++)
            {
                String strLeName = "f_location_le" + i;
                String strTbName = "f_location_tb" + i;
                String strTiHName = "f_location_ti_h" + i;
                String strTiMName = "f_location_ti_m" + i;
                String strStateName = "f_location_state" + i + "_";

                int nLocationTi = 0;            // 지별 여과 지속 시간을 계산하기 위한 변수
                int nLocationState = 0;         // 지별 현재 상태를 저장하기 위한 변수
                int nLocationStateCount = 0;    // 지별 현재 상태가 중복 임을 확인하기 위한 변수

                for(TagManageDTO tagManage : tagManageList)
                {
                    for(ProcessRealtimeDTO dto : filterRealtime)
                    {
                        if(tagManage.getItem().equalsIgnoreCase(strLeName) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 지별 수위 값 등록
                            locationLeMap.put("location" + i, Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strTbName) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 지별 탁도 값 등록
                            locationTbMap.put("location" + i, Float.parseFloat(dto.getValue()));
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
                        else if(tagManage.getItem().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_FILTER)== true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true &&
                                Float.parseFloat(dto.getValue()) > 0)
                        {
                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
                            nLocationState = CommonValue.FILTER_STATE_FILTER;
                            nLocationStateCount ++;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_RELAX) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true &&
                                Float.parseFloat(dto.getValue()) > 0)
                        {
                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
                            nLocationState = CommonValue.FILTER_STATE_RELAX;
                            nLocationStateCount ++;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_BW) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true &&
                                Float.parseFloat(dto.getValue()) > 0)
                        {
                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
                            nLocationState = CommonValue.FILTER_STATE_BW;
                            nLocationStateCount ++;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_BW_RELAX) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true &&
                                Float.parseFloat(dto.getValue()) > 0)
                        {
                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
                            nLocationState = CommonValue.FILTER_STATE_BW_RELAX;
                            nLocationStateCount ++;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_CIRCULATION) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true &&
                                Float.parseFloat(dto.getValue()) > 0)
                        {
                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
                            nLocationState = CommonValue.FILTER_STATE_CIRCULATION;
                            nLocationStateCount ++;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_CIRCULATION_RELAX) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true &&
                                Float.parseFloat(dto.getValue()) > 0)
                        {
                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
                            nLocationState = CommonValue.FILTER_STATE_CIRCULATION_RELAX;
                            nLocationStateCount ++;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strStateName + CommonValue.FILTER_STATE_CIRCULATION_STANDBY) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true &&
                                Float.parseFloat(dto.getValue()) > 0)
                        {
                            // 현재 지별 상태를 저장하고 지별 상태 중복 확인 값을 증가
                            nLocationState = CommonValue.FILTER_STATE_CIRCULATION_STANDBY;
                            nLocationStateCount ++;
                            break;
                        }
                    }
                }
                locationTiMap.put("location"+i, nLocationTi);

                if(nLocationStateCount == 1)
                {
                    // 지별 상태가 중복되어 있지 않을 경우 현재 지별 상태를 locationStateMap에 등록
                    locationStateMap.put("location"+i, nLocationState);
                    if(nLocationState == CommonValue.FILTER_STATE_FILTER)
                    {
                        nOperationCount ++;
                    }
                }
                else
                {
                    // 지별 상태가 중복되어 있다면 상태 없음 처리
                    locationStateMap.put("location"+i, CommonValue.FILTER_STATE_NONE);
                }
            }

            try
            {

                // 현재 운영지 수
                aiFilterInfo.put("f_operation_count", nOperationCount);

                // AI 운영지 수 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_operation_count(), LinkedHashMap.class);
                ArrayList<String>keyList = new ArrayList<>(mapTemp.keySet());
                Object objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiFilterInfo.put("ai_f_operation_count", objectTemp);

                // 지별 현재 상태
                aiFilterInfo.put("f_location_state", locationStateMap);

                // AI 지별 수위 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_le(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                locationTemp = new LinkedHashMap<>();
                for(String strKey : keyList)
                {
                    LinkedHashMap<String, Object> locationMapTemp = objectMapper.convertValue(mapTemp.get(strKey), LinkedHashMap.class);
                    ArrayList<String> locationKeyList = new ArrayList<>(locationMapTemp.keySet());
                    locationTemp.put(strKey, locationMapTemp.get(locationKeyList.get(0)));
                }
                aiFilterInfo.put("ai_f_location_le", locationTemp);

                // 지별 수위
                aiFilterInfo.put("f_location_le", locationLeMap);

                // 지별 탁도
                aiFilterInfo.put("f_location_tb", locationTbMap);

                // 지별 여과 지속 시간
                aiFilterInfo.put("f_location_ti", locationTiMap);

                // AI 지별 여과 지속 시간 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_ti(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiFilterInfo.put("ai_f_location_ti", objectTemp);

                // AI 지별 역세 시작 시점 예측
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_bw_start_ti(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiFilterInfo.put("ai_f_location_bw_start_ti", objectTemp);

                // AI 지별 운영 스케쥴
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_operation(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                aiFilterInfo.put("ai_f_location_schedule", objectTemp);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            catch(NumberFormatException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error, Number Format Exception\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiFilterInfo);

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

    // 실시간 여과 공정 지별 세부 항목 조회
    @RequestMapping(value = "/filter/location/{locationNumber}", method = RequestMethod.GET)
    public ResponseEntity<String> getLocationFilter(@PathVariable int locationNumber)
    {
        log.info("getLocationFilter, locationNumber:[{}]", locationNumber);

        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_FILTER);
        log.info("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_FILTER, tagManageList.size());
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

            // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
            Calendar calendarToday = Calendar.getInstance();
            calendarToday.set(Calendar.MINUTE, 0);
            calendarToday.set(Calendar.SECOND, 0);
            calendarToday.set(Calendar.HOUR_OF_DAY, 0);
            SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
            String strPartitionName = partitionNameFormat.format(calendarToday.getTime());

            // get ai_filter_realtime
            AiFilterRealtimeDTO aiFilterRealtime = databaseService.getLatestAiFilterRealtimeValue();
            log.info("getLatestAiFilterRealtimeValue, result:[{}]", aiFilterRealtime != null ? 1 : 0);

            // get filter_realtime
            List<ProcessRealtimeDTO> filterRealtime = databaseService.getLatestFilterRealtimeValue(strPartitionName);
            log.info("getLatestFilterRealtimeValue, result:[{}]", filterRealtime.size());
            if(aiFilterRealtime != null)
            {
                // JSON 처리를 위한 ObjectMapper 선언
                ObjectMapper objectMapper = new ObjectMapper();

                // Make Response Body
                LinkedHashMap<String, Object> aiFilterLocationInfo = new LinkedHashMap<>();
                LinkedHashMap<String, Object> mapTemp;

                // update_time
                aiFilterLocationInfo.put("update_time", aiFilterRealtime.getUpdate_time());

                // 수위, 탁도, 여과 지속 시간, 역세 후 대기 시간/분을 저장하기 위한 변수 선언
                String strLeName = "f_location_le" + locationNumber;
                String strTbName = "f_location_tb" + locationNumber;
                String strTiHName = "f_location_ti_h" + locationNumber;
                String strTiMName = "f_location_ti_m" + locationNumber;
                String strBwWaitTiHName = "f_location_bw_wait_ti_h" + locationNumber;
                String strBwWaitTiMName = "f_location_bw_wait_ti_m" + locationNumber;

                int nLocationTi = 0;        // 여과 지속 시간을 계산하기 위한 변수
                int nLocationBwWaitTi = 0;  // 역세 후 대기 시간을 계산하기 위한 변수

                // Realtime data from SCADA
                // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiFilterLocationInfo에 등록
                for(TagManageDTO tagManage : tagManageList)
                {
                    for (ProcessRealtimeDTO dto : filterRealtime)
                    {
                        if (tagManage.getItem().equalsIgnoreCase("d1_in_fr") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 1계열 혼화지 유입 유량
                            aiFilterLocationInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if (tagManage.getItem().equalsIgnoreCase("d2_in_fr") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 2계열 혼화지 유입 유량
                            aiFilterLocationInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("f_sp") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 여과 속도
                            aiFilterLocationInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("f_out_fr") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 여과지 유출 유량
                            aiFilterLocationInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strLeName) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 여과지 수위
                            aiFilterLocationInfo.put("f_location_le", Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strTbName) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 여과지 탁도
                            aiFilterLocationInfo.put("f_location_tb", Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strTiHName) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 여과 지속 시간 계산(시)
                            nLocationTi += (int)Float.parseFloat(dto.getValue()) * 60;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strTiMName) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 여과 지속 시간 계산(분)
                            nLocationTi += (int)Float.parseFloat(dto.getValue());
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strBwWaitTiHName) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 역세 후 대기 시간 계산(시)
                            nLocationBwWaitTi += (int)Float.parseFloat(dto.getValue()) * 60;
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase(strBwWaitTiMName) == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 역세 후 대기 시간 계산(분)
                            nLocationBwWaitTi += (int)Float.parseFloat(dto.getValue());
                            break;
                        }
                    }
                }

                aiFilterLocationInfo.put("f_location_ti", nLocationTi);
                aiFilterLocationInfo.put("f_location_bw_wait_ti", nLocationBwWaitTi);

                try
                {
                    // AI 수위 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_le(), LinkedHashMap.class);
                    ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                    Object objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    aiFilterLocationInfo.put("ai_f_location_le", mapTemp.get("location" + locationNumber));

                    // AI 여과 지속 시간 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_ti(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    Integer ai_f_location_ti = Integer.parseInt(mapTemp.get("location" + locationNumber).toString());
                    aiFilterLocationInfo.put("ai_f_location_ti", ai_f_location_ti);

                    // AI 역세 시작 시간 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_bw_start_ti(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    aiFilterLocationInfo.put("ai_f_location_bw_start_ti", mapTemp.get("location" + locationNumber));

                    // AI 여과 종료 시간 예측(AI 여과 지속 시간 예측 - 현재 여과 지속 시간)
                    aiFilterLocationInfo.put("ai_f_location_end_ti", ai_f_location_ti - nLocationTi);

                    // AI 역세 후 대기 시간 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getF_location_bw_wait_ti(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    aiFilterLocationInfo.put("f_location_bw_wait_ti", mapTemp.get("location" + locationNumber));

                    // AI 운영 스케쥴 예측
                    // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                    mapTemp = objectMapper.readValue(aiFilterRealtime.getAi_f_location_operation(), LinkedHashMap.class);
                    keyList = new ArrayList<>(mapTemp.keySet());
                    objectTemp = mapTemp.get(keyList.get(0));

                    mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                    aiFilterLocationInfo.put("ai_f_location_schedule", mapTemp.get("location" + locationNumber));
                }
                catch(JsonProcessingException e)
                {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("location", aiFilterLocationInfo);

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
                String strErrorBody = "{\"reason\":\"Empty ai_filter\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Empty tag_manage\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 여과 공정 제어모드 변경
    @RequestMapping(value = "/filter/control/operation", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlFilter(@RequestBody InterfaceOperationModeDTO operationMode)
    {
        log.info("putOperationControlFilter, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_filter_init's operation_mode
//        log.info("update aiFilterOperationMode:[{}], mode:[{}]",
//                databaseService.modAiFilterOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(f_operation_mode)
        AiProcessInitDTO aiFilterInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE);
        log.info("getAiFilterInit, result:[{}]", aiFilterInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            // Kafka에 전송할 값 정의
            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
            controlMap.put("tag", aiFilterInit.getName());
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
                if(dto.getItem().equalsIgnoreCase("f_operation_mode_a") == true)
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
            log.error("JsonProcessingException Occurred in /filter/control/operation API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 여과 공정 최대 여과 지속 시간 설정
    @RequestMapping(value = "/filter/control/ti", method = RequestMethod.PUT)
    public ResponseEntity<String> putTiControlFilter(@RequestBody InterfaceFilterTiDTO filterTi)
    {
        int nTi = filterTi.getF_location_ti_set_max();
        log.info("putTiControlFilter, ti:[{}]", nTi);

        // 잘못된 시간 값 검사
        if(nTi < 0)
        {
            log.error("Invalid ti:[{}]", nTi);

            String strErrorBody = "{\"reason\":\"Invalid ti\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        int nResult = databaseService.modAiFilterInitTi(nTi);
        log.info("modAiFilterInitTi, result:[{}]", nResult);

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

    // AI 여과, GAC 여과 운영 스케쥴 예측값 조회
    @RequestMapping(value = "/filter/schedule", method = RequestMethod.GET)
    public ResponseEntity<String> getScheduleFilter()
    {
        log.info("Recv getScheduleFilter");

        // get ai_filter_realtime
        AiFilterRealtimeDTO aiFilterRealtime = databaseService.getLatestAiFilterRealtimeValue();
        log.info("getLatestAiFilterRealtimeValue, result:[{}]", aiFilterRealtime != null ? 1 : 0);

        // get ai_gac_realtime
        AiGacRealtimeDTO aiGacRealtime = databaseService.getLatestAiGacRealtimeValue();
        log.info("getLatestAiGacRealtimeValue, result:[{}]", aiGacRealtime != null ? 1 : 0);

        if(aiFilterRealtime != null && aiGacRealtime != null)
        {
            try
            {
                // 전체 schedule을 저장할 scheduleMap, 여과/GAC 여과의 스케쥴을 저장할 filterMap, gacMap 선언
                LinkedHashMap<String, Object> scheduleMap = new LinkedHashMap<>();
                LinkedHashMap<String, Object> filterMap = new LinkedHashMap<>();
                LinkedHashMap<String, Object> gacMap = new LinkedHashMap<>();

                // 여과 schedule
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                ObjectMapper objectMapper = new ObjectMapper();
                LinkedHashMap<String, Object> mapTemp = objectMapper.readValue(aiFilterRealtime.getPred_schedule_final(), LinkedHashMap.class);
                List<String> keyList = new ArrayList<>(mapTemp.keySet());
                Object objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                for(String key : keyList)
                {
                    if(key.indexOf("location") >= 0)
                    {
                        filterMap.put(key, mapTemp.get(key));
                    }
                }

                // GAC 여과 schedule
                // 데이터 값이 JSON으로 저장되어 있으므로 JSON 처리
                mapTemp = objectMapper.readValue(aiGacRealtime.getPred_schedule_mv_bw(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));

                mapTemp = objectMapper.convertValue(objectTemp, LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                for(String key : keyList)
                {
                    if(key.indexOf("location") >= 0)
                    {
                        gacMap.put(key, mapTemp.get(key));
                    }
                }

                scheduleMap.put("filter", filterMap);
                scheduleMap.put("gac", gacMap);

                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("schedule", scheduleMap);

                // ObjectMapper를 통해 JSON 값을 String으로 변환
                String strBody = objectMapper.writeValueAsString(responseBody);
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
            String strErrorBody = "{\"reason\":\"Empty ai_filter or ai_gac\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
