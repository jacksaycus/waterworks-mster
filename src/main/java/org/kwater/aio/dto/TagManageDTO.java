package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 태그명
public class TagManageDTO
{
    private String algorithm_code;  // 알고리즘 공정 코드
    private String process_code;    // 공정 코드
    private Integer series;         // 계열
    private Integer location;       // 지
    private String item;            // 항목명
    private String name;            // 태그명
    private String display;         // 표시명
    private Integer type;           // 종류(0:실시간 태그, 1:알고리즘 태그, 2:시각화 태그, 3:내부 업데이트 태그)
}
