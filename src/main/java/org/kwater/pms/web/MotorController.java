package org.kwater.pms.web;

import static org.kwater.pms.web.common.Message.SUCCESS;

import org.kwater.pms.service.MotorService;
import org.kwater.pms.web.common.DateForm;
import org.kwater.pms.web.common.RequestForm;
import org.kwater.pms.web.common.ResponseDTO;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
//@Api(tags = "송수 펌프 모터 API")
@RequestMapping("/api/v1/motor")
public class MotorController {

    private final MotorService motorService;

    @GetMapping("/alarm")
    //@ApiOperation("송수펌프모터 설비별 알람"
      //  + "알람이 있는 설비만 검색(위의 motor id 참조), 검색안된 설비는 정상")
    public ResponseEntity<ResponseDTO> alarm() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), motorService.alarm()));
    }

    @GetMapping("/runningInfo")
    //@ApiOperation("가동중 조회")
    public ResponseEntity<ResponseDTO> runningInfo() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), motorService.runningInfo()));
    }

    @GetMapping("/count")
    //@ApiOperation("가동대수")
    public ResponseEntity<ResponseDTO> count() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), motorService.count()));
    }

    @PostMapping("/distribution")
    //@ApiOperation("분포도 scadaId 필요 flow_rate : 토출"
     //   + " pressure : 흡입")
    public ResponseEntity<ResponseDTO> distribution(@RequestBody RequestForm requestForm) {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), motorService.distribution(requestForm)));
    }

    @PostMapping("/vibrationGraph")
    //@ApiOperation("총진동량 조회 모든 모터 한번에 조회 - 그래프"
//        + " motor_de_amp : 모터 부하"
//        + " motor_nde_amp : 모터 반부하"
//        + " pump_de_amp : 펌프 부하"
//        + " pump_nde_amp : 펌프 반부하")
    public ResponseEntity<ResponseDTO> totalVibrationAll(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), motorService.vibrationGraph(dateForm)));
    }

    @GetMapping("/vibrationValues")
    //@ApiOperation("총진동량 조회 모든 모터 한번에 조회 - 값"
//        + " motor_de_amp : 모터 부하"
//        + " motor_nde_amp : 모터 반부하"
//        + " pump_de_amp : 펌프 부하"
//        + " pump_nde_amp : 펌프 반부하")
    public ResponseEntity<ResponseDTO> vibrationValues() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), motorService.vibrationValues()));
    }

    @GetMapping("/vibrationOne")
    //@ApiOperation("총진동량 조회 모터 한개씩 조회")
    public ResponseEntity<ResponseDTO> totalVibrationOne(@RequestBody RequestForm requestForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), motorService.vibrationFindById(requestForm)));
    }

    @PostMapping("/vibrationFindById")
    //@ApiOperation("총진동량 조회(기간조회) 한개씩 조회")
    public ResponseEntity<ResponseDTO> totalVibrationFindById(
        @RequestBody RequestForm requestForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), motorService.vibrationFindById(requestForm)));
    }

    @GetMapping("/alarmDetails")
    //@ApiOperation("상세화면 알람 (상태정보는 조회했을 시 조회가 되면 이상이라는 뜻으로 화면에 표시해줘야함)"
//        + "m.unbalance_alarm : 모터 질량불평형 여부"
//        + " m.misalignment_alarm : 모터 축정렬불량 여부"
//        + " m.rotor_alarm : 모터 로터 고장 여부"
//        + " m.de_bpfo_alarm : 모터 드라이브 베어링 외륜"
//        + " m.DE_BPFI_alarm : 모터 드라이브 베어링 내륜"
//        + " m.DE_BSF_alarm : 모터 드라이브 베어링 볼"
//        + " m.DE_FTF_alarm : 모터 드라이브 베어링 트레인"
//        + " m.NDE_BPFO_alarm : 모터 논 드라이브 베어링 외륜"
//        + " m.NDE_BPFI_alarm : 모터 논 드라이브 베어링 내륜"
//        + " m.NDE_BSF_alarm : 모터 논 드라이브 베어링 볼"
//        + " m.NDE_FTF_alarm : 모터 논 드라이브 베어링 트레인"
//        + " p.impeller_alarm : 펌프 임펠러"
//        + " p.cavitation_alarm : 펌프 cavitation"
//        + " p.de_bpfo_alarm : 펌프 드라이브 베어링 외륜"
//        + " p.DE_BPFI_alarm : 펌프 드라이브 베어링 내륜"
//        + " p.DE_BSF_alarm : 펌프 드라이브 베어링 볼"
//        + " p.DE_FTF_alarm : 펌프 드라이브 베어링 트레인"
//        + " p.NDE_BPFO_alarm : 펌프 논 드라이브 베어링 외륜"
//        + " p.NDE_BPFI_alarm : 펌프 논 드라이브 베어링 내륜"
//        + " p.NDE_BSF_alarm : 펌프 논 드라이브 베어링 볼"
//        + " p.NDE_FTF_alarm : 펌프 논 드라이브 베어링 트레인")
    public ResponseEntity<ResponseDTO> alarmDetails() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), motorService.alarmDetails()));
    }

    @GetMapping("/scadaInfo")
    //@ApiOperation("상태정보 - 토출, 흡입압력"
//        + "discharge_pressure : 토출\n"
//        + "suction_pressure : 흡입")
    public ResponseEntity<ResponseDTO> scadaInfo() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), motorService.scadaInfo()));
    }

    @GetMapping("/flowPressure/{id}")
    //@ApiOperation("상태정보 - 유량, 압력"
//        + " pump_scada_01 : 평택"
//        + " flow_rate : 토출"
//        + " pressure : 흡입")
    public ResponseEntity<ResponseDTO> flowPressure(@PathVariable String id) {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), motorService.flowPressure(id)));
    }

    @PostMapping("/motorDetails")
    //@ApiOperation("모터 부하 총진동량, 모터 반부하 총진동량 ~ 펌프 반부하 총진동량"
//        + "m.DE_rms_amp : 모터 부하 총진동량"
//        + " m.NDE_rms_amp : 모터 반 부하 총진동량"
//        + " m.misalignment_amp : 모터 축정렬"
//        + " m.unbalance_amp : 모터 불평형"
//        + " m.rotor_amp : 모터 회전자"
//        + " m.de_amp : 모터 부하 베어링"
//        + " m.NDE_amp : 모터 반부하 베어링"
//        + " p.DE_rms_amp : 펌프 부하 총진동량"
//        + " p.NDE_rms_amp : 펌프 반 부하 총진동량"
//        + " p.cavitation_amp : 펌프 케비테이션"
//        + " p.impeller_amp : 펌프 임펠러"
//        + " p.DE_amp : 펌프 부하 베어링"
//        + " p.NDE_amp : 펌프 반부하 베어링")
    public ResponseEntity<ResponseDTO> motorDetails(@RequestBody RequestForm requestForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), motorService.motorDetails(requestForm)));
    }

    @PostMapping("/bearingTempInfo")
    //@ApiOperation("모터 베어링 온도 scadaId 필요")
    public ResponseEntity<ResponseDTO> bearingTempInfo(@RequestBody RequestForm requestForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), motorService.bearingTempInfo(requestForm)));
    }

    @PostMapping("/windingTempInfo")
    //@ApiOperation("모터 권선 온도 scadaId 필요")
    public ResponseEntity<ResponseDTO> windingTempInfo(@RequestBody RequestForm requestForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), motorService.windingTempInfo(requestForm)));
    }
}
