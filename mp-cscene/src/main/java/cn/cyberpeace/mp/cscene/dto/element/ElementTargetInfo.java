package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.dto.element
 * @Author: hhuang
 * @CreateTime: 2025/02/25  16:35
 * @Description: 节点信息
 * @Version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElementTargetInfo {

    @ApiModelProperty(value = "节点ip")
    private String ip;

    @ApiModelProperty(value = "节点名称")
    private String name;

    @ApiModelProperty(value = "所属区域")
    private String area;

    @ApiModelProperty(value = "操作系统")
    private String systemTypeName;

    @ApiModelProperty(value = "设备型号")
    private String device;

    @ApiModelProperty(value = "节点状态，危险等级,0:正常；1：高；2：中；3：低；4：低")
    private Integer hostStatus;

    @ApiModelProperty(value = "节点漏洞信息列表")
    private List<ElementVulnerabilityInfo> vulnerabilityInfos;
}
