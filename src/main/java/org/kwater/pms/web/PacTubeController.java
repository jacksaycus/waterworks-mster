package org.kwater.pms.web;

import static org.kwater.pms.web.common.Message.SUCCESS;

import org.kwater.pms.service.PacTubeService;
import org.kwater.pms.web.common.DateForm;
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

//@Api(tags = "PAC 튜브 펌프 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pacTube")
public class PacTubeController {

    private final PacTubeService pacTubeService;

    @GetMapping("/alarm")
    //@ApiOperation("설비별 알람 - 알람이 있는 설비만 검색, 검색안된 설비는 정상")
    public ResponseEntity<ResponseDTO> alarm() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), pacTubeService.alarm()));
    }

    @GetMapping("/pacTubeInfo")
    //@ApiOperation("가동중, 운영현황 조회, 상태정보")
    public ResponseEntity<ResponseDTO> pacTubeInfo() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), pacTubeService.pacTubeInfo()));
    }

    @PostMapping("/currentInfo")
    //@ApiOperation("전류 조회")
    public ResponseEntity<ResponseDTO> currentInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), pacTubeService.currentInfo(dateForm)));
    }

    @PostMapping("/detailInfo")
    //@ApiOperation("상세 조회"
//        + "current : 과전류"
//        + " diff_voltage : 전압변동량"
//        + " pac_ch_single_i : 주입량 고려 전류 ")
    public ResponseEntity<ResponseDTO> detailInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), pacTubeService.detailInfo(dateForm)));
    }

}
