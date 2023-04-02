package org.kwater.aio.controller;

import org.kwater.aio.ai_dto.AiProcessControlDTO;
import org.kwater.aio.ai_dto.AiProcessInitDTO;
import org.kwater.aio.dto.AlarmInfoDTO;
import org.kwater.aio.dto.AlarmNotifyDTO;
import org.kwater.aio.dto.InterfaceAlarmControlDTO;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.AlarmInfoList;
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
@Slf4j
public class AlarmController
{
    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    AlarmInfoList alarmInfoList;

    // 최근 알람 이력 조회
    @RequestMapping(value = "/alarm/notify", method = RequestMethod.GET)
    public ResponseEntity<String> getNotifyAlarm()
    {
        log.info("Recv getNotifyAlarm");

        // 최근 1분간의 알람을 조회하기 위한 Date 정의
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1);
        Date startTime = calendar.getTime();

        // 최근 1분 알람 조회
        List<AlarmNotifyDTO> alarmNotifyList = databaseService.getAlarmNotify(startTime);
        log.info("getAlarmNotify, result:[{}]", alarmNotifyList.size());
        if(alarmNotifyList.size() > 0)
        {
            // Make Response Body
            List<LinkedHashMap<String, Object>> resultArray = new ArrayList<>();

            for(AlarmNotifyDTO alarmNotify : alarmNotifyList)
            {
                // 최근 1분간 발보된 알람 중 타 시스템 발보 및 반자동 제어를 위한 알람 추출
                AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(alarmNotify.getAlarm_id());
                if(alarmInfo == null)
                {
                    continue;
                }
                else if(alarmInfo.getType() == CommonValue.ALARM_TYPE_OFF ||
                        alarmInfo.getType() == CommonValue.ALARM_TYPE_THRESHOLD)
                {
                    // update alarm_notify ack_state
//                    databaseService.modAlarmNotifyAckState(alarmNotify.getAlarm_notify_index(), true);
                }
                else if(alarmInfo.getType() == CommonValue.ALARM_TYPE_ANOTHER_SYSTEM)
                {
                    // update alarm_notify ack_state
//                    databaseService.modAlarmNotifyAckState(alarmNotify.getAlarm_notify_index(), true);

                    // add resultArray
                    LinkedHashMap<String, Object> singleBody = new LinkedHashMap<>();
                    singleBody.put("alarm_notify_index", alarmNotify.getAlarm_notify_index());
                    singleBody.put("alarm_id", alarmNotify.getAlarm_id());
                    singleBody.put("type", alarmInfo.getType());
                    singleBody.put("message", alarmInfo.getDisplay_name());
                    singleBody.put("url", alarmInfo.getUrl());
                    singleBody.put("alarm_time", alarmNotify.getAlarm_time());

                    resultArray.add(singleBody);
                }
                else if(alarmInfo.getType() == CommonValue.ALARM_TYPE_SEMI_AUTO)
                {
                    // update alarm_notify ack_state
//                    databaseService.modAlarmNotifyAckState(alarmNotify.getAlarm_notify_index(), true);

                    int nOperationMode = 0; // 반자동 제어 모드임을 확인하는 변수
                    if(alarmNotify.getAlarm_id() == CommonValue.ALARM_RECEIVING_AI_CONTROL)
                    {
                        // 착수 공정의 현재 운전모드를 조회하여 nOperationMode에 대입
                        AiProcessInitDTO aiReceivingInit = databaseService.getAiReceivingInit(CommonValue.B_OPERATION_MODE);
                        log.info("getAiReceivingInit, result:[{}]", aiReceivingInit != null ? 1 : 0);

                        if(aiReceivingInit == null)
                        {
                            continue;
                        }

                        // get operation mode
                        nOperationMode = aiReceivingInit.getValue().intValue();
                    }
                    else if(alarmNotify.getAlarm_id() == CommonValue.ALARM_COAGULANT_AI_CONTROL)
                    {
                        // 약품 공정의 현재 운전모드를 조회하여 nOperationMode에 대입
                        AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE);
                        log.info("getAiCoagulantInit, result:[{}]", aiCoagulantInit != null ? 1 : 0);

                        if(aiCoagulantInit == null)
                        {
                            continue;
                        }

                        // get operation mode
                        nOperationMode = aiCoagulantInit.getValue().intValue();
                    }
                    else if(alarmNotify.getAlarm_id() == CommonValue.ALARM_MIXING_AI_CONTROL)
                    {
                        // 혼화응집 공정의 현재 운전모드를 조회하여 nOperationMode에 대입
                        AiProcessInitDTO aiMixingInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE);
                        log.info("getAiMixingInit, result:[{}]", aiMixingInit != null ? 1 : 0);

                        if(aiMixingInit == null)
                        {
                            continue;
                        }

                        // get operation mode
                        nOperationMode = aiMixingInit.getValue().intValue();
                    }
                    else if(alarmNotify.getAlarm_id() == CommonValue.ALARM_SEDIMENTATION_AI_CONTROL)
                    {
                        // 침전 공정의 현재 운전모드를 조회하여 nOperationMode에 대입
                        AiProcessInitDTO aiSedimentationInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE);
                        log.info("getAiSedimentationInit, result:[{}]", aiSedimentationInit != null ? 1 : 0);

                        if(aiSedimentationInit == null)
                        {
                            continue;
                        }
                        nOperationMode = aiSedimentationInit.getValue().intValue();
                    }
                    else if(alarmNotify.getAlarm_id() == CommonValue.ALARM_FILTER_AI_CONTROL)
                    {
                        // 여과 공정의 현재 운전모드를 조회하여 nOperationMode에 대입
                        AiProcessInitDTO aiFilterInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE);
                        log.info("getAiFilterInit, result:[{}]", aiFilterInit != null ? 1 : 0);

