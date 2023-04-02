package org.kwater.aio.controller;

import org.kwater.aio.dto.AccessTokenDTO;
import org.kwater.aio.dto.SystemConfigDTO;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.CommonValue;
import org.kwater.aio.util.GlobalSystemConfig;
import org.kwater.aio.util.PropertiesAuthentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
//@EnableSwagger2
@Slf4j
public class ConfigController
{
//    @Autowired
//    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    GlobalSystemConfig globalSystemConfig;

    // 시스템 환경 설정 정보 요청 - 관리자 전용
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public ResponseEntity<String> getSystemConfig(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        log.info("Recv getSystemConfig");
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
                // Stop auto login time update
//                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
//                Date expirationDate = new Date();
//                expirationDate.setTime(expirationDate.getTime() + expiredTime);
//                databaseService.modToken(token, expirationDate);
            }
        }

        if(bFindToken == true)
        {
            SystemConfigDTO systemConfigDTO = databaseService.getSystemConfig();
            if(systemConfigDTO != null)
            {
                log.info("getSystemConfig, result 1");

                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("config", systemConfigDTO);

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
                log.error("getSystemConfig, result null");
                String strErrorBody = "{\"reason\":\"Empty system_config\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    // 시스템 환경 설정 정보 수정 - 관리자 전용
    @RequestMapping(value = "/config", method = RequestMethod.PUT)
    public ResponseEntity<String> putSystemConfig(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @RequestBody SystemConfigDTO systemConfig)
    {
        log.info("Recv putSystemConfig");
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
                // Stop auto login time update
//                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
//                Date expirationDate = new Date();
//                expirationDate.setTime(expirationDate.getTime() + expiredTime);
//                databaseService.modToken(token, expirationDate);
            }
        }

        if(bFindToken == true)
        {
            // update system_config
            int nResult = databaseService.modSystemConfig(systemConfig);
            log.info("modSystemConfig, result:[{}]", nResult);
            if(nResult > 0)
            {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("config", systemConfig);

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
                // update globalSystemConfig
                globalSystemConfig.setSystemConfig(systemConfig);
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty system_config\"}";
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
