package org.kwater.aio.controller;

import org.kwater.aio.ai_dto.*;
import org.kwater.aio.dto.*;
//import org.kwater.aio.kafka.//kafkaProducer.;
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
public class CoagulantController
{
    @Autowired
    DatabaseServiceImpl databaseService;

    // 약품 공정 최근 데이터 조회
    @RequestMapping(value = "/coagulant/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestCoagulant()
    {
        log.info("Recv getLatestCoagulant");

        // 실시간 데이터 태이블에서 최근 값을 조회하기 위해 오늘 날짜의 PartitionName 설정
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat partitionNameFormat = new SimpleDateFormat("yyyyMMdd");
        String strPartitionName = partitionNameFormat.format(calendarToday.getTime());

        // get ai_coagulant_init(c_operation_mode)
        AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE);
        log.info("getAiCoagulantInit, result:[{}]", aiCoagulantInit != null ? 1 : 0);

        // get ai_coagulant_init
        List<AiProcessInitDTO> aiCoagulantInitList = databaseService.getAllAiCoagulantInit();
        log.info("getAllAiCoagulantInit, result:[{}]", aiCoagulantInitList.size());

        // get ai_coagulant_realtime
        AiCoagulantRealtimeDTO aiCoagulantRealtime = databaseService.getLatestAiCoagulantRealtimeValue();
        log.info("getLatestAiCoagulantRealtimeValue, result:[{}]", aiCoagulantRealtime != null ? 1 : 0);

        // get coagulant_realtime
        List<ProcessRealtimeDTO> coagulantRealtime = databaseService.getLatestCoagulantRealtimeValue(strPartitionName);
        log.info("getLatestCoagulantRealtimeValue, result:[{}]", coagulantRealtime.size());

        // get tag_manage(coagulant)
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_COAGULANT);
        log.info("getTagManageFromCode:[{}], result:[{}]", CommonValue.PROCESS_COAGULANT, tagManageList.size());

