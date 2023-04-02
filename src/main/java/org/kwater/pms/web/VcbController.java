package org.kwater.pms.web;

import static org.kwater.pms.web.common.Message.SUCCESS;

import org.kwater.pms.service.VcbService;
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

@RestController
//@Api(tags = "VCB 조회 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/vcb")
public class VcbController {

    private final VcbService vcbService;

    @GetMapping("/alarm")
    //@ApiOperation("설비별 알람")
    public ResponseEntity<ResponseDTO> alarm() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), vcbService.alarm()));
    }

    @GetMapping("/info")
    //@ApiOperation("가동중, 운영현황 조회, 상태정보"
     //   + "temp_1 ~ 20 : 온도데이터")
    public ResponseEntity<ResponseDTO> info() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), vcbService.info()));
    }

    @GetMapping("/count")
    //@ApiOperation("가동대수")
    public ResponseEntity<ResponseDTO> count() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), vcbService.count()));
    }

    @PostMapping("/discharge")
    //@ApiOperation("부분방전 크기"
    //    + "dbm_avg 부분방전 크기")
    public ResponseEntity<ResponseDTO> partDischarge(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), vcbService.partDischarge(dateForm)));
    }

    @PostMapping("/detailInfo")
    //@ApiOperation("상세 조회 각 설비당"
//        + " dbm : 부분방전 크기"
//        + " pps : 부분방전 펄스"
//        + " max_in : 내부온도 크기"
//        + " max_out : 외부온도 크기")
    public ResponseEntity<ResponseDTO> detailInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), vcbService.detailInfo(dateForm)));
    }

}
