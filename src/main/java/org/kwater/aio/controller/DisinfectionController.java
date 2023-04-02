package org.kwater.aio.controller;

import org.kwater.aio.ai_dto.AiDisinfectionRealtimeDTO;
import org.kwater.aio.ai_dto.AiProcessInitDTO;
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
public class DisinfectionController
{
    @Autowired
    DatabaseServiceImpl databaseService;

//    @Autowired
//    //kafkaProducer //kafkaProducer;

    // 소독 공정 최근 데이터 조회
    @RequestMapping(value = "/disinfection/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestDisinfection()
    {
        log.info("Recv getLatestDisinfection");

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());

        // get ai_disinfection_init(g_pre_operation_mode)
        AiProcessInitDTO aiPreDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE);
        log.info("getAiDisinfectionInit pre, result:[{}]", aiPreDisinfectionInit != null ? 1 : 0);

        // get ai_disinfection_init(g_peri_operation_mode)
        AiProcessInitDTO aiPeriDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PERI_OPERATION_MODE);
        log.info("getAiDisinfectionInit peri, result:[{}]", aiPeriDisinfectionInit != null ? 1 : 0);

        // get ai_disinfection_init(g_post_operation_mode)
        AiProcessInitDTO aiPostDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE);
        log.info("getAiDisinfectionInit post, result:[{}]", aiPostDisinfectionInit != null ? 1 : 0);

        // get ai_disinfection_init
        List<AiProcessInitDTO> aiDisinfectionInitList = databaseService.getAllAiDisinfectionInit();
        log.info("getAllAiDisinfectionInit, result:[{}]", aiDisinfectionInitList.size());

        // get ai_disinfection_realtime
        AiDisinfectionRealtimeDTO aiDisinfectionRealtime = databaseService.getLatestAiDisinfectionRealtimeValue();
        log.info("getLatestAiDisinfectionRealtimeValue, result:[{}]", aiDisinfectionRealtime != null ? 1 : 0);

        // get disinfection_realtime
        List<ProcessRealtimeDTO> disinfectionRealtime = databaseService.getLatestDisinfectionRealtimeValue(strPartitionName);
        log.info("getLatestDisinfectionRealtimeValue, result:[{}]", disinfectionRealtime.size());

        // get tag_manage(disinfection)
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_DISINFECTION);
        log.info("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_DISINFECTION, tagManageList.size());

        if(aiDisinfectionRealtime != null)
        {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiDisinfectionInfo = new LinkedHashMap<>();

            // update_time
            aiDisinfectionInfo.put("update_time", aiDisinfectionRealtime.getUpdate_time());

            // pre_operation_mode
            if(aiPreDisinfectionInit != null)
            {
                aiDisinfectionInfo.put("pre_operation_mode", aiPreDisinfectionInit.getValue().intValue());
            }
            else
            {
                aiDisinfectionInfo.put("pre_operation_mode", aiDisinfectionRealtime.getG_pre_operation_mode());
            }

            // peri_operataion_mode
            if(aiPeriDisinfectionInit != null)
            {
                aiDisinfectionInfo.put("peri_operation_mode", aiPeriDisinfectionInit.getValue().intValue());
            }
            else
            {
                aiDisinfectionInfo.put("peri_operation_mode", aiDisinfectionRealtime.getG_peri_operation_mode());
            }

            // post_operataion_mode
            if(aiPostDisinfectionInit != null)
            {
                aiDisinfectionInfo.put("post_operation_mode", aiPostDisinfectionInit.getValue().intValue());
            }
            else
            {
                aiDisinfectionInfo.put("post_operation_mode", aiDisinfectionRealtime.getG_post_operation_mode());
            }

            // Realtime data from SCADA
            // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiDisinfectionInfo에 등록
            for(TagManageDTO tagManage : tagManageList)
            {
                for(ProcessRealtimeDTO dto : disinfectionRealtime)
                {
                    if(tagManage.getItem().equalsIgnoreCase("b_te") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 원수 수온
                        aiDisinfectionInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                    else if(tagManage.getItem().equalsIgnoreCase("air_te") == true &&
                            tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                    {
                        // 기온
                       aiDisinfectionInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                        break;
                    }
                }
            }

            for(AiProcessInitDTO dto : aiDisinfectionInitList)
            {
                if(dto.getItem().equalsIgnoreCase("d1_target_cl") == true)
                {
                    // 1계열 혼화지 목표 잔류염소
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("d2_target_cl") == true)
                {
                    // 2계열 혼화지 목표 잔류염소
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e1_target_cl") == true)
                {
                    // 1계열 침전지 목표 잔류염소
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("e2_target_cl") == true)
                {
                    // 2계열 침전지 목표 잔류염소
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("h_target_cl") == true)
                {
                    // 정수지 목표 잔류염소
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_pre1_set_max") == true)
                {
                    // 1계열 전염소 최대 주입률 설정
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_pre1_set_min") == true)
                {
                    // 1계열 전염소 최소 주입률 설정
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_pre1_chg_limit_for_onetime") == true)
                {
                    // 1계열 전염소 변경 한계치
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_pre2_set_max") == true)
                {
                    // 2계열 전염소 최대 주입률 설정
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_pre2_set_min") == true)
                {
                    // 2계열 전염소 최소 주입률 설정
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_pre2_chg_limit_for_onetime") == true)
                {
                    // 2계열 전염소 주입률 변경 한계치
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_peri1_set_max") == true)
                {
                    // 1계열 중염소 최대 주입률 설정
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_peri1_set_min") == true)
                {
                    // 1계열 중염소 최소 주입률 설정
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_peri1_chg_limit_for_onetime") == true)
                {
                    // 1계열 중염소 주입률 변경 한계치
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_peri2_set_max") == true)
                {
                    // 2계열 중염소 최대 주입률 설정
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_peri2_set_min") == true)
                {
                    // 2계열 중염소 최소 주입률 설정
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_peri2_chg_limit_for_onetime") == true)
                {
                    // 2계열 중염소 주입률 변경 한계치
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_post_set_max") == true)
                {
                    // 후염소 최대 주입률 설정
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_post_set_min") == true)
                {
                    // 후염소 최소 주입률 설정
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
                else if(dto.getItem().equalsIgnoreCase("g_post_chg_limit_for_onetime") == true)
                {
                    // 후염소 주입률 변경 한계치
                    aiDisinfectionInfo.put(dto.getItem(), dto.getValue());
                }
            }

            // 계열별 혼화지 잔류염소
            aiDisinfectionInfo.put("d1_cl", aiDisinfectionRealtime.getD1_cl());
            aiDisinfectionInfo.put("d2_cl", aiDisinfectionRealtime.getD2_cl());

            // 계열별 혼화지 유입 유량
            aiDisinfectionInfo.put("d1_in_fr", aiDisinfectionRealtime.getD1_in_fr());
            aiDisinfectionInfo.put("d2_in_fr", aiDisinfectionRealtime.getD2_in_fr());

            // 계열별 침전지 잔류염소
            aiDisinfectionInfo.put("e1_cl", aiDisinfectionRealtime.getE1_cl());
            aiDisinfectionInfo.put("e2_cl", aiDisinfectionRealtime.getE2_cl());

            // 정수지 유입 잔류염소
            aiDisinfectionInfo.put("h_in_cl", aiDisinfectionRealtime.getH_in_cl());

            // 정수지 유출 잔류염소
            aiDisinfectionInfo.put("h_out_cl", aiDisinfectionRealtime.getH_out_cl());

            // 정수지 pH
            aiDisinfectionInfo.put("h_ph", aiDisinfectionRealtime.getH_ph());

            // 정수지 탁도
            aiDisinfectionInfo.put("h_tb", aiDisinfectionRealtime.getH_tb());

            // 계열별 전염소 주입률
            aiDisinfectionInfo.put("g_pre1_chlorination", aiDisinfectionRealtime.getG_pre1_chlorination());
            aiDisinfectionInfo.put("g_pre2_chlorination", aiDisinfectionRealtime.getG_pre2_chlorination());

            // 계열별 중염소 주입률
            aiDisinfectionInfo.put("g_peri1_chlorination", aiDisinfectionRealtime.getG_peri1_chlorination());
            aiDisinfectionInfo.put("g_peri2_chlorination", aiDisinfectionRealtime.getG_peri2_chlorination());

            // 후염소 주입률
            aiDisinfectionInfo.put("g_post_chlorination", aiDisinfectionRealtime.getG_post_chlorination());

            // 계열별 AI 전염소 보정값 예측
            aiDisinfectionInfo.put("ai_g_pre1_corrected", aiDisinfectionRealtime.getAi_g_pre1_corrected());
            aiDisinfectionInfo.put("ai_g_pre2_corrected", aiDisinfectionRealtime.getAi_g_pre2_corrected());

            // 계열별 AI 중염소 보정값 예측
            aiDisinfectionInfo.put("ai_g_peri1_corrected", aiDisinfectionRealtime.getAi_g_peri1_corrected());
            aiDisinfectionInfo.put("ai_g_peri2_corrected", aiDisinfectionRealtime.getAi_g_peri2_corrected());

            // AI 후염소 보정값 예측
            aiDisinfectionInfo.put("ai_g_post_corrected", aiDisinfectionRealtime.getAi_g_post_corrected());

            // 계열별 AI 전염소 증발량 예측
            aiDisinfectionInfo.put("ai_g_pre1_evaporation", aiDisinfectionRealtime.getAi_g_pre1_evaporation());
            aiDisinfectionInfo.put("ai_g_pre2_evaporation", aiDisinfectionRealtime.getAi_g_pre2_evaporation());

            // 계열별 AI 전염소 주입률 예측
            aiDisinfectionInfo.put("ai_g_pre1_chlorination", aiDisinfectionRealtime.getAi_g_pre1_chlorination());
            aiDisinfectionInfo.put("ai_g_pre2_chlorination", aiDisinfectionRealtime.getAi_g_pre2_chlorination());

            // 계열별 AI 중염소 주입률 예측
            aiDisinfectionInfo.put("ai_g_peri1_chlorination", aiDisinfectionRealtime.getAi_g_peri1_chlorination());
            aiDisinfectionInfo.put("ai_g_peri2_chlorination", aiDisinfectionRealtime.getAi_g_peri2_chlorination());

            // AI 후염소 주입률 예측
            aiDisinfectionInfo.put("ai_g_post_chlorination", aiDisinfectionRealtime.getAi_g_post_chlorination());


            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiDisinfectionInfo);

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
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 전염소 주입률 측정 이력 조회
    @RequestMapping(value = "/disinfection/history/pre", method = RequestMethod.PUT)
    public ResponseEntity<String> getPreHistoryDisinfection(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getPreHistoryDisinfection, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 소독 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList =
                databaseService.getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO);
        log.info("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());
        if(aiDisinfectionRealtimeList.size() > 0)
        {
            // Make Response Body
            LinkedHashMap<String, Object> seriesPreInfo = new LinkedHashMap<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate;

            // aiDisinfectionRealtimeList에서 계열별 전염소 주입률을 조회하여 series1PreMap, series2PreMap에 등록
            LinkedHashMap<String, Float> series1PreMap = new LinkedHashMap<>();
            LinkedHashMap<String, Float> series2PreMap = new LinkedHashMap<>();
            for(AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList)
            {
                strDate = simpleDateFormat.format(dto.getUpdate_time());
                series1PreMap.put(strDate, dto.getG_pre1_chlorination());
                series2PreMap.put(strDate, dto.getG_pre2_chlorination());
            }

            // seriesPreInfo에 series1PreMap, series2PreMap 등록
            seriesPreInfo.put("series1", series1PreMap);
            seriesPreInfo.put("series2", series2PreMap);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("pre_chlorination", seriesPreInfo);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody;
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
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 전염소 증발량 예측 이력 조회
    @RequestMapping(value = "/disinfection/history/evaporation", method = RequestMethod.PUT)
    public ResponseEntity<String> getEvaporationHistoryDisinfection(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getEvaporationHistoryDisinfection, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

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

            ObjectMapper objectMapper = new ObjectMapper();

            // aiDisinfectionRealtimeList에서 계열별 전염소 증발량을 조회하여 series1, series2에 등록
            for(AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList)
            {
                String strDate = simpleDateFormat.format(dto.getUpdate_time());
                series1.put(strDate, dto.getAi_g_pre1_evaporation());
                series2.put(strDate, dto.getAi_g_pre2_evaporation());
            }

            // pre_evaporation에 series1, series2 등록
            LinkedHashMap<String, Object> pre_evaporation = new LinkedHashMap<>();
            pre_evaporation.put("series1", series1);
            pre_evaporation.put("series2", series2);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("pre_evaporation", pre_evaporation);

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

    // 후염소 보정값 예측 이력 조회
    @RequestMapping(value = "/disinfection/history/corrected/post", method = RequestMethod.PUT)
    public ResponseEntity<String> getPostCorrectedHistoryDisinfection(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("getPostCorrectedHistoryDisinfection, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // 소독 공정 데이터 조회
        List<AiDisinfectionRealtimeDTO> aiDisinfectionRealtimeList =
                databaseService.getAiDisinfectionRealtimeValueFromUpdateTime(dateSearchDTO);
        log.info("getAiDisinfectionRealtimeValueFromUpdateTime, result:[{}]", aiDisinfectionRealtimeList.size());

        if(aiDisinfectionRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> corrected = new LinkedHashMap<>();

            // aiDisinfectionRealtimeList에서 후염소 보정값 예측을 조회하여 corrected에 등록
            for(AiDisinfectionRealtimeDTO dto : aiDisinfectionRealtimeList)
            {
                String strDate = simpleDateFormat.format(dto.getUpdate_time());
                corrected.put(strDate, dto.getAi_g_post_corrected());
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("corrected", corrected);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody;
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
        else
        {
            String strErrorBody = "{\"reason\":\"Empty ai_disinfection_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 소독(전) 공정 제어모드 변경
    @RequestMapping(value = "/disinfection/control/operation/pre", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlPreDisinfection(@RequestBody InterfaceOperationModeDTO operationMode)
    {
        log.info("putOperationControlPreDisinfection, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_disinfection_init's operation_mode
//        log.info("update aiDisinfectionOperationMode:[{}], mode:[{}]",
//                databaseService.modAiDisinfectionOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(g_operation_mode)
        AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE);
        log.info("getAiDisinfectionInit pre, result:[{}]", aiDisinfectionInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            // Kafka에 전송할 값 정의
            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
            controlMap.put("tag", aiDisinfectionInit.getName());
            controlMap.put("value", nOperationMode);
            controlMap.put("time", strDate);

            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = objectMapper.writeValueAsString(controlMap);
            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

            // Kafka에 소독(전) 공정 제어모드 변경 알람 전송
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
            for(TagManageDTO dto : tagManageList)
            {
                if(dto.getItem().equalsIgnoreCase("g_pre_operation_mode_a") == true)
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
            log.error("JsonProcessingException Occurred in /disinfection/control/operation/pre API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 소독(중) 공정 제어모드 변경
    @RequestMapping(value = "/disinfection/control/operation/peri", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlPeriDisinfection(@RequestBody InterfaceOperationModeDTO operationMode)
    {
        log.info("putOperationControlPeriDisinfection, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_disinfection_init's operation_mode
//        log.info("update aiDisinfectionOperationMode:[{}], mode:[{}]",
//                databaseService.modAiDisinfectionOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(g_operation_mode)
        AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PERI_OPERATION_MODE);
        log.info("getAiDisinfectionInit peri, result:[{}]", aiDisinfectionInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            // Kafka에 전송할 값 정의
            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
            controlMap.put("tag", aiDisinfectionInit.getName());
            controlMap.put("value", nOperationMode);
            controlMap.put("time", strDate);

            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = objectMapper.writeValueAsString(controlMap);
            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

            // Kafka에 소독(중) 공정 제어모드 변경 알람 전송
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
            for(TagManageDTO dto : tagManageList)
            {
                if(dto.getItem().equalsIgnoreCase("g_peri_operation_mode_a") == true)
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
            log.error("JsonProcessingException Occurred in /disinfection/control/operation/peri API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 소독(후) 공정 제어모드 변경
    @RequestMapping(value = "/disinfection/control/operation/post", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlPostDisinfection(@RequestBody InterfaceOperationModeDTO operationMode)
    {
        log.info("putOperationControlPostDisinfection, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_disinfection_init's operation_mode
//        log.info("update aiDisinfectionOperationMode:[{}], mode:[{}]",
//                databaseService.modAiDisinfectionOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(g_operation_mode)
        AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE);
        log.info("getAiDisinfectionInit post, result:[{}]", aiDisinfectionInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            // Kafka에 전송할 값 정의
            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
            controlMap.put("tag", aiDisinfectionInit.getName());
            controlMap.put("value", nOperationMode);
            controlMap.put("time", strDate);

            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 Kafka 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = objectMapper.writeValueAsString(controlMap);
            //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

            // Kafka에 소독(후) 공정 제어모드 변경 알람 전송
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
            for(TagManageDTO dto : tagManageList)
            {
                if(dto.getItem().equalsIgnoreCase("g_post_operation_mode_a") == true)
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
            log.error("JsonProcessingException Occurred in /disinfection/control/operation/post API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    // 소독(전) 알고리즘 설정값 변경
    @RequestMapping(value = "/disinfection/control/pre", method = RequestMethod.PUT)
    public ResponseEntity<String> putPreControlDisinfection(@RequestBody InterfaceDisinfectionPreDTO disinfectionPre)
    {
        log.info("putPreControlDisinfection, pre:[{}]", disinfectionPre);

        boolean result = true;

        // update 1계열 전염소 최대 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_pre1_set_max", disinfectionPre.getG_pre1_set_max()) == 1) && result;

        // update 1계열 전염소 최소 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_pre1_set_min", disinfectionPre.getG_pre1_set_min()) == 1) && result;

        // update 1계열 전염소 변경 한계치
        result = (databaseService.modAiDisinfectionInit("g_pre1_chg_limit_for_onetime", disinfectionPre.getG_pre1_chg_limit_for_onetime()) == 1) && result;

        // update 1계열 혼화지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("d1_target_cl", disinfectionPre.getD1_target_cl()) == 1) && result;

        // update 1계열 침전지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("e1_target_cl", disinfectionPre.getE1_target_cl()) == 1) && result;

        // update 2계열 전염소 최대 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_pre2_set_max", disinfectionPre.getG_pre2_set_max()) == 1) && result;

        // update 2계열 전염소 최소 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_pre2_set_min", disinfectionPre.getG_pre2_set_min()) == 1) && result;

        // update 2계열 전염소 변경 한계치
        result = (databaseService.modAiDisinfectionInit("g_pre2_chg_limit_for_onetime", disinfectionPre.getG_pre2_chg_limit_for_onetime()) == 1) && result;

        // update 2계열 혼화지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("d2_target_cl", disinfectionPre.getD2_target_cl()) == 1) && result;

        // update 2계열 침전지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("e2_target_cl", disinfectionPre.getE2_target_cl()) == 1) && result;

        // 업데이트가 성공하면 Kafka를 통해 설정값 전달
        if(result == true)
        {
            // send control value to kafka ai_control
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = simpleDateFormat.format(new Date().getTime());

            LinkedHashMap<String, Object> controlMap;
            ObjectMapper objectMapper = new ObjectMapper();

            List<AiProcessInitDTO> aiDisinfectionInitList = databaseService.getAllAiDisinfectionInit();
            log.info("getAllAiDisinfectionInit, result:[{}]", aiDisinfectionInitList.size());

            try
            {
                for(AiProcessInitDTO dto : aiDisinfectionInitList)
                {
                    if(dto.getItem().equalsIgnoreCase("d1_target_cl") == true)
                    {
                        // 1계열 혼화지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPre.getD1_target_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("d2_target_cl") == true)
                    {
                        // 2계열 혼화지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPre.getD2_target_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("e1_target_cl") == true)
                    {
                        // 1계열 침전지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPre.getE1_target_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("e2_target_cl") == true)
                    {
                        // 2계열 침전지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPre.getE2_target_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_pre1_chg_limit_for_onetime") == true)
                    {
                        // 1계열 전염소 변경 한계치 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPre.getG_pre1_chg_limit_for_onetime());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_pre1_set_max") == true)
                    {
                        // 1계열 전염소 최대 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPre.getG_pre1_set_max());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_pre1_set_min") == true)
                    {
                        // 1계열 전염소 최소 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPre.getG_pre1_set_min());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_pre2_chg_limit_for_onetime") == true)
                    {
                        // 2계열 전염소 변경 한계치 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPre.getG_pre2_chg_limit_for_onetime());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_pre2_set_max") == true)
                    {
                        // 2계열 전염소 최대 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPre.getG_pre2_set_max());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_pre2_set_min") == true)
                    {
                        // 2계열 전염소 최소 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPre.getG_pre2_set_min());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                }
            }
            catch(JsonProcessingException e)
            {
                log.error("JsonProcessingException Occurred in /disinfection/control/pre API");
            }


            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_disinfection_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 소독(중) 알고리즘 설정값 변경
    @RequestMapping(value = "/disinfection/control/peri", method = RequestMethod.PUT)
    public ResponseEntity<String> putPeriControlDisinfection(@RequestBody InterfaceDisinfectionPeriDTO disinfectionPeri)
    {
        log.info("putPeriControlDisinfection, peri:[{}]", disinfectionPeri);

        boolean result = true;

        // update 1계열 중염소 최대 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_peri1_set_max", disinfectionPeri.getG_peri1_set_max()) == 1) && result;

        // update 1계열 중염소 최소 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_peri1_set_min", disinfectionPeri.getG_peri1_set_min()) == 1) && result;

        // update 1계열 중염소 변경 한계치
        result = (databaseService.modAiDisinfectionInit("g_peri1_chg_limit_for_onetime", disinfectionPeri.getG_peri1_chg_limit_for_onetime()) == 1) && result;

        // update 1계열 혼화지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("d1_target_cl", disinfectionPeri.getD1_target_cl()) == 1) && result;

        // update 1계열 침전지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("e1_target_cl", disinfectionPeri.getE1_target_cl()) == 1) && result;

        // update 2계열 중염소 최대 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_peri2_set_max", disinfectionPeri.getG_peri2_set_max()) == 1) && result;

        // update 2계열 중염소 최소 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_peri2_set_min", disinfectionPeri.getG_peri2_set_min()) == 1) && result;

        // update 2계열 중염소 변경 한계치
        result = (databaseService.modAiDisinfectionInit("g_peri2_chg_limit_for_onetime", disinfectionPeri.getG_peri2_chg_limit_for_onetime()) == 1) && result;

        // update 2계열 혼화지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("d2_target_cl", disinfectionPeri.getD2_target_cl()) == 1) && result;

        // update 2계열 침전지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("e2_target_cl", disinfectionPeri.getE2_target_cl()) == 1) && result;

        // 업데이트가 성공하면 Kafka를 통해 설정값 전달
        if(result == true)
        {
            // send control value to kafka ai_control
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = simpleDateFormat.format(new Date().getTime());

            LinkedHashMap<String, Object> controlMap;
            ObjectMapper objectMapper = new ObjectMapper();

            List<AiProcessInitDTO> aiDisinfectionInitList = databaseService.getAllAiDisinfectionInit();
            log.info("getAllAiDisinfectionInit, result:[{}]", aiDisinfectionInitList.size());

            try
            {
                for(AiProcessInitDTO dto : aiDisinfectionInitList)
                {
                    if(dto.getItem().equalsIgnoreCase("d1_target_cl") == true)
                    {
                        // 1계열 혼화지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPeri.getD1_target_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("d2_target_cl") == true)
                    {
                        // 2계열 혼화지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPeri.getD2_target_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("e1_target_cl") == true)
                    {
                        // 1계열 침전지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPeri.getE1_target_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("e2_target_cl") == true)
                    {
                        // 2계열 침전지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPeri.getE2_target_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_peri1_chg_limit_for_onetime") == true)
                    {
                        // 1계열 중염소 변경 한계치 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPeri.getG_peri1_chg_limit_for_onetime());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_peri1_set_max") == true)
                    {
                        // 1계열 중염소 최대 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPeri.getG_peri1_set_max());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_peri1_set_min") == true)
                    {
                        // 1계열 중염소 최소 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPeri.getG_peri1_set_min());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_peri2_chg_limit_for_onetime") == true)
                    {
                        // 2계열 중염소 변경 한계치 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPeri.getG_peri2_chg_limit_for_onetime());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_peri2_set_max") == true)
                    {
                        // 2계열 중염소 최대 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPeri.getG_peri2_set_max());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_peri2_set_min") == true)
                    {
                        // 2계열 중염소 최소 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPeri.getG_peri2_set_min());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                }
            }
            catch(JsonProcessingException e)
            {
                log.error("JsonProcessingException Occurred in /disinfection/control/peri API");
            }

            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_disinfection_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 소독(후) 알고리즘 설정값 변경
    @RequestMapping(value = "/disinfection/control/post", method = RequestMethod.PUT)
    public ResponseEntity<String> putPostControlDisinfection(@RequestBody InterfaceDisinfectionPostDTO disinfectionPost)
    {
        log.info("putPostControlDisinfection, post:[{}]", disinfectionPost);

        boolean result = true;

        // update 후염소 최대 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_post_set_max", disinfectionPost.getG_post_set_max()) == 1) && result;

        // update 후염소 최소 주입률 설정
        result = (databaseService.modAiDisinfectionInit("g_post_set_min", disinfectionPost.getG_post_set_min()) == 1) && result;

        // update 후염소 변경 한계치
        result = (databaseService.modAiDisinfectionInit("g_post_chg_limit_for_onetime", disinfectionPost.getG_post_chg_limit_for_onetime()) == 1) && result;

        // update 정수지 목표 잔류염소
        result = (databaseService.modAiDisinfectionInit("h_target_cl", disinfectionPost.getH_target_cl()) == 1) && result;

        // 업데이트가 성공하면 Kafka를 통해 설정값 전달
        if(result == true)
        {
            // send control value to kafka ai_control
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = simpleDateFormat.format(new Date().getTime());

            LinkedHashMap<String, Object> controlMap;
            ObjectMapper objectMapper = new ObjectMapper();

            List<AiProcessInitDTO> aiDisinfectionInitList = databaseService.getAllAiDisinfectionInit();
            log.info("getAllAiDisinfectionInit, result:[{}]", aiDisinfectionInitList.size());

            try
            {
                for(AiProcessInitDTO dto : aiDisinfectionInitList)
                {
                    if(dto.getItem().equalsIgnoreCase("h_target_cl") == true)
                    {
                        // 정수지 목표 잔류염소 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPost.getH_target_cl());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_post_chg_limit_for_onetime") == true)
                    {
                        // 후염소 변경 한계치 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPost.getG_post_chg_limit_for_onetime());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_post_set_max") == true)
                    {
                        // 후염소 최대 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPost.getG_post_set_max());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                    else if(dto.getItem().equalsIgnoreCase("g_post_set_min") == true)
                    {
                        // 후염소 최소 주입률 설정 값을 설정하여 Kafka 전송
                        controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        controlMap.put("value", disinfectionPost.getG_post_set_min());
                        controlMap.put("time", strDate);

                        String strBody = objectMapper.writeValueAsString(controlMap);
                        //kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);
                    }
                }
            }
            catch(JsonProcessingException e)
            {
                log.error("JsonProcessingException Occurred in /disinfection/control/post API");
            }

            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_disinfection_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
