package org.kwater.aio.controller;

import org.kwater.aio.dto.AccessTokenDTO;
import org.kwater.aio.dto.ProcessCodeDTO;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.CommonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@EnableSwagger2
@Slf4j
public class ProcessCodeController
{
    @Autowired
    DatabaseServiceImpl databaseService;

    // 공정 코드 정보 요청 - 관리자 전용
    @RequestMapping(value = "/process/code", method = RequestMethod.GET)
    public ResponseEntity<String> getProcessCode(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        log.info("Recv getProcessCode");

        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            // 관리자 권한인지 확인
            nAuthority = accessTokenDTO.getAuthority();
            if(nAuthority == CommonValue.USER)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            else if(nAuthority == CommonValue.NONE_AUTHORITY)
            {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            else
            {
                bFindToken = true;
            }
        }

        if(bFindToken == true)
        {
            // 공정 코드 정보 조회
            List<ProcessCodeDTO> processCodeList = databaseService.getAllProcessCode();
            log.info("getAllProcessCode, result:[{}]", processCodeList.size());
            if(processCodeList.size() > 0)
            {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("code", processCodeList);

                ObjectMapper objectMapper = new ObjectMapper();
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
                String strErrorBody = "{\"reason\":\"Empty process_code\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }
}
