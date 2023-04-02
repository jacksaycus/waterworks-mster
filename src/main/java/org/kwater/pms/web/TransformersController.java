package org.kwater.pms.web;

import static org.kwater.pms.web.common.Message.SUCCESS;

import org.kwater.pms.service.TransformersService;
import org.kwater.pms.web.common.DateForm;
import org.kwater.pms.web.common.Message;
import org.kwater.pms.web.common.ResponseDTO;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Api(tags = "변압기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transformers")
public class TransformersController {

    private final TransformersService transformersService;

    //@ApiOperation("설비별 알람")
    @GetMapping("/alarm")
    public ResponseEntity<ResponseDTO> alarm() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), transformersService.alarm()));
    }

    //@ApiOperation("가동중, 운영현황 조회, 상태정보"
     //   + " temp_1 ~ 20 : 온도데이터")
    @GetMapping("/info")
    public ResponseEntity<ResponseDTO> info() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), transformersService.info()));
    }

    //@ApiOperation("가동대수")
    @GetMapping("/count")
    public ResponseEntity<ResponseDTO> count() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), transformersService.count()));
    }

    //@ApiOperation("부분 방전 크기"
     //   + " dbm_avg: 부분방전 크기")
    @PostMapping("/disCharge")
    public ResponseEntity<ResponseDTO> disCharge(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), transformersService.disCharge(dateForm)));
    }

    //@ApiOperation("상세 조회"
//        + "dbm : 부분방전 크기"
//        + " pps : 부분방전 펄스"
//        + " max_in : 내부온도 크기"
//        + " max_out : 외부온도 크기")
    @PostMapping("/detailInfo")
    public ResponseEntity<ResponseDTO> detailInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), transformersService.detailInfo(dateForm)));
    }


}
