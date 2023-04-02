CREATE user 'water'@'localhost' IDENTIFIED BY 'works'
GRANT ALL PRIVILEGES ON waterworks.* TO 'water'@'localhost';
FLUSH PRIVILEGES;
-- 테이블 waio.coagulants_simulation 구조 내보내기
CREATE TABLE IF NOT EXISTS `coagulants_simulation` (
  `simulation_index` int(11) NOT NULL AUTO_INCREMENT,
  `reg_time` timestamp NULL DEFAULT NULL,
  `complete_time` timestamp NULL DEFAULT NULL,
  `userid` varchar(32) NOT NULL,
  `state` tinyint(4) NOT NULL,
  `cluster_id` int(11) DEFAULT NULL,
  `chemical1_code` varchar(3) DEFAULT NULL,
  `injection1_percent` float DEFAULT NULL,
  `chemical2_code` varchar(3) DEFAULT NULL,
  `injection2_percent` float DEFAULT NULL,
  `tb` float NOT NULL,
  `ph` float NOT NULL,
  `te` float NOT NULL,
  `cu` float NOT NULL,
  PRIMARY KEY (`simulation_index`)
) 
;

CREATE TABLE IF NOT EXISTS `coagulant_realtime` (
  `update_time` datetime NOT NULL,
  `name` varchar(256) NOT NULL DEFAULT '0',
  `value` varchar(256) NOT NULL DEFAULT '0',
  `quality` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`update_time`,`name`),
  KEY `update_time` (`update_time`)
) 
;
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.control_tags 구조 내보내기
CREATE TABLE IF NOT EXISTS `control_tags` (
  `algorithm_code` varchar(4) NOT NULL DEFAULT '',
  `series` tinyint(4) NOT NULL DEFAULT 0,
  `location` tinyint(4) NOT NULL DEFAULT 0,
  `item` varchar(64) NOT NULL DEFAULT '',
  `name` varchar(256) NOT NULL DEFAULT '',
  `display` varchar(256) NOT NULL DEFAULT '',
  PRIMARY KEY (`algorithm_code`,`series`,`location`,`item`,`name`)
)
;
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.dashboard_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `dashboard_info` (
  `dashboard_id` int(11) NOT NULL AUTO_INCREMENT,
  `data` longtext DEFAULT NULL,
  PRIMARY KEY (`dashboard_id`)
)

