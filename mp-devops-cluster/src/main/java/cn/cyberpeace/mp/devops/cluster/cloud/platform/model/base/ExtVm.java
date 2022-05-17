package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 扩展虚拟机信息
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("虚拟机信息")
public class ExtVm implements Serializable {

    @ApiModelProperty("flavor信息")
    private String flavor;

    /**
     * 支持初始化
     */
    @ApiModelProperty("支持初始化")
    private Boolean initSupport;

    /**
     * 支持快速创建
     */
    @ApiModelProperty("支持快速创建")
    private Boolean quickCreateSupport;

    /**
     * 接入方式
     */
    @ApiModelProperty("接入方式")
    private List<AccessProtocol> accessProtocols;

    /**
     * 额外信息
     */
    @ApiModelProperty("扩展信息")
    private Object extInfo;

    /**
     * 实例是否存在
     */
    @ApiModelProperty("实例是否存在")
    private Boolean instanceExist;

    @ApiModelProperty("快照数量")
    private Integer snapshotCount;

    @ApiModelProperty("实例ID")
    private String instanceUuid;

    @ApiModelProperty("实例状态")
    private Integer instanceState;

    @ApiModelProperty("CPU核数")
    private Integer cpu;

    @ApiModelProperty("内存大小")
    private Integer ram;

    @ApiModelProperty("磁盘大小")
    private Integer disk;
}
