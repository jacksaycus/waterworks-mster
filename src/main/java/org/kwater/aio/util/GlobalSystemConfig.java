package org.kwater.aio.util;

import jakarta.annotation.PostConstruct;
import org.kwater.aio.dto.SystemConfigDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

//import javax.annotation.PostConstruct;

@Component
@Getter
@Setter
@ToString
public class GlobalSystemConfig
{
    private SystemConfigDTO systemConfig;

    @PostConstruct
    public void init()
    {
        systemConfig = new SystemConfigDTO();
    }

    public String getScada1_information()
    {
        return systemConfig.getScada1_address() + ":" + systemConfig.getScada1_port();
    }

    public String getScada1_daq()
    {
        return systemConfig.getScada1_address() + ":" + systemConfig.getDaq1_port();
    }

    public String getScada2_information()
    {
        return systemConfig.getScada2_address() + ":" + systemConfig.getScada2_port();
    }

    public String getScada2_daq()
    {
        return systemConfig.getScada2_address() + ":" + systemConfig.getDaq2_port();
    }

    public String getAnalysis1_ResourceManager()
    {
        return systemConfig.getAnalysis1_address() + ":" + systemConfig.getAnalysis1_rm();
    }

    public String getAnalysis1_NodeManager()
    {
        return systemConfig.getAnalysis1_address() + ":" + systemConfig.getAnalysis1_nm();
    }

    public String getAnalysis1_NameNode()
    {
        return systemConfig.getAnalysis1_address() + ":" + systemConfig.getAnalysis1_nn();
    }

    public String getAnalysis2_ResourceManager()
    {
        return systemConfig.getAnalysis2_address() + ":" + systemConfig.getAnalysis2_rm();
    }

    public String getAnalysis2_NodeManager()
    {
        return systemConfig.getAnalysis2_address() + ":" + systemConfig.getAnalysis2_nm();
    }

    public String getAnalysis2_NameNode()
    {
        return systemConfig.getAnalysis2_address() + ":" + systemConfig.getAnalysis2_nn();
    }
}
