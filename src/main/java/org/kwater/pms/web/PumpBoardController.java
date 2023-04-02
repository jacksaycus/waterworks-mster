package org.kwater.pms.web;

import static org.kwater.pms.web.common.Message.SUCCESS;

import org.kwater.pms.service.PumpBoardService;
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

//@Api(tags = "펌프 기동반 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pumpBoard")
public class PumpBoardController {

    private final PumpBoardService pumpBoardService;

    @GetMapping("/alarm")
    //@ApiOperation("알람이 있는 설비만 검색, 검색안된 설비는 정상")
    public ResponseEntity<ResponseDTO> alarm() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), pumpBoardService.alarm()));
    }

    @GetMapping("/count")
    //@ApiOperation("펌프기동반 가동대수")
    public ResponseEntity<ResponseDTO> count() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), pumpBoardService.count()));
    }

    @GetMapping("/disCharge")
    //@ApiOperation("부분방전 크기 조회"
//        + "dbm_avg : 부분방전 크기")
    public ResponseEntity<ResponseDTO> disCharge() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), pumpBoardService.disCharge()));
    }

    @GetMapping("/info")
    //@ApiOperation("상태정보 온도")
    public ResponseEntity<ResponseDTO> info() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), pumpBoardService.info()));
    }

    @PostMapping("/detailInfo")
    //@ApiOperation("상세 조회 모든 펌프기동반"
//        + " dbm : 부분방전 크기"
//        + " pps : 부분방전 펄스"
//        + " max_in : 내부온도 크기"
//        + " max_out : 외부온도 크기")
    public ResponseEntity<ResponseDTO> detailInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), pumpBoardService.detailInfo(dateForm)));
    }


}
