package org.kwater.pms.web;

import org.kwater.pms.service.MainService;
import org.kwater.pms.web.common.Message;
import org.kwater.pms.web.common.ResponseDTO;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Api(tags = "메인 화면 API")
@RestController
@RequestMapping("/api/v1/main")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping("/waterPacAll")
    //@ApiOperation("이상감지 전류데이터(송수쪽은 부분방전크기) --- 착수/약품")
    public ResponseEntity<ResponseDTO> waterPacAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.waterPacAll()));
    }

    @GetMapping("/rapidAggloSludgeAll")
    //@ApiOperation("혼화/응집/침전")
    public ResponseEntity<ResponseDTO> rapidAggloSludgeAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.rapidAggloSludgeAll()));
    }

    @GetMapping("/filterGacAll")
    //@ApiOperation("여과/GAC")
    public ResponseEntity<ResponseDTO> filterGacAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.filterGacAll()));
    }

    @GetMapping("motifCoolAll")
   // @ApiOperation("(오존/소독) 오존발생기, 모티브펌프, 냉각수펌프, 차염발생기")
    public ResponseEntity<ResponseDTO> motifCoolAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.motifCoolAll()));
    }

    @GetMapping("pumpTransVcbAll")
   // @ApiOperation("(송수) 기동반, 변압기반, VCB 반")
    public ResponseEntity<ResponseDTO> pumpTransformerVcbAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.pumpTransformerVcbAll()));
    }

    @GetMapping("waterAlarmAll")
   // @ApiOperation("(착수/약품) 유입밸브, PAC, PAHCS, 급수펌프 === 설비별 알람")
    public ResponseEntity<ResponseDTO> waterAlarmAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.waterAlarmAll()));
    }

    @GetMapping("rapidAggloSludgeAlarmAll")
   // @ApiOperation("혼화/응집/침전 알람")
    public ResponseEntity<ResponseDTO> rapidAggloSludgeAlarmAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.rapidAggloSludgeAlarmAll()));
    }

    @GetMapping("filterAlarmAll")
    //@ApiOperation("여과/GAC 알람")
    public ResponseEntity<ResponseDTO> filterAlarmAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.filterAlarmAll()));
    }

    @GetMapping("ozoneAlarmAll")
    //@ApiOperation("(오존/소독) 오존발생기, 모티브펌프, 냉각수펌프, 차염발생기 알람")
    public ResponseEntity<ResponseDTO> ozoneAlarmAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.ozoneAlarmAll()));
    }

    @GetMapping("pumpAlarmAll")
   // @ApiOperation("(송수) 기동반, 변압기반, VCB 반 알람")
    public ResponseEntity<ResponseDTO> pumpAlarmAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.pumpAlarmAll()));
    }

    @GetMapping("motorDataAll")
   // @ApiOperation("자율진단 송수펌프모터 펌프 부하, 반부하, 모터의 부하, 반부하 총진동값")
    public ResponseEntity<ResponseDTO> motorDataAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.motorDataAll()));
    }

    @GetMapping("pumpBearingAll")
    //@ApiOperation("송수펌프모터 베어링 온도")
    public ResponseEntity<ResponseDTO> pumpBearingTempAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.pumpBearingTempAll()));
    }

    @GetMapping("motorAlarm")
    //@ApiOperation("송수펌프모터 알람")
    public ResponseEntity<ResponseDTO> motorAlarm() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.motorAlarm()));
    }

    @GetMapping("alarmDataAll")
    //@ApiOperation("모든 설비 알람")
    public ResponseEntity<ResponseDTO> alarmDataAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.alarmDataAll()));
    }

    @GetMapping("operationAll")
   // @ApiOperation("모든 설비 가동중 조회")
    public ResponseEntity<ResponseDTO> operationAll() {
        return ResponseEntity.ok(ResponseDTO.ok(Message.SUCCESS.getMessage(), mainService.operationAll()));
    }

}
