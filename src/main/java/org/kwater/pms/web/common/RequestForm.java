package org.kwater.pms.web.common;

//import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestForm {

    //@ApiModelProperty(example = "상단 정보의 ID")
    private String id;

   // @ApiModelProperty(example = "2021-10-22 00:00:00")
    private String startDate;

   // @ApiModelProperty(example = "2021-10-23 00:00:00")
    private String endDate;

}
