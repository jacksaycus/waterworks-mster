package org.kwater.aio.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AlgorithmHealthStatus
{
    private int receiving;
    private int coagulant;
    private int mixing;
    private int sedimentation;
    private int filter;
    private int gac;
    private int disinfection;
    private int ozone;
}
