package org.kwater.utils.aio;
public class CommonValue
{
    // Authority
    public static int NONE_AUTHORITY = -1;
    public static int ADMIN = 0;
    public static int USER = 1;

    // Login History
    public static int INVALID_LOGOUT = -1;
    public static int LOGOUT = 0;
    public static int LOGIN = 1;

    // Time
    public static int ONE_SECOND    = 1000;
    public static int TEN_SECOND    = ONE_SECOND * 10;
    public static int THIRTY_SECOND = TEN_SECOND * 3;
    public static int ONE_MINUTE    = 1000 * 60;
    public static int TEN_MINUTE    = ONE_MINUTE * 10;
    public static int ONE_HOUR      = 1000 * 60 * 60;
    public static int NINE_HOUR     = ONE_HOUR * 9;
    public static int ONE_DAY       = 1000 * 60 * 60 * 24;

    // System Monitoring Type
    public static int MONITORING_TYPE_PROCESSOR_USED    = 1;
    public static int MONITORING_TYPE_MEMORY_USED       = 2;
    public static int MONITORING_TYPE_PARTITON_USED     = 3;
    public static int MONITORING_TYPE_SENT_THROUGHPUT   = 4;
    public static int MONITORING_TYPE_RECV_THROUGHPUT   = 5;
    public static int MONITORING_TYPE_ANALYSIS_DB       = 11;
    public static int MONITORING_TYPE_VISUALIZE_API     = 12;
    public static int MONITORING_TYPE_COLLECTOR         = 13;

    // Alarm Type
    public static int ALARM_TYPE_OFF        = 0;
    public static int ALARM_TYPE_THRESHOLD  = 1;
    public static int ALARM_TYPE_SEMI_AUTO  = 2;
    public static int ALARM_TYPE_ANOTHER_SYSTEM = 3;
    public static String ALARM_VALUE_ON     = "ON";
    public static String ALARM_VALUE_OFF    = "OFF";
    public static String ALARM_VALUE_CHANGE = "CHANGE";

    // Alarm ID
    public static int ALARM_RECEIVING_AI_CONTROL        = 131000;
    public static int ALARM_RECEIVING_AI_MODULE_ERROR   = 131100;
    public static int ALARM_COAGULANT_AI_CONTROL        = 132000;
    public static int ALARM_COAGULANT_AI_MODULE_ERROR   = 132100;
    public static int ALARM_MIXING_AI_CONTROL           = 133000;
    public static int ALARM_MIXING_AI_MODULE_ERROR      = 133100;
    public static int ALARM_SEDIMENTATION_AI_CONTROL    = 134000;
    public static int ALARM_SEDIMENTATION_AI_MODULE_ERROR = 134100;
    public static int ALARM_FILTER_AI_CONTROL           = 135000;
    public static int ALARM_FILTER_AI_MODULE_ERROR      = 135100;
    public static int ALARM_GAC_AI_CONTROL              = 136000;
    public static int ALARM_GAC_AI_MODULE_ERROR         = 136100;
    public static int ALARM_DISINFECTION_AI_CONTROL     = 137000;
    public static int ALARM_DISINFECTION_AI_MODULE_ERROR = 137100;
    public static int ALARM_OZONE_AI_CONTROL            = 138000;
    public static int ALARM_OZONE_AI_MODULE_ERROR       = 138100;
    public static int MAX_WAIO_ALARM_COUNT  = 199999;
    public static int MAX_EMS_ALARM_COUNT   = 299999;
    public static int MAX_PMS_ALARM_COUNT   = 399999;

