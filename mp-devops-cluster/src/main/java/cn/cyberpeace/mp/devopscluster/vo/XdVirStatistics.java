package cn.cyberpeace.mp.devopscluster.vo;

import lombok.Data;

@Data
public class XdVirStatistics {

    private String companyName;
    private Integer totalNum = 0;
    private Integer upNum = 0;
    private Integer downNum = 0;
}
