package org.kwater.pms.web;

import static org.kwater.pms.web.common.Message.SUCCESS;

import org.kwater.pms.service.FilterBackWashService;
import org.kwater.pms.web.common.DateForm;
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

//@Api(tags = "여과 역세 펌프 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/filterBackWash")
public class FilterBackWashController {

    private final FilterBackWashService filterBackWashService;

    @GetMapping("/alarm")
    //@ApiOperation("설비별 알람"
//        + "알람이 있는 설비만 검색(위의 auto_water_pump_01 id 참조), 검색안된 설비는 정상")
    public ResponseEntity<ResponseDTO> alarm() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), filterBackWashService.alarm()));
    }

    @GetMapping("/info")
    //@ApiOperation("가동중, 운영현황 조회, 상태정보"
//        + " flow_rate : 유량"
//        + " pressure : 압력")
    public ResponseEntity<ResponseDTO> filterBackWashInfo() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), filterBackWashService.filterBackWashInfo()));
    }

    @PostMapping("/distribution")
    //@ApiOperation("분포도")
    public ResponseEntity<ResponseDTO> distribution(@RequestBody RequestForm requestForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), filterBackWashService.distribution(requestForm)));
    }

    @GetMapping("/count")
    //@ApiOperation("가동대수")
    public ResponseEntity<ResponseDTO> count() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), filterBackWashService.count()));
    }

    @PostMapping("/currentInfo")
    //@ApiOperation("전류 조회")
    public ResponseEntity<ResponseDTO> currentInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), filterBackWashService.currentInfo(dateForm)));
    }

    @PostMapping("/detailInfo")
    //@ApiOperation("상세 조회 모든 pac pump"
//        + " current_limit : 과전류"
//        + " voltage_diff : 전압변동량"
//        + " current_unbalance : 전류불평형"
//        + " voltage_unbalance : 전압불평형")
    public ResponseEntity<ResponseDTO> detailInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), filterBackWashService.detailInfo(dateForm)));
    }

}
