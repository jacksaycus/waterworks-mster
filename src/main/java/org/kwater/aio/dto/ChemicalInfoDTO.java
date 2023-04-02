package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChemicalInfoDTO
{
    private String code;
    private String shortname;
    private String fullname;
}
