package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// process_code
public class ProcessCodeDTO
{
    private Integer code_index;
    private String code;        // 코드명
    private String english;     // 영문명
    private String korean;      // 국문명
    private String is_use;      // 사용 여부(0:미사용, 1:사용)
}
