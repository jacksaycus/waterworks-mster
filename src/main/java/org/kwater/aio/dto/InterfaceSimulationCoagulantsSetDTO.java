package org.kwater.aio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InterfaceSimulationCoagulantsSetDTO
{
    @JsonProperty("ai_coagulants1_input")
    private float ai_coagulants1_input;

    @JsonProperty("ai_coagulants1_type")
    private String ai_coagulants1_type;

    @JsonProperty("ai_coagulants2_input")
    private float ai_coagulants2_input;

    @JsonProperty("ai_coagulants2_type")
    private String ai_coagulants2_type;
}
