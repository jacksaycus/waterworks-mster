package org.kwater.aio.controller;

import org.kwater.aio.dto.LoginHistoryDTO;
import org.kwater.aio.dto.UserDTO;
import org.kwater.aio.dto.InterfaceLoginDTO;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.dto.AccessTokenDTO;
import org.kwater.aio.util.CommonValue;
import org.kwater.aio.util.PropertiesAuthentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@RestController
//@EnableSwagger2
@Slf4j
public class LoginController
{
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    // 이중화 구조에서 시각화 Health Check를 위해 hostname을 반환
    @RequestMapping(value = "/hostname", method = RequestMethod.GET)
    public ResponseEntity<String> getHostname(HttpServletRequest request)
    {
        log.info("Receive getHostname");

        try
        {
            String strBody = InetAddress.getLocalHost().getHostName();
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        }
        catch(UnknownHostException e)
        {
            String strErrorBody = "{\"reason\":\"Unknown Host\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    // 시각화 서비스 로그인
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> postLogin(@RequestBody InterfaceLoginDTO interfaceLoginDto, HttpServletRequest request)
    {
        log.info("Recv postLogin [{}]", interfaceLoginDto.getUserid());
        // Get User Information from Database
        UserDTO userDTO = databaseService.getUser(interfaceLoginDto.getUserid(), interfaceLoginDto.getPassword());
        log.info("getUser:[{}], result:[{}]", interfaceLoginDto.getUserid(), userDTO != null ? 1: 0);

        if(userDTO != null)
        {
            // Create Access Token
            String strToken = "";

            List<AccessTokenDTO> accessTokenList = databaseService.getAllTokens();
            int nTokenCount = accessTokenList.size();
            log.info("getAllTokens Token:[{}]", nTokenCount);

            // Create Access Token / Check Duplicate Token
            if(nTokenCount > 0)
            {
                boolean bDuplicate = true;
                while(bDuplicate == true)
                {
                    bDuplicate = false;
                    strToken = createToken(propertiesAuthentication.getKey(), propertiesAuthentication.getExpiration());
                    for(int i = 0; i < nTokenCount; i++)
                    {
                        if(accessTokenList.get(i).getAccess_token().equalsIgnoreCase(strToken) == true)
                        {
                            bDuplicate = true;
                            break;
                        }
                    }
                }
            }
            else
            {
                strToken = createToken(propertiesAuthentication.getKey(), propertiesAuthentication.getExpiration());
            }


            // Set expiration date
            Date expirationDate = new Date();
            try
            {
                long exp = getExpirationDateFromJwtString(propertiesAuthentication.getKey(), strToken);
                expirationDate.setTime(exp);
            }
            catch(InterruptedException e)
            {
                expirationDate = new Date();
            }

            // Create AccessTokenDTO
            AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
            accessTokenDTO.setUserid(userDTO.getUserid());
            accessTokenDTO.setName(userDTO.getName());
            accessTokenDTO.setAccess_token(strToken);
            accessTokenDTO.setAuthority(userDTO.getAuthority());
            accessTokenDTO.setExpiration(expirationDate);

            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("access_token", accessTokenDTO.getAccess_token());
            responseBody.put("authority", userDTO.getAuthority());
            responseBody.put("expiration", accessTokenDTO.getExpiration());

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

            // Insert Access Token
            databaseService.addToken(accessTokenDTO);

            // Insert login_history
            LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
            loginHistoryDTO.setUserid(userDTO.getUserid());
            loginHistoryDTO.setName(userDTO.getName());
            loginHistoryDTO.setType(CommonValue.LOGIN);
            loginHistoryDTO.setTimestamp(new Date());

            // Get Request source address
            if(request.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1")  == true)
            {
                loginHistoryDTO.setAddress("127.0.0.1");
            }
            else
            {
                String strAddress = "";
                Enumeration<String> forwarded = request.getHeaders("X-Forwarded-For");
                while(forwarded.hasMoreElements())
                {
                    strAddress = forwarded.nextElement();
                }

                loginHistoryDTO.setAddress(strAddress == "" ? request.getRemoteAddr() : strAddress);
            }
            log.info("addLoginHistory:[{}], result:[{}]",
                    loginHistoryDTO.getUserid(), databaseService.addLoginHistory(loginHistoryDTO));

            return new ResponseEntity<>(strBody, HttpStatus.CREATED);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
        }
    }

    // 로그인 시간 연장 - 관리자 전용
    @RequestMapping(value = "login", method = RequestMethod.PUT)
    public ResponseEntity<String> putLogin(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        log.info("Recv putLogin");
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority = CommonValue.NONE_AUTHORITY;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        // Check Authority
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
                // 로그인 시간 연장
                bFindToken = true;
                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
                Date expirationDate = new Date();
                expirationDate.setTime(expirationDate.getTime() + expiredTime);
                databaseService.modToken(token, expirationDate);
                accessTokenDTO = databaseService.getToken(token);
            }
        }

        if(bFindToken == true)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("access_token", accessTokenDTO.getAccess_token());
            responseBody.put("authority", nAuthority);
            responseBody.put("expiration", accessTokenDTO.getExpiration());

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
            return new ResponseEntity<>(strBody, HttpStatus.CREATED);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    // 로그인 이력 조회 - 관리자 전용
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<String> getLogin(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        log.info("Recv getLogin");
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        // Check Authority
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
            // get login_history
            List<LoginHistoryDTO> loginHistoryDTOS = databaseService.getAllLoginHistory();
            log.info("getAllLoginHistory, result:[{}]", loginHistoryDTOS.size());

            if(loginHistoryDTOS.size() > 0)
            {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("login", loginHistoryDTOS);

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
                String strErrorBody = "{\"reason\":\"Empty login_history\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    // 로그아웃
    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteLogout(@RequestHeader("X-ACCESS-TOKEN") String token, HttpServletRequest request)
    {
        log.info("Recv deleteLogout");
        // Check Access Token
        boolean bFindToken = false;
        String strUserid = "", strName = "";

        // Delete AccessToken
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);

        if(accessTokenDTO != null)
        {
            strUserid = accessTokenDTO.getUserid();
            strName = accessTokenDTO.getName();

            databaseService.delToken(token);
            bFindToken = true;
        }

        if(bFindToken == true)
        {
            // Insert login_history
            LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
            loginHistoryDTO.setUserid(strUserid);
            loginHistoryDTO.setName(strName);
            loginHistoryDTO.setType(CommonValue.LOGOUT);
            loginHistoryDTO.setTimestamp(new Date());

            // Get request source address
            if(request.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1")  == true)
            {
                loginHistoryDTO.setAddress("127.0.0.1");
            }
            else
            {
                String strAddress = "";
                Enumeration<String> forwarded = request.getHeaders("X-Forwarded-For");
                while(forwarded.hasMoreElements())
                {
                    strAddress = forwarded.nextElement();
                }

                loginHistoryDTO.setAddress(strAddress == "" ? request.getRemoteAddr() : strAddress);
            }
            int nResult = databaseService.addLoginHistory(loginHistoryDTO);

            log.info("[{}] logout result:[{}]", strUserid, nResult);
            return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    private static String createToken(String key, long minute)
    {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<>();
        long expiredTime = CommonValue.ONE_MINUTE * minute;
        Date expirationDate = new Date();
        expirationDate.setTime(expirationDate.getTime() + expiredTime);
        payloads.put("exp", expirationDate.getTime());
        payloads.put("data", "login success");

        String strJwt = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();

        return strJwt;
    }

    private static String getTokenFromJwtString(String key, String jwtTokenString) throws InterruptedException
    {
        Claims claims = Jwts.parser()
                .setSigningKey(key.getBytes())
                .parseClaimsJws(jwtTokenString)
                .getBody();

        Date expiration = claims.get("exp", Date.class);
        //System.out.println(expiration);

        String data = claims.get("data", String.class);
        //System.out.println(data);

        return data;
    }

    private static long getExpirationDateFromJwtString(String key, String jwtTokenString) throws InterruptedException
    {
        Claims claims = Jwts.parser()
                .setSigningKey(key.getBytes())
                .parseClaimsJws(jwtTokenString)
                .getBody();

        Date dExpiration = claims.get("exp", Date.class);

        long expiration = dExpiration.getTime() / 1000; // JWT Date get Bug
        //System.out.println(expiration);

        return expiration;
    }
}
