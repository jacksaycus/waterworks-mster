package org.kwater.aio.service;

public interface IAlarmService
{
    Integer alarmNotify(String alarmCode, String hostname, Object value, boolean onceADay);
    Integer alarmNotify(int alarmId, String message, String url, String time);
}