                        if(aiFilterInit == null)
                        {
                            continue;
                        }

                        // get operation mode
                        nOperationMode = aiFilterInit.getValue().intValue();
                    }
                    else if(alarmNotify.getAlarm_id() == CommonValue.ALARM_GAC_AI_CONTROL)
                    {
                        // GAC 여과 공정의 현재 운전모드를 조회하여 nOperationMode에 대입
                        AiProcessInitDTO aiGacInit = databaseService.getAiGacInit(CommonValue.I_OPERATION_MODE);
                        log.info("getAiGacInit, result:[{}]", aiGacInit != null ? 1 : 0);

                        if(aiGacInit == null)
                        {
                            continue;
                        }

                        // get operation mode
                        nOperationMode = aiGacInit.getValue().intValue();
                    }
                    else if(alarmNotify.getAlarm_id() == CommonValue.ALARM_DISINFECTION_AI_CONTROL)
                    {
                        // 소독 공정(전/중/후)의 현재 운전모드를 조회하여 nOperationMode에 대입
                        List<Integer> operationList = new ArrayList<>();

                        // Get Pre Disinfection Operation Mode
                        AiProcessInitDTO aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE);
                        log.info("getAiDisinfectionInit pre, result:[{}]", aiDisinfectionInit != null ? 1 : 0);
                        operationList.add(aiDisinfectionInit != null ? aiDisinfectionInit.getValue().intValue() : 0);

                        // Get Peri Disinfection Operation Mode
                        aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PERI_OPERATION_MODE);
                        log.info("getAiDisinfectionInit peri, result:[{}]", aiDisinfectionInit != null ? 1 : 0);
                        operationList.add(aiDisinfectionInit != null ? aiDisinfectionInit.getValue().intValue() : 0);

                        // Get Post Disinfection Operation Mode
                        aiDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE);
                        log.info("getAiDisinfectionInit post, result:[{}]", aiDisinfectionInit != null ? 1 : 0);
                        operationList.add(aiDisinfectionInit != null ? aiDisinfectionInit.getValue().intValue() : 0);

                        for(Integer operationMode : operationList)
                        {
                            if(operationMode == CommonValue.OPERATION_MODE_SEMI_AUTO)
                            {
                                nOperationMode = CommonValue.OPERATION_MODE_SEMI_AUTO;
                                break;
                            }
                            else
                            {
                                nOperationMode = CommonValue.OPERATION_MODE_MANUAL;
                            }
                        }
                    }
                    else if(alarmNotify.getAlarm_id() == CommonValue.ALARM_OZONE_AI_CONTROL)
                    {
                        // 오존 공정의 현재 운전모드를 조회하여 nOperationMode에 대입
                        AiProcessInitDTO aiOzoneInit = databaseService.getAiOzoneInit(CommonValue.IO_OPERATION_MODE);
                        log.info("getAiOzoneInit, result:[{}]", aiOzoneInit != null ? 1 : 0);

                        if(aiOzoneInit == null)
                        {
                            continue;
                        }

                        // get operation mode
                        nOperationMode = aiOzoneInit.getValue().intValue();
                    }
                    else
                    {
                        continue;
                    }

                    // 현재 운전모드가 반자동 제어 모드가 아니라면 resultArray에 포함시키지 않음
                    if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO)
                    {
                        continue;
                    }

                    // add resultArray
                    LinkedHashMap<String, Object> singleBody = new LinkedHashMap<>();
                    singleBody.put("alarm_notify_index", alarmNotify.getAlarm_notify_index());
                    singleBody.put("alarm_id", alarmNotify.getAlarm_id());
                    singleBody.put("type", alarmInfo.getType());
                    singleBody.put("message", alarmInfo.getDisplay_name());
                    singleBody.put("url", alarmInfo.getUrl());
                    singleBody.put("alarm_time", alarmNotify.getAlarm_time());

                    resultArray.add(singleBody);
                }
            }

            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("alarm", resultArray);

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
            String strErrorBody = "{\"reason\":\"Empty alarm_notify\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 반자동 제어 팝업에서 제어버튼을 눌렀을 때 SCADA로 제어메시지를 전송
    @RequestMapping(value = "/alarm/control", method = RequestMethod.PUT)
    public ResponseEntity<String> putControlAlarm(@RequestBody InterfaceAlarmControlDTO alarmControl)
    {
        log.info("putControlAlarm, alarm_id:[{}]", alarmControl.getAlarm_id());

        // 등록된 알람ID인지 검사
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(alarmControl.getAlarm_id());
        log.info("getAlarmInfoFromAlarmId, result:[{}]", alarmInfo != null ? 1 : 0);
        if(alarmInfo == null)
        {
            log.error("Does not exist Alarm:[{}]", alarmControl.getAlarm_id());

            String strErrorBody = "{\"reason\":\"Invalid alarm_id\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        /////////////////////////////////////////////////
        // get Control
        /////////////////////////////////////////////////
        if(alarmControl.getAlarm_id() == CommonValue.ALARM_RECEIVING_AI_CONTROL)
        {
            // get Receiving Control
            AiProcessInitDTO aiReceivingInit = databaseService.getAiReceivingInit(CommonValue.B_OPERATION_MODE);
            log.info("getAiReceivingInit, result:[{}]", aiReceivingInit != null ? 1 : 0);

            if(aiReceivingInit == null)
            {
                String strErrorBody = "{\"reason\":\"Empty ai_receiving_init\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // 착수 공정의 현재 운전모드가 반자동 제어가 아니라면 SCADA로 제어명령을 전송하지 않음
            int nOperationMode = aiReceivingInit.getValue().intValue();
            if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO)
            {
                String strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get latest(1minute) control value(kafka_flag = 1)
            // 최근 착수 공정에 등록된 제어 명령을 조회
            AiProcessControlDTO queryDto = new AiProcessControlDTO();
            queryDto.setRun_time(alarmControl.getAlarm_time());
            queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
            List<AiProcessControlDTO> aiReceivingControlList = databaseService.getListAiReceivingControl(queryDto);

            if(aiReceivingControlList.size() > 0)
            {
                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try
                {
                    for(AiProcessControlDTO dto : aiReceivingControlList)
                    {
                        // send control value to kafka ai_control
                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        // Pulse / Value 값 구분하여 설정
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
//                        kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                        // update kafka_flag=3 (kafka_send)
                        AiProcessControlDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                        databaseService.modAiReceivingControlKafkaFlag(updateDto);
                    }

                    return new ResponseEntity<>("", HttpStatus.OK);
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Alarm Receiving Control Process");
                    String strErrorBody = "{\"reason\":\"JsonProcessing Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
                catch(NumberFormatException e)
                {
                    log.error("NumberException Occurred in Alarm Receiving Control Process");
                    String strErrorBody = "{\"reason\":\"NumberFormatException Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty receiving control\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else if(alarmControl.getAlarm_id() == CommonValue.ALARM_COAGULANT_AI_CONTROL)
        {
            // get Coagulant control
            AiProcessInitDTO aiCoagulantInit = databaseService.getAiCoagulantInit(CommonValue.C_OPERATION_MODE);
            log.info("getAiCoagulantInit, result:[{}]", aiCoagulantInit != null ? 1 : 0);

            if(aiCoagulantInit == null)
            {
                String strErrorBody = "{\"reason\":\"Empty ai_coagulant_init\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // 약품 공정의 현재 운전모드가 반자동 제어가 아니라면 SCADA로 제어명령을 전송하지 않음
            int nOperationMode = aiCoagulantInit.getValue().intValue();
            if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO)
            {
                String strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get latest(1minute) control value(kafka_flag = 1)
            // 최근 약품 공정에 등록된 제어 명령을 조회
            AiProcessControlDTO queryDto = new AiProcessControlDTO();
            queryDto.setRun_time(alarmControl.getAlarm_time());
            queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
            List<AiProcessControlDTO> aiCoagulantControlList = databaseService.getListAiCoagulantControl(queryDto);

            if(aiCoagulantControlList.size() > 0)
            {
                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try
                {
                    for(AiProcessControlDTO dto : aiCoagulantControlList)
                    {
                        // send control value to kafka ai_control
                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        // Pulse / Value 값 구분하여 설정
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
//                        kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                        // update kafka_flag=3 (kafka_send)
                        AiProcessControlDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                        databaseService.modAiCoagulantControlKafkaFlag(updateDto);
                    }

                    return new ResponseEntity<>("", HttpStatus.OK);
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Alarm Coagulant Control Process");
                    String strErrorBody = "{\"reason\":\"JsonProcessing Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
                catch(NumberFormatException e)
                {
                    log.error("NumberException Occurred in Alarm Coagulant Control Process");
                    String strErrorBody = "{\"reason\":\"NumberFormatException Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty coagulant control\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else if(alarmControl.getAlarm_id() == CommonValue.ALARM_MIXING_AI_CONTROL)
        {
            // get Mixing control
            AiProcessInitDTO aiMixingInit = databaseService.getAiMixingInit(CommonValue.D_OPERATION_MODE);
            log.info("getAiMixingInit, result:[{}]", aiMixingInit != null ? 1 : 0);

            if(aiMixingInit == null)
            {
                String strErrorBody = "{\"reason\":\"Empty ai_mixing_init\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // 혼화응집 공정의 현재 운전모드가 반자동 제어가 아니라면 SCADA로 제어명령을 전송하지 않음
            int nOperationMode = aiMixingInit.getValue().intValue();
            if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO)
            {
                String strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get latest(1minute) control value(kafka_flag = 1)
            // 최근 혼화응집 공정에 등록된 제어 명령을 조회
            AiProcessControlDTO queryDto = new AiProcessControlDTO();
            queryDto.setRun_time(alarmControl.getAlarm_time());
            queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
            List<AiProcessControlDTO> aiMixingControlList = databaseService.getListAiMixingControl(queryDto);
            if(aiMixingControlList.size() > 0)
            {
                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try
                {
                    for(AiProcessControlDTO dto : aiMixingControlList)
                    {
                        // send control value to kafka ai_control
                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        // Pulse / Value 값 구분하여 설정
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
//                        kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                        // update kafka_flag=3 (kafka_send)
                        AiProcessControlDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                        databaseService.modAiMixingControlKafkaFlag(updateDto);
                    }

                    return new ResponseEntity<>("", HttpStatus.OK);
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Alarm Mixing Control Process");
                    String strErrorBody = "{\"reason\":\"JsonProcessing Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
                catch(NumberFormatException e)
                {
                    log.error("NumberException Occurred in Alarm Mixing Control Process");
                    String strErrorBody = "{\"reason\":\"NumberFormatException Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty Mixing control\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else if(alarmControl.getAlarm_id() == CommonValue.ALARM_SEDIMENTATION_AI_CONTROL)
        {
            // get Sedimentation Control
            AiProcessInitDTO aiSedimentationInit = databaseService.getAiSedimentationInit(CommonValue.E_OPERATION_MODE);
            log.info("getAiSedimentationInit, result:[{}]", aiSedimentationInit != null ? 1 : 0);

            if(aiSedimentationInit == null)
            {
                String strErrorBody = "{\"reason\":\"Empty ai_sedimentation_init\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // 침전 공정의 현재 운전모드가 반자동 제어가 아니라면 SCADA로 제어명령을 전송하지 않음
            int nOperationMode = aiSedimentationInit.getValue().intValue();
            if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO)
            {
                String strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get latest(1minute) control value(kafka_flag = 1)
            // 최근 침전 공정에 등록된 제어 명령을 조회
            AiProcessControlDTO queryDto = new AiProcessControlDTO();
            queryDto.setRun_time(alarmControl.getAlarm_time());
            queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
            List<AiProcessControlDTO> aiSedimentationControlList = databaseService.getListAiSedimentationControl(queryDto);
            if(aiSedimentationControlList.size() > 0)
            {
                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try
                {
                    for(AiProcessControlDTO dto : aiSedimentationControlList)
                    {
                        // send control value to kafka ai_control
                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        // Pulse / Value 값 구분하여 설정
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
//                        kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                        // update kafka_flag=3 (kafka_send)
                        AiProcessControlDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                        databaseService.modAiSedimentationControlKafkaFlag(updateDto);
                    }

                    return new ResponseEntity<>("", HttpStatus.OK);
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Alarm Sedimentation Control Process");
                    String strErrorBody = "{\"reason\":\"JsonProcessing Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
                catch(NumberFormatException e)
                {
                    log.error("NumberException Occurred in Alarm Sedimentation Control Process");
                    String strErrorBody = "{\"reason\":\"NumberFormatException Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Invalid alarm_id\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else if(alarmControl.getAlarm_id() == CommonValue.ALARM_FILTER_AI_CONTROL)
        {
            // get Filter Control
            AiProcessInitDTO aiFilterInit = databaseService.getAiFilterInit(CommonValue.F_OPERATION_MODE);
            log.info("getAiFilterInit, result:[{}]", aiFilterInit != null ? 1 : 0);

            if(aiFilterInit == null)
            {
                String strErrorBody = "{\"reason\":\"Empty ai_filter_init\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // 여과 공정의 현재 운전모드가 반자동 제어가 아니라면 SCADA로 제어명령을 전송하지 않음
            int nOperationMode = aiFilterInit.getValue().intValue();
            if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO)
            {
                String strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get latest(1minute) control value(kafka_flag = 1)
            // 최근 여과 공정에 등록된 제어 명령을 조회
            AiProcessControlDTO queryDto = new AiProcessControlDTO();
            queryDto.setRun_time(alarmControl.getAlarm_time());
            queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
            List<AiProcessControlDTO> aiFilterControlList = databaseService.getListAiFilterControl(queryDto);

            if(aiFilterControlList.size() > 0)
            {
                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try
                {
                    for(AiProcessControlDTO dto : aiFilterControlList)
                    {
                        // send control value to kafka ai_control
                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        // Pulse / Value 값 구분하여 설정
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
//                        kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                        // update kafka_flag=3 (kafka_send)
                        AiProcessControlDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                        databaseService.modAiFilterControlKafkaFlag(updateDto);
                    }

                    return new ResponseEntity<>("", HttpStatus.OK);
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Alarm Filter Control Process");
                    String strErrorBody = "{\"reason\":\"JsonProcessing Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
                catch(NumberFormatException e)
                {
                    log.error("NumberException Occurred in Alarm Filter Process");
                    String strErrorBody = "{\"reason\":\"NumberFormatException Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty Filter control\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else if(alarmControl.getAlarm_id() == CommonValue.ALARM_GAC_AI_CONTROL)
        {
            // get GAC Control
            AiProcessInitDTO aiGacInit = databaseService.getAiGacInit(CommonValue.I_OPERATION_MODE);
            log.info("getAiGacInit, result:[{}]", aiGacInit != null ? 1 : 0);

            if(aiGacInit == null)
            {
                String strErrorBody = "{\"reason\":\"Empty ai_gac_init\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // GAC 여과 공정의 현재 운전모드가 반자동 제어가 아니라면 SCADA로 제어명령을 전송하지 않음
            int nOperationMode = aiGacInit.getValue().intValue();
            if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO)
            {
                String strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get latest(1minute) control value(kafka_flag = 1)
            // 최근 GAC 여과 공정에 등록된 제어 명령을 조회
            AiProcessControlDTO queryDto = new AiProcessControlDTO();
            queryDto.setRun_time(alarmControl.getAlarm_time());
            queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
            List<AiProcessControlDTO> aiGacControlList = databaseService.getListAiGacControl(queryDto);
            if(aiGacControlList.size() > 0)
            {
                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try
                {
                    for(AiProcessControlDTO dto : aiGacControlList)
                    {
                        // send control value to kafka ai_control
                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        // Pulse / Value 값 구분하여 설정
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
//                        kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                        // update kafka_flag=3 (kafka_send)
                        AiProcessControlDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                        databaseService.modAiGacControlKafkaFlag(updateDto);
                    }

                    return new ResponseEntity<>("", HttpStatus.OK);
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Alarm GAC Control Process");
                    String strErrorBody = "{\"reason\":\"JsonProcessing Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
                catch(NumberFormatException e)
                {
                    log.error("NumberException Occurred in Alarm GAC Process");
                    String strErrorBody = "{\"reason\":\"NumberFormatException Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty GAC control\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else if(alarmControl.getAlarm_id() == CommonValue.ALARM_DISINFECTION_AI_CONTROL)
        {
            // get Pre Disinfection Control
            AiProcessInitDTO aiPreDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PRE_OPERATION_MODE);
            log.info("getAiDisinfectionInit, result:[{}]", aiPreDisinfectionInit != null ? 1 : 0);

            if(aiPreDisinfectionInit == null)
            {
                String strErrorBody = "{\"reason\":\"Empty pre ai_disinfection_init\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // 소독(전) 공정의 현재 운전모드가 반자동 제어가 아니라면 SCADA로 제어명령을 전송하지 않음
            int nOperationMode = aiPreDisinfectionInit.getValue().intValue();
            if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO)
            {
                String strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get latest(1minute) control value(kafka_flag = 1)
            // 최근 소독(전) 공정에 등록된 제어 명령을 조회
            AiProcessControlDTO queryDto = new AiProcessControlDTO();
            queryDto.setRun_time(alarmControl.getAlarm_time());
            queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
            List<AiProcessControlDTO> aiPreDisinfectionControlList = databaseService.getListAiPreDisinfectionControl(queryDto);

            if(aiPreDisinfectionControlList.size() > 0)
            {
                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try
                {
                    for(AiProcessControlDTO dto : aiPreDisinfectionControlList)
                    {
                        // send control value to kafka ai_control
                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        // Pulse / Value 값 구분하여 설정
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
//                        kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                        // update kafka_flag=3 (kafka_send)
                        AiProcessControlDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                        databaseService.modAiPreDisinfectionControlKafkaFlag(updateDto);
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Alarm Pre Disinfection Control Process");
                    String strErrorBody = "{\"reason\":\"JsonProcessing Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
                catch(NumberFormatException e)
                {
                    log.error("NumberException Occurred in Alarm Pre Disinfection Control Process");
                    String strErrorBody = "{\"reason\":\"NumberFormatException Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty pre disinfection control\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get Peri Disinfection Control
            AiProcessInitDTO aiPeriDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_PERI_OPERATION_MODE);
            log.info("getAiDisinfectionInit, result:[{}]", aiPeriDisinfectionInit != null ? 1 : 0);

            if(aiPeriDisinfectionInit == null)
            {
                String strErrorBody = "{\"reason\":\"Empty peri ai_disinfection_init\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // 소독(중) 공정의 현재 운전모드가 반자동 제어가 아니라면 SCADA로 제어명령을 전송하지 않음
            nOperationMode = aiPeriDisinfectionInit.getValue().intValue();
            if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO)
            {
                String strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get latest(1minute) control value(kafka_flag = 1)
            // 최근 소독(중) 공정에 등록된 제어 명령을 조회
            queryDto = new AiProcessControlDTO();
            queryDto.setRun_time(alarmControl.getAlarm_time());
            queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
            List<AiProcessControlDTO> aiPeriDisinfectionControlList = databaseService.getListAiPeriDisinfectionControl(queryDto);

            if(aiPeriDisinfectionControlList.size() > 0)
            {
                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try
                {
                    for(AiProcessControlDTO dto : aiPeriDisinfectionControlList)
                    {
                        // send control value to kafka ai_control
                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        // Pulse / Value 값 구분하여 설정
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
//                        kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                        // update kafka_flag=3 (kafka_send)
                        AiProcessControlDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                        databaseService.modAiPeriDisinfectionControlKafkaFlag(updateDto);
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Alarm Peri Disinfection Control Process");
                    String strErrorBody = "{\"reason\":\"JsonProcessing Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
                catch(NumberFormatException e)
                {
                    log.error("NumberException Occurred in Alarm Peri Disinfection Control Process");
                    String strErrorBody = "{\"reason\":\"NumberFormatException Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty peri disinfection control\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get Post Disinfection Control
            AiProcessInitDTO aiPostDisinfectionInit = databaseService.getAiDisinfectionInit(CommonValue.G_POST_OPERATION_MODE);
            log.info("getAiDisinfectionInit, result:[{}]", aiPostDisinfectionInit != null ? 1 : 0);

            if(aiPostDisinfectionInit == null)
            {
                String strErrorBody = "{\"reason\":\"Empty post ai_disinfection_init\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // 소독(후) 공정의 현재 운전모드가 반자동 제어가 아니라면 SCADA로 제어명령을 전송하지 않음
            nOperationMode = aiPostDisinfectionInit.getValue().intValue();
            if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO)
            {
                String strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get latest(1minute) control value(kafka_flag = 1)
            // 최근 소독(후) 공정에 등록된 제어 명령을 조회
            queryDto = new AiProcessControlDTO();
            queryDto.setRun_time(alarmControl.getAlarm_time());
            queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
            List<AiProcessControlDTO> aiPostDisinfectionControlList = databaseService.getListAiPostDisinfectionControl(queryDto);

            if(aiPostDisinfectionControlList.size() > 0)
            {
                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try
                {
                    for(AiProcessControlDTO dto : aiPostDisinfectionControlList)
                    {
                        // send control value to kafka ai_control
                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        // Pulse / Value 값 구분하여 설정
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
//                        kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                        // update kafka_flag=3 (kafka_send)
                        AiProcessControlDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                        databaseService.modAiPostDisinfectionControlKafkaFlag(updateDto);
                    }
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Alarm Post Disinfection Control Process");
                    String strErrorBody = "{\"reason\":\"JsonProcessing Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
                catch(NumberFormatException e)
                {
                    log.error("NumberException Occurred in Alarm Post Disinfection Control Process");
                    String strErrorBody = "{\"reason\":\"NumberFormatException Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty disinfection control\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>("", HttpStatus.OK);
        }
        else if(alarmControl.getAlarm_id() == CommonValue.ALARM_OZONE_AI_CONTROL)
        {
            // get Ozone control
            AiProcessInitDTO aiOzoneInit = databaseService.getAiOzoneInit(CommonValue.IO_OPERATION_MODE);
            log.info("getAiOzoneInit, result:[{}]", aiOzoneInit != null ? 1 : 0);

            if(aiOzoneInit == null)
            {
                String strErrorBody = "{\"reason\":\"Empty ai_ozone_init\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // 오존 공정의 현재 운전모드가 반자동 제어가 아니라면 SCADA로 제어명령을 전송하지 않음
            int nOperationMode = aiOzoneInit.getValue().intValue();
            if(nOperationMode != CommonValue.OPERATION_MODE_SEMI_AUTO)
            {
                String strErrorBody = "{\"reason\":\"Invalid ai operation state\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }

            // get latest(1minute) control value(kafka_flag = 1)
            // 최근 오존 공정에 등록된 제어 명령을 조회
            AiProcessControlDTO queryDto = new AiProcessControlDTO();
            queryDto.setRun_time(alarmControl.getAlarm_time());
            queryDto.setKafka_flag(CommonValue.KAFKA_FLAG_POPUP);
            List<AiProcessControlDTO> aiOzoneControlList = databaseService.getListAiOzoneControl(queryDto);

            if(aiOzoneControlList.size() > 0)
            {
                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try
                {
                    for(AiProcessControlDTO dto : aiOzoneControlList)
                    {
                        // send control value to kafka ai_control
                        LinkedHashMap<String, Object> controlMap = new LinkedHashMap<>();
                        controlMap.put("tag", dto.getName());
                        // Pulse / Value 값 구분하여 설정
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
//                        kafkaProducer.sendMessage(CommonValue.KAFKA_TOPIC_CONTROL, strBody);

                        // update kafka_flag=3 (kafka_send)
                        AiProcessControlDTO updateDto = dto;
                        updateDto.setKafka_flag(CommonValue.KAFKA_FLAG_SEND);
                        databaseService.modAiOzoneControlKafkaFlag(updateDto);
                    }

                    return new ResponseEntity<>("", HttpStatus.OK);
                }
                catch(JsonProcessingException e)
                {
                    log.error("JsonProcessingException Occurred in Alarm Ozone Control Process");
                    String strErrorBody = "{\"reason\":\"JsonProcessing Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
                catch(NumberFormatException e)
                {
                    log.error("NumberException Occurred in Alarm Ozone Control Process");
                    String strErrorBody = "{\"reason\":\"NumberFormatException Exception\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty ozone control\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"Invalid alarm_id\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 등록된 알람 정보 조회
    @RequestMapping(value = "/alarm/info", method = RequestMethod.GET)
    public ResponseEntity<String> getAlarmInfo()
    {
        log.info("getAlarmInfo");

        // 전체 알람 정보 조회
        List<AlarmInfoDTO> alarmInfoDTOList = databaseService.getAlarmInfo();
        log.info("getAlarmInfo, result:[{}]", alarmInfoDTOList.size());
        if(alarmInfoDTOList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("alarm_info", alarmInfoDTOList);

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
            String strErrorBody = "{\"reason\":\"Empty alarm_info\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