        if(aiCoagulantRealtime != null)
        {
            // JSON 처리를 위한 ObjectMapper 선언
            ObjectMapper objectMapper = new ObjectMapper();

            // Make Response Body
            LinkedHashMap<String, Object> aiCoagulantInfo = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp;

            // update_time
            aiCoagulantInfo.put("update_time", aiCoagulantRealtime.getUpdate_time());

            try
            {
                // operation_mode
                if(aiCoagulantInit != null)
                {
                    aiCoagulantInfo.put("operation_mode", aiCoagulantInit.getValue().intValue());
                }
                else
                {
                    aiCoagulantInfo.put("operation_mode", aiCoagulantRealtime.getC_operation_mode());
                }

                for(AiProcessInitDTO dto : aiCoagulantInitList)
                {
                    if(dto.getItem().equalsIgnoreCase("c_cf_apac_max") == true)
                    {
                        // APAC 주입률 상한
                        aiCoagulantInfo.put(dto.getItem(), dto.getValue());
                    }
                    else if(dto.getItem().equalsIgnoreCase("c_cf_polymax_max") == true)
                    {
                        // POLYMAX 주입률 상한
                        aiCoagulantInfo.put(dto.getItem(), dto.getValue());
                    }
                    else if(dto.getItem().equalsIgnoreCase("c_cf_apac_min") == true)
                    {
                        // APAC 주입률 하한
                        aiCoagulantInfo.put(dto.getItem(), dto.getValue());
                    }
                    else if(dto.getItem().equalsIgnoreCase("c_cf_polymax_min") == true)
                    {
                        // POLYMAX 주입률 하한
                        aiCoagulantInfo.put(dto.getItem(), dto.getValue());
                    }
                }

                // Realtime data from SCADA
                Float d1_mm_fr_apac = 0.0f, c1_cf_apac = 0.0f;
                Float d2_mm_fr_apac = 0.0f, c2_cf_apac = 0.0f;
                Float d1_mm_fr_polymax = 0.0f, c1_cf_polymax = 0.0f;
                Float d2_mm_fr_polymax = 0.0f, c2_cf_polymax = 0.0f;
                // tag_manage에 정의한 태그명을 통해 실시간 데이터를 가져와 aiCoagulantInfo에 등록
                for(TagManageDTO tagManage: tagManageList)
                {
                    for(ProcessRealtimeDTO dto : coagulantRealtime)
                    {
                        if(tagManage.getItem().equalsIgnoreCase("b_tb") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 원수 탁도
                            aiCoagulantInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("b_ph") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 원수 pH
                            aiCoagulantInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("b_te") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 원수 수온
                            aiCoagulantInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("b_cu") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 원수 전기전도도
                            aiCoagulantInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("b_in_fr") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 원수 유입 유량
                            aiCoagulantInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("d1_mm_fr_apac") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 1계열 APAC 혼화기 유량
                            d1_mm_fr_apac = Float.parseFloat(dto.getValue());
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("d2_mm_fr_apac") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 2계열 APAC 혼화기 유량
                            d2_mm_fr_apac = Float.parseFloat(dto.getValue());
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("d1_mm_fr_polymax") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 1계열 polymax 혼화기 유량
                            d1_mm_fr_polymax = Float.parseFloat(dto.getValue());
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("d2_mm_fr_polymax") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 2계열 polymax 혼화기 유량
                            d2_mm_fr_polymax = Float.parseFloat(dto.getValue());
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("d1_in_fr") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 1계열 혼화지 유입 유량
                            aiCoagulantInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("d2_in_fr") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 2계열 혼화지 유입 유량
                            aiCoagulantInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("e1_tb_b") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 1계열 침전지 후탁도
                            aiCoagulantInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("e2_tb_b") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 2계열 침전지 후탁도
                            aiCoagulantInfo.put(tagManage.getItem(), Float.parseFloat(dto.getValue()));
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("c1_cf_apac") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 1계열 APAC 약품 주입률
                            c1_cf_apac = Float.parseFloat(dto.getValue());
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("c2_cf_apac") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 2계열 APAC 약품 주입률
                            c2_cf_apac = Float.parseFloat(dto.getValue());
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("c1_cf_polymax") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 1계열 Polymax 약품 주입률
                            c1_cf_polymax = Float.parseFloat(dto.getValue());
                            break;
                        }
                        else if(tagManage.getItem().equalsIgnoreCase("c2_cf_polymax") == true &&
                                tagManage.getName().equalsIgnoreCase(dto.getName()) == true)
                        {
                            // 2계열 Polymax 약품 주입률
                            c2_cf_polymax = Float.parseFloat(dto.getValue());
                            break;
                        }
                    }
                }

                // 계열별 약품 종류
                aiCoagulantInfo.put("c1_cf_coagulant", d1_mm_fr_apac > d1_mm_fr_polymax ? "APAC" : "POLYMAX");
                aiCoagulantInfo.put("c2_cf_coagulant", d2_mm_fr_apac > d2_mm_fr_polymax ? "APAC" : "POLYMAX");

                // 계열별 혼화기 유량
                aiCoagulantInfo.put("c1_mm_fr", d1_mm_fr_apac > d1_mm_fr_polymax ? d1_mm_fr_apac : d1_mm_fr_polymax);
                aiCoagulantInfo.put("c2_mm_fr", d2_mm_fr_apac > d2_mm_fr_polymax ? d2_mm_fr_apac : d2_mm_fr_polymax);

                // 계열별 약품 주입률
                aiCoagulantInfo.put("c1_cf", d1_mm_fr_apac > d1_mm_fr_polymax ? c1_cf_apac : c1_cf_polymax);
                aiCoagulantInfo.put("c2_cf", d2_mm_fr_apac > d2_mm_fr_polymax ? c2_cf_apac : c2_cf_polymax);

                // ai_cluster_id
                aiCoagulantInfo.put("ai_cluster_id", aiCoagulantRealtime.getAi_cluster_id());

                // 계열별 AI 약품 종류 예측
                mapTemp = objectMapper.readValue(aiCoagulantRealtime.getAi_c_coagulant(), LinkedHashMap.class);
                ArrayList<String> keyList = new ArrayList<>(mapTemp.keySet());
                Object objectTemp = mapTemp.get(keyList.get(0));
                JsonCSeriesString ai_c_coagulant = objectMapper.convertValue(objectTemp, JsonCSeriesString.class);
                aiCoagulantInfo.put("ai_c1_cf_coagulant", ai_c_coagulant.getSeries1().toUpperCase());
                aiCoagulantInfo.put("ai_c2_cf_coagulant", ai_c_coagulant.getSeries2().toUpperCase());

                // 계열별 AI 주입률 예측 최종값
                mapTemp = objectMapper.readValue(aiCoagulantRealtime.getAi_c_final(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                JsonCSeriesFloat ai_c_final = objectMapper.convertValue(objectTemp, JsonCSeriesFloat.class);
                aiCoagulantInfo.put("ai_c1_cf_final", ai_c_final.getSeries1());
                aiCoagulantInfo.put("ai_c2_cf_final", ai_c_final.getSeries2());

                // 계열별 AI 주입률 예측
                mapTemp = objectMapper.readValue(aiCoagulantRealtime.getAi_c_result(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                JsonCSeriesFloat ai_c_result = objectMapper.convertValue(objectTemp, JsonCSeriesFloat.class);
                aiCoagulantInfo.put("ai_c1_cf_result", ai_c_result.getSeries1());
                aiCoagulantInfo.put("ai_c2_cf_result", ai_c_result.getSeries2());

                // 계열별 AI 주입률 보정값
                mapTemp = objectMapper.readValue(aiCoagulantRealtime.getAi_c_corrected(), LinkedHashMap.class);
                keyList = new ArrayList<>(mapTemp.keySet());
                objectTemp = mapTemp.get(keyList.get(0));
                JsonCSeriesFloat ai_c_corrected = objectMapper.convertValue(objectTemp, JsonCSeriesFloat.class);
                aiCoagulantInfo.put("ai_c1_cf_corrected", ai_c_corrected.getSeries1());
                aiCoagulantInfo.put("ai_c2_cf_corrected", ai_c_corrected.getSeries2());

            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", aiCoagulantInfo);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
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
            String strErrorBody = "{\"reason\":\"Empty ai_coagulant\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 약품 공정 제어모드 변경
    @RequestMapping(value = "/coagulant/control/operation", method = RequestMethod.PUT)
    public ResponseEntity<String> putOperationControlCoagulant(@RequestBody InterfaceOperationModeDTO operationMode)
    {
        log.info("putOperationControlCoagulant, mode:[{}]", operationMode.getOperation());

        // 잘못된 제어모드 값 검사
        int nOperationMode = operationMode.getOperation();
        if(nOperationMode < CommonValue.OPERATION_MODE_MANUAL || nOperationMode > CommonValue.OPERATION_MODE_FULL_AUTO)
        {
            log.error("Invalid operation mode:[{}]", nOperationMode);

            String strErrorBody = "{\"reason\":\"Invalid operation mode\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Update ai_coagulant_init's operation_mode
//        log.info("update aiCoagulantOperationMode:[{}], mode:[{}]",
//                databaseService.modAiCoagulantOperationMode(nOperationMode), nOperationMode);

        // send control value to kafka ai_control(c_operation_mode)
        AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE);
        log.info("getAiCoagulantInit, result:[{}]", aiCoagulantInit != null ? 1 : 0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = simpleDateFormat.format(new Date().getTime());

        try
        {
            // Kafka에 전송할 값 정의
            LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
            controlMap.put("tag", aiCoagulantInit.getName());
            controlMap.put("value", nOperationMode);
            controlMap.put("time", strDate);

            // ObjectMapper를 통해 JSON 값을 String으로 변환하여 kafka 전송
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = objectMapper.writeValueAsString(controlMap);
            //kafkaProducer..sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

            // Kafka에 약품 공정 제어모드 변경 알람 전송
            List<TagManageDTO> tagManageList = databaseService.getTagManageFromType(CommonValue.TAG_MANAGE_TYPE_UI);
            for(TagManageDTO dto : tagManageList)
            {
                if(dto.getItem().equalsIgnoreCase("c_operation_mode_a") == true)
                {
                    // Kafka에 전송할 값 정의
                    controlMap = new LinkedHashMap<>();
                    controlMap.put("tag", dto.getName());
                    controlMap.put("value", nOperationMode == CommonValue.OPERATION_MODE_MANUAL ? false : true);
                    controlMap.put("time", strDate);

                    // ObjectMapper를 통해 JSON 값을 String으로 변환하여 kafka 전송
                    objectMapper = new ObjectMapper();
                    strBody = objectMapper.writeValueAsString(controlMap);
                    //kafkaProducer..sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                    break;
                }
            }
        }
        catch(JsonProcessingException e)
        {
            log.error("JsonProcessingException Occurred in /coagulant/control/operation API");
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/coagulant/control/ai", method = RequestMethod.PUT)
    public ResponseEntity<String> putAiControlCoagulant(@RequestBody InterfaceCoagulantAiDTO coagulantAi)
    {
        log.info("putAiControlCoagulant, ai:[{}]", coagulantAi);

        boolean result = true;

        // update APAC 주입률 상한
        result = (databaseService.modAiCoagulantInit("c_cf_apac_max", coagulantAi.getC_cf_apac_max()) == 1) && result;

        // update POLYMAX 주입률 상한
        result = (databaseService.modAiCoagulantInit("c_cf_polymax_max", coagulantAi.getC_cf_polymax_max()) == 1) && result;

        // update APAC 주입률 하한
        result = (databaseService.modAiCoagulantInit("c_cf_apac_min", coagulantAi.getC_cf_apac_min()) == 1) && result;

        // update POLYMAX 주입률 하한
        result = (databaseService.modAiCoagulantInit("c_cf_polymax_min", coagulantAi.getC_cf_polymax_min()) == 1) && result;

        if(result == true)
        {
            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"ai_coagulant_init update_fail\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 테스트 코드
    @RequestMapping(value = "/coagulant/history/tb", method = RequestMethod.PUT)
    public ResponseEntity<String> getTbHistoryCoagulant(@RequestBody InterfaceDateSearchDTO dateSearch)
    {
        log.info("getTbHistoryCoagulant, start:[{}], end:[{}]", dateSearch.getStart_time(), dateSearch.getEnd_time());

        List<AiCoagulantRealtimeDTO> aiCoagulantRealtimeList =
                databaseService.getAiCoagulantRealtimeValueFromUpdateTime(dateSearch);
        log.info("getAiCoagulantRealtimeValueFromUpdateTime, result:[{}]", aiCoagulantRealtimeList.size());
        if(aiCoagulantRealtimeList.size() > 0)
        {
            // Make Response Body
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LinkedHashMap<String, Object> series1 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> series2 = new LinkedHashMap<>();
            LinkedHashMap<String, Object> mapTemp;
            ObjectMapper objectMapper = new ObjectMapper();

            try
            {
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

                String strBody = objectMapper.writeValueAsString(responseBody);
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            }
            catch(JsonProcessingException e)
            {
                log.error("JsonProcessingException Occurred in /coagulant/history/tb API");
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

    @RequestMapping(value = "/coagulant/simulation", method = RequestMethod.POST)
    public ResponseEntity<String> postSimulationCoagulant(@RequestBody InterfaceAiCoagulantSimulationDTO simulation)
    {
        log.info("Recv postSimulationCoagulant, [{}]", simulation);

        // Make simulation data
        AiCoagulantSimulationDTO aiCoagulantSimulation = new AiCoagulantSimulationDTO();
        aiCoagulantSimulation.setReg_time(new Date());
        aiCoagulantSimulation.setState(CommonValue.STATE_STANDBY);
        aiCoagulantSimulation.setB_tb(simulation.getB_tb());
        aiCoagulantSimulation.setB_ph(simulation.getB_ph());
        aiCoagulantSimulation.setB_te(simulation.getB_te());
        aiCoagulantSimulation.setB_cu(simulation.getB_cu());
        aiCoagulantSimulation.setB_in_fr(simulation.getB_in_fr());
        aiCoagulantSimulation.setE1_tb(simulation.getE1_tb());
        aiCoagulantSimulation.setE2_tb(simulation.getE2_tb());

        // Insert simulation data
        int nResult = databaseService.addAiCoagulantSimulation(aiCoagulantSimulation);
        log.info("addCoagulantSimulation, result:[{}]", nResult);
        if(nResult > 0)
        {
            return new ResponseEntity<>("", HttpStatus.CREATED);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.CONFLICT.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/coagulant/simulation", method = RequestMethod.PUT)
    public ResponseEntity<String> getSimulationCoagulant(@RequestBody InterfaceDateSearchDTO dateSearch)
    {
        log.info("getSimulationCoagulant, start:[{}], end:[{}]", dateSearch.getStart_time(), dateSearch.getEnd_time());

        // Get simulation data
        List<AiCoagulantSimulationDTO> aiCoagulantSimulationList = databaseService.getAiCoagulantSimulation(dateSearch);
        log.info("getAiCoagulantSimulation, result:[{}]", aiCoagulantSimulationList.size());

        if(aiCoagulantSimulationList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("simulation", aiCoagulantSimulationList);

            // ObjectMapper를 통해 JSON 값을 String으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String strBody = "";
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
            String strErrorBody = "{\"reason\":\"Empty ai_coagulant_simulation\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