    // Alarm Code
    public static String ALARM_CODE_CPU_UPPER1           = "cpu_upper1";
    public static String ALARM_CODE_CPU_UPPER2           = "cpu_upper2";
    public static String ALARM_CODE_CPU_UPPER3           = "cpu_upper3";
    public static String ALARM_CODE_CPU_UPPER4           = "cpu_upper4";
    public static String ALARM_CODE_CPU_UPPER5           = "cpu_upper5";
    public static String ALARM_CODE_MEMORY_UPPER1        = "memory_upper1";
    public static String ALARM_CODE_MEMORY_UPPER2        = "memory_upper2";
    public static String ALARM_CODE_MEMORY_UPPER3        = "memory_upper3";
    public static String ALARM_CODE_MEMORY_UPPER4        = "memory_upper4";
    public static String ALARM_CODE_MEMORY_UPPER5        = "memory_upper5";
    public static String ALARM_CODE_DISK_UPPER1          = "disk_upper1";
    public static String ALARM_CODE_DISK_UPPER2          = "disk_upper2";
    public static String ALARM_CODE_DISK_UPPER3          = "disk_upper3";
    public static String ALARM_CODE_DISK_UPPER4          = "disk_upper4";
    public static String ALARM_CODE_DISK_UPPER5          = "disk_upper5";
    public static String ALARM_CODE_VISUALIZE_API_OFF1   = "visualize_api_off1";
    public static String ALARM_CODE_VISUALIZE_API_OFF2   = "visualize_api_off2";
    public static String ALARM_CODE_SYSTEM_OFF1          = "system_off1";
    public static String ALARM_CODE_SYSTEM_OFF2          = "system_off2";
    public static String ALARM_CODE_SYSTEM_OFF3          = "system_off3";
    public static String ALARM_CODE_SYSTEM_OFF4          = "system_off4";
    public static String ALARM_CODE_SYSTEM_OFF5          = "system_off5";
    public static String ALARM_CODE_ANALYSIS_OFF        = "analysis_off";
    public static String ALARM_CODE_DAQ_OFF1             = "daq_off1";
    public static String ALARM_CODE_DAQ_OFF2             = "daq_off2";
    public static String ALARM_CODE_COLLECTOR_OFF1       = "collector_off1";
    public static String ALARM_CODE_COLLECTOR_OFF2       = "collector_off2";
    public static String ALARM_CODE_COAGULANTS_CHANGE1  = "coagulants_change1";
    public static String ALARM_CODE_COAGULANTS_CHANGE2  = "coagulants_change2";
    public static String ALARM_CODE_COAGULANTS_ERROR    = "coagulants_error";
    public static String ALARM_CODE_SERVER_PASSWORD1     = "server_password1";
    public static String ALARM_CODE_SERVER_PASSWORD2     = "server_password2";
    public static String ALARM_CODE_SERVER_PASSWORD3     = "server_password3";
    public static String ALARM_CODE_SERVER_PASSWORD4     = "server_password4";
    public static String ALARM_CODE_SERVER_PASSWORD5     = "server_password5";

    public static String ALARM_CODE_RECEIVING_AI_CONTROL    = "receiving_ai_control";
    public static String ALARM_CODE_COAGULANT_AI_CONTROL    = "coagulant_ai_control";
    public static String ALARM_CODE_MIXING_AI_CONTROL       = "mixing_ai_control";
    public static String ALARM_CODE_SEDIMENTATION_AI_CONTROL    = "sedimentation_ai_control";
    public static String ALARM_CODE_FILTER_AI_CONTROL       = "filter_ai_control";
    public static String ALARM_CODE_GAC_AI_CONTROL          = "gac_ai_control";
    public static String ALARM_CODE_DISINFECTION_AI_CONTROL = "disinfection_ai_control";
    public static String ALARM_CODE_OZONE_AI_CONTROL        = "ozone_ai_control";

    // Health Check Status
    public static int HEALTH_CHECK_NORMAL   = 0;
    public static int HEALTH_CHECK_FAIL     = 1;
    public static int HEALTH_CHECK_ALARM    = 2;

    // Hadoop HAState
    public static String HASTATE_INITIALIZING   = "INITIALIZING";
    public static String HASTATE_ACTIVE         = "ACTIVE";
    public static String HASTATE_STANDBY        = "STANDBY";
    public static String HASTATE_STOPPED        = "STOPPED";
    public static String HASTATE_ERROR          = "ERROR";

    // Hadoop Monitoring Item
    public static String ANALYSIS1_HOSTNAME = "analysis1";
    public static String ANALYSIS2_HOSTNAME = "analysis2";
    public static String ANALYSIS3_HOSTNAME = "analysis3";
    public static String ANALYSIS4_HOSTNAME = "analysis4";
    public static String ANALYSIS5_HOSTNAME = "analysis5";
    public static String HADOOP_RM          = "Resource Manager";
    public static String HADOOP_NM          = "Node Manager";
    public static String HADOOP_NN          = "Name Node";
    public static String HADOOP_DN          = "Data Node";

    // Server Active State
    public static int ACTIVE_STATE_NONE     = 0;
    public static int ACTIVE_STATE_FIRST    = 1;
    public static int ACTIVE_STATE_SECOND   = 2;

    // DAQ Monitoring Item
    public static String COLLECTOR1_HOSTNAME = "collector1";
    public static String COLLECTOR2_HOSTNAME = "collector2";

    // Simulation State
    public static int STATE_STANDBY         = 0;
    public static int STATE_ANALYSIS_ING    = 1;
    public static int STATE_COMPLETED       = 2;
    public static int STATE_INPUT_ERROR     = 3;

    // Operation Mode
    public static String B_OPERATION_MODE           = "b_operation_mode";
    public static String C_OPERATION_MODE           = "c_operation_mode";
    public static String D_OPERATION_MODE           = "d_operation_mode";
    public static String E_OPERATION_MODE           = "e_operation_mode";
    public static String F_OPERATION_MODE           = "f_operation_mode";
    public static String G_OPERATION_MODE           = "g_operation_mode";
    public static String G_PRE_OPERATION_MODE       = "g_pre_operation_mode";
    public static String G_PERI_OPERATION_MODE       = "g_peri_operation_mode";
    public static String G_POST_OPERATION_MODE       = "g_post_operation_mode";
    public static String I_OPERATION_MODE           = "i_operation_mode";
    public static String IO_OPERATION_MODE          = "io_operation_mode";
    public static int OPERATION_MODE_MANUAL         = 0;
    public static int OPERATION_MODE_SEMI_AUTO      = 1;
    public static int OPERATION_MODE_FULL_AUTO      = 2;

