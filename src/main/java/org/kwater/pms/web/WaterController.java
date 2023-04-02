package org.kwater.pms.web;

import static org.kwater.pms.web.common.Message.SUCCESS;

import org.kwater.pms.service.WaterControlService;
import org.kwater.pms.web.common.RequestForm;
import org.kwater.pms.web.common.ResponseDTO;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//@Api(tags = "착수정 유입밸브 API")
@RequestMapping("/api/v1/waterControl")
public class WaterController {

    private final WaterControlService waterControlService;

    @GetMapping("/alarm")
    //@ApiOperation(value = "알람")
    public ResponseEntity<ResponseDTO> alarm() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS
            .getMessage(), waterControlService.alarm()));
    }

    @GetMapping("/eqOpen")
    //@ApiOperation(value = "개폐여부"
//        + "개폐여부 개도 : eq_open\n"
//        + "가동여부 : 0이상이면 가동\n"
//        + "상태정보 개도 : eq_open")
    public ResponseEntity<ResponseDTO> eqOpen() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), waterControlService.eqOpen()));
    }

    @PostMapping("/currentGraph")
    //@ApiOperation(value = "전류 그래프")
    public ResponseEntity<ResponseDTO> currentGraph(@RequestBody RequestForm requestForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), waterControlService.currentGraph(requestForm)));
    }

    @GetMapping("/statusInfo")
    //@ApiOperation(value = "상태정보 둘중에 하나라도 1이면 이상상태")
    public ResponseEntity<ResponseDTO> statusInfo() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), waterControlService.statusInfo()));
    }

    @PostMapping("/overCurrentGraph")
    //@ApiOperation(value = "과전류 그래프 기간조회")
    public ResponseEntity<ResponseDTO> overCurrentGraph(@RequestBody RequestForm requestForm) {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(),
            waterControlService.overCurrentGraph(requestForm)));
    }

    @PostMapping("/voltageFluctuationGraph")
    //@ApiOperation(value = "전압변동량 그래프 기간조회")
    public ResponseEntity<ResponseDTO> voltageFluctuationGraph(
        @RequestBody RequestForm requestForm) {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(),
            waterControlService.voltageFluctuationGraph(requestForm)));
    }

    @GetMapping("/currentAndOpen")
    //@ApiOperation(value = "전류와 개도 그래프")
    public ResponseEntity<ResponseDTO> currentAndOpen() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), waterControlService.currentAndOpen()));
    }

    @GetMapping("/flow")
    //@ApiOperation("유량 순시,적시, 압력"
//        + " flow_instantaneous : 유량 순시"
//        + " flow_intergration : 유량 적시"
//        + " flow_press : 유량 압력")
    public ResponseEntity<ResponseDTO> flow() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), waterControlService.flow()));
    }
}
