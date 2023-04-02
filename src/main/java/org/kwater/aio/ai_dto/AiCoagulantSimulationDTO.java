package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_coagulant_simulation
// 약품 공정 시뮬레이션 테이블
public class AiCoagulantSimulationDTO
{
    private Integer simulation_index;   // 모의 순번
    private Date reg_time;              // 모의 등록 시각
    private Date complete_time;         // 모의 완료 시각
    private Integer state;              // 모의 요청 상태 (0:요청, 1:분석중, 2:완료, 3:에러)
    private String ai_c1_cf_coagulant;  // 1계열 약품 종류
    private Float ai_c1_cf;             // 1계열 약품 주입률
    private String ai_c2_cf_coagulant;  // 2계열 약품 종류
    private Float ai_c2_cf;             // 2계열 약품 주입률
    private Float b_tb;                 // 원수 탁도
    private Float b_ph;                 // 원수 pH
    private Float b_te;                 // 원수 수온
    private Float b_cu;                 // 원수 전기전도도
    private Float b_in_fr;              // 원수 유입 유량
    private Float e1_tb;                // 1계열 침전지 탁도
    private Float e2_tb;                // 2계열 침전지 탁도
}