    // Passwd
    public static int PASSWD_USER           = 0;
    public static int PASSWD_STATE          = 1;
    public static int PASSWD_LATEST_DATE    = 2;
    public static int PASSWD_MINIMUM_COUNT  = 3;
    public static int PASSWD_MAXIMUM_COUNT  = 4;
    public static int PASSWD_EXPIRED_REMAINING = 5;
    public static int PASSWD_EXPIRED_DELAY  = 6;
    public static int PASSWD_SIZE           = 7;
    public static int PASSWD_INFINITY       = 99999;

    // Tag Manage Type
    public static int TAG_MANAGE_TYPE_SCADA = 0;
    public static int TAG_MANAGE_TYPE_AI    = 1;
    public static int TAG_MANAGE_TYPE_UI    = 2;
    public static int TAG_MANAGE_TYPE_INIT  = 3;

    // Algorithm Process Code
    public static String PROCESS_RECEIVING      = "B";
    public static String PROCESS_COAGULANT      = "C";
    public static String PROCESS_MIXING         = "D";
    public static String PROCESS_SEDIMENTATION  = "E";
    public static String PROCESS_FILTER         = "F";
    public static String PROCESS_DISINFECTION   = "G";
    public static String PROCESS_GAC            = "I";
    public static String PROCESS_OZONE          = "IO";
    public static String PROCESS_EMS            = "EMS";

    // Filter State
    public static int FILTER_STATE_NONE                 = 0;
    public static int FILTER_STATE_FILTER               = 1;
    public static int FILTER_STATE_RELAX                = 2;
    public static int FILTER_STATE_BW                   = 3;
    public static int FILTER_STATE_BW_RELAX             = 4;
    public static int FILTER_STATE_CIRCULATION          = 5;
    public static int FILTER_STATE_CIRCULATION_RELAX    = 6;
    public static int FILTER_STATE_CIRCULATION_STANDBY  = 7;

    // GAC State
    public static int GAC_STATE_NONE                    = 0;
    public static int GAC_STATE_FILTER                  = 1;
    public static int GAC_STATE_RELAX                   = 2;
    public static int GAC_STATE_BW                      = 3;
    public static int GAC_STATE_BW_STANDBY              = 4;

    // EMS Mode
    public static int EMS_MODE_OFF = 0;
    public static int EMS_MODE_ON = 1;

    // AI On/Off
    public static int AI_OFF    = 0;
    public static int AI_ON     = 1;

    // KAFKA FLAG
    public static int KAFKA_FLAG_INIT       = 0;
    public static int KAFKA_FLAG_POPUP      = 1;
    public static int KAFKA_FLAG_CONFIRM    = 2;
    public static int KAFKA_FLAG_SEND       = 3;

    // KAFKA TOPIC
    public static String KAFKA_TOPIC_CONTROL    = "ai_control";
    public static String KAFKA_TOPIC_RESULT     = "control";
    public static String KAFKA_TOPIC_POPUP      = "ai_popup";

    // Control Value
    public static String CONTROL_TRUE   = "true";

    // Supervisor Process States
    public static int PROCESS_STATE_STOPPED     = 0;
    public static int PROCESS_STATE_STARTING    = 10;
    public static int PROCESS_STATE_RUNNING     = 20;
    public static int PROCESS_STATE_BACKOFF     = 30;
    public static int PROCESS_STATE_STOPPING    = 40;
    public static int PROCESS_STATE_EXITED      = 100;
    public static int PROCESS_STATE_FATAL       = 200;
    public static int PROCESS_STATE_UNKNOWN     = 1000;

    // Supervisor Process name
    public static String PROCESS_RECEIVING_NAME     = "kwater_ai_b_process";
    public static String PROCESS_COAGULANT_NAME     = "kwater_ai_c_process";
    public static String PROCESS_MIXING_NAME        = "kwater_ai_d_process";
    public static String PROCESS_SEDIMENTATION_NAME = "kwater_ai_e_process";
    public static String PROCESS_FILTER_NAME        = "kwater_ai_f_process";
    public static String PROCESS_GAC_NAME           = "kwater_ai_i_process";
    public static String PROCESS_DISINFECTION_NAME  = "kwater_ai_g_process";
    public static String PROCESS_OZONE_NAME         = "kwater_ai_io_process";

    // Filter/GAC TI
    public static String F_LOCATION_TI_SET_MAX      = "AIF-2007";
    public static String I_LOCATION_TI_SET_MAX      = "AII-2005";
}
