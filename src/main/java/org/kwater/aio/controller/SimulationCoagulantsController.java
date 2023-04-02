package org.kwater.aio.controller;

import org.kwater.aio.dto.AccessTokenDTO;
import org.kwater.aio.dto.CoagulantsSimulationDTO;
import org.kwater.aio.dto.InterfaceSimulationCoagulantsSetDTO;
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
public class SimulationCoagulantsController
{
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    GlobalSystemConfig globalSystemConfig;

    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectionRequestTimeout(3 * CommonValue.ONE_SECOND)
            .build();

    @RequestMapping(value = "/simulation/coagulants", method = RequestMethod.POST)
    public ResponseEntity<String> postSimulationCoagulants(@RequestBody CoagulantsSimulationDTO coagulantsSimulationDTO)
    {
        log.info("Recv postSimulationCoagulants");

        coagulantsSimulationDTO.setUserid("user");
        coagulantsSimulationDTO.setReg_time(new Date());
        coagulantsSimulationDTO.setState(CommonValue.STATE_STANDBY);

        int nResult = databaseService.addCoagulantsSimulation(coagulantsSimulationDTO);
        log.info("insert coagulants_simulation, userid:[{}], result:[{}]", coagulantsSimulationDTO.getUserid(), nResult);
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

    @RequestMapping(value = "/simulation/coagulants/set", method = RequestMethod.POST)
    public ResponseEntity<String> postSimulationCoagulantsSet(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @RequestBody InterfaceSimulationCoagulantsSetDTO interfaceSimulationCoagulantsSetDTO)
    {
        log.info("Recv postSimulationCoagulantsSet");
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

        if(interfaceSimulationCoagulantsSetDTO.getAi_coagulants1_type() == null ||
                interfaceSimulationCoagulantsSetDTO.getAi_coagulants2_type() == null)
        {
            String strErrorBody = "{\"reason\":\"Coagulants must not null.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        if(interfaceSimulationCoagulantsSetDTO.getAi_coagulants1_input() <= 0.0f ||
                interfaceSimulationCoagulantsSetDTO.getAi_coagulants2_input() <= 0.0f)
        {
            String strErrorBody = "{\"reason\":\"Coagulants input must not null.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }


        if(bFindToken == true)
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = simpleDateFormat.format(new Date());
            Map<String, Object> requestBody = new HashMap<>();

            requestBody.put("update_time", strDate);
            requestBody.put("ai_coagulants1_input", interfaceSimulationCoagulantsSetDTO.getAi_coagulants1_input());
            requestBody.put("ai_coagulants1_result", 0.0f);
            requestBody.put("ai_coagulants1_revision", 0.0f);
            requestBody.put("ai_coagulants1_type", interfaceSimulationCoagulantsSetDTO.getAi_coagulants1_type());
            requestBody.put("ai_coagulants2_input", interfaceSimulationCoagulantsSetDTO.getAi_coagulants2_input());
            requestBody.put("ai_coagulants2_result", 0.0f);
            requestBody.put("ai_coagulants2_revision", 0.0f);
            requestBody.put("ai_coagulants2_type", interfaceSimulationCoagulantsSetDTO.getAi_coagulants2_type());
            String strBody = "";

            ObjectMapper objectMapper = new ObjectMapper();

            try
            {
                strBody = objectMapper.writeValueAsString(requestBody);
            }
            catch(JsonProcessingException e)
            {
                log.error("Json processing Exception occurred");
            }

            // Send [POST] coagulants SCADA Server1
            String strUri = "http://" + globalSystemConfig.getScada1_information() + "/coagulants";
            HttpPost httpPost = new HttpPost(strUri);
            StringEntity stringEntity = new StringEntity(strBody, "UTF-8");
            httpPost.setEntity(stringEntity);
            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Content-Type", "application/json");
            HttpResponse response1 = HttpSend.send(httpPost);

            // Send [POST] coagulants SCADA Server2
            strUri = "http://" + globalSystemConfig.getScada2_information() + "/coagulants";
            httpPost = new HttpPost(strUri);
            stringEntity = new StringEntity(strBody, "UTF-8");
            httpPost.setEntity(stringEntity);
            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Content-Type", "application/json");
            HttpResponse response2 = HttpSend.send(httpPost);

            if(response1 != null || response2 != null)
            {
                int nStatus = response1.getStatusLine().getStatusCode();
                if(nStatus == org.apache.http.HttpStatus.SC_CREATED)
                {
                    return new ResponseEntity<>("", HttpStatus.CREATED);
                }
                else if(nStatus == org.apache.http.HttpStatus.SC_CONFLICT)
                {
                    String strErrorBody = "{\"reason\":\"" + HttpStatus.CONFLICT.getReasonPhrase() + "\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.CONFLICT);
                }
                else
                {
                    String strErrorBody = "{\"reason\":\"" + HttpStatus.BAD_REQUEST.getReasonPhrase() + "\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                String strErrorBody = "{\"reason\":\"No response from SCADA Server.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/simulation/coagulants", method = RequestMethod.GET)
    public ResponseEntity<String> getAllSimulationCoagulants()
    {
        log.info("Recv getAllSimulationCoagulants");

        List<CoagulantsSimulationDTO> coagulantsSimulationList = databaseService.getAllCoagulantsSimulation();
        log.info("getAllCoagulantsSimulation, result:[{}]", coagulantsSimulationList.size());
        if(coagulantsSimulationList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("simulation_coagulants", coagulantsSimulationList);

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
            String strErrorBody = "{\"reason\":\"Empty coagulants_simulation\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/simulation/coagulants/uncompleted", method = RequestMethod.GET)
    public ResponseEntity<String> getUncompletedSimulationCoagulants()
    {
        log.info("Recv getUncompletedSimulationCoagulants");

        List<CoagulantsSimulationDTO> coagulantsSimulationList =
                databaseService.getCoagulantsSimulationLowerState(CommonValue.STATE_COMPLETED);
        log.info("getCoagulantsSimulationLowerState, result:[{}]", coagulantsSimulationList.size());
        if(coagulantsSimulationList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("simulation_coagulants", coagulantsSimulationList);

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
            String strErrorBody = "{\"reason\":\"Empty coagulants_simulation\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/simulation/coagulants/completed", method = RequestMethod.GET)
    public ResponseEntity<String> getCompletedSimulationCoagulants()
    {
        log.info("Recv getCompletedSimulationCoagulants");

        List<CoagulantsSimulationDTO> coagulantsSimulationList =
                databaseService.getCoagulantsSimulationUpperState(CommonValue.STATE_STANDBY);
        log.info("getCoagulantsSimulationUpperState, result:[{}]", coagulantsSimulationList.size());
        if(coagulantsSimulationList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("simulation_coagulants", coagulantsSimulationList);

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
            String strErrorBody = "{\"reason\":\"Empty coagulants_simulation\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
