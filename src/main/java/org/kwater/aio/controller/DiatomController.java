package org.kwater.aio.controller;

import org.kwater.aio.dto.AccessTokenDTO;
import org.kwater.aio.dto.DiatomDTO;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@EnableSwagger2
@Slf4j
public class DiatomController
{
//    @Autowired
//    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    GlobalSystemConfig globalSystemConfig;

    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectionRequestTimeout(3 * CommonValue.ONE_SECOND)
            .build();

    @RequestMapping(value = "/diatom", method = RequestMethod.POST)
    public ResponseEntity<String> postDiatom(@RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody DiatomDTO diatom)
    {
        log.info("Recv postDiatom:[{}]", diatom.getUpdate_time());
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
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
            // Insert diatom
            diatom.setUpdate_time(new Date());
            int nResult = databaseService.addDiatom(diatom);
            log.info("addDiatom:[{}], result:[{}]", diatom.getMeasure_time(), nResult);
            if(nResult > 0)
            {
                // Make Scada Request Body
                String strBody;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = simpleDateFormat.format(diatom.getMeasure_time());

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("measure_time", strDate);
                requestBody.put("pd1_diatom", diatom.getPd1_diatom());
                requestBody.put("pd2_diatom", diatom.getPd2_diatom());
                requestBody.put("pd3_diatom", diatom.getPd3_diatom());
                requestBody.put("pd1_diatom_big", diatom.getPd1_diatom_big());
                requestBody.put("pd2_diatom_big", diatom.getPd2_diatom_big());
                requestBody.put("pd3_diatom_big", diatom.getPd3_diatom_big());
                requestBody.put("pd_diatom_avg", diatom.getPd_diatom_avg());

                try
                {
                    strBody = new ObjectMapper().writeValueAsString(requestBody);
                }
                catch(JsonProcessingException e)
                {
                    strBody = "";
                    log.error("Json processing Exception occurred");
                }

                // Send [POST] diatom SCADA Server1
                String strUri = "http://" + globalSystemConfig.getScada1_information() + "/diatom";
                HttpPost httpPost = new HttpPost(strUri);
                httpPost.setConfig(requestConfig);
                StringEntity stringEntity = new StringEntity(strBody, "UTF-8");
                httpPost.setEntity(stringEntity);
                httpPost.setHeader("Content-Type", "application/json");
                HttpResponse response1 = HttpSend.send(httpPost);

                // Send [POST] diatom SCADA Server2
                strUri = "http://" + globalSystemConfig.getScada2_information() + "/diatom";
                httpPost = new HttpPost(strUri);
                httpPost.setConfig(requestConfig);
                stringEntity = new StringEntity(strBody, "UTF-8");
                httpPost.setEntity(stringEntity);
                httpPost.setHeader("Content-Type", "application/json");
                HttpResponse response2 = HttpSend.send(httpPost);

                if(response1 != null || response2 != null)
                {
                    log.info("Send diatom information to SCADA Server1:[{}]", response1.getStatusLine().getStatusCode());
                    log.info("Send diatom information to SCADA Server2:[{}]", response2.getStatusLine().getStatusCode());
                }
                else
                {
                    log.info("Send diatom information to SCADA Server, Response null...");
                }

                return new ResponseEntity<>("", HttpStatus.CREATED);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.CONFLICT.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.CONFLICT);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/diatom", method = RequestMethod.GET)
    public ResponseEntity<String> getDiatom()
    {
        log.info("Recv getDiatom");

        List<DiatomDTO> diatomList = databaseService.getDiatom();
        log.info("getDiatom, result:[{}]", diatomList.size());
        if(diatomList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("diatom", diatomList);

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
            String strErrorBody = "{\"reason\":\"Empty Diatom\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/diatom/{diatom_index}", method = RequestMethod.PUT)
    public ResponseEntity<String> putDiatom(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable int diatom_index,
            @RequestBody DiatomDTO diatom)
    {
        log.info("Recv putDiatom:[{}]", diatom_index);
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
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
            diatom.setDiatom_index(diatom_index);

            // Update diatom
            int nResult = databaseService.modDiatom(diatom);
            log.info("modDiatom:[{}], result:[{}]", diatom_index, nResult);
            if(nResult > 0)
            {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("diatom", diatom);

                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();
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
                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/diatom/{diatom_index}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteDiatom(@RequestHeader("X-ACCESS-TOKEN") String token, @PathVariable int diatom_index)
    {
        log.info("Recv deleteDiatom:[{}]", diatom_index);
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
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
            int nResult = databaseService.delDiatom(diatom_index);
            log.info("delDiatom:[{}], result:[{}]", diatom_index, nResult);
            if(nResult > 0)
            {
                return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }
}
