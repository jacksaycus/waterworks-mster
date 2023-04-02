package org.kwater.pms.web;

import static org.kwater.pms.web.common.Message.SUCCESS;

import org.kwater.pms.service.AgglomerateService;
import org.kwater.pms.web.common.DateForm;
import org.kwater.pms.web.common.ResponseDTO;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Api(tags = "응집기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agglomerate")
public class AgglomerateController {

    private final AgglomerateService agglomerateService;

    @GetMapping("/alarm")
    //@ApiOperation("설비별 알람 - 알람이 있는 설비만 검색(위의 agglomerate_01 id 참조), 검색안된 설비는 정상")
    public ResponseEntity<ResponseDTO> alarm() {
        return ResponseEntity.ok(ResponseDTO.ok(SUCCESS.getMessage(), agglomerateService.alram()));
    }

    @GetMapping("/agglomerateInfo/{id}")
    //@ApiOperation("가동중, 운영현황, 상태정보 조회 (0 보다 크면 해당 응집지가 ON 상태)")
    public ResponseEntity<ResponseDTO> agglomerateInfo(@PathVariable String id) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), agglomerateService.agglomerateInfo(id)));
    }

    @GetMapping("/agglomerateCount")
    //@ApiOperation("가동대수")
    public ResponseEntity<ResponseDTO> agglomerateCount() {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), agglomerateService.agglomerateCount()));
    }

    @PostMapping("/currentInfo")
    //@ApiOperation("전류 조회")
    public ResponseEntity<ResponseDTO> currentInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), agglomerateService.currentInfo(dateForm)));
    }

    @PostMapping("/detailInfo")
    //@ApiOperation("상세조회 모든 pac 펌프\n"
//        + "current_limit : 과전류\n"
//        + "voltage_diff : 전압변동량\n"
//        + "current_unbalance : 전류불평형\n"
//        + "voltage_unbalance : 전압불평형")
    public ResponseEntity<ResponseDTO> detailInfo(@RequestBody DateForm dateForm) {
        return ResponseEntity.ok(
            ResponseDTO.ok(SUCCESS.getMessage(), agglomerateService.detailInfo(dateForm)));
    }


}
