package org.kwater.pms.web;

import static org.kwater.pms.web.common.Message.SUCCESS;

import org.kwater.pms.service.CoolingPumpService;
import org.kwater.pms.web.common.DateForm;
import org.kwater.pms.web.common.ResponseDTO;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
//@Api(tags = "냉각수 펌프 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coolingPump")
public class CoolingPumpController {

    private final CoolingPumpService coolingPumpService;

    @GetMapping("/alarm")
    //@ApiOperation("설비별 알람"
     //   + " 알람이 있는 설비만 검색, 검색안된 설비는 정상")
    public ResponseEntity<ResponseDTO> alarm() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), coolingPumpService.alarm()));
    }

    @GetMapping("/coolingPumpInfo")
    //@ApiOperation("가동중, 운영현황 조회, 상태정보"
//        + " outflow_temp : 냉각수 온도"
//        + " flowmeter : 냉각수 유량")
    public ResponseEntity<ResponseDTO> coolingPumpInfo() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), coolingPumpService.coolingPumpInfo()));
    }

    @GetMapping("/count")
    //@ApiOperation("가동대수")
    public ResponseEntity<ResponseDTO> coolingCount() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), coolingPumpService.coolingCount()));
    }

    @PostMapping("/currentInfo")
    //@ApiOperation("전류 조회")
    public ResponseEntity<ResponseDTO> currentInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), coolingPumpService.currentInfo(dateForm)));
    }

    @PostMapping("/detailInfo")
    //@ApiOperation("상세 조회 모든 pac pump"
//        + " current_limit : 과전류"
//        + " voltage_diff : 전압변동량"
//        + " current_unbalance : 전류불평형"
//        + " voltage_unbalance : 전압불평형")
    public ResponseEntity<ResponseDTO> detailInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), coolingPumpService.detailInfo(dateForm)));
    }

}
