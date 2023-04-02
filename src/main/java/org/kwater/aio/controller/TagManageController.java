package org.kwater.aio.controller;

import org.kwater.aio.dto.AccessTokenDTO;
import org.kwater.aio.dto.TagManageDTO;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.CommonValue;
import org.kwater.aio.util.ProcessCodeList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@EnableSwagger2
@Slf4j
public class TagManageController
{
    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    ProcessCodeList processCodeList;

    @RequestMapping(value = "/tagmanage", method = RequestMethod.POST)
    public ResponseEntity<String> postTagManage(
            @RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody TagManageDTO tagManage)
    {
        log.info("Recv postTagManage, tagManage:[{}]", tagManage);

        // Check tagManage
        if(tagManage.getAlgorithm_code() == null || tagManage.getProcess_code() == null ||
                tagManage.getSeries() == null || tagManage.getLocation() == null || tagManage.getItem() == null ||
                tagManage.getName() == null || tagManage.getDisplay() == null || tagManage.getType() == null)
        {
            String strErrorBody = "{\"reason\":\"tag manage data must not null.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Check code
        if(processCodeList.isExist(tagManage.getAlgorithm_code()) == false)
        {
            String strErrorBody = "{\"reason\":\"Invalid algorithm code\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
        if(processCodeList.isExist(tagManage.getProcess_code()) == false)
        {
            String strErrorBody = "{\"reason\":\"Invalid process code\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

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
            }
        }

        if(bFindToken == true)
        {
            // Insert tag_manage
            int nResult = databaseService.addTagManage(tagManage);
            log.info("Insert tag_manage, result:[{}]", nResult);
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
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/tagmanage", method = RequestMethod.GET)
    public ResponseEntity<String> getTagManage(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        log.info("Recv getTagManage");

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
            }
        }

        if(bFindToken == true)
        {
            List<TagManageDTO> tagManageList = databaseService.getAllTagManage();
            log.info("getAllTagManage, result:[{}]", tagManageList.size());
            if(tagManageList.size() > 0)
            {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("tagmanage", tagManageList);

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
                String strErrorBody = "{\"reason\":\"Empty tag_manage\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/tagmanage", method = RequestMethod.PUT)
    public ResponseEntity<String> putTagManage(
            @RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody TagManageDTO tagManage)
    {
        log.info("Recv putTagManage, tagManage:[{}]", tagManage);

        // Check tagManage
        if(tagManage.getAlgorithm_code() == null || tagManage.getProcess_code() == null ||
                tagManage.getSeries() == null || tagManage.getLocation() == null || tagManage.getItem() == null ||
                tagManage.getName() == null || tagManage.getDisplay() == null || tagManage.getType() == null)
        {
            String strErrorBody = "{\"reason\":\"tag manage data must not null.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Check code
        if(processCodeList.isExist(tagManage.getAlgorithm_code()) == false)
        {
            String strErrorBody = "{\"reason\":\"Invalid algorithm code\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
        if(processCodeList.isExist(tagManage.getProcess_code()) == false)
        {
            String strErrorBody = "{\"reason\":\"Invalid process code\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

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
            }
        }

        if(bFindToken == true)
        {
            int nResult = databaseService.modTagManage(tagManage);

            if(nResult > 0)
            {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("tagmanage", tagManage);

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

    @RequestMapping(value = "/tagmanage", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTagManage(
            @RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody TagManageDTO tagManage)
    {
        log.info("Recv deleteTagManage, tagManage:[{}]", tagManage);

        // Check tagManage
        if(tagManage.getAlgorithm_code() == null || tagManage.getProcess_code() == null ||
                tagManage.getSeries() == null || tagManage.getLocation() == null || tagManage.getItem() == null ||
                tagManage.getName() == null)
        {
            String strErrorBody = "{\"reason\":\"tag manage data must not null.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Check code
        if(processCodeList.isExist(tagManage.getAlgorithm_code()) == false)
        {
            String strErrorBody = "{\"reason\":\"Invalid algorithm code\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
        if(processCodeList.isExist(tagManage.getProcess_code()) == false)
        {
            String strErrorBody = "{\"reason\":\"Invalid process code\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

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
            }
        }

        if(bFindToken == true)
        {
            int nResult = databaseService.delTagManage(tagManage);
            log.info("delTagManage, result:[{}]", nResult);

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
