package org.kwater.aio.controller;

import org.kwater.aio.dto.ProcessRealtimeDTO;
import org.kwater.aio.dto.TagManageDTO;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.CommonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
//@EnableSwagger2
@Slf4j
public class EmsController
{
    @Autowired
    DatabaseServiceImpl databaseService;

    // 실시간 EMS 요약 항목 조회
    @RequestMapping(value = "/ems/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestEms()
    {
        log.info("Recv getLatestEms");

        // tag_manage에서 EMS로 등록된 태그 조회
        List<TagManageDTO> tagManageList = databaseService.getTagManageFromCode(CommonValue.PROCESS_EMS);
        log.info("getTagManageFromCode[{}], result:[{}]", CommonValue.PROCESS_EMS, tagManageList.size());
        if(tagManageList.size() == 0)
        {
            String strErrorBody = "{\"reason\":\"Empty tag_manage\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // 실시간 EMS 데이터 태이블 최근 값 조회
        List<ProcessRealtimeDTO> emsRealtimeList = databaseService.getLatestEmsRealtimeValue();
        log.info("getLatestEmsRealtimeValue, result:[{}]", emsRealtimeList.size());
        if(emsRealtimeList.size() == 0)
        {
            String strErrorBody = "{\"reason\":\"Empty ems_realtime\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Make Response Body
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedHashMap<String, Object> latestMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> pumpMap = new LinkedHashMap<>();

        LinkedHashMap<String, Object> peakMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> zPowerMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> aiZPowerMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> aiZPowerPeakMap = new LinkedHashMap<>();

        LinkedHashMap<String, Object> drMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> zCblMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> aiZDrPowerMap = new LinkedHashMap<>();


        for(TagManageDTO tagManage : tagManageList)
        {
            for(ProcessRealtimeDTO emsRealtime : emsRealtimeList)
            {
                if(tagManage.getItem().equalsIgnoreCase("h1_operation_mode") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // update_time
                    latestMap.put("update_time", emsRealtime.getUpdate_time());

                    // 정속펌프 운전모드
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("h2_operation_mode") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 변속 펌프 운전모드
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("h1_pm1") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 평택 1번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("h1_pm2") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 평택 2번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("h1_pm3") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 평택 3번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("h1_pm4") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 평택 4번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("h2_pm1") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 송산 1번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("h2_pm2") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 송산 2번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("h2_pm_sp1") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 송산 1번 펌프 주파수
                    pumpMap.put(tagManage.getItem(), Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("h2_pm_sp2") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 송산 2번 펌프 주파수
                    pumpMap.put(tagManage.getItem(), Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("ai_h1_pm1") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // AI 평택 1번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("ai_h1_pm2") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // AI 평택 2번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("ai_h1_pm3") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // AI 평택 3번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("ai_h1_pm4") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // AI 평택 4번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("ai_h2_pm1") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // AI 송산 1번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("ai_h2_pm2") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // AI 송산 2번 펌프 ON
                    pumpMap.put(tagManage.getItem(), (int)Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("ai_h2_pm_sp1") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // AI 송산 1번 펌프 주파수
                    pumpMap.put(tagManage.getItem(), Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().equalsIgnoreCase("ai_h2_pm_sp2") == true &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // AI 송산 2번 펌프 주파수
                    pumpMap.put(tagManage.getItem(), Float.parseFloat(emsRealtime.getValue()));
                }
                else if(tagManage.getItem().indexOf("z_power") == 0 &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 시간별 발생전력
                    zPowerMap.put(
                            tagManage.getItem().replaceAll("z_power", ""),
                            Float.parseFloat(emsRealtime.getValue())
                    );
                }
                else if(tagManage.getItem().indexOf("ai_z_power") == 0 &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    if(tagManage.getItem().indexOf("ai_z_power_peak") == 0 &&
                            tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                    {
                        // 시간별 AI 전력피크예상
                        aiZPowerPeakMap.put(
                                tagManage.getItem().replaceAll("ai_z_power_peak", ""),
                                Float.parseFloat(emsRealtime.getValue())
                        );
                    }
                    else
                    {
                        // 시간별 AI 예상전력
                        aiZPowerMap.put(
                                tagManage.getItem().replaceAll("ai_z_power", ""),
                                Float.parseFloat(emsRealtime.getValue())
                        );
                    }
                }
                else if(tagManage.getItem().indexOf("z_cbl") == 0 &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 시간별 DR CBL
                    zCblMap.put(
                            tagManage.getItem().replaceAll("z_cbl", ""),
                            Float.parseFloat(emsRealtime.getValue())
                    );
                }
                else if(tagManage.getItem().indexOf("ai_z_dr_power") == 0 &&
                        tagManage.getName().equalsIgnoreCase(emsRealtime.getName()) == true)
                {
                    // 시간별 AI DR 예상전력
                    aiZDrPowerMap.put(
                            tagManage.getItem().replaceAll("ai_z_dr_power", ""),
                            Float.parseFloat(emsRealtime.getValue())
                    );
                }
            }
        }
        // 시간별 발생전력, AI 예상전력, AI 전력피크예상 값을 peakMap에 등록
        peakMap.put("z_power", zPowerMap);
        peakMap.put("ai_z_power", aiZPowerMap);
        peakMap.put("ai_z_power_peak", aiZPowerPeakMap);

        // 시간별 DR CBL, AI DR 예상전력 값을 drMap에 등록
        drMap.put("z_cbl", zCblMap);
        drMap.put("ai_z_dr_power", aiZDrPowerMap);

        // 펌프 상태, peak, dr 값을 latestMap에 등록
        latestMap.put("pump", pumpMap);
        latestMap.put("peak", peakMap);
        latestMap.put("dr", drMap);

        try
        {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("latest", latestMap);

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
}
