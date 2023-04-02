package org.kwater.aio.service;

import org.kwater.aio.dto.AlarmInfoDTO;
import org.kwater.aio.dto.AlarmNotifyDTO;

import org.kwater.aio.util.AlarmInfoList;
import org.kwater.aio.util.CommonValue;
import org.kwater.aio.util.GlobalSystemConfig;
import org.kwater.aio.util.HttpSend;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AlarmServiceImpl implements IAlarmService
{
    @Autowired
    AlarmInfoList alarmInfoList;

    @Autowired
    DatabaseServiceImpl databaseService;

    @Autowired
    GlobalSystemConfig globalSystemConfig;

 

    // HTTP Connection 설정
    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectTimeout(3 * CommonValue.ONE_SECOND)
            .setConnectionRequestTimeout(3 * CommonValue.ONE_SECOND)
            .build();

    @Override
    public Integer alarmNotify(int alarmId, String message, String url, String time)
    {
        // Get AlarmInfo, if not exist, add alarm_info(EMS, PMS)
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmId(alarmId);
        if(alarmInfo == null)
        {
            if(alarmId > CommonValue.MAX_WAIO_ALARM_COUNT)
            {
                alarmInfo = new AlarmInfoDTO();
                alarmInfo.setAlarm_id(alarmId);
                alarmInfo.setCode_name(message);
                alarmInfo.setDisplay_name(message);
                alarmInfo.setUrl(url);
                alarmInfo.setType(CommonValue.ALARM_TYPE_ANOTHER_SYSTEM);
                alarmInfo.setCompare(0);
                alarmInfo.setValue("");
                alarmInfo.setScada_send(false);
                int nResult = databaseService.addAlarmInfo(alarmInfo);
                log.info("Add AlarmInfo:[{}], result:[{}]", alarmId, nResult);
                if(nResult > 0)
                {
                    alarmInfoList.addAlarmInfo(alarmInfo);
                }
            }
            else
            {
                log.error("Does not exist alarm:[{}]", alarmId);
                return null;
            }
        }

        // Insert alarm_notify
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date alarmTime = simpleDateFormat.parse(time);

            AlarmNotifyDTO alarmNotify = new AlarmNotifyDTO();
            alarmNotify.setAlarm_id(alarmInfo.getAlarm_id());
            alarmNotify.setAlarm_time(alarmTime);
            // 알람 ID에 따라 호스트명 결정
            if(alarmId > 0 && alarmId <= CommonValue.MAX_WAIO_ALARM_COUNT)
            {
                alarmNotify.setHostname("WAIO");
                alarmNotify.setValue(alarmInfo.getValue());
            }
            else if(alarmId > CommonValue.MAX_WAIO_ALARM_COUNT && alarmId <= CommonValue.MAX_EMS_ALARM_COUNT)
            {
                alarmNotify.setHostname("EMS");
                alarmNotify.setValue("EMS");
            }
            else if(alarmId > CommonValue.MAX_EMS_ALARM_COUNT && alarmId <= CommonValue.MAX_PMS_ALARM_COUNT)
            {
                alarmNotify.setHostname("PMS");
                alarmNotify.setValue("PMS");
            }

            int nResult = databaseService.addAlarmNotify(alarmNotify);

            return nResult;
        }
        catch (ParseException e)
        {
            log.error("Invalid Date format, [{}]", time);

            return null;
        }
    }

    @Override
    public Integer alarmNotify(String alarmCode, String hostname, Object value, boolean onceADay)
    {
        // Get AlarmInfo
        AlarmInfoDTO alarmInfo = alarmInfoList.getAlarmInfoFromAlarmCode(alarmCode);
        if(alarmInfo != null)
        {
            // Check once a day
            if(onceADay == true)
            {
                // Check latest alarm_time
                Date yesterdayDate = new Date();
                yesterdayDate.setTime(yesterdayDate.getTime() - CommonValue.ONE_DAY);
                AlarmNotifyDTO alarmNotify = databaseService.getLatestAlarmNotify(
                        alarmInfo.getAlarm_id(), yesterdayDate, hostname);

                if(alarmNotify != null)
                {
                    // If exist a alarm, ignore this alarm.
                    return null;
                }
            }

            // Insert alarm_notify
            AlarmNotifyDTO alarmNotify = new AlarmNotifyDTO();
            alarmNotify.setAlarm_id(alarmInfo.getAlarm_id());
            alarmNotify.setAlarm_time(new Date());
            alarmNotify.setHostname(hostname);
            alarmNotify.setValue(String.format("%s", value));

            int nResult = databaseService.addAlarmNotify(alarmNotify);

            // Send alarm information to SCADA Server
            // If alarm_info's "scada_send" is true
            if(alarmInfo.isScada_send() == true && alarmInfo.getTag() != null)
            {
                // Make body
                String strBody;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = simpleDateFormat.format(alarmNotify.getAlarm_time());
                Map<String, Object> requestBody = new HashMap<>();
//                requestBody.put("alarm_id", alarmInfo.getAlarm_id());
//                requestBody.put("alarm_time", strDate);
//                requestBody.put("hostname", hostname);
//                requestBody.put("value", value);
                requestBody.put("tag", alarmInfo.getTag());
                requestBody.put("time", strDate);
                requestBody.put("value", false);

                ObjectMapper objectMapper = new ObjectMapper();
                try
                {
                    strBody = objectMapper.writeValueAsString(requestBody);
                }
                catch(JsonProcessingException e)
                {
                    strBody = "";
                    log.error("JsonProcessingException occurred...value:[{}]", requestBody.toString());
                }
                //kafkaProducer.sendMessage("alarm", strBody);
            }
            return nResult;
        }
        else
        {
            log.error("Does not exist alarm:[{}]", alarmCode);
            return null;
        }
    }
}
