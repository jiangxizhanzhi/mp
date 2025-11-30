package cn.cyberpeace.mp.devopscluster.vo.xd.one;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Phy {

    /**
     * 名称
     */
    private String nodename;
    /**
     * 系统名称
     */
    private String sysname;
    /**
     * 地址
     */
    private String instance;
    private String cpuPercent;
    private String usedMemory;
    private String totalMemory;

}
