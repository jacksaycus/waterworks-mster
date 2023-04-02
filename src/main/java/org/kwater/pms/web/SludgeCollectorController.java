package org.kwater.pms.web;

import static org.kwater.pms.web.common.Message.SUCCESS;

import org.kwater.pms.service.SludgeCollectorService;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sludge")
//@Api(tags = "슬러지 수집기 API")
public class SludgeCollectorController {

    private final SludgeCollectorService sludgeCollectorService;

    @GetMapping("/alarm")
    //@ApiOperation("설비별 알람"
//        + "알람이 있는 설비만 검색, 검색안된 설비는 정상")
    public ResponseEntity<ResponseDTO> alarm() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), sludgeCollectorService.alarm()));
    }

    @GetMapping("/sludgeInfo")
    //@ApiOperation("가동중, 운영현황 조회, 상태정보")
    public ResponseEntity<ResponseDTO> sludgeInfo() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), sludgeCollectorService.sludgeInfo()));
    }

    @GetMapping("/count")
    //@ApiOperation("가동대수")
    public ResponseEntity<ResponseDTO> count() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), sludgeCollectorService.count()));
    }

    @PostMapping("/currentInfo")
    //@ApiOperation("전류 조회")
    public ResponseEntity<ResponseDTO> currentInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), sludgeCollectorService.currentInfo(dateForm)));
    }

    @PostMapping("/detailInfo")
    //@ApiOperation("상세 조회"
//        + "current_limit : 과전류"
//        + " voltage_diff : 전압변동량"
//        + " current_unbalance : 전류불평형"
//        + " voltage_unbalance : 전압불평형")
    public ResponseEntity<ResponseDTO> detailInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), sludgeCollectorService.detailInfo(dateForm)));
    }

    @GetMapping("/torqueInfo")
    //@ApiOperation("상세 조회"
//        + " torque_1: 1계열에서 1번, 2계열에서는 5번"
//        + " torque_2: 1계열에서 2번, 2계열에서는 6번"
//        + " torque_3: 1계열에서 3번, 2계열에서는 7번"
//        + " torque_4: 1계열에서 4번, 2계열에서는 8번")
    public ResponseEntity<ResponseDTO> torqueInfo() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), sludgeCollectorService.torqueInfo()));
    }

}
