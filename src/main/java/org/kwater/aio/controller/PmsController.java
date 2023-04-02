package org.kwater.aio.controller;

import org.kwater.aio.dto.PmsAiDTO;
import org.kwater.aio.dto.PmsScadaDTO;
import org.kwater.aio.service.DatabaseServiceImpl;
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

import java.util.LinkedHashMap;
import java.util.List;

@RestController
//@EnableSwagger2
@Slf4j
public class PmsController
{
    @Autowired
    DatabaseServiceImpl databaseService;

    // 실시간 PMS 요약 항목 조회
    @RequestMapping(value = "/pms/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestPms()
    {
        log.info("Recv getLatestPms");

        // AI PMS 예측 값 조회
        List<PmsAiDTO> pmsAiList = databaseService.getLatestPmsAiValue();
        log.info("getLatestPmsAiValue, result:[{}]", pmsAiList.size());

        // PMS 실시간 측정값 조회
        List<PmsScadaDTO> pmsScadaList = databaseService.getLatestPmsScadaValue();
        log.info("getLatestPmsScadaValue, result:[{}]", pmsScadaList.size());

        // Make Response Body
        LinkedHashMap<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("ai", pmsAiList);
        responseBody.put("scada", pmsScadaList);

        String strBody;
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
}
