package org.kwater.pms.web;

import org.kwater.pms.service.PahcsTubeService;
import org.kwater.pms.web.common.DateForm;
import org.kwater.pms.web.common.Message;
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

//@Api(tags = "PAHCS튜브 펌프 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pahcsTube")
public class PahcsController {

    private final PahcsTubeService pahcsTubeService;

    @GetMapping("/alarm")
    //@ApiOperation("설비별 알람 - 알람이 있는 설비만 검색, 검색안된 설비는 정상")
    public ResponseEntity<ResponseDTO> alarm() {
        return ResponseEntity.ok(
            ResponseDTO.ok(Message.SUCCESS.getMessage(), pahcsTubeService.alarm()));
    }

    @GetMapping("/pahcsTubeInfo")
    //@ApiOperation("가동중, 운영현황 조회, 상태정보")
    public ResponseEntity<ResponseDTO> pahcsTubeInfo() {
        return ResponseEntity.ok(
            ResponseDTO.ok(Message.SUCCESS.getMessage(), pahcsTubeService.pahcsTubeInfo()));
    }

    @PostMapping("/currentInfo")
    //@ApiOperation("전류 조회")
    public ResponseEntity<ResponseDTO> currentInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(Message.SUCCESS.getMessage(), pahcsTubeService.currentInfo(dateForm)));
    }

    @PostMapping("/detailInfo")
    //@ApiOperation("상세 조회"
//        + "current : 과전류"
//        + " diff_voltage : 전압변동량"
//        + " pac_ch_single_i : 주입량 고려 전류")
    public ResponseEntity<ResponseDTO> detailInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(Message.SUCCESS.getMessage(), pahcsTubeService.detailInfo(dateForm)));
    }
}
