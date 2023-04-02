package org.kwater.aio.vo;

import org.kwater.aio.vo.NameNodeJMX;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class HadoopJmxBeans
{
    List<NameNodeJMX> beans;
}
