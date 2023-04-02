package org.kwater.aio.controller;

import org.kwater.aio.ai_dto.FrequencyDTO;
import org.kwater.aio.dto.*;
import org.kwater.aio.resource_dto.CoagulantsDTO;
import org.kwater.aio.service.DatabaseServiceImpl;
import org.kwater.aio.util.PropertiesAuthentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

@RestController
//@EnableSwagger2
@Slf4j
public class SensorsController
{
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    @RequestMapping(value = "/sensors/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestSensors()
    {
        log.info("Recv getLatestSensors");

        SensorDTO sensorDTO = databaseService.getLatestSensor();
        log.info("getLatestSensor result:[{}]", sensorDTO != null ? 1 : 0);

        if(sensorDTO != null)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("sensors", sensorDTO);

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
            String strErrorBody = "{\"reason\":\"Empty Sensor or Diatom\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/sensors/history", method = RequestMethod.GET)
    public ResponseEntity<String> getHistorySensors()
    {
        log.info("Recv getHistorySensors");

        List<CoagulantsDTO> coagulantsList = databaseService.getHistorySensor();
        log.info("getHistorySensor result:[{}]", coagulantsList.size());

        if(coagulantsList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("coagulants", coagulantsList);

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
            String strErrorBody = "{\"reason\":\"Empty Sensor\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/sensors/coagulants", method = RequestMethod.GET)
    public ResponseEntity<String> getCoagulantsSensors()
    {
        log.info("Recv getCoagulantsSensors");

        CoagulantsDTO coagulantsDTO = databaseService.getLatestCoagulants();
        log.info("getLatestCoagulants result:[{}]", coagulantsDTO == null ? 0 : 1);

        if(coagulantsDTO != null)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("coagulants", coagulantsDTO);

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
            String strErrorBody = "{\"reason\":\"Empty Coagulants\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/sensors/trend/tb", method = RequestMethod.GET)
    public ResponseEntity<String> getTbTrendSensors()
    {
        log.info("Recv getTbTrendSensors");

        // Get start_time (before one month)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date startTime = calendar.getTime();

        // Make Body
        // get hs_e1_tb distinct count
        int nTotalSize = 0;
        List<FrequencyDTO> frequencyList = databaseService.getDistinctCountHsE1Tb(startTime);
        log.info("getDistinctCountHsE1Tb, result:[{}]", frequencyList.size());
        nTotalSize += frequencyList.size();

        Map<String, Object> responseBody = new HashMap<>();
        Map<String, Integer> countBody = new HashMap<>();
        for(FrequencyDTO dto : frequencyList)
        {
            countBody.put(dto.getValue(), dto.getCount());
        }
        if(countBody.isEmpty() == true)
        {
            responseBody.put("hs_e1_tb", null);
        }
        else
        {
            responseBody.put("hs_e1_tb", countBody);
        }

        // get hs_e2_tb distinct count
        frequencyList = databaseService.getDistinctCountHsE2Tb(startTime);
        log.info("getDistinctCountHsE2Tb, result:[{}]", frequencyList.size());
        nTotalSize += frequencyList.size();

        countBody = new HashMap<>();
        for(FrequencyDTO dto : frequencyList)
        {
            countBody.put(dto.getValue(), dto.getCount());
        }
        if(countBody.isEmpty() == true)
        {
            responseBody.put("hs_e2_tb", null);
        }
        else
        {
            responseBody.put("hs_e2_tb", countBody);
        }

        // get hs_f_tb distinct count
        frequencyList = databaseService.getDistinctCountHsFTb(startTime);
        log.info("getDistinctCountHsFTb, result:[{}]", frequencyList.size());
        nTotalSize += frequencyList.size();

        countBody = new HashMap<>();
        for(FrequencyDTO dto : frequencyList)
        {
            countBody.put(dto.getValue(), dto.getCount());
        }
        if(countBody.isEmpty() == true)
        {
            responseBody.put("hs_f_tb", null);
        }
        else
        {
            responseBody.put("hs_f_tb", countBody);
        }

        if(nTotalSize == 0)
        {
            String strErrorBody = "{\"reason\":\"Empty TB Trend\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }

        String strBody = "";
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

    @RequestMapping(value = "/sensors/trend/tbcoagulants", method = RequestMethod.GET)
    public ResponseEntity<String> getTbCoagulantsTrendSensors()
    {
        log.info("Recv getTbTrendSensors");

        // Get start_time (before one month)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -48);
        Date startTime = calendar.getTime();

        List<SensorDTO> sensorList = databaseService.getSensor(startTime);
        log.info("getSensor, result:[{}]", sensorList.size());

        if(sensorList.size() > 0)
        {
            List<TrendTbCoagulantsDTO> trendTbCoagulantsList = new ArrayList<>();
            for(SensorDTO sensorDTO : sensorList)
            {
                TrendTbCoagulantsDTO trendTbCoagulantsDTO = new TrendTbCoagulantsDTO();
                trendTbCoagulantsDTO.setUpdate_time(sensorDTO.getUpdate_time());
                trendTbCoagulantsDTO.setHs_tb(sensorDTO.getHs_tb());
                trendTbCoagulantsDTO.setHs_e1_tb(sensorDTO.getHs_e1_tb());
                trendTbCoagulantsDTO.setHs_e2_tb(sensorDTO.getHs_e2_tb());
                trendTbCoagulantsDTO.setAi_coagulants1_input(sensorDTO.getAi_coagulants1_input());
                trendTbCoagulantsDTO.setAi_coagulants2_input(sensorDTO.getAi_coagulants2_input());
                trendTbCoagulantsDTO.setAi_coagulants1_type(sensorDTO.getAi_coagulants1_type());
                trendTbCoagulantsDTO.setAi_coagulants2_type(sensorDTO.getAi_coagulants2_type());
                trendTbCoagulantsDTO.setReal_coagulants1_input(sensorDTO.getReal_coagulants1_input());
                trendTbCoagulantsDTO.setReal_coagulants2_input(sensorDTO.getReal_coagulants2_input());
                trendTbCoagulantsDTO.setReal_coagulants1_type(sensorDTO.getReal_coagulants1_type());
                trendTbCoagulantsDTO.setReal_coagulants2_type(sensorDTO.getReal_coagulants2_type());
                trendTbCoagulantsList.add(trendTbCoagulantsDTO);
            }

            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("trend_tbcoagulants", trendTbCoagulantsList);

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
            String strErrorBody = "{\"reason\":\"Empty TB & Coagulants Trend\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/sensors/trend/tbcoagulants/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestTbCoagulantsTrendSensors()
    {
        log.info("Recv getLatestTbTrendSensors");

        // Get Latest sensor
        SensorDTO sensorDTO = databaseService.getLatestSensor();
        log.info("getLatestSensor, result:[{}]", sensorDTO != null ? 1 : 0);

        if(sensorDTO != null)
        {
            TrendTbCoagulantsDTO trendTbCoagulantsDTO = new TrendTbCoagulantsDTO();
            trendTbCoagulantsDTO.setUpdate_time(sensorDTO.getUpdate_time());
            trendTbCoagulantsDTO.setHs_tb(sensorDTO.getHs_tb());
            trendTbCoagulantsDTO.setHs_e1_tb(sensorDTO.getHs_e1_tb());
            trendTbCoagulantsDTO.setHs_e2_tb(sensorDTO.getHs_e2_tb());
            trendTbCoagulantsDTO.setAi_coagulants1_input(sensorDTO.getAi_coagulants1_input());
            trendTbCoagulantsDTO.setAi_coagulants2_input(sensorDTO.getAi_coagulants2_input());
            trendTbCoagulantsDTO.setAi_coagulants1_type(sensorDTO.getAi_coagulants1_type());
            trendTbCoagulantsDTO.setAi_coagulants2_type(sensorDTO.getAi_coagulants2_type());
            trendTbCoagulantsDTO.setReal_coagulants1_input(sensorDTO.getReal_coagulants1_input());
            trendTbCoagulantsDTO.setReal_coagulants2_input(sensorDTO.getReal_coagulants2_input());
            trendTbCoagulantsDTO.setReal_coagulants1_type(sensorDTO.getReal_coagulants1_type());
            trendTbCoagulantsDTO.setReal_coagulants2_type(sensorDTO.getReal_coagulants2_type());

            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("trend_tbcoagulants", trendTbCoagulantsDTO);

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
            String strErrorBody = "{\"reason\":\"Empty TB & Coagulants Trend\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/sensors/hs/tb", method = RequestMethod.PUT)
    public ResponseEntity<String> getTbHsSensors(@RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("Recv getTbHsSensors, start:[{}], end:[{}]", dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // Get hs tb data
        List<TrendTbDTO> trendTbList = databaseService.getTrendTb(dateSearchDTO);
        log.info("getTrendTb, result:[{}]", trendTbList.size());

        if(trendTbList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("hs_tb", trendTbList);

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
//            String strErrorBody = "{\"reason\":\"Empty TB Trend\"}";
//            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            // Result == 0, OK
            return new ResponseEntity<>("", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/sensors/hs/{code}", method = RequestMethod.PUT)
    public ResponseEntity<String> getCodeHsSensors(
            @PathVariable String code,
            @RequestBody InterfaceDateSearchDTO dateSearchDTO)
    {
        log.info("Recv getCodeHsSensors, code:[{}], start:[{}], end:[{}]",
                code, dateSearchDTO.getStart_time(), dateSearchDTO.getEnd_time());

        // Get hs realtime sensor data
        List<TrendCodeDTO> trendCodeList = databaseService.getTrendCode(code, dateSearchDTO);
        log.info("getTrendCode, code:[{}], result:[{}]", code, trendCodeList.size());

        if(trendCodeList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put(code, trendCodeList);

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
            String strErrorBody = "{\"reason\":\"Empty Realtime Data\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
