package org.kwater.aio.controller;

import org.kwater.aio.dto.ClassInfoDTO;
import org.kwater.aio.dto.ClusterInfoDTO;
import org.kwater.aio.dto.CoagulantsAnalysisDTO;
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

import java.util.*;

@RestController
//@EnableSwagger2
@Slf4j
public class AnalysisCoagulantsController
{
//    @Autowired
//    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    @RequestMapping(value = "/analysis/coagulants", method = RequestMethod.GET)
    public ResponseEntity<String> getCoagulantsAnalysis()
    {
        log.info("Recv getCoagulantsAnalysis");

        List<CoagulantsAnalysisDTO> coagulantsAnalysisList = databaseService.getAllCoagulantsAnalysis();
        log.info("getAllCoagulantsAnalysis, result:[{}]", coagulantsAnalysisList.size());
        if(coagulantsAnalysisList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("analysis_coagulants", coagulantsAnalysisList);

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
            String strErrorBody = "{\"reason\":\"Empty coagulants_analysis\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/coagulants/latest", method = RequestMethod.GET)
    public ResponseEntity<String> getLatestCoagulantsAnalysis()
    {
        log.info("Recv getLatestCoagulantsAnalysis");

        CoagulantsAnalysisDTO coagulantsAnalysis = databaseService.getLatestCoagulantsAnalysis();

        log.info("getLatestCoagulantsAnalysis, result:[{}]", coagulantsAnalysis != null ? 1 : 0);
        if(coagulantsAnalysis != null)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("analysis_coagulants", coagulantsAnalysis);

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
            String strErrorBody = "{\"reason\":\"Empty coagulants_analysis\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/coagulants/cluster", method = RequestMethod.GET)
    public ResponseEntity<String> getClusterCoagulantsAnalysis()
    {
        log.info("Recv getClusterCoagulantsAnalysis");

        List<ClusterInfoDTO> clusterInfoList = databaseService.getAllCoagulantsClusterInfo();
        log.info("getAllCoagulantsClusterInfo, result:[{}]", clusterInfoList.size());

        if(clusterInfoList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("cluster_info", clusterInfoList);

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
            String strErrorBody = "{\"reason\":\"Empty cluster_info\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/analysis/rawwater", method = RequestMethod.POST)
    public ResponseEntity<String> postRawwaterAnalysis(@RequestBody ClassInfoDTO classInfo)
    {
        log.info("Recv postRawwaterAnalysis");

        int nResult = databaseService.addRawWaterClassInfo(classInfo);
        log.info("insert class_info, class_index:[{}]", classInfo.getClass_index());
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

    @RequestMapping(value = "/coagulants/rawwater", method = RequestMethod.GET)
    public ResponseEntity<String> getRawwaterAnalysis()
    {
        log.info("Recv getRawwaterAnalysis");

        List<ClassInfoDTO> classInfoList = databaseService.getAllRawWaterClassInfo();
        log.info("GetAllRawWaterClassInfo, result:[{}]", classInfoList.size());

        if(classInfoList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("class_info", classInfoList);

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
            String strErrorBody = "{\"reason\":\"Empty class_info\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/coagulants/rawwater/{classIndex}", method = RequestMethod.PUT)
    public ResponseEntity<String> putRawwaterAnalysis(@RequestBody ClassInfoDTO classInfo, @PathVariable int classIndex)
    {
        log.info("Recv putRawwaterAnalysis[{}]", classIndex);

        classInfo.setClass_index(classIndex);

        int nResult = databaseService.modRawWaterClassInfo(classInfo);
        log.info("modRawWaterClassInfo[{}], result:[{}]", classIndex, nResult);

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

    @RequestMapping(value = "/analysis/rawwater/{classIndex}", method = RequestMethod.DELETE)
    public ResponseEntity<String> delRawwaterAnalysis(@PathVariable int classIndex)
    {
        log.info("Recv delRawwaterAnalysis[{}]", classIndex);

        int nResult = databaseService.delRawWaterClassInfo(classIndex);
        log.info("delRawWaterClassInfo[{}], result:[{}]", classIndex, nResult);

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

    @RequestMapping(value = "/analysis/coagulants/time", method = RequestMethod.GET)
    public ResponseEntity<String> getTimeCoagulantsAnalysis()
    {
        log.info("Recv getTimeCoagulantsAnalysis");

        List<CoagulantsAnalysisDTO> coagulantsAnalysisList = databaseService.get2LatestCoagulantsAnalysis();
        log.info("get2LatestCoagulantsAnalysis, result:[{}]", coagulantsAnalysisList.size());

        if(coagulantsAnalysisList.size() > 0)
        {
            Date compareDate = null;
            long lDifferent = 0;
            for(CoagulantsAnalysisDTO dto : coagulantsAnalysisList)
            {
                // First, set compareDate
                if(compareDate == null)
                {
                    compareDate = dto.getEnd_time();
                    continue;
                }

                // Second, calculate different
                lDifferent = compareDate.getTime() - dto.getEnd_time().getTime();
            }

            int nPeriod = (int)(lDifferent / CommonValue.ONE_MINUTE);
            Date latestDate = coagulantsAnalysisList.get(0).getEnd_time();
            Date nextDate = new Date();
            nextDate.setTime(latestDate.getTime() + lDifferent);

            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("period", nPeriod);
            responseBody.put("latest", latestDate);
            responseBody.put("next", nextDate);

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
            String strErrorBody = "{\"reason\":\"Empty coagulants_analysis\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // for demo
    ///////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/analysis/coagulants/minute", method = RequestMethod.GET)
    public ResponseEntity<String> getAnalysisCoagulantsMinute()
    {
        log.info("Recv getAnalysisCoagulantsMinute");

        // Get start_time (before one month)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -6);
        calendar.add(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startTime = calendar.getTime();
        List<CoagulantsAnalysisDTO> coagulantsAnalysisList = databaseService.getMinuteCoagulantsAnalysis(startTime);

        log.info("getMinuteCoagulantsAnalysis, result:[{}]", coagulantsAnalysisList.size());
        if(coagulantsAnalysisList.size() > 0)
        {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("analysis_coagulants", coagulantsAnalysisList);

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
            String strErrorBody = "{\"reason\":\"Empty coagulants_analysis\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }
}
