package org.kwater.aio.controller;

import org.kwater.aio.dto.AccessTokenDTO;
import org.kwater.aio.dto.UserDTO;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.CommonValue;
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
import java.util.List;
import java.util.Map;


@RestController
//@EnableSwagger2
@Slf4j
public class UsersController
{
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    // 사용자 생성 - 관리자 전용
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<String> postUsers(@RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody UserDTO user)
    {
        log.info("Recv postUsers[{}]", user.getUserid());
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
            // Insert user
            int nResult = databaseService.addUser(user);
            log.info("Create User[{}], result:[{}]", user.getUserid(), nResult);
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

    // 특정 사용자 정보 조회 - 관리자 전용
    @RequestMapping(value = "/users/{userid}", method = RequestMethod.GET)
    public ResponseEntity<String> getUsers(@RequestHeader("X-ACCESS-TOKEN") String token, @PathVariable String userid)
    {
        log.info("Recv getUsers[{}]", userid);
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
            // get User Info
            UserDTO userDTO = databaseService.getUserFromUserid(userid);
            log.info("getUserFromUserid:[{}], result:[{}]", userid, userDTO != null ? 1 : 0);

            if(userDTO != null)
            {
                // Make Response Body
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("userid", userDTO.getUserid());
                userInfo.put("name", userDTO.getName());
                userInfo.put("partname", userDTO.getPartname());
                userInfo.put("authority", userDTO.getAuthority());

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("users", userInfo);

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

    // 전체 사용자 정보 조회 - 관리자 전용
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<String> getAllUsers(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        log.info("Recv getAllUsers");
        // Check Access Token
        boolean bFindToken = false;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            // 관리자 권한인지 확인
            if(accessTokenDTO.getAuthority() != CommonValue.ADMIN)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            bFindToken = true;
            // Stop auto login time update
//            long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
//            Date expirationDate = new Date();
//            expirationDate.setTime(expirationDate.getTime() + expiredTime);
//            databaseService.modToken(token, expirationDate);
        }

        if(bFindToken == true)
        {
            List<UserDTO> userDTOS = databaseService.getAllUser();
            log.info("getAllUser, result:[{}]", userDTOS.size());

            if(userDTOS.size() > 0)
            {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("login", userDTOS);

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
            else
            {
                String strErrorBody = "{\"reason\":\"Empty user\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    // 사용자 정보 수정 - 관리자 전용
    @RequestMapping(value = "/users/{userid}", method = RequestMethod.PUT)
    public ResponseEntity<String> putUsers(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable String userid,
            @RequestBody UserDTO user)
    {
        log.info("Recv putUsers[{}]", userid);
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority = CommonValue.NONE_AUTHORITY;
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
            // Update user
            user.setUserid(userid);
            int nResult = databaseService.modUser(nAuthority, user);
            log.info("modUser:[{}], result:[{}]", user.getUserid(), nResult);
            if(nResult > 0)
            {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("users", user);

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

    // 사용자 삭제 - 관리자 전용
    @RequestMapping(value = "/users/{userid}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUsers(@RequestHeader("X-ACCESS-TOKEN") String token, @PathVariable String userid)
    {
        log.info("Recv deleteUsers[{}]", userid);
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
            int nResult = databaseService.delUser(userid);
            log.info("delUser:[{}], result:[{}]", userid, nResult);

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

    // 사용자 비밀번호 변경 - 관리자 전용
    @RequestMapping(value = "/users/pw/{userid}", method = RequestMethod.PUT)
    public ResponseEntity<String> putUsersPw(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable String userid,
            @RequestBody UserDTO user)
    {
        log.info("Recv putUserPw[{}]", userid);
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
            // Update user
            int nResult = databaseService.modUserPw(userid, user.getPassword());
            log.info("modUserPw:[{}], result:[{}]", userid, nResult);
            if(nResult > 0)
            {
                return new ResponseEntity<>("", HttpStatus.OK);
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
