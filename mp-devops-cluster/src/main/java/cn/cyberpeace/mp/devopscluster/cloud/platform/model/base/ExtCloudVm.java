package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

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
public class ExtCloudVm implements Serializable {

    /**
     * flavor类型
     */
    private String flavor;

    /**
     * 支持初始化
     */
    private Boolean initSupport;

    /**
     * 支持快速创建
     */
    private Boolean quickCreateSupport;

    /**
     * 接入方式
     */
    private List<AccessProtocol> accessProtocols;

    /**
     * 快速构建
     */
    private List<QuickCreateParam> quickCreateParams;

    /**
     * 额外信息
     */
    private Object extInfo;

    /**
     * 实例是否存在
     */
    private Boolean instanceExist;

    /**
     * 快照计数
     */
    private Integer snapshotCount;

    /**
     * 实例ID
     */
    private String instanceUuid;

    /**
     * 镜像状态 0:没有实例，1：创建中，2创建成功，3创建失败
     */
    private Integer instanceState;

    /**
     * CPU核数
     */
    private Integer cpu;

    /**
     * 内存大小
     */
    private Integer ram;

    /**
     * 磁盘大小
     */
    private Integer disk;

    /**
     * 是否挂载ISO文件
     */
    private Boolean isoAttach;

    /**
     * ISO文件名称
     */
    private String isoAttachFilename;

    /**
     * ISO临时文件地址
     */
    private String isoTmpdirUuid;

    /**
     * 镜像操作系统类型(1:Linux,2:Windows)
     */
    private String imageOSType;

    /**
     * 来源(1:云平台,2:用户上传)
     */
    private Integer sourceType;

    /**
     * publisher_name(微软云镜像参数)
     */
    private String publisherName;

    /**
     * offer_name(微软云镜像参数)
     */
    private String offerName;

    /**
     * sku(微软云镜像参数)
     */
    private String sku;

    /**
     * 对应的云靶标主键
     */
    private Integer targetPk;
}
