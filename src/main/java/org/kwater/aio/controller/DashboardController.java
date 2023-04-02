package org.kwater.aio.controller;

import org.kwater.aio.dto.AccessTokenDTO;
import org.kwater.aio.dto.DashboardDataDTO;
import org.kwater.aio.dto.DashboardIdDTO;
import org.kwater.aio.dto.DashboardInfoDTO;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.CommonValue;
import org.kwater.aio.util.Conversion;
import org.kwater.aio.util.PropertiesAuthentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.Map;

@RestController
//@EnableSwagger2
@Slf4j
public class DashboardController
{
//    @Autowired
//    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    @RequestMapping(value = "/dashboard", method = RequestMethod.POST)
    public ResponseEntity<String> postDashboard(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @RequestBody DashboardInfoDTO dashboardInfo)
    {
        log.info("Recv postDashboard");
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
            // Convert Class value to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> dashboardData = objectMapper.convertValue(dashboardInfo, Map.class);
            String strData = "";
            try
            {
                strData = objectMapper.writeValueAsString(dashboardData);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            int nResult = databaseService.addDashboardInfo(strData);
            log.info("addDashboardInfo, result:[{}]", nResult);
            return new ResponseEntity<>("", HttpStatus.CREATED);
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/dashboard/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestDashboard()
    {
        log.info("Recv getLatestDashboard");

        DashboardIdDTO dashboardId = databaseService.getLatestDashboardInfo();
        log.info("getLatestDashboardInfo, result:[{}]", dashboardId != null ? dashboardId.getDashboard_id() : null);
        if(dashboardId != null)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("dashboard_id", dashboardId.getDashboard_id());

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

    @RequestMapping(value = "/dashboard/{dashboard_id}", method = RequestMethod.GET)
    public ResponseEntity<String> getDashboard(@PathVariable String dashboard_id)
    {
        log.info("Recv getDashboard, dashboard_id:[{}]", dashboard_id);
        // Check dashboard_id type
        if(Conversion.isNumber(dashboard_id) == false)
        {
            String strErrorBody = "{\"reason\":\"dashboard_id must number.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        int nDashboardId = Integer.parseInt(dashboard_id);
        DashboardDataDTO dashboardData = databaseService.getDashboardInfo(nDashboardId);
        log.info("getDashboardInfo, result:[{}]", dashboardData != null ? 1 : 0);

        if(dashboardData != null)
        {
            // Make Response Body
            String strBody;
            ObjectMapper objectMapper = new ObjectMapper();
            try
            {
                DashboardInfoDTO dashboardInfo = objectMapper.readValue(dashboardData.getData(), DashboardInfoDTO.class);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("dashboard", dashboardInfo);

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

    @RequestMapping(value = "/dashboard/{dashboard_id}", method = RequestMethod.PUT)
    public ResponseEntity<String> putDashboard(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable String dashboard_id,
            @RequestBody DashboardInfoDTO dashboardInfo)
    {
        log.info("Recv putDashboard, dashboard_id:[{}]", dashboard_id);
        // Check dashboard_id type
        if(Conversion.isNumber(dashboard_id) == false)
        {
            String strErrorBody = "{\"reason\":\"dashboard_id must number.\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        // Check dashboardInfo
        if(dashboardInfo.getCreate_time() == null || dashboardInfo.getUpdate_time() == null || dashboardInfo.getTitle() == null ||
        dashboardInfo.getThumb() == null || dashboardInfo.getProcess_list() == null || dashboardInfo.getPath_list() == null)
        {
            String strErrorBody = "{\"reason\":\"dashboard_data must not null.\"}";
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
            int nDashboardId = Integer.parseInt(dashboard_id);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> dashboardData = objectMapper.convertValue(dashboardInfo, Map.class);
            String strData;
            try
            {
                strData = objectMapper.writeValueAsString(dashboardData);
            }
            catch(JsonProcessingException e)
            {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            int nResult = databaseService.modDashboardInfo(nDashboardId, strData);
            log.info("modDashbardInfo, result:[{}]", nResult);

            // If update is successful, retrieve new updated record
            if(nResult > 0)
            {
                DashboardDataDTO dashboardDataDTO = databaseService.getDashboardInfo(nDashboardId);
                log.info("getDashboardInfo, result:[{}]", dashboardData != null ? 1 : 0);

                if(dashboardData != null)
                {
                    // Make Response Body
                    String strBody;
                    try
                    {
                        DashboardInfoDTO dashboardInfoDTO = objectMapper.readValue(dashboardDataDTO.getData(), DashboardInfoDTO.class);

                        Map<String, Object> responseBody = new HashMap<>();
                        responseBody.put("dashboard", dashboardInfoDTO);

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

    @RequestMapping(value = "/dashboard/{dashboard_id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteDashboard(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable String dashboard_id)
    {
        log.info("Recv deleteDashboard, dashboard_id:[{}]", dashboard_id);
        // Check dashboard_id type
        if(Conversion.isNumber(dashboard_id) == false)
        {
            String strErrorBody = "{\"reason\":\"dashboard_id must number.\"}";
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
            int nDashboardId = Integer.parseInt(dashboard_id);
            int nResult = databaseService.delDashboardInfo(nDashboardId);
            log.info("delDashboardInfo, result:[{}]", nResult);

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