-- 내보낼 데이터가 선택되어 있지 않습니다.
;
-- 테이블 waio.diatom 구조 내보내기
CREATE TABLE IF NOT EXISTS `diatom` (
  `diatom_index` int(11) NOT NULL AUTO_INCREMENT,
  `update_time` timestamp NULL DEFAULT NULL,
  `measure_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `pd1_diatom` int(11) NOT NULL,
  `pd2_diatom` int(11) NOT NULL,
  `pd3_diatom` int(11) NOT NULL,
  `pd1_diatom_big` tinyint(4) NOT NULL,
  `pd2_diatom_big` tinyint(4) NOT NULL,
  `pd3_diatom_big` tinyint(4) NOT NULL,
  `pd_diatom_avg` varchar(32) NOT NULL,
  PRIMARY KEY (`diatom_index`)
)
;
CREATE TABLE IF NOT EXISTS `disinfection_realtime` (
  `update_time` datetime NOT NULL,
  `name` varchar(256) NOT NULL DEFAULT '0',
  `value` varchar(256) NOT NULL DEFAULT '0',
  `quality` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`update_time`,`name`),
  KEY `update_time` (`update_time`)
) ;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.disk_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `disk_info` (
  `hostname` varchar(64) NOT NULL,
  `model` varchar(128) NOT NULL,
  `size` bigint(20) NOT NULL,
  PRIMARY KEY (`hostname`,`model`,`size`)
);

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.ems_realtime 구조 내보내기
CREATE TABLE IF NOT EXISTS `ems_realtime` (
  `update_time` datetime NOT NULL,
  `name` varchar(256) NOT NULL,
  `value` varchar(256) NOT NULL,
  `quality` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`name`)
) ;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.filter_realtime 구조 내보내기
CREATE TABLE IF NOT EXISTS `filter_realtime` (
  `update_time` datetime NOT NULL,
  `name` varchar(256) NOT NULL DEFAULT '0',
  `value` varchar(256) NOT NULL DEFAULT '0',
  `quality` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`update_time`,`name`),
  KEY `update_time` (`update_time`)
);
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.gac_realtime 구조 내보내기
CREATE TABLE IF NOT EXISTS `gac_realtime` (
  `update_time` datetime NOT NULL,
  `name` varchar(256) NOT NULL DEFAULT '0',
  `value` varchar(256) NOT NULL DEFAULT '0',
  `quality` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`update_time`,`name`),
  KEY `update_time` (`update_time`)
) ;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.interface_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `interface_info` (
  `hostname` varchar(64) NOT NULL,
  `name` varchar(16) NOT NULL,
  `display_name` varchar(128) NOT NULL,
  `ipv4` varchar(32) NOT NULL,
  `mac` varchar(48) NOT NULL,
  PRIMARY KEY (`hostname`,`name`)
) ;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.login_history 구조 내보내기
CREATE TABLE IF NOT EXISTS `login_history` (
  `history_index` int(11) NOT NULL AUTO_INCREMENT,
  `userid` varchar(32) NOT NULL DEFAULT '0',
  `name` varchar(16) NOT NULL DEFAULT '0',
  `type` tinyint(4) NOT NULL DEFAULT 0,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `address` varchar(64) NOT NULL DEFAULT '0',
  PRIMARY KEY (`history_index`)
);

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.mixing_realtime 구조 내보내기
CREATE TABLE IF NOT EXISTS `mixing_realtime` (
  `update_time` datetime NOT NULL,
  `name` varchar(256) NOT NULL DEFAULT '0',
  `value` varchar(256) NOT NULL DEFAULT '0',
  `quality` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`update_time`,`name`),
  KEY `update_time` (`update_time`)
) ;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.ozone_realtime 구조 내보내기
CREATE TABLE IF NOT EXISTS `ozone_realtime` (
  `update_time` datetime NOT NULL,
  `name` varchar(256) NOT NULL DEFAULT '0',
  `value` varchar(256) NOT NULL DEFAULT '0',
  `quality` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`update_time`,`name`),
  KEY `update_time` (`update_time`)
) ;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.partition_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `partition_info` (
  `hostname` varchar(64) NOT NULL,
  `name` varchar(64) NOT NULL,
  `total_size` bigint(20) NOT NULL,
  `usable_size` bigint(20) NOT NULL,
  PRIMARY KEY (`hostname`,`name`)
);
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.process_code 구조 내보내기
CREATE TABLE IF NOT EXISTS `process_code` (
  `code_index` tinyint(4) NOT NULL,
  `code` varchar(4) NOT NULL DEFAULT '',
  `english` varchar(256) NOT NULL DEFAULT '',
  `korean` varchar(256) NOT NULL DEFAULT '',
  `is_use` tinyint(4) NOT NULL,
  PRIMARY KEY (`code_index`)
) ;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.receiving_realtime 구조 내보내기
CREATE TABLE IF NOT EXISTS `receiving_realtime` (
  `update_time` datetime NOT NULL,
  `name` varchar(256) NOT NULL DEFAULT '0',
  `value` varchar(256) NOT NULL DEFAULT '0',
  `quality` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`update_time`,`name`),
  KEY `update_time` (`update_time`)
)

-- 내보낼 데이터가 선택되어 있지 않습니다.
;
-- 테이블 waio.sedimentation_realtime 구조 내보내기
CREATE TABLE IF NOT EXISTS `sedimentation_realtime` (
  `update_time` datetime NOT NULL,
  `name` varchar(256) NOT NULL DEFAULT '0',
  `value` varchar(256) NOT NULL DEFAULT '0',
  `quality` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`update_time`,`name`),
  KEY `update_time` (`update_time`)
) 
;
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.sensor 구조 내보내기
CREATE TABLE IF NOT EXISTS `sensor` (
  `sensor_index` int(11) NOT NULL AUTO_INCREMENT,
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `pd1_diatom` int(11) NOT NULL,
  `pd2_diatom` int(11) NOT NULL,
  `pd3_diatom` int(11) NOT NULL,
  `pd1_diatom_big` tinyint(4) NOT NULL,
  `pd2_diatom_big` tinyint(4) NOT NULL,
  `pd3_diatom_big` tinyint(4) NOT NULL,
  `pd_diatom_avg` varchar(32) NOT NULL,
  `pd_toc` float NOT NULL,
  `pd_ch` float NOT NULL,
  `sj_al` float NOT NULL,
  `hs_tb` float NOT NULL,
  `hs_ph` float NOT NULL,
  `hs_te` float NOT NULL,
  `hs_cu` float NOT NULL,
  `hs_e1_tb` float NOT NULL,
  `hs_e2_tb` float NOT NULL,
  `hs_f_tb` float NOT NULL,
  `ai_coagulants1_input` float NOT NULL,
  `ai_coagulants2_input` float NOT NULL,
  `ai_coagulants1_type` varchar(32) NOT NULL,
  `ai_coagulants2_type` varchar(32) NOT NULL,
  `scl_coagulants1_input` float NOT NULL,
  `scl_coagulants2_input` float NOT NULL,
  `scl_coagulants1_type` varchar(32) NOT NULL,
  `scl_coagulants2_type` varchar(32) NOT NULL,
  `user_coagulants1_input` float NOT NULL,
  `user_coagulants2_input` float NOT NULL,
  `user_coagulants1_type` varchar(32) NOT NULL,
  `user_coagulants2_type` varchar(32) NOT NULL,
  `real_coagulants1_input` float NOT NULL DEFAULT 0,
  `real_coagulants2_input` float NOT NULL DEFAULT 0,
  `real_coagulants1_type` varchar(32) NOT NULL DEFAULT '0',
  `real_coagulants2_type` varchar(32) NOT NULL DEFAULT '0',
  `operation1_mode` tinyint(4) NOT NULL,
  `operation2_mode` tinyint(4) NOT NULL,
  PRIMARY KEY (`sensor_index`)
) 

-- 내보낼 데이터가 선택되어 있지 않습니다.
;
-- 테이블 waio.system_config 구조 내보내기
CREATE TABLE IF NOT EXISTS `system_config` (
  `scada1_address` varchar(16) NOT NULL,
  `scada1_port` int(11) NOT NULL,
  `daq1_port` int(4) NOT NULL,
  `scada2_address` varchar(16) NOT NULL,
  `scada2_port` int(11) NOT NULL,
  `daq2_port` int(11) NOT NULL,
  `analysis1_address` varchar(16) NOT NULL,
  `analysis1_rm` int(11) NOT NULL,
  `analysis1_nm` int(11) NOT NULL,
  `analysis1_nn` int(11) NOT NULL,
  `analysis2_address` varchar(16) NOT NULL,
  `analysis2_rm` int(11) NOT NULL,
  `analysis2_nm` int(11) NOT NULL,
  `analysis2_nn` int(11) NOT NULL,
  `kafka` varchar(256) NOT NULL
) 
;
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.system_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `system_info` (
  `hostname` varchar(64) NOT NULL,
  `name` varchar(128) DEFAULT NULL,
  `os` varchar(128) NOT NULL,
  `model` varchar(64) NOT NULL,
  `processor_name` varchar(64) NOT NULL,
  `package_count` tinyint(4) NOT NULL,
  `core_count` tinyint(4) NOT NULL,
  `logical_count` tinyint(4) NOT NULL,
  `max_frequency` bigint(20) NOT NULL,
  `total_memory` bigint(20) NOT NULL,
  `available_memory` bigint(20) NOT NULL,
  `db_used` bigint(20) DEFAULT NULL,
  `db_free` bigint(20) DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`hostname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 내보낼 데이터가 선택되어 있지 않습니다.
;
-- 테이블 waio.system_monitoring 구조 내보내기
CREATE TABLE IF NOT EXISTS `system_monitoring` (
  `monitoring_index` int(11) NOT NULL AUTO_INCREMENT,
  `hostname` varchar(64) NOT NULL,
  `type` tinyint(4) NOT NULL,
  `name` varchar(64) NOT NULL,
  `value` varchar(32) NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`monitoring_index`),
  KEY `update_time` (`update_time`)
) 

