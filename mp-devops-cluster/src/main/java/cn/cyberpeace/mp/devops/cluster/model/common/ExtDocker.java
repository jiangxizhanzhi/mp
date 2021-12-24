package cn.cyberpeace.mp.devops.cluster.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtDocker{

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    private String file;

    /**
     * 启动参数
     */
    @ApiModelProperty(value = "启动参数",hidden = true)
    private String settings;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer syncStatus;

    /**
     * 临时文件ID
     */
    @ApiModelProperty("临时文件ID")
    private String tmpdirUuid;


    @ApiModelProperty("用户文件名称")
    private String userFilename;


    @ApiModelProperty("实例ID")
    private String instanceUuid;

    @ApiModelProperty("实例状态")
    private Integer instanceState;

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

    @ApiModelProperty("快照数量")
    private Integer snapshotCount;

    /**
     * 额外信息
     */
    @ApiModelProperty("扩展信息")
    private Object extInfo;


    /**
     * 接入方式
     */
    @ApiModelProperty("接入方式")
    private List<AccessProtocol> accessProtocols;
}
