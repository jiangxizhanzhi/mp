package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vuln extends BaseModel {
    private String resourceId;

    // 漏洞编号
    private String number;

    // 漏洞名称
    private String name;

    // cvss评分
    private Double cvss;

    // 漏洞类型 0 无， 1 低， 2 中， 3 高， 4 严重
    private Integer type;

    // 适用平台
    private String application;
}