-- 내보낼 데이터가 선택되어 있지 않습니다.
;
-- 테이블 waio.tag_description 구조 내보내기
CREATE TABLE IF NOT EXISTS `tag_description` (
  `tag_index` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL DEFAULT '0',
  `description` varchar(256) NOT NULL DEFAULT '0',
  `created` date DEFAULT NULL,
  PRIMARY KEY (`tag_index`),
  UNIQUE KEY `name` (`name`)
) 
;
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.tag_manage 구조 내보내기
CREATE TABLE IF NOT EXISTS `tag_manage` (
  `algorithm_code` varchar(4) NOT NULL DEFAULT '',
  `process_code` varchar(4) NOT NULL DEFAULT '',
  `series` tinyint(4) NOT NULL DEFAULT 0,
  `location` tinyint(4) NOT NULL DEFAULT 0,
  `item` varchar(64) NOT NULL DEFAULT '',
  `name` varchar(256) NOT NULL DEFAULT '',
  `display` varchar(256) NOT NULL DEFAULT '',
  `type` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`algorithm_code`,`process_code`,`series`,`location`,`item`,`name`,`type`) USING BTREE
) 
;
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.tag_value 구조 내보내기
CREATE TABLE IF NOT EXISTS `tag_value` (
  `update_time` datetime NOT NULL,
  `value_index` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `value` varchar(256) NOT NULL,
  `quality` tinyint(4) NOT NULL,
  PRIMARY KEY (`update_time`,`value_index`),
  KEY `value_index` (`value_index`)
) 
;
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.tb_ai_diag_moter 구조 내보내기
CREATE TABLE IF NOT EXISTS `tb_ai_diag_moter` (
  `moter_id` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `center_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `acq_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `unbalance_amp` float DEFAULT NULL,
  `unbalance_alarm` tinyint(1) DEFAULT NULL,
  `misalignment_amp` float DEFAULT NULL,
  `misalignment_alarm` tinyint(1) DEFAULT NULL,
  `rotor_amp` float DEFAULT NULL,
  `rotor_alarm` tinyint(1) DEFAULT NULL,
  `de_amp` float DEFAULT NULL,
  `de_bpfo_alarm` tinyint(1) DEFAULT NULL,
  `DE_BPFI_alarm` tinyint(1) DEFAULT NULL,
  `DE_BSF_alarm` tinyint(1) DEFAULT NULL,
  `DE_FTF_alarm` tinyint(1) DEFAULT NULL,
  `NDE_amp` float DEFAULT NULL,
  `NDE_BPFO_alarm` tinyint(1) DEFAULT NULL,
  `NDE_BPFI_alarm` tinyint(1) DEFAULT NULL,
  `NDE_BSF_alarm` tinyint(1) DEFAULT NULL,
  `NDE_FTF_alarm` tinyint(1) DEFAULT NULL,
  `motor_vib_severity_lvl` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DE_rms_amp` float DEFAULT NULL,
  `DE_rms_alarm` tinyint(1) DEFAULT NULL,
  `NDE_rms_amp` float DEFAULT NULL,
  `NDE_rms_alarm` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`moter_id`,`center_id`,`acq_date`),
  KEY `idx_tb_ai_diag_moter_acq_date` (`acq_date`)
) 
;
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.tb_ai_diag_pump 구조 내보내기
CREATE TABLE IF NOT EXISTS `tb_ai_diag_pump` (
  `pump_id` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `center_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `acq_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `impeller_amp` float DEFAULT NULL,
  `impeller_alarm` tinyint(1) DEFAULT NULL,
  `cavitation_amp` float DEFAULT NULL,
  `cavitation_alarm` tinyint(1) DEFAULT NULL,
  `DE_amp` float DEFAULT NULL,
  `DE_bpfo_alarm` tinyint(1) DEFAULT NULL,
  `DE_BPFI_alarm` tinyint(1) DEFAULT NULL,
  `DE_BSF_alarm` tinyint(1) DEFAULT NULL,
  `DE_FTF_alarm` tinyint(1) DEFAULT NULL,
  `NDE_amp` float DEFAULT NULL,
  `NDE_BPFO_alarm` tinyint(1) DEFAULT NULL,
  `NDE_BPFI_alarm` tinyint(1) DEFAULT NULL,
  `NDE_BSF_alarm` tinyint(1) DEFAULT NULL,
  `NDE_FTF_alarm` tinyint(1) DEFAULT NULL,
  `pump_vib_severity_lvl` varchar(2) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DE_rms_amp` float DEFAULT NULL,
  `DE_rms_alarm` tinyint(1) DEFAULT NULL,
  `NDE_rms_amp` float DEFAULT NULL,
  `NDE_rms_alarm` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`pump_id`,`center_id`,`acq_date`),
  KEY `idx_tb_ai_diag_pump_acq_date` (`acq_date`)
) 
;
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.tb_pump_scada 구조 내보내기
CREATE TABLE IF NOT EXISTS `tb_pump_scada` (
  `pump_scada_id` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `center_id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `acq_date` datetime NOT NULL,
  `eq_on` tinyint(1) DEFAULT NULL,
  `frequency` float DEFAULT NULL,
  `flow_rate` float DEFAULT NULL,
  `pressure` float DEFAULT NULL,
  `proc_stat` int(11) DEFAULT NULL,
  `r_temp` float DEFAULT NULL,
  `s_temp` float DEFAULT NULL,
  `t_temp` float DEFAULT NULL,
  `brg_motor_de_temp` float DEFAULT NULL,
  `brg_motor_nde_temp` float DEFAULT NULL,
  `brg_pump_de_temp` float DEFAULT NULL,
  `brg_pump_nde_temp` float DEFAULT NULL,
  `discharge_pressure` float DEFAULT NULL,
  `suction_pressure` float DEFAULT NULL,
  PRIMARY KEY (`pump_scada_id`,`center_id`,`acq_date`),
  KEY `idx_tb_pump_scada_acq_date` (`acq_date`)
)
;
-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 waio.user 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `userid` varchar(32) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(16) NOT NULL,
  `partname` varchar(32) NOT NULL,
  `authority` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`userid`)
) 

-- 내보낼 데이터가 선택되어 있지 않습니다.
;
-- 테이블 waio.water_purification_info 구조 내보내기
CREATE TABLE IF NOT EXISTS `water_purification_info` (
  `code` varchar(10) NOT NULL,
  `shortname` varchar(100) NOT NULL,
  `fullname` varchar(100) NOT NULL,
  PRIMARY KEY (`code`)
) 


